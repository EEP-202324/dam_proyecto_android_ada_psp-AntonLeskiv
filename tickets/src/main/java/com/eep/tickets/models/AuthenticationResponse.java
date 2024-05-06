package com.eep.tickets.models;

public class AuthenticationResponse {
    private boolean logged;
    private String message;
    private String role;

    public AuthenticationResponse(boolean logged, String message, String role) {
        this.logged = logged;
        this.message = message;
        this.role = role;
    }

    public AuthenticationResponse(boolean logged, String message) {
        this.logged = logged;
        this.message = message;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
