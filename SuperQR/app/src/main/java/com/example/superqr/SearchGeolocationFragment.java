package com.example.superqr;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchGeolocationFragment extends Fragment {

    private static final String playerKey = "playerKey";
    private Player player;

    private ListView nearbyQRList;
    private ArrayAdapter<LocationStore> nearbyQRAdapter;
    private ArrayList<LocationStore> nearbyQRCodes = new ArrayList<>();

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

        // https://www.geeksforgeeks.org/how-to-create-dynamic-listview-in-android-using-firebase-firestore/
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("codes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                QRCode code = d.toObject(QRCode.class);

                                if ((Math.abs(code.getStoreLocation().getLatitude() - player.getPlayerLocation().getLatitude()) < 0.5) &&
                                        (Math.abs(code.getStoreLocation().getLongitude()) - player.getPlayerLocation().getLongitude()) < 0.5) {
                                    nearbyQRCodes.add(code.getStoreLocation());
                                }
                            }
                            QRGeolocationListView adapter = new QRGeolocationListView(requireContext(), nearbyQRCodes);
                            nearbyQRList.setAdapter(adapter);
                        }
                    }
                });

        return searchGeoView;
    }


}
