package com.example.menu;

import javafx.stage.Stage;

import java.util.List;

public class FromTheSeaPage extends MenuCategoryPage {

    public FromTheSeaPage(Stage primaryStage) {
        super(primaryStage, "FROM THE SEA", List.of(
                "Grilled Salmon",
                "Fish & Chips",
                "Shrimp Cocktail",
                "Lobster Thermidor"
        ));
    }
}
