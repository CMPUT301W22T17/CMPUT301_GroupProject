package com.example.superqr;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The LocationStore acts as a storage
 * for an object's Location
 */
public class LocationStore implements Parcelable {

    private double latitude;
    private double longitude;

    /**
     * empty constructor for firebase
     */
    public LocationStore() {

    }

    protected LocationStore(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<LocationStore> CREATOR = new Creator<LocationStore>() {
        @Override
        public LocationStore createFromParcel(Parcel in) {
            return new LocationStore(in);
        }

        @Override
        public LocationStore[] newArray(int size) {
            return new LocationStore[size];
        }
    };

    /**
     * set latitude and longitude to a specific location
     * @param newLatitude
     * @param newLongitude
     */
    public void setLocation(double newLatitude, double newLongitude) {
        this.latitude = newLatitude;
        this.longitude = newLongitude;
    }

    /**
     * get the latitude
     * @return latitude: Double
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * get the Longitude
     * @return longitude: Double
     */
    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
    }
}
