package com.example.superqr;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements View.OnClickListener {

    //initialize variables, and key used to pass through
    private static final String playerKey = "playerKey";
    private Player player;
    private MapView map;
    private MapController controller;
    private Marker playerMarker;
    private Marker locationMarker;
    private Drawable playerPin;
    private Drawable QRPin;
    private Drawable locationPin;
    private GeoPoint playerPoint;
    private LocationStore singleCodeLocation = null;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton searchLocationButton;
    private EditText locationSearchText;
    private ArrayList<Marker> nearbySearchedCodes = new ArrayList<Marker>();

    /* From: github.io
     * At: https://osmdroid.github.io/osmdroid/How-to-use-the-osmdroid-library.html
     * Authors: Neil Boyd, kurtzmarc, Alex O'Ree, Matt Kergall https://osmdroid.github.io/osmdroid/team-list.html
     * To: Initialize the map API
     */

    /* From github.io
     * At: https://osmdroid.github.io/osmdroid/javadocAll/index.html
     * Authors: Neil Boyd, kurtzmarc, Alex O'Ree, Matt Kergall https://osmdroid.github.io/osmdroid/team-list.html
     * To: Used to reference the classes and methods that come with osmdroid in order to implement the map
     */

    /* From stackoverflow.com
     * At: https://stackoverflow.com/questions/54811451/osmdroid-default-marker-moving-when-zooming-out-on-android-api-28
     * Author: PottokDev https://stackoverflow.com/users/11096412/pottokdev
     * To: Make the scaling for the markers on the map fixed
     */
    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player
     *      Current player of the game
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
        /* From: stackoverflow.com
         * At: https://stackoverflow.com/questions/14897143/integrating-osmdroid-with-fragments
         * Author: viduka https://stackoverflow.com/users/2440169/viduka
         */
        View view = inflater.inflate(R.layout.activity_display_map, container, false);

        zoomInButton = view.findViewById(R.id.zoom_in_button);
        zoomInButton.setOnClickListener(this);

        zoomOutButton = view.findViewById(R.id.zoom_out_button);
        zoomOutButton.setOnClickListener(this);

        searchLocationButton = view.findViewById(R.id.search_location_button);
        searchLocationButton.setOnClickListener(this);

        locationSearchText = (EditText) view.findViewById(R.id.location_search_text);

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
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        if (singleCodeLocation == null) {

            // get Player from MainActivity
            player = (Player) getArguments().getParcelable(playerKey);
            Location playerLocation = new Location("map_location");
            playerLocation.setLatitude(player.getLocation().getLatitude());
            playerLocation.setLongitude(player.getLocation().getLongitude());
            playerPoint = new GeoPoint(playerLocation);
            setToLocation(playerPoint);
            addLocationMarkers();
            addQRLocationMarkers(playerLocation, 0.04, true);
        }

        else {

            // create qr marker
            Marker codeMarker = new Marker(map);
            codeMarker.setIcon(QRPin);

            Location codeLocation = new Location("map_location");
            codeLocation.setLatitude(singleCodeLocation.getLatitude());
            codeLocation.setLongitude(singleCodeLocation.getLongitude());
            GeoPoint codePoint = new GeoPoint(codeLocation);
            codeMarker.setPosition(codePoint);
            codeMarker.setTitle(Double.toString(singleCodeLocation.getLatitude()) + ", " + Double.toString(singleCodeLocation.getLongitude()));
            codeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(codeMarker);
            setToLocation(codePoint);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zoom_in_button:
                controller.zoomIn();
                break;

            case R.id.zoom_out_button:
                controller.zoomOut();
                break;
            case R.id.search_location_button:
                String searchedLocation = locationSearchText.getText().toString();
                if (searchedLocation != "") {
                    try {
                        searchLocation(searchedLocation);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "No location entered.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Create location icons used to show where the player is.
     */
    private void createLocationIcons() {
        /* Icon Attributions:
         * From: flaticon.com
         * Player Map Marker Icon: <a href="https://www.flaticon.com/free-icons/location" title="location icons">Location icons created by IconMarketPK - Flaticon</a>
         * QR Map Marker Icon: <a href="https://www.flaticon.com/free-icons/location" title="location icons">Location icons created by IconMarketPK - Flaticon</a>
         * Searched Location Map Marker Icon: <a href="https://www.flaticon.com/free-icons/marker" title="marker icons">Marker icons created by IconMarketPK - Flaticon</a>
         */

        /* From: stackoverflow.com
         * At: https://stackoverflow.com/questions/60301641/customized-icon-in-osmdroid-marker-android
         * Author: Haniku https://stackoverflow.com/users/4778388/haniku
         */
        Drawable initialPlayerPin = ResourcesCompat.getDrawable(getResources(), R.drawable.player_marker, null);
        playerPin = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap((((BitmapDrawable) initialPlayerPin).getBitmap()), (int) (33.0f * getResources().getDisplayMetrics().density), (int) (36.0f * getResources().getDisplayMetrics().density), true));

        Drawable initialQRPin = ResourcesCompat.getDrawable(getResources(), R.drawable.qr_marker, null);
        QRPin = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap((((BitmapDrawable) initialQRPin).getBitmap()), (int) (33.0f * getResources().getDisplayMetrics().density), (int) (36.0f * getResources().getDisplayMetrics().density), true));

        Drawable initialLocationPin = ResourcesCompat.getDrawable(getResources(), R.drawable.location_marker, null);
        locationPin = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap((((BitmapDrawable) initialLocationPin).getBitmap()), (int) (33.0f * getResources().getDisplayMetrics().density), (int) (36.0f * getResources().getDisplayMetrics().density), true));

        playerMarker = new Marker(map);
        playerMarker.setIcon(playerPin);

    }

    /**
     * Set the zoom and center the map onto the central given location.
     * @param point
     *      The point that the map centers in on.
     */
    private void setToLocation(GeoPoint point) {
        /* From: stackoverflow.com
         * At: https://stackoverflow.com/questions/40257342/how-to-display-user-location-on-osmdroid-mapview
         * Author: Sergey Nikitin https://stackoverflow.com/users/4837089/sergey-nikitin
         */
        controller.setZoom(18);
        controller.zoomIn();
        controller.setZoom(2);
        controller.animateTo(point);
        controller.setCenter(point);
    }

    /**
     * Put markers onto the map of the player.
     */
    private void addLocationMarkers() {

        playerMarker.setPosition(playerPoint);
        playerMarker.setTitle(Double.toString(player.getLocation().getLatitude()) + ", " + Double.toString(player.getLocation().getLongitude()));
        playerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(playerMarker);
    }

    /**
     * Put markers onto the map of the QR codes that are nearby the player
     */
    private void addQRLocationMarkers(Location centerLocation, double radius, boolean nearPlayer) {

        if (!nearbySearchedCodes.isEmpty()) {
            for (int i = 0; i < nearbySearchedCodes.size(); i++) {
                map.getOverlays().remove(nearbySearchedCodes.get(i));
                nearbySearchedCodes.remove(nearbySearchedCodes.get(i));
            }
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /* From: firebase.google.com
         * At: https://firebase.google.com/docs/firestore/query-data/queries
         * Author: Not Provided
         */
        db.collection("codes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                QRCode code = d.toObject(QRCode.class);
                                double latDifference = (double) (Math.abs(code.getLocation().getLatitude() - centerLocation.getLatitude()));
                                double longDifference = (double) (Math.abs(code.getLocation().getLongitude() - centerLocation.getLongitude()));

                                if (latDifference < radius && longDifference < radius) {

                                    Location location = new Location("map_location");
                                    location.setLatitude(code.getLocation().getLatitude());
                                    location.setLongitude(code.getLocation().getLongitude());

                                    GeoPoint QRPoint = new GeoPoint(location);

                                    Marker QRMarker = new Marker(map);
                                    QRMarker.setIcon(QRPin);
                                    QRMarker.setPosition(QRPoint);

                                    double euclideanDistance = getEuclideanDistance(centerLocation, code.getLocation());

                                    /* From: stackoverflow.com
                                     * At: https://stackoverflow.com/questions/2538787/how-to-print-a-float-with-2-decimal-places-in-java
                                     * Author: Bozho https://stackoverflow.com/users/203907/bozho
                                     */
                                    DecimalFormat decimalRounder = new DecimalFormat();
                                    decimalRounder.setMaximumFractionDigits(4);
                                    if (!nearPlayer) {
                                        QRMarker.setTitle(Double.toString(Double.parseDouble(decimalRounder.format(euclideanDistance))) + " km away from your searched location."
                                                + "\nScore: " + Integer.toString(code.getScore()));
                                        nearbySearchedCodes.add(QRMarker);
                                    }
                                    else {
                                        QRMarker.setTitle(Double.toString(Double.parseDouble(decimalRounder.format(euclideanDistance))) + " km away from you."
                                                + "\nScore: " + Integer.toString(code.getScore()));
                                    }
                                    QRMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    map.getOverlays().add(QRMarker);
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Searches for the location that the player gives and places a marker for it on the map
     * @param searchedLocation
     *      A string representing the location that the player is searching for
     * @throws IOException
     */
    private void searchLocation(String searchedLocation) throws IOException {
        /* From: stackoverflow.com
         * At: https://stackoverflow.com/questions/69148288/how-to-search-location-name-on-osmdroid-to-get-latitude-longitude
         * Author: Faiz Azhar Ristya Nugraha https://stackoverflow.com/users/12543189/faiz-azhar-ristya-nugraha
         */
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> geoResults = geocoder.getFromLocationName(searchedLocation, 1);
            if (!geoResults.isEmpty()) {

                map.getOverlays().remove(locationMarker);
                Address address = geoResults.get(0);
                Location location = new Location("map_location");
                location.setLatitude(address.getLatitude());
                location.setLongitude(address.getLongitude());
                GeoPoint foundPoint = new GeoPoint(address.getLatitude(), address.getLongitude());

                locationMarker = new Marker(map);
                locationMarker.setIcon(locationPin);
                setToLocation(foundPoint);
                locationMarker.setPosition(foundPoint);

                locationMarker.setTitle(Double.toString(address.getLatitude()) + ", " + Double.toString(address.getLongitude()));
                locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(locationMarker);

                locationSearchText.getText().clear();
                addQRLocationMarkers(location, 0.05, false);

            }
            else {
                Toast.makeText(getActivity(), "Cannot find location.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException error) {
            Log.d(TAG, error.getMessage());
        }
    }

    /**
     * Calculates the euclidean distance between two given points
     * @param centerPoint
     *      The point the other point compares with
     * @param otherPoint
     *      The point that the center point compares with
     * @return The euclidean distance between the center and other point
     */
    private double getEuclideanDistance(Location centerPoint, LocationStore otherPoint) {

        double centerLatitudeKm = centerPoint.getLatitude() * 110.574;
        double centerLongitudeKm = centerPoint.getLongitude() * (111.320 * Math.cos(centerPoint.getLatitude()));
        double otherLatitudeKm = otherPoint.getLatitude() * 110.574;
        double otherLongitudeKm = otherPoint.getLongitude() * (111.320 * Math.cos(otherPoint.getLatitude()));
        double distance = Math.sqrt(Math.pow(centerLatitudeKm - otherLatitudeKm, 2) + Math.pow(centerLongitudeKm - otherLongitudeKm, 2));

        return distance;
    }

}
