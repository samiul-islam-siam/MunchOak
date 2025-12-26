package com.munchoak.payment;

public class PaymentItem {
    public final int foodId;
    public final int qty;
    public final double price;
    public final String name;

    public PaymentItem(int foodId, int qty, double price, String name) {
        this.foodId = foodId;
        this.qty = qty;
        this.price = price;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
