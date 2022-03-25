package com.example.superqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchGeolocationFragment extends Fragment {

    private static final String playerKey = "playerKey";
    private Player player;

    private ListView nearbyQRList;
    private ArrayAdapter<LocationStore> nearbyQRAdapter;
    private ArrayList<LocationStore> nearbyQRCodes = new ArrayList<>();
    private double radius = 0.15;

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

                                double latDifference = (double) (Math.abs(code.getLocation().getLatitude() - player.getLocation().getLatitude()));
                                double longDifference = (double) (Math.abs(code.getLocation().getLongitude() - player.getLocation().getLongitude()));
                                if (latDifference < radius && longDifference < radius) {
                                    nearbyQRCodes.add(code.getLocation());
                                }
                            }
                            QRGeolocationListView adapter = new QRGeolocationListView(requireContext(), nearbyQRCodes);
                            nearbyQRList.setAdapter(adapter);
                            nearbyQRList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    LocationStore codeLocation = nearbyQRCodes.get(i);
                                    // open map fragment here

                                    // https://stackoverflow.com/questions/17063378/how-to-pass-bundle-from-fragment-to-fragment
                                    Bundle codeBundle = new Bundle();
                                    codeBundle.putParcelable("code_location", codeLocation);
                                    Fragment mapFragment = new MapFragment();
                                    mapFragment.setArguments(codeBundle);

                                    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

                                    fragmentTransaction.replace(R.id.map_container, mapFragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();

                                }
                            });
                        }
                    }
                });

        return searchGeoView;
    }


}
