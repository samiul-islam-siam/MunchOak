package com.example.menu;

import javafx.stage.Stage;

import java.util.List;

public class SoupPage extends MenuCategoryPage {

    public SoupPage(Stage primaryStage) {
        super(primaryStage, "SOUP", List.of("Tomato Soup", "Chicken Soup", "Mushroom Soup"));
    }
}
