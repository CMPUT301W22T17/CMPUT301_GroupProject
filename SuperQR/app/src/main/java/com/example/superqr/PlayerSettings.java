package com.example.superqr;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerSettings implements Parcelable {
    private String username;
    private String phone;
    private String email;
    private QRCode loginQR;

    //temp empty constructor
    public PlayerSettings() {
        username = "NewUser";
        phone = "123-123-1234";
        email = "ashe@pokemon.com";
    }

    public PlayerSettings(String username, String phone, String email) {
        this.username = username;
        this.phone = phone;
        this.email = email;
    }

    // returns a QR code for player login

    protected PlayerSettings(Parcel in) {
        username = in.readString();
        phone = in.readString();
        email = in.readString();
        loginQR = in.readParcelable(QRCode.class.getClassLoader());
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

    public QRCode getLoginQR() {
        return loginQR;
    }

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

    public void setLoginQR(QRCode loginQR) {
        this.loginQR = loginQR;
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
        parcel.writeParcelable(loginQR, i);
    }
}
