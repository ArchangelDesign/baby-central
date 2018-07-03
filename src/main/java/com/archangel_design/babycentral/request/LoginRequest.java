package com.archangel_design.babycentral.request;

public class LoginRequest {

    private String email;

    private String password;

    private String deviceId;

    public String getEmail() {
        return email;
    }

    public LoginRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public LoginRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public LoginRequest setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
}
