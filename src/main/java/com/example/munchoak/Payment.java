//package com.example.munchoak;
//
//import java.time.Instant;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//
//public class Payment {
//    private static int nextId = 1;
//    private int id;
//    private double amount;
//    private boolean success;
//    private String timestamp;
//
//    public Payment(double amount) {
//        this.id = nextId++;
//        this.amount = amount;
//        this.timestamp = Instant.now().toString();
//        this.success = false;
//    }
//
//    public void processPayment() {
//        // Dummy: always success
//        this.success = true;
//    }
//
//    public int getId() { return id; }
//    public double getAmount() { return amount; }
//    public boolean isSuccess() { return success; }
//    public String getTimestamp() { return timestamp; }
//
//    public void saveToDatabase(int cartId) {
//        String sql = "INSERT INTO PaymentHistory (Cart_ID, Amount, Timestamp, Status) VALUES (?, ?, ?, ?)";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, cartId);
//            stmt.setDouble(2, amount);
//            stmt.setString(3, timestamp);
//            stmt.setString(4, success ? "PAID" : "FAILED");
//            stmt.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
package com.example.munchoak;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Instant;

public class Payment {
    private static int nextId = 1;
    private int id;
    private double amount;
    private boolean success;
    private String timestamp;

    public Payment(double amount) {
        this.id = nextId++;
        this.amount = amount;
        this.timestamp = Instant.now().toString();
        this.success = false;
    }

    public void processPayment() {
        // Dummy payment: always success
        this.success = true;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public boolean isSuccess() { return success; }
    public String getTimestamp() { return timestamp; }

    public void saveToDatabase(User user, int cartId) {
        String sql = "INSERT INTO PaymentHistory (User_ID, Cart_ID, Amount, Timestamp, Status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getId());
            stmt.setInt(2, cartId);
            stmt.setDouble(3, amount);
            stmt.setString(4, timestamp);
            stmt.setString(5, success ? "PAID" : "FAILED");
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
