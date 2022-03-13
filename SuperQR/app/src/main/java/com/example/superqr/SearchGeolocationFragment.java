package com.example.superqr;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchGeolocationFragment extends Fragment {

    private static final String playerKey = "playerKey";
    private Player player;

    private ListView nearbyQRList;
    private ArrayAdapter<LocationStore> nearbyQRAdapter;
    private ArrayList<LocationStore> nearbyQRCodes;

    private Map map;

    public SearchGeolocationFragment() {
        // Required empty public constructor
    }

    public static SearchGeolocationFragment newInstance(Player player) {
        SearchGeolocationFragment fragment = new SearchGeolocationFragment();
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
        View searchGeoView = inflater.inflate(R.layout.fragment_search_geolocation, container, false);

        player = (Player) getArguments().getParcelable(playerKey);

        nearbyQRList = searchGeoView.findViewById(R.id.nearby_qr_codes);
        map = new Map(player);
        map.addQRLocations();
        nearbyQRCodes = map.getQRLocations();
        nearbyQRAdapter = new QRGeolocationListView(requireContext(), nearbyQRCodes);
        nearbyQRList.setAdapter(nearbyQRAdapter);

        return searchGeoView;
    }


}
