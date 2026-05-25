package com.purchasehistorysystem.util;

import com.github.javakeyring.Keyring;

public class AuthTokenStorage {
    private static final String SERVICE = "PurchaseHistorySystem";
    private static final String ACCOUNT = "auth_token";

    public static void saveToken(String token) throws Exception {
        Keyring.create().setPassword(SERVICE, ACCOUNT, token);
    }

    public static String loadToken() {
        try {
            return Keyring.create().getPassword(SERVICE, ACCOUNT);
        }

        catch (Exception exception) {
            return null;
        }
    }

    public static void clearToken() throws Exception {
        Keyring.create().deletePassword(SERVICE, ACCOUNT);
    }
}
