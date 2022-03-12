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

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ListView nearbyQRList;
    private ArrayAdapter<Location> nearbyQRAdapter;
    private ArrayList<Location> nearbyQRCodes;

    private static final String playerKey = "playerKey";
    private Player player;
    private Map map;

    public SearchGeolocationFragment() {
        // Required empty public constructor
    }


    public static SearchGeolocationFragment newInstance(String param1, String param2) {
        SearchGeolocationFragment fragment = new SearchGeolocationFragment();
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
        View searchGeoView = inflater.inflate(R.layout.fragment_search_geolocation, container, false);

        nearbyQRList = searchGeoView.findViewById(R.id.nearby_qr_codes);
        map = new Map(player);

        nearbyQRCodes = map.getQRLocations();
        nearbyQRAdapter = new QRGeolocationListView(requireContext(), nearbyQRCodes);
        nearbyQRList.setAdapter(nearbyQRAdapter);

        return searchGeoView;
    }


}
