package com.example.superqr;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class DisplayMapActivity extends AppCompatActivity {

    // Taken from "Hello osmdroid World"
    // At: https://osmdroid.github.io/osmdroid/How-to-use-the-osmdroid-library.html
    // Reference: https://osmdroid.github.io/osmdroid/javadocs/osmdroid-android/debug/index.html?org/osmdroid/views/MapView.html

    MapView map;
    Location location = new Location("test");
    MapController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        Context mapContext = getApplicationContext();
        Configuration.getInstance().load(mapContext, PreferenceManager.getDefaultSharedPreferences(mapContext));
        setContentView(R.layout.activity_display_map);

        map = (MapView) findViewById(R.id.map);
        //map.setTileSource(TileSourceFactory.MAPNIK);

        // https://stackoverflow.com/questions/40257342/how-to-display-user-location-on-osmdroid-mapview

        location.setLatitude(43.64259);
        location.setLongitude(-79.38705);
        controller = new MapController(map);

        GeoPoint testPoint = new GeoPoint(location);
        controller.setZoom(18);
        controller.zoomIn();
        controller.animateTo(testPoint);
        controller.setCenter(testPoint);

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
}