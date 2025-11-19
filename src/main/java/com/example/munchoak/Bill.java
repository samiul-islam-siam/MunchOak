package com.example.munchoak;

import com.example.manager.Session;
import javafx.stage.Stage;

import java.util.Map;

public class Bill {
    private final Stage primaryStage = null;
    private Cart cart =null;
    private final Payment payment;
    private  CartPage cartPage = new CartPage(primaryStage,cart);
    public Bill(Cart cart, Payment payment) {
        this.cart = cart;
        this.payment = payment;
    }

    public String generateReceipt(Map<Integer, FoodItems> foodMap) {
        StringBuilder sb = new StringBuilder();

        sb.append("============================================\n");
        sb.append("              MunchOak Restaurant            \n");
        sb.append("============================================\n");
        sb.append("User ID     : ").append(Session.getCurrentUserId()).append("\n");
        sb.append("Payment ID  : ").append(payment.getId()).append("\n");
        sb.append("Date/Time   : ")
                .append(payment.getTimestamp().substring(0, 19).replace("T", " "))
                .append("\n");
        sb.append("Pay Status  : ")
                .append(payment.isSuccess() ? "Paid" : "Pending").append("\n");
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

        // Using cart values (no UI dependency)
        sb.append(String.format("%-25s %10.2f\n", "Delivery Amount:", cartPage.getDeliveryAmount()));
        sb.append(String.format("%-25s %10.2f\n", "Tax Amount:", cartPage.getTaxAmount()));
        sb.append(String.format("%-25s %10.2f\n", "Service Fee:", cartPage.getServiceFeeAmount()));
        sb.append(String.format("%-25s %10.2f\n", "Discount:", cartPage.getDisCount()));

        double finalTotal = total +
                cartPage.getDeliveryAmount() +
                cartPage.getTaxAmount() +
                cartPage.getServiceFeeAmount() -
                cartPage.getDisCount();

        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-20s %15.2f\n", "TOTAL:", finalTotal));
        sb.append("============================================\n");
        sb.append("         Thank you for dining with us!       \n");
        sb.append("============================================\n");

        return sb.toString();
    }
}
