package com.example.superqr;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewQRCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Displays the image and score of a QR code.
 * Players can comment on all QR codes.
 * If the player owns the QR code or the player is an admin, then the player is able to delete the
 * QR code.
 */
public class ViewQRCodeFragment extends Fragment {
    private static final String playerKey = "playerKey";
    private static final String otherPlayerKey = "otherPlayerKey";
    private static final String codeKey = "codeKey";
    private static final String linkKey = "linkKey";
    private Player player;
    private Player otherPlayer;
    private QRCode code;
    private String link;
    private ImageView objectImage;
    private Button deleteBtn;
    private EditText editText;
    private Button addBtn;
    private ListView listView;
    private FirebaseFirestore db ;
    private StorageReference mStorageRef;
    private ArrayAdapter<String> adapter;
    private TextView scoreText;

    public ViewQRCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ViewQRCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewQRCodeFragment newInstance(Player player, Player otherPlayer, QRCode code, String link) {
        ViewQRCodeFragment fragment = new ViewQRCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(playerKey, player);
        bundle.putParcelable(otherPlayerKey, otherPlayer);
        bundle.putParcelable(codeKey, code);
        bundle.putString(linkKey, link);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        player = (Player) getArguments().getParcelable(playerKey);
        otherPlayer = (Player) getArguments().getParcelable(otherPlayerKey);
        code = (QRCode) getArguments().getParcelable(codeKey);
        link = (String) getArguments().getString(linkKey);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_qr_code, container, false);

        objectImage = view.findViewById(R.id.code_image);
        deleteBtn = view.findViewById(R.id.deleteQRButton);
        scoreText = view.findViewById(R.id.score_text);
        editText = view.findViewById(R.id.comment_text);
        addBtn = view.findViewById(R.id.addCommentButton);
        listView = view.findViewById(R.id.comment_list);

        //dynamically set text box size to size of display
        /* From: stackoverflow.com
         * At: https://stackoverflow.com/questions/11629675/get-screen-width-and-height-in-a-fragment
         * Author: rajpara https://stackoverflow.com/users/582571/rajpara
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int editTextWidth = width - 200;
        editText.setLayoutParams(new LinearLayout.LayoutParams(editTextWidth,LinearLayout.LayoutParams.WRAP_CONTENT));
        scoreText.setText(String.valueOf(code.getScore()));

        //set delete button to invisible if player is not on their own QRCode, and is not admin
        if (player!=otherPlayer && !player.getIsAdmin()) {
            deleteBtn.setVisibility(View.GONE);
        }

        //display photo
        String[] isPlaceholder = link.split("-");
        if (isPlaceholder[0].equals("placeholder")) { // No photo in FireStorage
            Picasso.get().load(R.drawable.image_placeholder).into(objectImage);
        }
        else { // Has photo in FireStorage
            Picasso.get().load(link).into(objectImage);
        }
        updateAdapter(code.getComments());

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player == otherPlayer) {
                    player = removeCode(player, code);
                }
                else {
                    otherPlayer = removeCode(otherPlayer, code);
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, BrowseFragment.newInstance(player));
                fragmentTransaction.commit();
                }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = editText.getText().toString();
                if (!(comment.equals(""))) {
                    if (player == otherPlayer) {
                        player = addComment(player, otherPlayer, code, comment);


                    } else {
                        otherPlayer = addComment(otherPlayer, player, code, comment);
                    }
                }
                updateAdapter(code.getComments());
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        return view;
    }

    /**
     * removes a player's QRCode in the database and photo
     * returns updated player object
     * @param player
     * @param code
     */
    public Player removeCode(Player player, QRCode code){
        PlayerStats stats = player.getStats();
        stats.deleteQRCode(code);
        player.setStats(stats);
        db.collection("users").document(player.getSettings().getUsername()).update(
                "stats.qrCodes", FieldValue.arrayRemove(code),
                "stats.counts", (stats.getCounts()),
                "stats.highestScore", (stats.getHighestScore()),
                "stats.lowestScore", (stats.getLowestScore()),
                "stats.totalScore", (stats.getTotalScore()));

        mStorageRef.child(player.getPlayerID()).child(code.getHash()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        return player;
    }

    /**
     * Adds otherPlayer's comment to player's QRCode in the database
     * returns updated player object with the comment.
     * @param player Player viewing QR code
     * @param otherPlayer Player that scanned the QR code being viewed
     * @param code QR code being viewed
     * @param comment Comment on the QR code
     * @return
     */
    public Player addComment(Player player, Player otherPlayer, QRCode code, String comment){
        PlayerStats stats = player.getStats();
        ArrayList<QRCode> codes = stats.getQrCodes();
        String commentU = otherPlayer.getSettings().getUsername().concat(": " + comment);
        int position = codes.indexOf(code);
        if (position != -1) {
            code.addComment(commentU);
            codes.set(position, code);
        }
        stats.setQrCodes(codes);
        player.setStats(stats);

        //update DB
        db.collection("users").document(player.getSettings().getUsername()).update(
                "stats.qrCodes", (codes),
                "stats.counts", (stats.getCounts()),
                "stats.highestScore", (stats.getHighestScore()),
                "stats.lowestScore", (stats.getLowestScore()),
                "stats.totalScore", (stats.getTotalScore()));
        return player;
    }

    /**
     * update comment listview adapter
     * @param comments Comment on the QR code
     */
    public void updateAdapter(ArrayList<String> comments){
        String[] strComments = new String[code.getComments().size()];

        for (int i = 0; i < code.getComments().size(); i++) {
            strComments[i] = code.getComments().get(i);
        }
        /* From: youtube.com
         * At: https://www.youtube.com/watch?v=MCfsfgpmkEI
         * Author: Ralph Alex Charlemagne https://www.youtube.com/channel/UCBzSxKOoOn1jxmFp4wSwMRA
         */
        adapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, strComments);
        listView.setAdapter(adapter);

        editText.setText("");
    }

}