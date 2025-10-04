package com.example.munchoak;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.Instant;
import java.util.Map;

public class Payment {
    private int id;
    private double amount;
    private boolean success;
    private String timestamp;

    // Use a fixed user ID for now
    private static final int USER_ID = 202500;

    public Payment(double amount) {
        this.amount = amount;
        this.timestamp = Instant.now().toString();
        this.success = false;
    }

    public void processPayment(Cart cart, Map<Integer, FoodItems> foodMap) {
        Stage paymentStage = new Stage();
        paymentStage.setTitle("Payment");

        // Card Number Field
        Label cardLabel = new Label("Card Number:");
        TextField cardField = new TextField();
        cardField.setPromptText("Enter card number");

        // PIN Field
        Label pinLabel = new Label("PIN:");
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("Enter PIN");

        // Pay Button
        Button payButton = new Button("Pay");
        payButton.setDefaultButton(true);

        VBox vbox = new VBox(10, cardLabel, cardField, pinLabel, pinField, payButton);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);
        paymentStage.setScene(scene);
        paymentStage.show();

        payButton.setOnAction(e -> {
            String cardNumber = cardField.getText().trim();
            String pin = pinField.getText().trim();

            if (cardNumber.isEmpty() || pin.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields!");
                alert.show();
                return;
            }

            this.success = true; // mark payment as success

            try (Connection conn = DatabaseConnection.getConnection()) {

                // Ensure user 202500 exists
                if (!userExists(conn, USER_ID)) {
                    createUser(conn, USER_ID, "defaultUser", "password123");
                }

                // Insert payment
                String sql = "INSERT INTO PaymentHistory (User_ID, TotalAmount, PaymentMethod) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, USER_ID);
                stmt.setDouble(2, amount);
                stmt.setString(3, "Card");
                stmt.executeUpdate();

                // Get generated Payment_ID
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            paymentStage.close();

            // Generate Bill
            Bill bill = new Bill(cart, this);
            String receipt = bill.generateReceipt(foodMap);

            Stage billStage = new Stage();
            billStage.setTitle("Bill Receipt");

            TextArea receiptArea = new TextArea(receipt);
            receiptArea.setEditable(false);
            receiptArea.setStyle("-fx-font-size: 14px; -fx-font-family: monospace;");
            receiptArea.setPrefWidth(500);
            receiptArea.setPrefHeight(400);

            VBox billBox = new VBox(15, receiptArea);
            billBox.setPadding(new Insets(20));
            billBox.setAlignment(Pos.CENTER);

            billStage.setScene(new Scene(billBox, 500, 400));
            billStage.show();
        });
    }

    // Check if the user exists
    private boolean userExists(Connection conn, int userId) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE User_ID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    // Create default user
    private void createUser(Connection conn, int userId, String username, String password) throws SQLException {
        String sql = "INSERT INTO Users (User_ID, Username, Password) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.setString(2, username);
        stmt.setString(3, password);
        stmt.executeUpdate();
    }

    // Getters
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public boolean isSuccess() { return success; }
    public String getTimestamp() { return timestamp; }
}
