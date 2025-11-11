package com.example.menu;

import javafx.stage.Stage;

import java.util.List;

public class DrinksPage extends MenuCategoryPage {

    public DrinksPage(Stage primaryStage) {
        super(primaryStage, "DRINKS", List.of(
                "Cola",
                "Orange Juice",
                "Mojito",
                "Lemonade",
                "Iced Tea"
        ));
    }
}
