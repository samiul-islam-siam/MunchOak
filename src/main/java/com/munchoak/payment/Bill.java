package com.munchoak.payment;

import javafx.scene.control.Alert;

import java.util.List;

public class Bill {
    private final com.munchoak.cart.Cart cart;
    private final Payment payment;
    private final double savedFullTotal;
    private final double baseSubtotal;

    public Bill(com.munchoak.cart.Cart cart, Payment payment) {
        this(cart, payment, 0.0, 0.0);
    }

    public Bill(com.munchoak.cart.Cart cart, Payment payment, double savedFullTotal, double baseSubtotal) {
        this.cart = cart;
        this.payment = payment;
        this.savedFullTotal = savedFullTotal;
        this.baseSubtotal = baseSubtotal;
    }

    // Always-correct history receipt (menu-independent)
    public static String generateReceiptFromSnapshot(int paymentId, String timestampFromHistory) {
        PaymentBreakdown b = PaymentStorage.getPaymentBreakdown(paymentId);

        if (b == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Unavailable Item");
            alert.setHeaderText(null);
            alert.setContentText("Sorry, Bill is currently unavailable!");
            alert.showAndWait();
            return null;
        }

        List<PaymentItem> items = PaymentStorage.getPaymentItems(paymentId);

        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("\t   MunchOak Restaurant \n");
        sb.append("============================================\n");
        sb.append("User ID : ").append(b.userId).append("\n");
        sb.append("User Name : ").append(b.userName).append("\n");
        sb.append("Payment ID : ").append(paymentId).append("\n");

        if (timestampFromHistory != null && !timestampFromHistory.isBlank()) {
            String ts = timestampFromHistory;
            if (ts.length() >= 19) ts = ts.substring(0, 19).replace("T", " ");
            sb.append("Date/Time : ").append(ts).append("\n");
        }

        sb.append("Pay Status : Paid\n"); // always Paid

        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-25s %5s %10s\n", "Item", "Qty", "Price"));
        sb.append("--------------------------------------------\n");

        for (PaymentItem pi : items) {
            String name = (pi.name != null && !pi.name.isBlank())
                    ? pi.name
                    : ("(Unknown item #" + pi.foodId + ")");
            double lineTotal = pi.price * pi.qty;

            sb.append(String.format("%-25s %5d %10.2f\n", name, pi.qty, lineTotal));
        }

        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-31s %10.2f\n\n", "Total Add-ons:", b.addons));
        sb.append(String.format("%-26s %15.2f\n\n", "Subtotal:", b.baseSubtotal));

        sb.append(String.format("%-31s %10.2f\n", "Delivery Amount:", b.delivery));
        sb.append(String.format("%-31s %10.2f\n", "Tax Amount:", b.tax));
        sb.append(String.format("%-31s %10.2f\n", "Service Fee:", b.service));
        sb.append(String.format("%-31s %10.2f\n", "Tip:", b.tip));
        sb.append(String.format("%-31s %10.2f\n", "Discount:", -b.discountAmount));

        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-26s %15.2f\n", "TOTAL:", b.total));
        sb.append("============================================\n");
        sb.append("\tThank you for dining with us! \n");
        sb.append("============================================\n");
        return sb.toString();
    }
}