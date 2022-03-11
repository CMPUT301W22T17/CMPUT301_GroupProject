package com.example.superqr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;



/**
 * Player is the main interface by which most classes are managed by.
 */

public class Player implements Parcelable, Comparable<Player> {
    private PlayerSettings settings;
    private PlayerStats stats;
    private Location location;
    private LocationRequest locationRequest = LocationRequest.create();


    public Player(String userName, String phone, String email) {
        this.settings = new PlayerSettings(userName, phone, email);
        this.stats = new PlayerStats();
        this.location = new Location("map_location");
    }

    protected Player(Parcel in) {
        settings = in.readParcelable(PlayerSettings.class.getClassLoader());
        stats = in.readParcelable(PlayerStats.class.getClassLoader());
        location = in.readParcelable(Location.class.getClassLoader());
        locationRequest = in.readParcelable(LocationRequest.class.getClassLoader());
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public Location getPlayerLocation() {
        return this.location;
    }

    public PlayerSettings getSettings() {
        return this.settings;
    }

    public PlayerStats getStats() {
        return this.stats;
    }

    public void updateLocation(Activity activity){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isGPSEnabled(activity)) {
                LocationServices.getFusedLocationProviderClient(activity)
                        .requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                LocationServices.getFusedLocationProviderClient(activity)
                                        .removeLocationUpdates(this);

                                if (locationResult.getLocations().size() > 0) {
                                    int index = locationResult.getLocations().size() - 1;
                                    double latitude = locationResult.getLocations().get(index).getLatitude();
                                    double longitude = locationResult.getLocations().get(index).getLongitude();
                                    location.setLatitude(latitude);
                                    location.setLongitude(longitude);
                                }
                            }
                        }, Looper.getMainLooper());
            }
            else {
                turnOnGPS(activity);
            }
        }
        else {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private boolean isGPSEnabled(Activity activity){
        LocationManager locationManager;
        boolean isEnabled;
        locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    private void turnOnGPS(Activity activity){
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(activity.getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {

            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                Toast.makeText(activity, "GPS is already turned on", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {

                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                            resolvableApiException.startResolutionForResult(activity,0x1);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Device does not have location
                        break;
                }
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(settings, i);
        parcel.writeParcelable(stats, i);
        parcel.writeParcelable(location, i);
        parcel.writeParcelable(locationRequest, i);
    }


    @Override
    public int compareTo(Player player) {
        if (this.stats.getTotalScore() == player.stats.getTotalScore()) {
            return 0;
        } else if (this.stats.getTotalScore() > player.stats.getTotalScore()) {
            return -1;
        } else {
            return 1;
        }
    }


    @Override
    public int compareTo(Player player) {
        if (this.stats.getHighestScore() == player.stats.getHighestScore()) {
            return 0;
        } else if (this.stats.getHighestScore() > player.stats.getHighestScore()) {
            return -1;
        } else {
            return 1;
        }
    }
}
