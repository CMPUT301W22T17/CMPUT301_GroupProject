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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setUserInfo();
        setQRInfo();

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