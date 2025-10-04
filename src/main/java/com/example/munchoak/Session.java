package com.example.munchoak;


public class Session {
    private static int currentUserId = 1;  // Default user (for now)

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(int id) {
        currentUserId = id;
    }
}
