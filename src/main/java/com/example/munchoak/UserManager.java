package com.example.munchoak;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    // Get User_ID by username
    public static int getUserId(String username) {
        int userId = -1;
        String query = "SELECT User_ID FROM Users WHERE Username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("User_ID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }
}