package com.example.manager;

public class UserManager {

    // Get User_ID by username (file-based)
    public static int getUserId(String username) {
        return FileStorage.getUserId(username);
    }
}
