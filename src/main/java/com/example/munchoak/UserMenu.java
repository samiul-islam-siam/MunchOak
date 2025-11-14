package com.example.munchoak;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class UserMenu extends BaseMenu {

    @Override
    public Node getView() {
        Node node = super.getView();

        if (node instanceof VBox vbox) {
            // Remove admin-only controls
            vbox.getChildren().remove(showAddFormBtn);   // Remove "Add Food"
            vbox.getChildren().remove(formBox);          // Hide Add/Edit form
            vbox.getChildren().remove(categoryButtons);  // Remove category management buttons
            //vbox.getChildren().remove(showEditDialog());
        }

        System.out.println("User Menu Loaded");
        return node;
    }

    // Override admin-only functions to disable them
    @Override
    protected void addCategory() {
        System.out.println("User cannot manage categories.");
    }

    @Override
    protected void addFoodItem() {
        System.out.println("User cannot add food items.");
    }

    @Override
    protected void deleteFoodItem(FoodItems food) {
        System.out.println("User cannot delete food items.");
    }

    @Override
    protected void updateFoodItem() {
        System.out.println("User cannot update food items.");
    }
}
