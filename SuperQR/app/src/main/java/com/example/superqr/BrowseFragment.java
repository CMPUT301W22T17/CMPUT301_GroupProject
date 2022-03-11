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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrowseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseFragment newInstance(String param1, String param2) {
        BrowseFragment fragment = new BrowseFragment();
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