package com.example.superqr;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationStore implements Parcelable {

    private double latitude;
    private double longitude;

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

    public void setLocation(double newLatitude, double newLongitude) {
        this.latitude = newLatitude;
        this.longitude = newLongitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

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
