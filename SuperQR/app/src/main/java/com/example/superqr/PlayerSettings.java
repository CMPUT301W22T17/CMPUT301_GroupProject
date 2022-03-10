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
  
    public QRCode getLoginQR() {
        return loginQR;
    }

    // returns player status QR code
    public QRCode getStatusQR() {
        return statusQR;
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

    public void setStatusQR(QRCode statusQR) {
        this.statusQR = statusQR;
    }
}
