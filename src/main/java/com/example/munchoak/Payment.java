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
import java.util.HashMap;
import java.util.Map;

public class Payment {
    private final int id;
    private final double amount;  // This is the subtotal
    private String paymentMethod;
    private boolean success;
    private final String timestamp;
    private double discount = 0.0;
    private double tip = 0.0;

    public Payment(int id, double amount) {
        this(id, amount, Instant.now().toString());
    }

    public Payment(int id, double amount, String timestamp) {
        this.id = id;
        this.amount = amount;
        this.paymentMethod = "";
        this.timestamp = timestamp;
        this.success = false;
        this.discount = 0.0;
        this.tip = 0.0;
    }

    // Main payment entry point (opens the method selection UI)
    public void processPayment(Cart cart, Map<Integer, FoodItems> foodMap) {
        Stage stage = new Stage();
        stage.setTitle("Select Payment Method");

        Label title = new Label("Choose Payment Method");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button cardBtn = new Button("Pay with Card");
        Button cashBtn = new Button("Pay Cash");
        Button backBtn = new Button("Back");

        styleButton(cardBtn);
        styleButton(cashBtn);
        styleSecondaryButton(backBtn);

        VBox vbox = new VBox(20, title, cardBtn, cashBtn, backBtn);
        vbox.setPadding(new Insets(25));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: linear-gradient(to bottom, #eef2f3, #dfe9f3);");

        stage.setScene(new Scene(vbox, 340, 260));
        stage.show();

        cardBtn.setOnAction(e -> {
            stage.close();
            cardPayment(cart, foodMap);
        });

        cashBtn.setOnAction(e -> {
            stage.close();
            cashPayment(cart, foodMap);
        });

        backBtn.setOnAction(e -> stage.close());
    }


    // --- CARD PAYMENT METHOD ---
    private void cardPayment(Cart cart, Map<Integer, FoodItems> foodMap) {

        Stage paymentStage = new Stage();
        paymentStage.setTitle("Card Payment");

        Label title = new Label("Enter Card Details");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label cardLabel = new Label("Card Number:");
        TextField cardField = new TextField();
        cardField.setPromptText("XXXX XXXX XXXX XXXX");

        Label pinLabel = new Label("PIN:");
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("****");

        Button payButton = new Button("Pay Now");
        Button backButton = new Button("Back");

        styleButton(payButton);
        styleSecondaryButton(backButton);

        VBox vbox = new VBox(12, title, cardLabel, cardField, pinLabel, pinField, payButton, backButton);
        vbox.setPadding(new Insets(25));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");

        Scene scene = new Scene(vbox, 350, 330);
        paymentStage.setScene(scene);
        paymentStage.show();

        payButton.setOnAction(e -> {
            String card = cardField.getText().trim();
            String pin = pinField.getText().trim();

            if (card.isEmpty() || pin.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please fill all fields!").show();
                return;
            }
            if (card.length() < 8 || pin.length() < 4) {
                new Alert(Alert.AlertType.ERROR, "Invalid card (8-digit) or PIN (4-digit)!").show();
                return;
            }

            paymentMethod = "Card";
            this.success = true;
            paymentStage.close();

            Bill bill = new Bill(cart, this);
            String receipt = bill.generateReceipt(foodMap);
            showBill(receipt);

            cart.getBuyHistory().clear();
        });

        backButton.setOnAction(e -> {
            paymentStage.close();
            processPayment(cart, foodMap);
        });
    }


    // --- CASH PAYMENT METHOD ---
    private void cashPayment(Cart cart, Map<Integer, FoodItems> foodMap) {
        paymentMethod = "Cash";
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

        // Compute subtotal (cart price)
        double subtotal = 0;
        for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
            FoodItems food = foodMap.get(entry.getKey());
            if (food != null) {
                subtotal += food.getPrice() * entry.getValue();
            }
        }

        // Prevent empty checkout
        if (cart.getBuyHistory().isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Your cart is empty!").show();
            return;
        }

        try {
            int userId = Session.getCurrentUserId(); // Identify user making purchase

            // Store payment + cart contents into file (persistence) - saves subtotal as amount
            int paymentId = FileStorage.createPaymentAndCart(
                    userId,
                    cart,
                    foodMap,
                    "Card"  // Default; can be overridden later
            );

            // FIXED: The caller (e.g., CheckoutPage) should save discount/tip separately via FileStorage.savePaymentDiscountTip(paymentId, discount, tip)

            // Create Payment instance for handling UI + method (subtotal only)
            Payment payment = new Payment(paymentId, subtotal);

            // Show payment method selection popup (uncomment if needed)
            // payment.processPayment(cart, foodMap);

        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Checkout failed: " + e.getMessage()).show();
        }
    }

    public static int getLastPaymentId() {
        return FileStorage.getLastPaymentId();
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

    private void styleButton(Button btn) {
        btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #4facfe, #00f2fe);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 10 18;" +
                        "-fx-background-radius: 12;"
        );

        btn.setOnMouseEntered(e ->
                btn.setStyle(
                        "-fx-background-color: linear-gradient(to right, #3399ff, #00e6f6);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 15px;" +
                                "-fx-padding: 10 18;" +
                                "-fx-background-radius: 12;"
                )
        );

        btn.setOnMouseExited(e ->
                btn.setStyle(
                        "-fx-background-color: linear-gradient(to right, #4facfe, #00f2fe);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 15px;" +
                                "-fx-padding: 10 18;" +
                                "-fx-background-radius: 12;"
                )
        );
    }

    private void styleSecondaryButton(Button btn) {
        btn.setStyle(
                "-fx-background-color: #dee2e6;" +
                        "-fx-text-fill: #333;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 14;" +
                        "-fx-background-radius: 10;"
        );
    }


    // --- Setters (for use in CheckoutPage and History) ---
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setTip(double tip) {
        this.tip = tip;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;  // Returns subtotal
    }

    public double getDiscount() {
        return discount;
    }

    public double getTip() {
        return tip;
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