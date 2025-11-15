package com.example.munchoak;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Cart implements Serializable {
    public static int numberOfCarts = 0;
    private int id;
    private int userId;
    private String token;
    private HashMap<Integer, Integer> buyHistory; // Food_ID -> quantity
    private String timestamp;

    public Cart(int userId, String token) {
        this.id = ++numberOfCarts;
        this.userId = userId;
        this.token = token;
        this.buyHistory = new HashMap<>();
        this.timestamp = Instant.now().toString();
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public HashMap<Integer, Integer> getBuyHistory() {
        return buyHistory;
    }

    public void addToCart(Integer foodId, int count) {
        buyHistory.put(foodId, buyHistory.getOrDefault(foodId, 0) + count);
    }

    public void removeFromCart(Integer foodId) {
        if (buyHistory.containsKey(foodId)) {
            int current = buyHistory.get(foodId);
            if (current > 1) buyHistory.put(foodId, current - 1);
            else buyHistory.remove(foodId);
        }
    }

    public void removeFromCartEntirely(Integer foodId) {
        buyHistory.remove(foodId);
    }

    public double getTotalPrice(Map<Integer, FoodItems> foodMap) {
        double total = 0.0;
        for (Map.Entry<Integer, Integer> entry : buyHistory.entrySet()) {
            int foodId = entry.getKey();
            int qty = entry.getValue();
            FoodItems item = foodMap.get(foodId);
            if (item != null) total += item.getPrice() * qty;
        }
        return total;
    }
}