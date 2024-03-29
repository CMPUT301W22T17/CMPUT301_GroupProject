package com.example.superqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 *
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplaySearchPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * This fragment displays user information
 * such as username, email, and phone
 * are displayed in a textview respectively
 * There is a "View Code" button that is visible to everyone
 * by pressing the "View code" button, user will be brought to
 * a fragment to view QR codes that are scanned by this player
 * The "DELETE" button is only visible to an admin, where they
 * can delete the given player from the database.
 */
public class DisplaySearchPlayerFragment extends Fragment {
    // the fragment initialization parameters
    private static final String playerKey = "playerKey";
    private static final String otherPlayerKey = "otherPlayerKey";
    private FirebaseFirestore db;
    private Player player;
    private Player otherPlayer;
    private TextView otherPlayerUserNameTextView;
    private TextView otherPlayerEmailTextView;
    private TextView otherPlayerPhoneTextView;
    private TextView viewHighScoreTextView;
    private TextView viewLowScoreTextView;
    private TextView totalScannedTextView;
    private TextView totalScoreTextView;
    private TextView highScoreText;
    private TextView lowScoreText;
    private TextView totalScoreText;
    private TextView totalScannedText;
    private Button viewCodeButton;
    private Button deleteButton;
    private StorageReference mStorageRef;

    public DisplaySearchPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param otherPlayer
     * @return A new instance of fragment DisplaySearchPlayerFragment.
     */

    public static DisplaySearchPlayerFragment newInstance(Player player, Player otherPlayer) {
        DisplaySearchPlayerFragment fragment = new DisplaySearchPlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable(playerKey, player);
        args.putParcelable(otherPlayerKey, otherPlayer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View displayPlayerView = inflater.inflate(R.layout.fragment_display_search_player, container, false);

        otherPlayerUserNameTextView = displayPlayerView.findViewById(R.id.other_player_username);
        otherPlayerEmailTextView = displayPlayerView.findViewById(R.id.other_player_email);
        otherPlayerPhoneTextView = displayPlayerView.findViewById(R.id.other_player_phone);
        viewHighScoreTextView = displayPlayerView.findViewById(R.id.view_high_score_text_view);
        viewLowScoreTextView = displayPlayerView.findViewById(R.id.view_low_score_text_view);
        totalScannedTextView = displayPlayerView.findViewById(R.id.total_qr_scanned);
        totalScoreTextView = displayPlayerView.findViewById(R.id.total_qr_score);
        viewCodeButton = displayPlayerView.findViewById(R.id.view_others_code_button);
        deleteButton = displayPlayerView.findViewById(R.id.delete_button);
        highScoreText = displayPlayerView.findViewById(R.id.high_score_view);
        lowScoreText = displayPlayerView.findViewById(R.id.other_player_low_score_view);
        totalScoreText = displayPlayerView.findViewById(R.id.other_player_total_score);
        totalScannedText = displayPlayerView.findViewById(R.id.other_player_total_scanned);

        player = (Player) getArguments().getParcelable(playerKey);
        otherPlayer = (Player) getArguments().getParcelable(otherPlayerKey);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        setViews();

        if (player.getIsAdmin()) {
            deleteButton.setVisibility(View.VISIBLE);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otherPlayerName = otherPlayer.getSettings().getUsername();
                DocumentReference df = db.collection("users").document(otherPlayerName);

                ArrayList<QRCode> codes = otherPlayer.getStats().getQrCodes();
                for (int i = 0; i < codes.size(); i++){
                    QRCode code = codes.get(i);
                    mStorageRef.child(otherPlayer.getPlayerID()).child(code.getHash()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }

                df.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        hideView();
                        Toast.makeText(getActivity(), ("Deleted Player " + otherPlayerName), Toast.LENGTH_SHORT).show();
                        Fragment browseFragment = BrowseFragment.newInstance(player);
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.other_player_info_fragment, browseFragment);
                        fragmentTransaction.commit();
                    }
                });


            }
        });

        viewCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideView();
                Fragment fragment = ViewPlayerCodesFragment.newInstance(player, otherPlayer);
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.other_player_info_fragment, fragment);
                fragmentTransaction.commit();
            }
        });


        return displayPlayerView;
    }

    /**
     * display a player's info in the correct field
     */
    private void setViews() {
        otherPlayerUserNameTextView.setText(otherPlayer.getSettings().getUsername());
        otherPlayerEmailTextView.setText(otherPlayer.getSettings().getEmail());
        otherPlayerPhoneTextView.setText(otherPlayer.getSettings().getPhone());
        viewHighScoreTextView.setText(Integer.toString(otherPlayer.getStats().getHighestScore().getScore()));
        viewLowScoreTextView.setText((Integer.toString(otherPlayer.getStats().getLowestScore().getScore())));
        totalScannedTextView.setText(Integer.toString(otherPlayer.getStats().getCounts()));
        totalScoreTextView.setText(Integer.toString(otherPlayer.getStats().getTotalScore()));
    }

    /**
     * make views invisible
     */
    private void hideView() {
        otherPlayerUserNameTextView.setVisibility(View.INVISIBLE);
        otherPlayerEmailTextView.setVisibility(View.INVISIBLE);
        otherPlayerPhoneTextView.setVisibility(View.INVISIBLE);
        viewHighScoreTextView.setVisibility(View.INVISIBLE);
        viewLowScoreTextView.setVisibility(View.INVISIBLE);
        totalScannedTextView.setVisibility(View.INVISIBLE);
        totalScoreTextView.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);
        viewCodeButton.setVisibility(View.INVISIBLE);
        highScoreText.setVisibility(View.INVISIBLE);
        lowScoreText.setVisibility(View.INVISIBLE);
        totalScoreText.setVisibility(View.INVISIBLE);
        totalScannedText.setVisibility(View.INVISIBLE);
    }
}