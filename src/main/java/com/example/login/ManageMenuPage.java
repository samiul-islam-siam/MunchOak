package com.example.login;
import com.example.manager.AdminFileStorage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ManageMenuPage {

    public static void show(Stage stage) {
        Label title = new Label("Manage Menu (Food Items)");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea foodList = new TextArea();
        foodList.setEditable(false);
        refreshFoodList(foodList);

        TextField newItemField = new TextField();
        newItemField.setPromptText("Enter new food item name");

        Button addBtn = new Button("Add Item");
        Button deleteBtn = new Button("Delete Item");
        Button backBtn = new Button("Back");

        addBtn.setOnAction(e -> {
            String item = newItemField.getText().trim();
            if (item.isEmpty()) return;
            try {
                AdminFileStorage.addFoodItem(item);
                refreshFoodList(foodList);
                newItemField.clear();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        deleteBtn.setOnAction(e -> {
            String item = newItemField.getText().trim();
            if (item.isEmpty()) return;
            try {
                AdminFileStorage.removeFoodItem(item);
                refreshFoodList(foodList);
                newItemField.clear();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        backBtn.setOnAction(e -> AdminDashboard.show(stage));

        VBox layout = new VBox(10, title, foodList, newItemField, addBtn, deleteBtn, backBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F0F8FF;");

        stage.setScene(new Scene(layout, 500, 500));
    }

    private static void refreshFoodList(TextArea foodList) {
        List<String> foods = AdminFileStorage.getAllFoodItems();
        foodList.setText(String.join("\n", foods));
    }
}

