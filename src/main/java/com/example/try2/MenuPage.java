package com.example.try2;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MenuPage {
    private final Stage primaryStage;

    public MenuPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getMenuScene() {
        // --- Root Layout ---
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #FFE4C4;");

        // --- Navigation Bar ---
        HBox navBar = new HBox(20);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(15, 30, 15, 30));
        navBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.05);");

        // --- Cart Icon ---
        Image cartImage = new Image(getClass().getResource("/com/example/try2/images/cart.png").toExternalForm());
        ImageView cartIcon = new ImageView(cartImage);
        cartIcon.setFitWidth(28);
        cartIcon.setFitHeight(28);
        cartIcon.setPreserveRatio(true);

        Button cartButton = new Button();
        cartButton.setGraphic(cartIcon);
        cartButton.getStyleClass().add("top-button");
        cartButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        navBar.getChildren().addAll(spacer, cartButton);

        // --- Main Content ---
        VBox content = new VBox(50);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(80, 60, 60, 60)); // Moved down more

        // --- BASE IMAGE: SMALLER (300px wide) ---
        Image baseImage = new Image(getClass().getResource("/com/example/try2/images/menu_banner.png").toExternalForm());
        ImageView baseView = new ImageView(baseImage);
        baseView.setPreserveRatio(true);
        baseView.setSmooth(true);
        baseView.setFitWidth(300);  // SMALLER

        // CROP: Show only TOP 75%
        baseView.layoutBoundsProperty().addListener((obs, old, newVal) -> {
            double fullHeight = baseImage.getHeight();
            double visibleHeight = fullHeight * 0.75;
            baseView.setViewport(new javafx.geometry.Rectangle2D(0, 0, baseImage.getWidth(), visibleHeight));
        });

        // --- OVERLAY IMAGE: ON THE SCOOP ---
        Image overlayImage = new Image(getClass().getResource("/com/example/try2/images/overlay_logo.png").toExternalForm());
        ImageView overlayView = new ImageView(overlayImage);
        overlayView.setPreserveRatio(true);
        overlayView.setSmooth(true);
        overlayView.setFitWidth(60);  // Smaller base
        overlayView.setOpacity(0.95);

        // --- Stack: Base + Overlay ---
        StackPane imageStack = new StackPane(baseView, overlayView);
        imageStack.setAlignment(Pos.TOP_CENTER);

        // POSITION ON SCOOP (adjusted for smaller base)
        StackPane.setMargin(overlayView, new Insets(35, 0, 0, 0)); // Fine-tuned

        // --- ZOOM: 30% to 800% (8×), 5 sec, REPEATS ---
        ScaleTransition zoom = new ScaleTransition(Duration.seconds(5.0), overlayView);
        zoom.setFromX(0.3);
        zoom.setFromY(0.3);
        zoom.setToX(8.0);   // 800%
        zoom.setToY(8.0);   // 800%
        zoom.setCycleCount(ScaleTransition.INDEFINITE);
        zoom.setAutoReverse(true);

        // START ON PAGE LOAD
        imageStack.layoutBoundsProperty().addListener((obs, old, newVal) -> {
            if (zoom.getStatus() != ScaleTransition.Status.RUNNING) {
                zoom.play();
            }
        });

        content.getChildren().add(imageStack);

        // --- ScrollPane ---
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #FFE4C4; -fx-background-color: #FFE4C4;");

        // --- Layout ---
        root.setTop(navBar);
        root.setCenter(scrollPane);

        // --- Scene ---
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/try2/style.css").toExternalForm()
        );

        return scene;
    }
}