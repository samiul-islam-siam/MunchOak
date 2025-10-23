package com.example.try2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private Stage primaryStage;
    private Scene mainScene;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        mainScene = createMainScene(); // load homepage scene
        stage.setScene(mainScene);

        stage.setTitle("Home Page + Extension");
        // stage.setFullScreen(true);  <-- REMOVE or COMMENT this line
        stage.show();
    }

    private Scene createMainScene() {
        HomePage home = new HomePage(primaryStage);

        VBox fullPage = home.getFullPage();

        ScrollPane scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        return new Scene(scrollPane, 1366, 768);
    }

    public Scene getMainScene() {
        return mainScene;
    }

    public static void main(String[] args) {
        launch();
    }
}
