package com.example.munchoak;

import com.example.manager.FileStorage;
import com.example.manager.Session;

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

        double subtotal = 0;

        for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
            int foodId = entry.getKey();
            int qty = entry.getValue();

            FoodItems item = foodMap.get(foodId);
            if (item != null) {
                double price = item.getPrice() * qty;
                subtotal += price;

                sb.append(String.format("%-20s %5d %10.2f\n",
                        item.getName(), qty, price));
            }
        }

        sb.append("--------------------------------------------\n");

        // Fixed values matching CartPage and CheckoutPage
        double delivery = 7.99;
        double tax = 7.00;
        double service = 1.50;
        double discount = FileStorage.getPaymentDiscount(payment.getId());
        double tip = FileStorage.getPaymentTip(payment.getId());

        // Match the order in the screenshot: Delivery, Tax, Service Fee, then add Tip, then Discount
        sb.append(String.format("%-26s %10.2f\n", "Delivery Amount:", delivery));
        sb.append(String.format("%-26s %10.2f\n", "Tax Amount:", tax));
        sb.append(String.format("%-26s %10.2f\n", "Service Fee:", service));
        sb.append(String.format("%-26s %10.2f\n", "Tip:", tip));
        sb.append(String.format("%-26s %10.2f\n", "Discount:", -discount));

        double finalTotal = subtotal + delivery + tax + service + tip - discount;

        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-21s %15.2f\n", "TOTAL:", finalTotal));
        sb.append("============================================\n");
        sb.append("         Thank you for dining with us!       \n");
        sb.append("============================================\n");

        return sb.toString();
    }
}