package com.example.try2;

import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MenuPage {
    private final Stage primaryStage;
    private static final double NORMAL_WIDTH = 1000;
    private static final double NORMAL_HEIGHT = 700;
    private Scene menuScene;
    private BorderPane root;  // Reusable root

    public MenuPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getMenuScene() {
        if (menuScene == null) {
            // Use current stage size if already in full screen/maximized, else normal
            double initWidth = primaryStage.isFullScreen() || primaryStage.isMaximized()
                    ? Math.max(primaryStage.getWidth(), NORMAL_WIDTH)
                    : NORMAL_WIDTH;
            double initHeight = primaryStage.isFullScreen() || primaryStage.isMaximized()
                    ? Math.max(primaryStage.getHeight(), NORMAL_HEIGHT)
                    : NORMAL_HEIGHT;

            root = buildRoot();  // Build layout once
            menuScene = new Scene(root, initWidth, initHeight);
            menuScene.getStylesheets().add(
                    getClass().getResource("/com/example/try2/style.css").toExternalForm()
            );
            attachResizeListeners();  // Fixed to handle full screen too
        }
        return menuScene;
    }

    private BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #FFE4C4;");

        HBox navBar = new HBox(20);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(15, 30, 15, 30));
        navBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.05);");

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

        VBox content = new VBox(50);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(80, 60, 60, 60));

        Image baseImage = new Image(getClass().getResource("/com/example/try2/images/menu_banner.png").toExternalForm());
        ImageView baseView = new ImageView(baseImage);
        baseView.setPreserveRatio(true);
        baseView.setSmooth(true);
        baseView.setFitWidth(300);
        baseView.layoutBoundsProperty().addListener((obs, old, newVal) -> {
            double fullHeight = baseImage.getHeight();
            double visibleHeight = fullHeight * 0.75;
            baseView.setViewport(new javafx.geometry.Rectangle2D(0, 0, baseImage.getWidth(), visibleHeight));
        });

        Image overlayImage = new Image(getClass().getResource("/com/example/try2/images/overlay_logo.png").toExternalForm());
        ImageView overlayView = new ImageView(overlayImage);
        overlayView.setPreserveRatio(true);
        overlayView.setSmooth(true);
        overlayView.setFitWidth(60);
        overlayView.setOpacity(0.95);

        // CHANGED ORDER: overlayView FIRST → appears ON TOP
        StackPane imageStack = new StackPane(overlayView, baseView);
        imageStack.setAlignment(Pos.TOP_CENTER);
        StackPane.setMargin(overlayView, new Insets(80, 0, 0, 0));

        ScaleTransition zoom = new ScaleTransition(Duration.seconds(5.0), overlayView);
        zoom.setFromX(0.5); zoom.setFromY(0.3);
        zoom.setToX(10.0); zoom.setToY(7.0);
        zoom.setCycleCount(ScaleTransition.INDEFINITE);
        zoom.setAutoReverse(true);
        imageStack.layoutBoundsProperty().addListener((obs, old, newVal) -> {
            if (zoom.getStatus() != ScaleTransition.Status.RUNNING) zoom.play();
        });

        content.getChildren().add(imageStack);

        // NEW SECTION: Menu Categories Grid (mimicking the provided image layout)
        VBox menuSection = new VBox(20);
        menuSection.setAlignment(Pos.CENTER);

        // Title
        Label title = new Label("OUR SPECIALS MENU");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: red;");
        menuSection.getChildren().add(title);

        // Grid for categories
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Row 1
        Label teasers = createMenuBox("TEASERS");
        Label wings = createMenuBox("WINGS");
        Label classicBurgers = createMenuBox("CLASSIC BURGERS");
        grid.add(teasers, 0, 0);
        grid.add(wings, 1, 0);
        grid.add(classicBurgers, 2, 0);

        // Row 2
        Label gourmetBurgers = createMenuBox("GOURMET BURGERS");
        Label poutines = createMenuBox("POUTINES");
        Label riceMeals = createMenuBox("RICE MEALS");
        grid.add(gourmetBurgers, 0, 1);
        grid.add(poutines, 1, 1);
        grid.add(riceMeals, 2, 1);

        // Row 3
        Label wholesomePlatters = createMenuBox("WHOLESOME PLATTERS");
        Label mocktails = createMenuBox("MOCKTAILS");
        Label shakes = createMenuBox("SHAKES");
        grid.add(wholesomePlatters, 0, 2);
        grid.add(mocktails, 1, 2);
        grid.add(shakes, 2, 2);

        // Bottom centered item
        Label madwrap = createMenuBox("MADWRAP");
        grid.add(madwrap, 1, 3);

        menuSection.getChildren().add(grid);
        content.getChildren().add(menuSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #FFE4C4; -fx-background-color: #FFE4C4;");

        root.setTop(navBar);
        root.setCenter(scrollPane);

        return root;
    }

    // NEW HELPER METHOD: Creates a styled box for each menu category
    private Label createMenuBox(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-border-color: red; " +
                        "-fx-border-width: 2; " +
                        "-fx-background-color: white; " +
                        "-fx-padding: 15 20 15 20; " +  // Top, right, bottom, left padding
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: black; " +
                        "-fx-alignment: center; " +
                        "-fx-background-radius: 5; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        );
        label.setMinSize(120, 60);
        label.setMaxSize(140, 80);  // Allow some flexibility for varying text lengths
        label.setWrapText(true);
        return label;
    }

    private void returnToHome() {
        // Preserve current state before switching
        boolean wasFullScreen = primaryStage.isFullScreen();
        boolean wasMaximized = primaryStage.isMaximized();

        HomePage homePage = new HomePage(primaryStage);
        VBox fullPage = homePage.getFullPage();
        ScrollPane scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        // Create new scene without explicit size—let stage dictate
        Scene homeScene = new Scene(scrollPane);
        primaryStage.setScene(homeScene);

        // Reapply state AFTER setting scene to ensure it takes effect on the new scene
        if (wasFullScreen) {
            primaryStage.setFullScreen(true);
        } else if (wasMaximized) {
            primaryStage.setMaximized(true);
        }
    }

    // --- FIXED: Handle both full screen and maximized without recreating scenes/roots ---
    private void attachResizeListeners() {
        // Full screen listener: No manual resize needed; just force layout refresh
        ChangeListener<Boolean> fullScreenListener = (obs, wasFull, isNowFull) -> {
            if (menuScene != null) {
                root.requestLayout(); // Ensures layout adapts instantly
            }
        };
        primaryStage.fullScreenProperty().addListener(fullScreenListener);

        // Maximized listener: Same as above
        ChangeListener<Boolean> maximizedListener = (obs, wasMax, isNowMax) -> {
            if (menuScene != null) {
                root.requestLayout(); // Ensures layout adapts instantly
            }
        };
        primaryStage.maximizedProperty().addListener(maximizedListener);

        // Scene attach listener: Trigger layout if scene is set while in special mode
        primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == menuScene && (primaryStage.isFullScreen() || primaryStage.isMaximized())) {
                root.requestLayout();
            }
        });
    }
}