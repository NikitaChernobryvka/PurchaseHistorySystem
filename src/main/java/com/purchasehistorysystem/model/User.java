package com.purchasehistorysystem.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String email;
    private String authToken;

    public User(int id, String username, String passwordHash, String email, String authToken) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.authToken = authToken;
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

    public String getEmail() {
        return email;
    }

    public String getAuthToken() {
        return authToken;
    }
}
