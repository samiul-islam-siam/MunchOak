package com.munchoak.cart;

public class CartItemView {
    private final int id;
    private final String name;
    private int quantity;
    private final double price;
    private final double addonPerItem;
    private double total;

    public CartItemView(int id, String name, int quantity, double price, double addonPerItem) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.addonPerItem = addonPerItem;
        this.total = (price + addonPerItem) * quantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getAddonPerItem() { return addonPerItem; }
    public double getTotal() { return total; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.total = (this.price + this.addonPerItem) * quantity;
    }
}