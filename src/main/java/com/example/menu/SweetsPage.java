package com.example.menu;

import javafx.stage.Stage;

import java.util.List;

public class SweetsPage extends MenuCategoryPage {

    public SweetsPage(Stage primaryStage) {
        super(primaryStage, "SWEETS", List.of(
                "Brownie",
                "Cheesecake",
                "Chocolate Mousse",
                "Ice Cream",
                "Cupcakes"
        ));
    }
}
