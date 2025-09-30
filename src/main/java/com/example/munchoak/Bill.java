package com.example.munchoak;

import com.example.munchoak.FoodItems;
import java.util.HashMap;
import java.util.Map;

public class Bill {
    private Cart cart;
    private Payment payment;

    public Bill(Cart cart, Payment payment) {
        this.cart = cart;
        this.payment = payment;
    }

    public String generateReceipt(Map<Integer, FoodItems> foodMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("----- Bill Receipt -----\n");
        sb.append("Cart ID: ").append(cart.getId()).append("\n");
        sb.append("Timestamp: ").append(cart.getTimestamp()).append("\n\n");

        double total = 0;
        for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
            int foodId = entry.getKey();
            int qty = entry.getValue();

            FoodItems item = foodMap.get(foodId);
            if (item != null) {
                double line = item.getPrice() * qty;
                total += line;
                sb.append(item.getName())
                        .append(" x ").append(qty)
                        .append(" = ").append(line).append("\n");
            }
        }

        sb.append("\nTotal: ").append(total).append("\n");
        sb.append("Payment ID: ").append(payment.getId()).append("\n");
        sb.append("Status: ").append(payment.isSuccess() ? "PAID" : "FAILED").append("\n");
        sb.append("------------------------\n");

        return sb.toString();
    }
}
