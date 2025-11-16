package com.example.menu;

import com.example.manager.FileStorage;
import com.example.munchoak.FoodItems;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import java.io.File;

public class UserMenu extends BaseMenu {

    @Override
    public Node getView() {
        Node node = super.getView();

        if (node instanceof VBox vbox) {
            // Remove admin-only controls
            vbox.getChildren().remove(showAddFormBtn);   // Remove "Add Food"
            vbox.getChildren().remove(formBox);          // Hide Add/Edit form
            vbox.getChildren().remove(categoryButtons);  // Remove category management buttons

            vbox.getChildren().remove(buttonMenu);
            File menuFile = FileStorage.getMenuFile();
            if (!menuFile.exists() || menuFile.length() == 0) {
                javafx.application.Platform.runLater(() -> {
                    viewCartButton.setVisible(false);
                    checkoutButton.setVisible(false);
                    // === Create nice empty state message ===
                    Label emoji = new Label("🍽");
                    emoji.setFont(Font.font("Segoe UI Emoji", 64));

                    Label title = new Label("Empty Menu Page");
                    title.setFont(Font.font("Poppins", FontWeight.BOLD, 28));
                    title.setTextFill(Color.web("#444"));

                    Label subtitle = new Label("Please try again later.");
                    subtitle.setFont(Font.font("Poppins", FontWeight.NORMAL, 18));
                    subtitle.setTextFill(Color.web("#666"));

                    VBox messageBox = new VBox(10, emoji, title, subtitle);
                    messageBox.setAlignment(Pos.CENTER);
                    messageBox.setPadding(new Insets(80));

                    StackPane wrapper = new StackPane(messageBox);
                    wrapper.setAlignment(Pos.CENTER);
                    wrapper.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #f9f9f9);");

                    vbox.getChildren().add(wrapper);
                });
            }
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
