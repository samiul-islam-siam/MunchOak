package com.munchoak.cart;

import com.munchoak.mainpage.FoodItems;

import java.util.Map;

public class CartPricing {

    public record Subtotals(double baseSubtotal, double totalAddons, double subtotal) {}

    public static Subtotals computeSubtotals(Cart cart, Map<Integer, FoodItems> foodMap) {
        double baseSubtotal = 0.0;
        double totalAddons = 0.0;

        for (var e : cart.getBuyHistory().entrySet()) {
            int foodId = e.getKey();
            int qty = e.getValue();
            FoodItems item = foodMap.get(foodId);
            if (item == null) continue;

            baseSubtotal += item.getPrice() * qty;
            totalAddons += cart.getAddonPerItem(foodId) * qty;
        }
        double subtotal = baseSubtotal + totalAddons;
        return new Subtotals(baseSubtotal, totalAddons, subtotal);
    }
}