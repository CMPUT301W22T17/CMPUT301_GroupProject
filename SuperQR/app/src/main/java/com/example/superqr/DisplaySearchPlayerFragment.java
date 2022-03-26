package com.example.superqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplaySearchPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplaySearchPlayerFragment extends Fragment {

    // the fragment initialization parameters
    private static final String playerKey = "playerKey";
    private Player otherPlayer;
    private TextView otherPlayerUserNameTextView;
    private TextView otherPlayerEmailTextView;
    private TextView otherPlayerPhoneTextView;
    private TextView viewHighScoreTextView;
    private TextView viewLowScoreTextView;
    private TextView totalScannedTextView;
    private TextView totalScoreTextView;

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
    // TODO: Rename and change types and number of parameters
    public static DisplaySearchPlayerFragment newInstance(Player otherPlayer) {
        DisplaySearchPlayerFragment fragment = new DisplaySearchPlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable(playerKey, otherPlayer);
        fragment.setArguments(args);
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
        View displayPlayerView = inflater.inflate(R.layout.fragment_display_search_player, container, false);

        otherPlayerUserNameTextView = displayPlayerView.findViewById(R.id.other_player_username);
        otherPlayerEmailTextView = displayPlayerView.findViewById(R.id.other_player_email);
        otherPlayerPhoneTextView = displayPlayerView.findViewById(R.id.other_player_phone);
        viewHighScoreTextView = displayPlayerView.findViewById(R.id.view_high_score_text_view);
        viewLowScoreTextView = displayPlayerView.findViewById(R.id.view_low_score_text_view);
        totalScannedTextView = displayPlayerView.findViewById(R.id.total_qr_scanned);
        totalScoreTextView = displayPlayerView.findViewById(R.id.total_qr_score);

        otherPlayer = getArguments().getParcelable(playerKey);

        setViews();

        return displayPlayerView;
    }

    private void setViews() {
        otherPlayerUserNameTextView.setText(otherPlayer.getSettings().getUsername());
        otherPlayerEmailTextView.setText(otherPlayer.getSettings().getEmail());
        otherPlayerPhoneTextView.setText(otherPlayer.getSettings().getPhone());
        viewHighScoreTextView.setText(Integer.toString(otherPlayer.getStats().getHighestScore().getScore()));
        viewLowScoreTextView.setText((Integer.toString(otherPlayer.getStats().getLowestScore().getScore())));
        totalScannedTextView.setText(Integer.toString(otherPlayer.getStats().getCounts()));
        totalScoreTextView.setText(Integer.toString(otherPlayer.getStats().getTotalScore()));
    }
}