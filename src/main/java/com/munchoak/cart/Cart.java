package com.munchoak.cart;

import javafx.scene.control.*;
import java.io.Serializable;
import java.util.HashMap;

public class Cart implements Serializable {
    private int totalCartCount = 0;
    public static int numberOfCarts = 0;
    private final int id;
    private final HashMap<Integer, Integer> buyHistory;
    private final HashMap<Integer, Double> addonPerItems;

    public Cart() {
        this.id = ++numberOfCarts;
        this.buyHistory = new HashMap<>();
        this.addonPerItems = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public HashMap<Integer, Integer> getBuyHistory() {
        return buyHistory;
    }

    public HashMap<Integer, Double> getAddonPerItems() {
        return addonPerItems;
    }


    // ============================================================
    //                CART OPERATIONS
    // ============================================================
    public void addToCart(Integer foodId, int count) {
        addToCart(foodId, count, 0.0);
    }

    public void addToCart(Integer foodId, int count, double addonPerItem) {
        buyHistory.put(foodId, buyHistory.getOrDefault(foodId, 0) + count);
        addonPerItems.put(foodId, addonPerItem);
        totalCartCount += count;
    }

    public int getTotalCartCount() {
        return totalCartCount;
    }

    public void removeFromCart(Integer foodId) {
        if (buyHistory.containsKey(foodId)) {
            int current = buyHistory.get(foodId);
            if (current > 1) {
                buyHistory.put(foodId, current - 1);
            } else {
                buyHistory.remove(foodId);
                addonPerItems.remove(foodId);
            }
            totalCartCount--;
        }
    }

    public void removeFromCartEntirely(Integer foodId) {
        if (buyHistory.containsKey(foodId)) {
            int current = buyHistory.get(foodId);
            totalCartCount -= current;
            buyHistory.remove(foodId);
            addonPerItems.remove(foodId);
        }
    }

    public double getAddonPerItem(int foodId) {
        return addonPerItems.getOrDefault(foodId, 0.0);
    }

    public int getTotalItems() {
        int total = 0;
        for (int qty : buyHistory.values()) {
            total += qty;
        }
        return total;
    }

    public void clearCart() {
        buyHistory.clear();
        addonPerItems.clear();
        totalCartCount = 0;
    }
}