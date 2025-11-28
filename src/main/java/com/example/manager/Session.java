/*
package com.example.manager;

public class Session {

    private static int currentUserId = 2025000;
    private static String currentUsername = "guest";
    private static String currentEmail = "guest@gmail.com";
    private static String currentPassword = "ai01*2#";

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentEmail() {
        return currentEmail;
    }

    public static String getCurrentPassword() {
        return currentPassword;
    }

    public static void setCurrentUser(String username) {
        currentUsername = username;
        currentUserId = FileStorage.getUserId(username);
        currentEmail = FileStorage.getUserEmail(username);
        currentPassword = FileStorage.getUserPassword(username);
    }

    public static void setCurrentUsername(String username)
    {
        currentUsername = username;
    }

    public static String getCurrentRole() {
        if ("admin".equalsIgnoreCase(currentUsername)) return "ADMIN";
        // if ("guest".equalsIgnoreCase(currentUsername)) return "GUEST";
        else return "USER";
    }

    public static void resetToGuest() {
        currentUsername = "guest";
        currentEmail = "guest@gmail.com";
        currentPassword = "ai01*2#";
    }

    public static void logout() {
        resetToGuest();
    }
}
*/
package com.example.manager;
import java.io.IOException;
public class Session {

    private static int currentUserId = 2025000;
    private static String currentUsername = "guest";
    private static String currentEmail = "guest@gmail.com";
    private static String currentPassword = "ai01*2#";
    private static boolean isAdmin = false; // new flag

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentEmail() {
        return currentEmail;
    }

    public static String getCurrentPassword() {
        return currentPassword;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static String getCurrentRole() {
        return isAdmin ? "ADMIN" : "USER";
    }

    public static void setCurrentUser(String username) {
        currentUsername = username;
        currentUserId = FileStorage.getUserId(username);
        currentEmail = FileStorage.getUserEmail(username);
        currentPassword = FileStorage.getUserPassword(username);
        isAdmin = false; // normal users are never admins
    }

    public static void setAdminUser() throws IOException {
        currentUsername = "admin";
        currentUserId = Integer.parseInt(AdminFileStorage.ADMIN_ID);
        currentEmail = "admin@example.com"; // optional
        currentPassword = AdminFileStorage.getAdminPassword(); // <-- new getter needed
        isAdmin = true; // set admin flag
    }

    public static void resetToGuest() {
        currentUsername = "guest";
        currentEmail = "guest@gmail.com";
        currentPassword = "ai01*2#";
        isAdmin = false;
    }

    public static void logout() {
        resetToGuest();
    }
}
