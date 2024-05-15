package com.eep.tickets.models;

public enum Role {
    ADMIN,
    USER;

    @Override
    public String toString() {
        return this.name();
    }
}
