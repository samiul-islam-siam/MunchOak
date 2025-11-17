package com.example.munchoak;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;

public class Payment {
    private final int id;
    private final double amount;
    private static String paymentMethod;
    private boolean success;
    private final String timestamp;

    public Payment(int id, double amount) {
        this.id = id;
        this.amount = amount;
        this.timestamp = Instant.now().toString();  // Save payment creation timestamp
        this.success = false;                       // Mark unpaid initially
    }

    // Main payment entry point (opens the method selection UI)
    public void processPayment(Cart cart, Map<Integer, FoodItems> foodMap) {
        Stage stage = new Stage();
        stage.setTitle("Select Payment Method");

        Button cardBtn = new Button("Pay with Card");
        Button cashBtn = new Button("Pay Cash");

        cardBtn.setPrefWidth(180);
        cashBtn.setPrefWidth(180);

        // Layout asking user to choose payment option
        VBox vbox = new VBox(20, new Label("Choose Payment Method:"), cardBtn, cashBtn);
        vbox.setPadding(new Insets(25));
        vbox.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(vbox, 300, 200));
        stage.show();

        // Card payment
        cardBtn.setOnAction(e -> {
            stage.close();
            paymentMethod = "Card";
            cardPayment(cart, foodMap);
        });

        // Cash on delivery
        cashBtn.setOnAction(e -> {
            stage.close();
            paymentMethod = "Cash";
            cashOnDelivery(cart, foodMap);
        });
    }

    // --- CARD PAYMENT METHOD ---
    private void cardPayment(Cart cart, Map<Integer, FoodItems> foodMap) {

        // UI asking card number + PIN
        Stage paymentStage = new Stage();
        paymentStage.setTitle("Card Payment");

        Label cardLabel = new Label("Card Number:");
        TextField cardField = new TextField();
        cardField.setPromptText("Enter card number");

        Label pinLabel = new Label("PIN:");
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("Enter PIN");

        Button payButton = new Button("Pay Now");
        payButton.setDefaultButton(true);  // Triggered by Enter key

        VBox vbox = new VBox(10, cardLabel, cardField, pinLabel, pinField, payButton);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 320, 220);
        paymentStage.setScene(scene);
        paymentStage.show();

        // Validate and complete card payment
        payButton.setOnAction(e -> {
            String card = cardField.getText().trim();
            String pin = pinField.getText().trim();

            // Basic form validation
            if (card.isEmpty() || pin.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please fill all fields!").show();
                return;
            }
            if (card.length() < 8 || pin.length() < 4) {
                // Very simple mock validation for card + PIN length
                new Alert(Alert.AlertType.ERROR, "Invalid card (8-digit) or PIN (4-digit)!").show();
                return;
            }

            // Payment successful (mock)
            this.success = true;
            paymentStage.close();

            // Generate and display bill
            Bill bill = new Bill(cart, this);
            String receipt = bill.generateReceipt(foodMap);
            showBill(receipt);

            cart.getBuyHistory().clear(); // Clear cart after payment
        });
    }

    // --- CASH ON DELIVERY METHOD ---
    private void cashOnDelivery(Cart cart, Map<Integer, FoodItems> foodMap) {
        this.success = false; // Payment still pending

        Bill bill = new Bill(cart, this);
        String receipt = bill.generateReceipt(foodMap);

        // Append pending payment info to bill
        receipt += "\n-----------------------------------\n";
        receipt += "Payment Method: Cash\n";
        receipt += "Status: Pending Payment\n";
        receipt += "-----------------------------------\n";

        showBill(receipt);
        cart.getBuyHistory().clear(); // Clear cart but keep unpaid order
    }

    // ===================== CHECKOUT HANDLER =====================
    public static void checkout(Cart cart) {

        // Build map of foodId -> FoodItems
        Map<Integer, FoodItems> foodMap = new HashMap<>();
        for (FoodItems food : FileStorage.loadMenu()) {
            foodMap.put(food.getId(), food);
        }

        // Compute total cart price
        double total = 0;
        for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
            FoodItems food = foodMap.get(entry.getKey());
            if (food != null) {
                total += food.getPrice() * entry.getValue();
            }
        }

        // Prevent empty checkout
        if (cart.getBuyHistory().isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Your cart is empty!").show();
            return;
        }

        try {
            int userId = Session.getCurrentUserId(); // Identify user making purchase

            // Store payment + cart contents into file (persistence)
            int paymentId = FileStorage.createPaymentAndCart(
                    userId,
                    cart,
                    foodMap,
                    paymentMethod
            );

            // Create Payment instance for handling UI + method
            Payment payment = new Payment(paymentId, total);

            // Show payment method selection popup
            payment.processPayment(cart, foodMap);

        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Checkout failed: " + e.getMessage()).show();
        }
    }

    // --- Helper to display receipt window ---
    private void showBill(String receipt) {
        Stage billStage = new Stage();
        billStage.setTitle("Bill Receipt");

        TextArea receiptArea = new TextArea(receipt);
        receiptArea.setEditable(false); // Prevent editing bill
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

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
