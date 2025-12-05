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
        sb.append("\t MunchOak Restaurant \n");
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

        double calculatedBaseSubtotal = 0.0;
        for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
            int foodId = entry.getKey();
            int qty = entry.getValue();
            FoodItems item = foodMap.get(foodId);
            if (item != null) {
                //double addonPerItem = cart.getAddonPerItem(foodId);
                double basePricePerItem = item.getPrice();
                double lineTotal = basePricePerItem * qty; // Baseline total
                calculatedBaseSubtotal += lineTotal;

                // Show base item
                sb.append(String.format("%-25s %5d %10.2f\n", item.getName(), qty, lineTotal));
                // Show add-ons detail if applicable
//                if (addonPerItem > 0) {
//                    sb.append(String.format("%-25s %5s %10.2f\n", "  + Add-ons", "", addonPerItem * qty));
//                }
            }
        }

        // FIXED: Use reverse-engineered subtotal to include add-ons for matching total
        double subtotal = (savedFullTotal > 0) ? savedFullTotal - 7.99 - 7.00 - 1.50 - payment.getTip() + payment.getDiscount() : calculatedBaseSubtotal;
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-21s %15.2f\n", "Subtotal:", calculatedBaseSubtotal)); // Subtotal includes add-ons

        // Fixed values matching CartPage and CheckoutPage
        double delivery = 7.99;
        double tax = 7.00;
        double service = 1.50;

        double discount = FileStorage.getPaymentDiscount(payment.getId());
        double tip = FileStorage.getPaymentTip(payment.getId());


        sb.append(String.format("%-26s %10.2f\n", "Total Add-ons:", savedFullTotal-calculatedBaseSubtotal+discount-delivery-tax-service-tip));
        sb.append(String.format("%-26s %10.2f\n", "Delivery Amount:", delivery));
        sb.append(String.format("%-26s %10.2f\n", "Tax Amount:", tax));
        sb.append(String.format("%-26s %10.2f\n", "Service Fee:", service));
        sb.append(String.format("%-26s %10.2f\n", "Tip:", tip));
        sb.append(String.format("%-26s %10.2f\n", "Discount:", -discount));

        // FIXED: Use saved full total to match table amount
        double finalTotal = (savedFullTotal > 0) ? savedFullTotal : (subtotal + delivery + tax + service + tip - discount);
        //double finalTotal = (calculatedBaseSubtotal + delivery + tax + service + tip - discount);
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-21s %15.2f\n", "TOTAL:", finalTotal));
        sb.append("============================================\n");
        sb.append("\t Thank you for dining with us! \n");
        sb.append("============================================\n");
        return sb.toString();
    }
}