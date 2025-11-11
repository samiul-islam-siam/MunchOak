package com.example.menu;

import javafx.stage.Stage;

import java.util.List;

public class FastFoodPage extends MenuCategoryPage {

    public FastFoodPage(Stage primaryStage) {
        super(primaryStage, "FAST FOOD", List.of("Burger", "Hotdog", "Nuggets"));
    }
}
