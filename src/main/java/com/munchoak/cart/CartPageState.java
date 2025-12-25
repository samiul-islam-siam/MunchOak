package com.munchoak.cart;

import java.util.concurrent.atomic.AtomicReference;

public class CartPageState {
    private double disCount;
    private double total;
    private String couponCode;

    // used for tip selection and live recalculation
    public final AtomicReference<Double> tip = new AtomicReference<>(0.0);

    public double getDisCount() { return disCount; }
    public void setDisCount(double disCount) { this.disCount = disCount; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
}