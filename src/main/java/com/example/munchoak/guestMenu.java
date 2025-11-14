package com.example.munchoak;

import com.example.manager.FileStorage;
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


public class guestMenu extends BaseMenu {

    @Override
    public Node getView() {
        Node node = super.getView();

        if (node instanceof VBox vbox) {

            vbox.getChildren().remove(showAddFormBtn);   // Remove "Add Food"
            vbox.getChildren().remove(formBox);          // Hide Add/Edit form
            vbox.getChildren().remove(categoryButtons);  // Remove category management buttons

            vbox.getChildren().remove(buttonMenu);

            if (cartButtons != null) {
                cartButtons.setVisible(false);
                cartButtons.setManaged(false);
            }
            File menuFile = FileStorage.getMenuFile();
            if (!menuFile.exists() || menuFile.length() == 0) {
                javafx.application.Platform.runLater(() -> {
                    viewCartButton.setVisible(false);
                    checkoutButton.setVisible(false);

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

        System.out.println("Guest Menu Loaded");
        return node;
    }

    @Override
    protected void addCategory() {
        System.out.println("Guest cannot manage categories.");
    }

    @Override
    protected void addFoodItem() {
        System.out.println("Guest cannot add food items.");
    }

    @Override
    protected void deleteFoodItem(FoodItems food) {
        System.out.println("Guest cannot delete food items.");
    }

    @Override
    protected void updateFoodItem() {
        System.out.println("Guest cannot update food items.");
    }
}
