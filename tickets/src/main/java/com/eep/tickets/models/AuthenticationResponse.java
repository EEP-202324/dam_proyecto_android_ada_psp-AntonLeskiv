package com.eep.tickets.models;

public class AuthenticationResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String role;

    public AuthenticationResponse(boolean success, String message, Long userId, String role) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.role = role;
    }

    public AuthenticationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}