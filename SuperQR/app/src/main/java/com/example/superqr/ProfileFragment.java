package com.example.superqr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Player player;

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

        final Button highQRScoreButton = (Button) getActivity().findViewById(R.id.view_high_score_button);

        /*
        highQRScoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // the 2 is a test number - need player's actual data to replace
                // need to pass in actual qr picture
                //new ViewQRScoreFragment("2").show(getParentFragmentManager(), "VIEW_HIGH_QR");
            }
        });

        final Button lowQRScoreButton = (Button) getView().findViewById(R.id.view_low_score_button);
        lowQRScoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // the 1 is a test number - need player's actual data to replace
                // need to pass in actual qr picture
                // https://stackoverflow.com/questions/20237531/how-can-i-access-getsupportfragmentmanager-in-a-fragment
                //new ViewQRScoreFragment("1").show(getParentFragmentManager(), "VIEW_LOW_QR");
            }
        });

         */

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_player_profile, container, false);
    }

    public void setUserInfo() {
        // https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        TextView usernameText = getActivity().findViewById(R.id.player_username);
        TextView emailText = getActivity().findViewById(R.id.player_email);
        TextView phoneText = getActivity().findViewById(R.id.player_phone);

        /* implement later
        usernameText.setText(player.getUsername());
        emailText.setText(player.getEmail());
        phoneText.setText(player.getPhone());
         */
    }

    public void setQRInfo() {
        TextView totalScannedText = getActivity().findViewById(R.id.total_qr_scanned);
        TextView totalScoreText = getActivity().findViewById(R.id.total_qr_score);

        /* implement later
        totalScannedText.setText(player.getTotalScanned());
        totalScoreText.setText(player.getTotalScore());
         */
    }



}