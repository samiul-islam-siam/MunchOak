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
    private int id;           // Payment ID
    private double amount;
    private boolean success;
    private String timestamp;

    private static final int START_PAYMENT_ID = 3001;       // Start Payment_ID
    private static final int START_USER_ID = 20250001;      // Start User_ID

    public Payment(double amount) {
        this.amount = amount;
        this.timestamp = Instant.now().toString();
        this.success = false;
    }

//    public void processPayment(Cart cart, Map<Integer, FoodItems> foodMap) {
//        Stage paymentStage = new Stage();
//        paymentStage.setTitle("Payment");
//
//        // Card Number Field
//        Label cardLabel = new Label("Card Number:");
//        TextField cardField = new TextField();
//        cardField.setPromptText("Enter card number");
//
//        // PIN Field
//        Label pinLabel = new Label("PIN:");
//        PasswordField pinField = new PasswordField();
//        pinField.setPromptText("Enter PIN");
//
//        // Pay Button
//        Button payButton = new Button("Pay");
//        payButton.setDefaultButton(true);
//
//        VBox vbox = new VBox(10, cardLabel, cardField, pinLabel, pinField, payButton);
//        vbox.setPadding(new Insets(20));
//        vbox.setAlignment(Pos.CENTER);
//
//        Scene scene = new Scene(vbox, 300, 200);
//        paymentStage.setScene(scene);
//        paymentStage.show();
//
//        payButton.setOnAction(e -> {
//            String cardNumber = cardField.getText().trim();
//            String pin = pinField.getText().trim();
//
//            if (cardNumber.isEmpty() || pin.isEmpty()) {
//                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields!");
//                alert.show();
//                return;
//            }
//
//            this.success = true;
//
//            try (Connection conn = DatabaseConnection.getConnection()) {
//                int userId = Session.getCurrentUserId();
//
//                // Ensure user exists
//                if (!userExists(conn, userId)) {
//                    userId = getNextUserId(conn);
//                    createUser(conn, userId, "user" + userId, "defaultPassword");
//                    Session.setCurrentUserId(userId); // update session
//                }
//
//                // Get next Payment_ID for this user
//                int nextPaymentId = getNextPaymentId(conn, userId);
//
//                // Insert payment
//                String sql = "INSERT INTO PaymentHistory (User_ID, TotalAmount, PaymentMethod) VALUES (?, ?, ?)";
//                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//                stmt.setInt(1, userId);
//                stmt.setDouble(2, amount);
//                stmt.setString(3, "Card");
//                stmt.executeUpdate();
//
//                ResultSet rs = stmt.getGeneratedKeys();
//                if (rs.next()) {
//                    this.id = rs.getInt(1);  // get Payment_ID automatically
//                }
//
//
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//
//            paymentStage.close();
//
//            // Generate Bill
//            Bill bill = new Bill(cart, this);
//            String receipt = bill.generateReceipt(foodMap);
//
//            Stage billStage = new Stage();
//            billStage.setTitle("Bill Receipt");
//
//            TextArea receiptArea = new TextArea(receipt);
//            receiptArea.setEditable(false);
//            receiptArea.setStyle("-fx-font-size: 14px; -fx-font-family: monospace;");
//            receiptArea.setPrefWidth(500);
//            receiptArea.setPrefHeight(400);
//
//            VBox billBox = new VBox(15, receiptArea);
//            billBox.setPadding(new Insets(20));
//            billBox.setAlignment(Pos.CENTER);
//
//            billStage.setScene(new Scene(billBox, 500, 400));
//            billStage.show();
//        });
//    }
public void processPayment(Cart cart, Map<Integer, FoodItems> foodMap) {
    Stage paymentStage = new Stage();
    paymentStage.setTitle("Payment");

    // Card Number Field
    Label cardLabel = new Label("Card Number:");
    TextField cardField = new TextField();

    // PIN Field
    Label pinLabel = new Label("PIN:");
    PasswordField pinField = new PasswordField();

    // Pay Button
    Button payButton = new Button("Pay");
    payButton.setDefaultButton(true);

    VBox vbox = new VBox(10, cardLabel, cardField, pinLabel, pinField, payButton);
    vbox.setPadding(new Insets(20));
    vbox.setAlignment(Pos.CENTER);

    paymentStage.setScene(new Scene(vbox, 300, 200));
    paymentStage.show();

    payButton.setOnAction(e -> {
        String cardNumber = cardField.getText().trim();
        String pin = pinField.getText().trim();
        if (cardNumber.isEmpty() || pin.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please fill all fields!").show();
            return;
        }
        this.success = true;

        try (Connection conn = DatabaseConnection.getConnection()) {
            int userId = Session.getCurrentUserId();

            // Ensure user exists
            if (!userExists(conn, userId)) {
                userId = getNextUserId(conn);
                createUser(conn, userId, "user" + userId, "defaultPassword");
                Session.setCurrentUserId(userId);
            }

            // Insert payment
            String sql = "INSERT INTO PaymentHistory (User_ID, TotalAmount, PaymentMethod) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, userId);
            stmt.setDouble(2, amount);
            stmt.setString(3, "Card");
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) this.id = rs.getInt(1);

            // Save individual items
            String itemSQL = "INSERT INTO PaymentItems (Payment_ID, Food_ID, Quantity) VALUES (?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSQL);
            for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
                itemStmt.setInt(1, this.id);
                itemStmt.setInt(2, entry.getKey());
                itemStmt.setInt(3, entry.getValue());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        paymentStage.close();

        // Show receipt
        Bill bill = new Bill(cart, this);
        String receipt = bill.generateReceipt(foodMap);
        Stage billStage = new Stage();
        billStage.setTitle("Bill Receipt");
        TextArea receiptArea = new TextArea(receipt);
        receiptArea.setEditable(false);
        receiptArea.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");
        VBox box = new VBox(receiptArea);
        box.setPadding(new Insets(20));
        billStage.setScene(new Scene(box, 500, 400));
        billStage.show();
    });
}


    // Check if user exists
    private boolean userExists(Connection conn, int userId) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE User_ID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    // Get next User_ID
    private int getNextUserId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(User_ID) AS maxId FROM Users";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int maxId = rs.getInt("maxId");
            return (maxId >= START_USER_ID) ? maxId + 1 : START_USER_ID;
        }
        return START_USER_ID;
    }

    // Create user
    private void createUser(Connection conn, int userId, String username, String password) throws SQLException {
        String sql = "INSERT INTO Users (User_ID, Username, Password) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.setString(2, username);
        stmt.setString(3, password);
        stmt.executeUpdate();
    }

    // Get next Payment_ID for a particular user
    private int getNextPaymentId(Connection conn, int userId) throws SQLException {
        String sql = "SELECT MAX(Payment_ID) AS maxId FROM PaymentHistory WHERE User_ID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int maxId = rs.getInt("maxId");
            if (maxId >= START_PAYMENT_ID) return maxId + 1;
        }
        return START_PAYMENT_ID;
    }

    // Getters
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public boolean isSuccess() { return success; }
    public String getTimestamp() { return timestamp; }
}
