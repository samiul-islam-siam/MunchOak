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
    private Scene mainScene;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        mainScene = createMainScene(); // load homepage scene
        stage.setScene(mainScene);
        stage.setTitle("Home Page + Extension");

        // FIXED: Load GLOBAL stylesheet - applies to ALL scenes (Home, Login, Reservation, etc.)
        // This ensures buttons keep custom styles after navigating back from Login "X" close
        var css = getClass().getResource("/com/example/view/styles/style.css");
        if (css != null) {
            setUserAgentStylesheet(css.toExternalForm()); // Key: App-wide CSS!
            System.out.println("✅ Global CSS loaded for all scenes: " + css.toExternalForm());
        } else {
            System.err.println("❌ CSS not found! Check file path: /com/example/view/styles/style.css");
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
        // 🔹 Smooth scroll effect (gradual movement)
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
        return new Scene(scrollPane, 1000, 700);
        // REMOVED: No need for per-scene CSS add - global handles it!
    }

    public Scene getMainScene() {
        return mainScene;
    }

    public static void main(String[] args) {
        FileStorage.ensureDefaultGuestUser();
        launch();
    }
}