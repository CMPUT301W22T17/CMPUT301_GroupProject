package com.example.superqr;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewPlayerCodesFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Displays QR codes in a grid, with each item in the grid consisting of the QR codes object photo
 * and the score of the QR code.
 */
public class ViewPlayerCodesFragment extends Fragment {
    private static final String playerKey = "playerKey";
    private static final String otherPlayerKey = "otherPlayerKey";
    private GridView codes;
    private Player player;
    private Player otherPlayer; //this is the player whose codes are being viewed
    private ArrayList<String> imageList;
    private int numCodes;
    private StorageReference playerRef;
    private ProgressBar loadingImage;
    private TextView noQRCodes;
    private int i;

    public ViewPlayerCodesFragment() {
        // Required empty public constructor
    }

    /**
     * @param player
     *      Current player of the game
     * @return
     *      A new instance of ViewPlayerCodes Fragment
     */
    public static ViewPlayerCodesFragment newInstance(Player player, Player otherPlayer) {
        ViewPlayerCodesFragment fragment = new ViewPlayerCodesFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(playerKey, player);
        bundle.putParcelable(otherPlayerKey, otherPlayer);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // From: geeksforgeeks.org
    // At: https://www.geeksforgeeks.org/gridview-using-custom-arrayadapter-in-android-with-example/
    // Author: clivefernandes777 https://auth.geeksforgeeks.org/user/clivefernandes777/articles

    // From: geeksforgeeks.org
    // At: https://www.geeksforgeeks.org/how-to-create-dynamic-gridview-in-android-using-firebase-firestore/
    // Author: chaitanyamunje https://auth.geeksforgeeks.org/user/chaitanyamunje/articles

    // From: geeksforgeeks.org
    // At: https://www.geeksforgeeks.org/how-to-view-all-the-uploaded-images-in-firebase-storage/
    // Author: annianni https://auth.geeksforgeeks.org/user/annianni/articles
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        player = (Player) getArguments().getParcelable(playerKey);
        otherPlayer = (Player) getArguments().getParcelable(otherPlayerKey);
        View root = inflater.inflate(R.layout.fragment_player_codes, container, false);

        codes = root.findViewById(R.id.browse_qr_codes);
        loadingImage = root.findViewById(R.id.loading_image_browse);
        noQRCodes = root.findViewById(R.id.no_qr_codes_text);

        imageList = new ArrayList<>();
        playerRef = FirebaseStorage.getInstance().getReference().child(otherPlayer.getPlayerID()); // Gets path to playerID
        numCodes = otherPlayer.getStats().getQrCodes().size();

        // Shows no codes text when player has no QR codes
        if (numCodes == 0) {
            loadingImage.setVisibility(View.GONE);
            noQRCodes.setVisibility(View.VISIBLE);
        }

        // Gets the images from FireStorage
        for (i = 0; i < numCodes; i++) {
            playerRef.child(otherPlayer.getStats().getQrCodes().get(i).getHash()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageList.add(uri.toString());
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) { // Image was found
                    if (imageList.size() == numCodes) {
                        setAdapter();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) { // No image found
                    imageList.add(String.format("placeholder" + "-" + "%s", otherPlayer.getStats().getQrCodes().get(i - 1).getHash()));
                    if (imageList.size() == numCodes) {
                        setAdapter();
                    }

                }
            });
        }
        codes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Launch another fragment to view QR code with more options
                codes.setVisibility(View.GONE);
                String link = (String) codes.getItemAtPosition(i);
                String[] isPlaceholder = link.split("-");
                String hash;
                try {
                    hash = FirebaseStorage.getInstance().getReferenceFromUrl(link).getName();
                }
                catch (IllegalArgumentException e) {
                    hash = isPlaceholder[1];
                }
                ArrayList<QRCode> codes = otherPlayer.getStats().getQrCodes();
                QRCode qrCode = null;
                for (int j = 0; j < codes.size(); j++){
                    QRCode curCode = codes.get(j);
                    if (hash.equals(curCode.getHash())){
                        qrCode = curCode;
                    }
                }
                ViewQRCodeFragment fragment = ViewQRCodeFragment.newInstance(player, otherPlayer, qrCode, link);
                displayFragment(fragment);
            }
        });

        return root;
    }

    /**
     * Sets the list of images to be adapted
     */
    private void setAdapter() {
        PlayerCodesGridView adapter = new PlayerCodesGridView(getActivity(), imageList, player, otherPlayer);
        codes.setAdapter(adapter);
        loadingImage.setVisibility(View.GONE);
    }
    /**
     * Places a fragment on frame_layout in the main activity
     * @param fragment Fragment to be displayed
     */
    public void displayFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.browse_container, fragment);
        fragmentTransaction.commit();
    }
}
