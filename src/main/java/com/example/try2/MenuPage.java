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

    public MenuPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getMenuScene() {
        if (menuScene == null) {
            menuScene = buildScene();
            attachMaximizedListener();
        }
        return menuScene;
    }

    private Scene buildScene() {
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

        double w = primaryStage.isMaximized() ? primaryStage.getWidth() : NORMAL_WIDTH;
        double h = primaryStage.isMaximized() ? primaryStage.getHeight() : NORMAL_HEIGHT;
        Scene scene = new Scene(root, w, h);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/try2/style.css").toExternalForm()
        );
        return scene;
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

    private void attachMaximizedListener() {
        ChangeListener<Boolean> listener = (obs, wasMax, isNowMax) -> {
            Scene newScene = buildScene();
            BorderPane oldRoot = (BorderPane) menuScene.getRoot();
            newScene.setRoot(oldRoot);
            menuScene = newScene;
            primaryStage.setScene(menuScene);
        };
        primaryStage.maximizedProperty().addListener(listener);
        primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == menuScene) {
                listener.changed(primaryStage.maximizedProperty(),
                        primaryStage.isMaximized(), primaryStage.isMaximized());
            }
        });
    }
}