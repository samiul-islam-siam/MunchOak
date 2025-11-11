package com.example.menu;

import javafx.stage.Stage;

import java.util.List;

public class MainCoursePage extends MenuCategoryPage {

    public MainCoursePage(Stage primaryStage) {
        super(primaryStage, "MAIN COURSE", List.of(
                "Steak",
                "Pasta",
                "Pizza",
                "Grilled Chicken",
                "Lasagna"
        ));
    }
}
