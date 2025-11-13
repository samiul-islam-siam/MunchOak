package com.example.munchoak;

import java.util.Map;

public class Bill {
    private final Cart cart;
    private final Payment payment;

    public Bill(Cart cart, Payment payment) {
        this.cart = cart;
        this.payment = payment;
    }

    public String generateReceipt(Map<Integer, FoodItems> foodMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("              MunchOak Restaurant            \n");
        sb.append("============================================\n");
        sb.append("Payment ID : ").append(payment.getId()).append("\n");
        sb.append("Date/Time  : ").append(payment.getTimestamp()
                .substring(0, 19).replace("T", " ")).append("\n");
        sb.append("Pay Status : ").append(payment.isSuccess() ? "Paid" : "Pending").append("\n");
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-20s %5s %10s\n", "Item", "Qty", "Price"));
        sb.append("--------------------------------------------\n");

        double total = 0;
        for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
            int foodId = entry.getKey();
            int qty = entry.getValue();
            FoodItems item = foodMap.get(foodId);
            if (item != null) {
                double price = item.getPrice() * qty;
                total += price;
                sb.append(String.format("%-20s %5d %10.2f\n",
                        item.getName(), qty, price));
            }
        }

        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-21s %15.2f\n", "TOTAL: ", total));
        sb.append("============================================\n");
        sb.append("         Thank you for dining with us!       \n");
        sb.append("============================================\n");
        return sb.toString();
    }
}
