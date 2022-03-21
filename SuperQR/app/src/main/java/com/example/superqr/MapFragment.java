package com.example.superqr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {
    //initialize variables, and key used to pass through
    private static final String playerKey = "playerKey";
    private Player player;

    // Taken from "Hello osmdroid World"
    // At: https://osmdroid.github.io/osmdroid/How-to-use-the-osmdroid-library.html
    // Reference: https://osmdroid.github.io/osmdroid/javadocs/osmdroid-android/debug/index.html?org/osmdroid/views/MapView.html

    MapView map;
    MapController controller;
    Marker playerMarker;
    Drawable playerPin;
    Drawable QRPin;
    GeoPoint playerPoint;
    LocationStore singleCodeLocation = null;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player current player of the game
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance(Player player) {
        MapFragment fragment = new MapFragment();
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

        //fixing
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));



        // Inflate the layout for this fragment
        // https://stackoverflow.com/questions/14897143/integrating-osmdroid-with-fragments
        View view = inflater.inflate(R.layout.activity_display_map, container, false);


        Bundle codeLocationBundle = getArguments();
        if (codeLocationBundle != null) {
            singleCodeLocation = codeLocationBundle.getParcelable("code_location");
        }

        Context mapContext = getActivity().getApplicationContext();
        Configuration.getInstance().load(mapContext, PreferenceManager.getDefaultSharedPreferences(mapContext));
        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        controller = new MapController(map);
        createLocationIcons();

        if (singleCodeLocation == null) {

            //get Player from MainActivity
            player = (Player) getArguments().getParcelable(playerKey);
            playerPoint = new GeoPoint(player.getPlayerLocation().getLatitude(), player.getPlayerLocation().getLongitude());

            setToUserLocation();
            addLocationMarkers();
        }

        else {

            // create qr marker
            Marker codeMarker = new Marker(map);
            codeMarker.setIcon(QRPin);
            GeoPoint codePoint = new GeoPoint(singleCodeLocation.getLatitude(), singleCodeLocation.getLongitude());
            codeMarker.setPosition(codePoint);
            codeMarker.setTitle(Double.toString(singleCodeLocation.getLatitude()) + ", " + Double.toString(singleCodeLocation.getLongitude()));
            codeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(codeMarker);

            // set to qr marker location
            controller.setZoom(18);
            controller.zoomIn();

            controller.animateTo(codePoint);
            controller.setCenter(codePoint);

        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    /**
     * Create location icons used to show where the player is.
     */
    private void createLocationIcons() {
        //https://stackoverflow.com/questions/60301641/customized-icon-in-osmdroid-marker-android
        // Player Map Marker Icon: <a href="https://www.flaticon.com/free-icons/location" title="location icons">Location icons created by IconMarketPK - Flaticon</a>
        // QR Map Marker Icon: <a href="https://www.flaticon.com/free-icons/location" title="location icons">Location icons created by IconMarketPK - Flaticon</a>

        Drawable initialPlayerPin = ResourcesCompat.getDrawable(getResources(), R.drawable.player_marker, null);
        playerPin = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap((((BitmapDrawable) initialPlayerPin).getBitmap()), (int) (33.0f * getResources().getDisplayMetrics().density), (int) (36.0f * getResources().getDisplayMetrics().density), true));

        Drawable initialQRPin = ResourcesCompat.getDrawable(getResources(), R.drawable.qr_marker, null);
        QRPin = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap((((BitmapDrawable) initialQRPin).getBitmap()), (int) (33.0f * getResources().getDisplayMetrics().density), (int) (36.0f * getResources().getDisplayMetrics().density), true));

        playerMarker = new Marker(map);
        playerMarker.setIcon(playerPin);

    }

    /**
     * Set the zoom and center the map onto user location.
     */
    private void setToUserLocation() {
        // https://stackoverflow.com/questions/40257342/how-to-display-user-location-on-osmdroid-mapview

        controller.setZoom(18);
        controller.zoomIn();

        controller.animateTo(playerPoint);
        controller.setCenter(playerPoint);

    }

    /**
     * Put markers onto the map of the nearby QR codes and player.
     */
    private void addLocationMarkers() {

        playerMarker.setPosition(playerPoint);
        playerMarker.setTitle(Double.toString(player.getPlayerLocation().getLatitude()) + ", " + Double.toString(player.getPlayerLocation().getLongitude()));
        playerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(playerMarker);

        addQRLocationMarkers();
    }

    private void addQRLocationMarkers() {
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
                                    Location location = new Location("map_location");
                                    location.setLatitude(code.getStoreLocation().getLatitude());
                                    location.setLongitude(code.getStoreLocation().getLongitude());

                                    GeoPoint QRPoint = new GeoPoint(location);

                                    Marker QRMarker = new Marker(map);
                                    QRMarker.setIcon(QRPin);
                                    QRMarker.setPosition(QRPoint);
                                    QRMarker.setTitle(Double.toString(code.getStoreLocation().getLatitude()) + ", " + Double.toString(code.getStoreLocation().getLongitude()));
                                    QRMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    map.getOverlays().add(QRMarker);
                                }
                            }
                        }
                    }
                });
    }

}
