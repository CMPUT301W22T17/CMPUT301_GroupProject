package com.example.superqr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Taken from "Hello osmdroid World"
    // At: https://osmdroid.github.io/osmdroid/How-to-use-the-osmdroid-library.html
    // Reference: https://osmdroid.github.io/osmdroid/javadocs/osmdroid-android/debug/index.html?org/osmdroid/views/MapView.html

    // actual code (keep)
    Map mapInfo;
    MapView map;

    MapController controller;
    Marker playerMarker;

    Drawable playerPin;
    Drawable QRPin;
    GeoPoint playerPoint;

    // test code
    Location location;
    Location qrLocation;
    Location qrLocation2;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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

            // actual code (keep)

            Context mapContext = getActivity().getApplicationContext();
            Configuration.getInstance().load(mapContext, PreferenceManager.getDefaultSharedPreferences(mapContext));

            map = (MapView) getActivity().findViewById(R.id.map);
            map.setTileSource(TileSourceFactory.MAPNIK);

            controller = new MapController(map);

            // test code
            mapInfo = new Map();

        /* actual code (add once more classes are built)
        mapInfo = new Map(player); // for player being the name of a Player class
         */

            // test code (to be removed when classes are more built)
            location = new Location("test");
            qrLocation = new Location("test");
            qrLocation2 = new Location("test");
            location.setLatitude(43.64259);
            location.setLongitude(-79.38705);
            qrLocation.setLatitude(43.643);
            qrLocation.setLongitude(-79.3871);
            qrLocation2.setLatitude(43.642);
            qrLocation2.setLongitude(-79.38695);
            mapInfo.addAQRLocation(qrLocation);
            mapInfo.addAQRLocation(qrLocation2);

            // actual code
            createLocationIcons();
            //setToUserLocation();

        /* actual code (to be added when classes are more built)
        playerPoint = new GeoPoint(mapInfo.getPlayerLocation());
         */

            // actual code
            //addLocationMarkers();

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_display_map, container, false);
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

    public void createLocationIcons() {
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

    public void setToUserLocation() {
        // https://stackoverflow.com/questions/40257342/how-to-display-user-location-on-osmdroid-mapview

        // test code
        playerPoint = new GeoPoint(location);

        // actual code
        controller.setZoom(18);
        controller.zoomIn();

        controller.animateTo(playerPoint);
        controller.setCenter(playerPoint);

    }


    public void addLocationMarkers() {

        playerMarker.setPosition(playerPoint);
        playerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(playerMarker);


        ArrayList<Location> QRCodeLocations = mapInfo.getQRLocations();
        for (Location QRLocation : QRCodeLocations) {
            GeoPoint QRPoint = new GeoPoint(QRLocation);
            Marker QRMarker = new Marker(map);
            QRMarker.setIcon(QRPin);
            QRMarker.setPosition(QRPoint);
            QRMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(QRMarker);
        }

    }


}