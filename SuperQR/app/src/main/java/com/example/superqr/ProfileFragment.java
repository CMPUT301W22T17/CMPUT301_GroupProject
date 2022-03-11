package com.example.superqr;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    //initialize variables, and key used to pass through
    private static final String playerKey = "playerKey";
    private Player player;
    private Button qrHighScoreButton;
    private Button qrLowScoreButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player current player of the game
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(Player player) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(playerKey, player);
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
        // Inflate the layout for this fragment
        View profileView = inflater.inflate(R.layout.activity_player_profile, container, false);
        qrHighScoreButton = (Button) profileView.findViewById(R.id.view_high_score_button);
        qrHighScoreButton.setOnClickListener(this);

        qrLowScoreButton = (Button) profileView.findViewById(R.id.view_low_score_button);
        qrLowScoreButton.setOnClickListener(this);

        player = (Player) getArguments().getParcelable(playerKey);

        // https://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
        // get the object from Main
        setUserInfo();
        setQRInfo();

        return profileView;
    }

    // https://stackoverflow.com/questions/11857022/fragment-implements-onclicklistener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_high_score_button:
                // replace 2 with the high qr score player has
                // https://stackoverflow.com/questions/25887373/calling-dialogfragment-from-fragment-not-fragmentactivity
                DialogFragment highQRScoreFragment = new ViewQRScoreFragment("2");
                highQRScoreFragment.show(getActivity().getSupportFragmentManager(), "high_qr_dialog");

            case R.id.view_low_score_button:
                // replace 1 with the low qr score player has
                DialogFragment lowQRScoreFragment = new ViewQRScoreFragment("1");
                lowQRScoreFragment.show(getActivity().getSupportFragmentManager(), "high_qr_dialog");
        }
    }

    /**
     * Displays the player's personal information
     */
    public void setUserInfo() {
        // https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        TextView usernameText = getActivity().findViewById(R.id.player_username);
        TextView emailText = getActivity().findViewById(R.id.player_email);
        TextView phoneText = getActivity().findViewById(R.id.player_phone);

        /* implement later
        usernameText.setText("player's username");
        emailText.setText("player's email");
        phoneText.setText("player's phone number");
         */

        /*String userName = player.getSettings().getUsername();
        String email = player.getSettings().getEmail();
        String phone = player.getSettings().getPhone();

        usernameText.setText(userName);
        emailText.setText(email);
        phoneText.setText(phone);*/

    }

    /**
     * Displays the player's QR stats about total scanned and total score for their QR codes
     */
    public void setQRInfo() {
        TextView totalScannedText = getActivity().findViewById(R.id.total_qr_scanned);
        TextView totalScoreText = getActivity().findViewById(R.id.total_qr_score);

        /* implement later
        totalScannedText.setText("player's total scanned");
        totalScoreText.setText("player's total score");
         */
    }

}