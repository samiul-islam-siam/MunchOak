package com.example.munchoak;

import com.example.manager.FileStorage;
import com.example.manager.Session;

import java.util.Map;

public class Bill {
    private final Cart cart;
    private final Payment payment;
    private final double savedFullTotal; // NEW: Passed from history to match table total
    private final double baseSubtotal; // NEW: Base subtotal without add-ons

    // UPDATED: Constructor to accept saved total and base subtotal for history bills
    public Bill(Cart cart, Payment payment) {
        this(cart, payment, 0.0, 0.0);
    }

    public Bill(Cart cart, Payment payment, double savedFullTotal, double baseSubtotal) {
        this.cart = cart;
        this.payment = payment;
        this.savedFullTotal = savedFullTotal;
        this.baseSubtotal = baseSubtotal;
    }

    public String generateReceipt(Map<Integer, FoodItems> foodMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("\t   MunchOak Restaurant \n");
        sb.append("============================================\n");
        sb.append("User ID : ").append(Session.getCurrentUserId()).append("\n");
        sb.append("Payment ID : ").append(payment.getId()).append("\n");
        sb.append("Date/Time : ")
                .append(payment.getTimestamp().substring(0, 19).replace("T", " "))
                .append("\n");
        sb.append("Pay Status : ")
                .append(payment.isSuccess() ? "Paid" : "Pending").append("\n");
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-25s %5s %10s\n", "Item", "Qty", "Price"));
        sb.append("--------------------------------------------\n");


        for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
            int foodId = entry.getKey();
            int qty = entry.getValue();
            FoodItems item = foodMap.get(foodId);
            if (item != null) {
                //double addonPerItem = cart.getAddonPerItem(foodId);
                double basePricePerItem = item.getPrice();
                double lineTotal = basePricePerItem * qty; // Baseline total
                // Show base item
                sb.append(String.format("%-25s %5d %10.2f\n", item.getName(), qty, lineTotal));
            }
        }


        FileStorage.PaymentBreakdown b =
                FileStorage.getPaymentBreakdown(payment.getId());
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-31s %10.2f\n\n", "Total Add-ons:", b.addons));
        sb.append(String.format("%-26s %15.2f\n\n", "Subtotal:", b.baseSubtotal)); // Subtotal includes add-ons

        sb.append(String.format("%-31s %10.2f\n", "Delivery Amount:", b.delivery));
        sb.append(String.format("%-31s %10.2f\n", "Tax Amount:", b.tax));
        sb.append(String.format("%-31s %10.2f\n", "Service Fee:", b.service));
        sb.append(String.format("%-31s %10.2f\n", "Tip:", b.tip));
        sb.append(String.format("%-31s %10.2f\n", "Discount:", -b.discountAmount));

        // FIXED: Use saved full total to match table amount
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-26s %15.2f\n", "TOTAL:", b.total));
        sb.append("============================================\n");
        sb.append("\tThank you for dining with us! \n");
        sb.append("============================================\n");
        return sb.toString();
    }
}