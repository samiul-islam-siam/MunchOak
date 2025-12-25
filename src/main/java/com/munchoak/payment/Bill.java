package com.munchoak.payment;

import com.munchoak.cart.Cart;
import javafx.scene.control.Alert;

import java.util.List;

public class Bill {
    private final Cart cart;
    private final Payment payment;
    private final double savedFullTotal; // Passed from history to match table total
    private final double baseSubtotal; // Base subtotal without add-ons

    // Constructor to accept saved total and base subtotal for history bills
    public Bill(Cart cart, Payment payment) {
        this(cart, payment, 0.0, 0.0);
    }

    public Bill(Cart cart, Payment payment, double savedFullTotal, double baseSubtotal) {
        this.cart = cart;
        this.payment = payment;
        this.savedFullTotal = savedFullTotal;
        this.baseSubtotal = baseSubtotal;
    }

    public String generateReceipt() {
        PaymentBreakdown b =
                PaymentStorage.getPaymentBreakdown(payment.getId());

        if (b == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Unavailable Item");
            alert.setHeaderText(null);
            alert.setContentText("Sorry, Bill is currently unavailable!");
            alert.showAndWait();
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("\t   MunchOak Restaurant \n");
        sb.append("============================================\n");
        sb.append("User ID : ").append(b.userId).append("\n");
        sb.append("User Name : ").append(b.userName).append("\n");
        sb.append("Payment ID : ").append(payment.getId()).append("\n");
        sb.append("Date/Time : ")
                .append(payment.getTimestamp().substring(0, 19).replace("T", " "))
                .append("\n");
        sb.append("Pay Status : ")
                .append(payment.isSuccess() ? "Paid" : "Pending").append("\n");
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-25s %5s %10s\n", "Item", "Qty", "Price"));
        sb.append("--------------------------------------------\n");

        List<PaymentItem> items =
                PaymentStorage.getPaymentItems(payment.getId());

        for (PaymentItem pi : items) {
            String name = pi.name;
            double lineTotal = pi.price * pi.qty;

            sb.append(String.format(
                    "%-25s %5d %10.2f\n",
                    name, pi.qty, lineTotal
            ));
        }

        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-31s %10.2f\n\n", "Total Add-ons:", b.addons));
        sb.append(String.format("%-26s %15.2f\n\n", "Subtotal:", b.baseSubtotal)); // Subtotal includes add-ons

        sb.append(String.format("%-31s %10.2f\n", "Delivery Amount:", b.delivery));
        sb.append(String.format("%-31s %10.2f\n", "Tax Amount:", b.tax));
        sb.append(String.format("%-31s %10.2f\n", "Service Fee:", b.service));
        sb.append(String.format("%-31s %10.2f\n", "Tip:", b.tip));
        sb.append(String.format("%-31s %10.2f\n", "Discount:", -b.discountAmount));

        // Use saved full total to match table amount
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-26s %15.2f\n", "TOTAL:", b.total));
        sb.append("============================================\n");
        sb.append("\tThank you for dining with us! \n");
        sb.append("============================================\n");
        return sb.toString();
    }
}