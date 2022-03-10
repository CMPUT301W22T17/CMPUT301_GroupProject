package com.example.superqr;

public class PlayerSettings {
    private String username;
    private String phone;
    private String email;
    private QRCode loginQR;
    private QRCode statusQR;

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
    public void loginQR() {
        //WIP
    }

    // returns player status QR code
    public void statusQR() {
        //WIP
    }

}
