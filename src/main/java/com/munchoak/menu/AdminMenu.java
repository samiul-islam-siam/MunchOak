package com.munchoak.menu;

import javafx.scene.Node;

public class AdminMenu extends BaseMenu {

    @Override
    public Node getView() {
        Node node = super.getView();

        // Hide cart buttons for admin
        if (cartButtons != null) {
            cartButtons.setVisible(false);
            cartButtons.setManaged(false);
        }

        // "Add Food" button stays visible
        if (showAddFormBtn != null) {
            showAddFormBtn.setVisible(true);
            showAddFormBtn.setManaged(true);
        }
        System.out.println("Admin Menu Loaded");
        return node;
    }
}
