package com.munchoak.payment;

public class PaymentBreakdown {
    public final double baseSubtotal;
    public final double addons;
    public final double discountAmount;
    public final double tip;
    public final double delivery;
    public final double tax;
    public final double service;
    public final double total;
    public final int userId;
    public final String userName;

    public PaymentBreakdown(
            double baseSubtotal,
            double addons,
            double discountAmount,
            double tip,
            double delivery,
            double tax,
            double service,
            double total,
            int userId,
            String userName
    ) {
        this.baseSubtotal = baseSubtotal;
        this.addons = addons;
        this.discountAmount = discountAmount;
        this.tip = tip;
        this.delivery = delivery;
        this.tax = tax;
        this.service = service;
        this.total = total;
        this.userId = userId;
        this.userName = userName;
    }
}