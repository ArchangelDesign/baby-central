package com.archangel_design.babycentral.service;

import com.archangel_design.babycentral.entity.SessionEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Authenticator extends UsernamePasswordAuthenticationToken {

    SessionService sessionServiceService;

    private HttpServletRequest request;

    public Authenticator(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public Authenticator setRequest(HttpServletRequest request) {
        this.request = request;
        return this;
    }

    public Authenticator setModel(SessionService sessionServiceService) {
        this.sessionServiceService = sessionServiceService;
        return this;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        if (super.isAuthenticated())
            return true;
        System.out.println(request.getRequestURI());
        // @TODO: use abstraction permission
        Pattern p = Pattern.compile("^/auth/.*");
        Matcher m = p.matcher(request.getRequestURI());
        if (m.matches())
            return true;
        p = Pattern.compile("^/v2/.*");
        m = p.matcher(request.getRequestURI());
        if (m.matches())
            return true;
        if (request.getMethod().equalsIgnoreCase("options"))
            return true;

        if (request.getRequestURI().matches("\\/swagger.*"))
            return true;

        if (request.getRequestURI().matches("\\/webjars.*"))
            return true;

        if (request.getRequestURI().matches("\\/user/avatar/.*"))
            return true;

        if (request.getRequestURI().matches("\\/baby/avatar/.*"))
            return true;

        SessionEntity s = sessionServiceService.getSession(request.getHeader("Authorization"));
        return s != null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof Authenticator))
            return false;

        return false;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }
}
