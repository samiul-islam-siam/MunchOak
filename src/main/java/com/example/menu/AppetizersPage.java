package com.example.menu;

import javafx.stage.Stage;

import java.util.List;

public class AppetizersPage extends MenuCategoryPage {

    public AppetizersPage(Stage primaryStage) {
        super(primaryStage, "APPETIZERS", List.of("Spring Rolls", "Fries", "Garlic Bread"));
    }
}
