package com.purchasehistorysystem.util;

import com.purchasehistorysystem.model.User;

import java.util.UUID;

public class UserSessionUtils {
    private static User currentUser;

    private UserSessionUtils(){}

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void cleanSession() {
        currentUser = null;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
