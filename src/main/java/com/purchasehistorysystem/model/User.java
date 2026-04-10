package com.purchasehistorysystem.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String passwordHint;

    public User(int id, String username, String passwordHash, String passwordHint) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.passwordHint = passwordHint;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordHint() {
        return passwordHint;
    }
}
