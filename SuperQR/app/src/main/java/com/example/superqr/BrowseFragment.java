package com.example.superqr;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrowseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseFragment extends Fragment implements View.OnClickListener {

    //initialize variables and key used to pass through
    private static final String playerKey = "playerKey";
    private Player player;

    private Button playerSearchButton;
    private Button viewRankingButton;
    private Button viewCodesButton;
    private Button searchGeolocationButton;

    public BrowseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player current player of the game
     * @return A new instance of fragment BrowseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseFragment newInstance(Player player) {
        BrowseFragment fragment = new BrowseFragment();
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
        View browseView = inflater.inflate(R.layout.fragment_browse, container, false);

        playerSearchButton = (Button) browseView.findViewById(R.id.player_search_button);
        playerSearchButton.setOnClickListener(this);

        viewRankingButton = (Button) browseView.findViewById(R.id.view_ranking_button);
        viewRankingButton.setOnClickListener(this);

        viewCodesButton = (Button) browseView.findViewById(R.id.view_codes_button);
        viewCodesButton.setOnClickListener(this);

        searchGeolocationButton = (Button) browseView.findViewById(R.id.search_geolocation_button);
        searchGeolocationButton.setOnClickListener(this);
        showButtons();

        player = (Player) getArguments().getParcelable(playerKey);

        return browseView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player_search_button:
                // https://stackoverflow.com/questions/16728426/android-nested-fragment-approach

                Fragment searchPlayerFragment = new SearchPlayerFragment();
                displayFragment(searchPlayerFragment);
                break;

            case R.id.view_ranking_button:
                Fragment leaderboardFragment = new LeaderboardFragment();
                displayFragment(leaderboardFragment);
                break;

            case R.id.search_geolocation_button:

                Fragment searchGeolocationFragment = new SearchGeolocationFragment();
                displayFragment(searchGeolocationFragment);
                break;

            case R.id.view_codes_button:
                Fragment viewCodesFragment = new ViewPlayerCodesFragment();
                displayFragment(viewCodesFragment);
                break;

        }
    }

    public void displayFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.browse_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        hideButtons();
    }

    public void hideButtons() {
        playerSearchButton.setVisibility(View.INVISIBLE);
        viewRankingButton.setVisibility(View.INVISIBLE);
        viewCodesButton.setVisibility(View.INVISIBLE);
        searchGeolocationButton.setVisibility(View.INVISIBLE);
    }

    public void showButtons() {
        playerSearchButton.setVisibility(View.VISIBLE);
        viewRankingButton.setVisibility(View.VISIBLE);
        viewCodesButton.setVisibility(View.VISIBLE);
        searchGeolocationButton.setVisibility(View.VISIBLE);
    }

}