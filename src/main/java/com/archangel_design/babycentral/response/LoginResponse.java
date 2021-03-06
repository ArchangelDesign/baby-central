package com.archangel_design.babycentral.response;

import com.archangel_design.babycentral.entity.UserEntity;

import java.util.Date;

public class LoginResponse {

    private String sessionId;

    private Date expiration;

    private Date created;

    private UserEntity user;

    public String getSessionId() {
        return sessionId;
    }

    public LoginResponse setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Date getExpiration() {
        return expiration;
    }

    public LoginResponse setExpiration(Date expiration) {
        this.expiration = expiration;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public LoginResponse setCreated(Date created) {
        this.created = created;
        return this;
    }

    public LoginResponse setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }
}
