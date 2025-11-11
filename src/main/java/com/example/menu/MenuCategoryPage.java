package com.example.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class MenuCategoryPage {

    private final Stage primaryStage;
    private final String categoryName;
    private final List<String> foodItems;

    public MenuCategoryPage(Stage primaryStage, String categoryName, List<String> foodItems) {
        this.primaryStage = primaryStage;
        this.categoryName = categoryName;
        this.foodItems = foodItems;
    }

    public Scene getScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);

        // Category title
        Label title = new Label(categoryName);
        title.setFont(Font.font("Arial", 28));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");

        // Food items
        VBox foodBox = new VBox(15);
        foodBox.setAlignment(Pos.CENTER);

        for (String food : foodItems) {
            Button foodButton = new Button(food);
            foodButton.setFont(Font.font(16));
            foodButton.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: red; -fx-border-width: 2; " +
                            "-fx-background-radius: 8; -fx-padding: 10 20; " +
                            "-fx-text-fill: black; -fx-cursor: hand;"
            );

            // Optional: handle food click
            foodButton.setOnAction(e -> System.out.println("Selected: " + food));

            foodBox.getChildren().add(foodButton);
        }

        // Back button
        Button backBtn = new Button("\u2190 Back");
        backBtn.setFont(Font.font(16));
        backBtn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: red; -fx-font-weight: bold; -fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> returnToMenu());

        root.getChildren().addAll(backBtn, title, foodBox);

        return new Scene(root, 1000, 700);
    }

    private void returnToMenu() {
        MenuPage menuPage = new MenuPage(primaryStage);
        primaryStage.setScene(menuPage.getMenuScene());
    }
}
