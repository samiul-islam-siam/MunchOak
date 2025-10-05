
    package com.example.login;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

    public class MainMenu extends Application {

        @Override
        public void start(Stage stage) {
            StackPane root = new StackPane();
            root.getChildren().add(new Label("Welcome to Restaurant Main Menu!"));
            Scene scene = new Scene(root, 400, 300);
            stage.setTitle("Main Menu");
            stage.setScene(scene);
            stage.show();
        }
    }


