package com.example.try2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        HomePage home = new HomePage();

        // Full page (Home + Extension)
        VBox fullPage = home.getFullPage();

        // Add scroll
        ScrollPane scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane, 1366, 768);
        stage.setScene(scene);
        stage.setTitle("Home Page + Extension");
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
