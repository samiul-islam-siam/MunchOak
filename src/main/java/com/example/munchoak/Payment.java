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

    private static final int START_PAYMENT_ID = 3001;
    private static final int START_USER_ID = 20250001;

    public Payment(double amount) {
        this.amount = amount;
        this.timestamp = Instant.now().toString();
        this.success = false;
    }

    public void processPayment(Cart cart, Map<Integer, FoodItems> foodMap) {
        Stage paymentStage = new Stage();
        paymentStage.setTitle("Payment");

        Label cardLabel = new Label("Card Number:");
        TextField cardField = new TextField();
        cardField.setPromptText("Enter card number");

        Label pinLabel = new Label("PIN:");
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("Enter PIN");

        Button payButton = new Button("Pay");
        payButton.setDefaultButton(true);

        VBox vbox = new VBox(10, cardLabel, cardField, pinLabel, pinField, payButton);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);
        paymentStage.setScene(scene);
        paymentStage.show();

        payButton.setOnAction(e -> {
            if (cardField.getText().trim().isEmpty() || pinField.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please fill all fields!").show();
                return;
            }

            this.success = true;

            try (Connection conn = DatabaseConnection.getConnection()) {
                int userId = Session.getCurrentUserId();

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

                // Insert purchased items
                String itemSql = "INSERT INTO PaymentItems (Payment_ID, Food_ID, Quantity) VALUES (?, ?, ?)";
                PreparedStatement itemStmt = conn.prepareStatement(itemSql);
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

            Bill bill = new Bill(cart, this);
            String receipt = bill.generateReceipt(foodMap);

            Stage billStage = new Stage();
            billStage.setTitle("Bill Receipt");

            TextArea receiptArea = new TextArea(receipt);
            receiptArea.setEditable(false);
            receiptArea.setStyle("-fx-font-size: 14px; -fx-font-family: monospace;");
            receiptArea.setPrefSize(500, 400);

            VBox billBox = new VBox(15, receiptArea);
            billBox.setPadding(new Insets(20));
            billBox.setAlignment(Pos.CENTER);

            billStage.setScene(new Scene(billBox));
            billStage.show();
            cart.getBuyHistory().clear();
        });
    }

    private boolean userExists(Connection conn, int userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM Users WHERE User_ID = ?");
        stmt.setInt(1, userId);
        return stmt.executeQuery().next();
    }

    private int getNextUserId(Connection conn) throws SQLException {
        ResultSet rs = conn.prepareStatement("SELECT MAX(User_ID) AS maxId FROM Users").executeQuery();
        if (rs.next()) {
            int maxId = rs.getInt("maxId");
            return maxId >= START_USER_ID ? maxId + 1 : START_USER_ID;
        }
        return START_USER_ID;
    }

    private void createUser(Connection conn, int userId, String username, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Users (User_ID, Username, Password) VALUES (?, ?, ?)");
        stmt.setInt(1, userId);
        stmt.setString(2, username);
        stmt.setString(3, password);
        stmt.executeUpdate();
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public boolean isSuccess() { return success; }
    public String getTimestamp() { return timestamp; }
}
