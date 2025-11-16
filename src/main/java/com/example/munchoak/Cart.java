package com.example.munchoak;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Cart implements Serializable {
    public static int numberOfCarts = 0;
    private final int id;
    private final HashMap<Integer, Integer> buyHistory;

    public Cart() {
        this.id = ++numberOfCarts;
        this.buyHistory = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public HashMap<Integer, Integer> getBuyHistory() {
        return buyHistory;
    }

    public void addToCart(Integer foodId, int count) {
        buyHistory.put(foodId, buyHistory.getOrDefault(foodId, 0) + count);
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