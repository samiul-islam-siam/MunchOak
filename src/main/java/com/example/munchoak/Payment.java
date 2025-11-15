package com.example.munchoak;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.Instant;
import java.util.Map;

public class Payment {
    private int id;
    private double amount;
    private boolean success;
    private String timestamp;

    public Payment(int id, double amount) {
        this.id = id;
        this.amount = amount;
        this.timestamp = Instant.now().toString();
        this.success = false;
    }

    // Main payment entry point
    public void processPayment(Cart cart, Map<Integer, FoodItems> foodMap) {
        Stage stage = new Stage();
        stage.setTitle("Select Payment Method");

        Button cardBtn = new Button("Pay with Card");
        Button cashBtn = new Button("Pay Cash");

        cardBtn.setPrefWidth(180);
        cashBtn.setPrefWidth(180);

        VBox vbox = new VBox(20, new Label("Choose Payment Method:"), cardBtn, cashBtn);
        vbox.setPadding(new Insets(25));
        vbox.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(vbox, 300, 200));
        stage.show();

        cardBtn.setOnAction(e -> {
            stage.close();
            cardPayment(cart, foodMap);
        });

        cashBtn.setOnAction(e -> {
            stage.close();
            cashOnDelivery(cart, foodMap);
        });
    }

    // --- CARD PAYMENT METHOD ---
    private void cardPayment(Cart cart, Map<Integer, FoodItems> foodMap) {
        Stage paymentStage = new Stage();
        paymentStage.setTitle("Card Payment");

        Label cardLabel = new Label("Card Number:");
        TextField cardField = new TextField();
        cardField.setPromptText("Enter card number");

        Label pinLabel = new Label("PIN:");
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("Enter PIN");

        Button payButton = new Button("Pay Now");
        payButton.setDefaultButton(true);

        VBox vbox = new VBox(10, cardLabel, cardField, pinLabel, pinField, payButton);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 320, 220);
        paymentStage.setScene(scene);
        paymentStage.show();

        payButton.setOnAction(e -> {
            String card = cardField.getText().trim();
            String pin = pinField.getText().trim();

            // simple validation
            if (card.isEmpty() || pin.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please fill all fields!").show();
                return;
            }
            if (card.length() < 8 || pin.length() < 4) {
                new Alert(Alert.AlertType.ERROR, "Invalid card (8-digit) or PIN (4-digit)!").show();
                return;
            }

            // payment success
            this.success = true;
            paymentStage.close();

            Bill bill = new Bill(cart, this);
            String receipt = bill.generateReceipt(foodMap);

            showBill(receipt);
            cart.getBuyHistory().clear();
        });
    }

    // --- CASH ON DELIVERY METHOD ---
    private void cashOnDelivery(Cart cart, Map<Integer, FoodItems> foodMap) {
        this.success = false; // not paid yet

        Bill bill = new Bill(cart, this);
        String receipt = bill.generateReceipt(foodMap);

        // modify receipt to reflect pending payment
        receipt += "\n-----------------------------------\n";
        receipt += "Payment Method: Cash\n";
        receipt += "Status: Pending Payment\n";
        receipt += "-----------------------------------\n";

        showBill(receipt);
        cart.getBuyHistory().clear();
    }

    // --- Helper to show receipt window ---
    private void showBill(String receipt) {
        Stage billStage = new Stage();
        billStage.setTitle("Bill Receipt");

        TextArea receiptArea = new TextArea(receipt);
        receiptArea.setEditable(false);
        receiptArea.setStyle("-fx-font-size: 14px; -fx-font-family: monospace;");
        receiptArea.setPrefSize(500, 400);

        VBox box = new VBox(15, receiptArea);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        billStage.setScene(new Scene(box));
        billStage.show();
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
