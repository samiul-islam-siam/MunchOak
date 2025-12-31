package com.munchoak.payment;

import com.munchoak.cart.Cart;
import com.munchoak.mainpage.FoodItems;
import com.munchoak.manager.MenuStorage;
import com.munchoak.manager.Session;
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

    // ===================== CHECKOUT HANDLER =====================
    public static void checkout(Cart cart) {

        // Build map of foodId -> FoodItems
        Map<Integer, FoodItems> foodMap = new HashMap<>();
        for (FoodItems food : MenuStorage.loadMenu()) {
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
        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Checkout failed: " + e.getMessage()).show();
        }
    }

    public static int getLastPaymentId() {
        return PaymentStorage.getLastPaymentId();
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