package com.example.manager;

public class Session {
    //guest default id
    private static int currentUserId = 2025000;  // Default user (for now)
    private static String currentUsername = "guest";

    //To set and access current user id and username.
    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(String name) {
        currentUserId = FileStorage.getUserId(name);
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setCurrentUsername(String name) {
        currentUsername = name;
    }

    //Uses in chat server
    public static String getCurrentRole() {
        if (currentUsername.equals("admin")) {
            return "ADMIN";
        } else {
            return "USER";
        }
    }
}