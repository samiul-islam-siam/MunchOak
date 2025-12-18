package com.example.munchoak;

import com.example.manager.FileStorage;
import com.example.view.HomePage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Home extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        Scene mainScene = createMainScene(); // load homepage scene
        stage.setScene(mainScene);
        stage.setTitle("Home Page");

        // Load global stylesheet
        var css = getClass().getResource("/com/example/view/styles/style.css");
        if (css != null) {
            mainScene.getStylesheets().add(css.toExternalForm());
            System.out.println("CSS loaded successfully: " + css);
        } else {
            System.out.println("CSS not found! Check file path.");
        }
        stage.show();
    }

    private Scene createMainScene() {
        HomePage home = new HomePage(primaryStage);
        VBox fullPage = home.getFullPage();

        // --- ScrollPane with smoother scroll ---
        ScrollPane scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        // --- Smooth scroll effect (gradual movement) ---
        scrollPane.setOnScroll(e -> {
            double deltaY = e.getDeltaY() * 0.002; // scroll speed factor
            double targetV = scrollPane.getVvalue() - deltaY;
            targetV = Math.max(0, Math.min(targetV, 1));
            Timeline smoothScroll = new Timeline();
            smoothScroll.getKeyFrames().add(
                    new KeyFrame(Duration.millis(180),
                            new javafx.animation.KeyValue(scrollPane.vvalueProperty(), targetV))
            );
            smoothScroll.play();
        });
        return new Scene(scrollPane, 1000, 700); // Scene area
    }

    public static void main(String[] args) {
        FileStorage.init();
        //FileStorage.ensureDefaultGuestUser();
        launch();
    }
}
