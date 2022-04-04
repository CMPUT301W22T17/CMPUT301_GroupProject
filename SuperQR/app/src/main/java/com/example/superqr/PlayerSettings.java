package com.example.superqr;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Contains a player's information
 * username, phone, and email
 */
public class PlayerSettings implements Parcelable {
    private String username;
    private String phone;
    private String email;

    public PlayerSettings(){
        // Required empty public constructor
    }

    public PlayerSettings(String username, String phone, String email) {
        this.username = username;
        this.phone = phone;
        this.email = email;
    }


    protected PlayerSettings(Parcel in) {
        username = in.readString();
        phone = in.readString();
        email = in.readString();
    }

    public static final Creator<PlayerSettings> CREATOR = new Creator<PlayerSettings>() {
        @Override
        public PlayerSettings createFromParcel(Parcel in) {
            return new PlayerSettings(in);
        }

        @Override
        public PlayerSettings[] newArray(int size) {
            return new PlayerSettings[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(phone);
        parcel.writeString(email);
    }
}
