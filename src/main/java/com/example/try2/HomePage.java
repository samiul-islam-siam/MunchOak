package com.example.try2;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePage implements HomePageComponent {
    private final StackPane root;
    private final ImageView bgView;
    private final List<Image> backgrounds = new ArrayList<>();
    private int currentBgIndex = 0;
    private final Stage primaryStage;
    private final double WIDTH = 1000;
    private final double HEIGHT = 700;
    private final List<HomePageComponent> sections;

    public HomePage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // --- Load background images ---
        backgrounds.add(new Image(getClass().getResource("/com/example/try2/images/bg1.png").toExternalForm()));
        backgrounds.add(new Image(getClass().getResource("/com/example/try2/images/bg2.png").toExternalForm()));
        backgrounds.add(new Image(getClass().getResource("/com/example/try2/images/bg3.png").toExternalForm()));
        backgrounds.add(new Image(getClass().getResource("/com/example/try2/images/bg4.png").toExternalForm()));

        bgView = new ImageView(backgrounds.get(0));
        bgView.setPreserveRatio(true);
        bgView.setSmooth(true);
        bgView.setCache(true);
        bgView.setFitWidth(WIDTH);
        bgView.setFitHeight(HEIGHT);

        // --- Navigation buttons ---
        Button homeBtn = new Button("HOME");
        Button menuBtn = new Button("MENU");
        Button fundBtn = new Button("FUND");
        Button callNowBtn = new Button("CALL NOW");
        Button loginBtn = new Button("Log In");

        // --- HAMBURGER ICON BUTTON (IMAGE ONLY, NO ACTION) ---
        Image hamburgerImg = new Image(getClass().getResource("/com/example/try2/images/hamburger.png").toExternalForm());
        ImageView hamburgerIcon = new ImageView(hamburgerImg);
        hamburgerIcon.setFitWidth(24);
        hamburgerIcon.setFitHeight(24);
        hamburgerIcon.setPreserveRatio(true);

        Button menuIconBtn = new Button();
        menuIconBtn.setGraphic(hamburgerIcon);
        menuIconBtn.setPrefSize(40, 40);
        menuIconBtn.setMinSize(40, 40);
        menuIconBtn.setMaxSize(40, 40);
        menuIconBtn.getStyleClass().addAll("top-button", "menu-icon-button");
        menuIconBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        // No action — just visual

        // --- Apply shared style class ---
        homeBtn.getStyleClass().add("top-button");
        menuBtn.getStyleClass().add("top-button");
        fundBtn.getStyleClass().add("top-button");
        callNowBtn.getStyleClass().add("top-button");
        loginBtn.getStyleClass().addAll("top-button", "login-button");

        // --- Layout setup ---
        HBox leftButtons = new HBox(20, homeBtn, menuBtn, fundBtn, callNowBtn);
        leftButtons.setAlignment(Pos.CENTER_LEFT);
        HBox rightButtons = new HBox(10, loginBtn, menuIconBtn);  // Hamburger icon visible
        rightButtons.setAlignment(Pos.CENTER_RIGHT);

        BorderPane navBar = new BorderPane();
        navBar.setLeft(leftButtons);
        navBar.setRight(rightButtons);
        navBar.getStyleClass().add("home-nav");
        navBar.setPadding(new Insets(5, 20, 5, 20));
        navBar.setStyle("-fx-background-color: transparent;");

        // --- Content overlay ---
        BorderPane content = new BorderPane();
        content.setTop(navBar);
        content.setBackground(Background.EMPTY);

        // --- Root layout: Black background behind everything ---
        root = new StackPane();
        root.setPrefSize(WIDTH, HEIGHT);
        root.setStyle("-fx-background-color: black;");

        // Add layers: black -> bg image -> content
        root.getChildren().addAll(bgView, content);

        // Load CSS
        var css = getClass().getResource("/com/example/try2/style.css");
        if (css != null) root.getStylesheets().add(css.toExternalForm());

        // --- Button Actions ---
        loginBtn.setOnAction(e -> openLoginPage());
        menuBtn.setOnAction(e -> openMenu());           // MENU opens MenuPage
        callNowBtn.setOnAction(e -> System.out.println("Calling now..."));
        homeBtn.setOnAction(e -> System.out.println("Home button clicked"));
        fundBtn.setOnAction(e -> System.out.println("Fund button clicked"));
        // menuIconBtn has no action

        // Initialize sections list
        this.sections = Arrays.asList(
                this,
                new HomePageThirdExtension(),
                new HomePageFourthExtension(),
                new HomePageFifthExtension(),
                new HomePageSixthExtension(primaryStage),  // Pass stage for ENTER MENU
                new HomePageSeventhExtension(),
                new HomePageExtension(),
                new HomePageEighthExtension()
        );

        initialize();
    }

    @Override
    public Region getRoot() {
        return root;
    }

    @Override
    public double getPrefWidth() {
        return WIDTH;
    }

    @Override
    public double getPrefHeight() {
        return HEIGHT;
    }

    @Override
    public void initialize() {
        startZoomSlideshow();
    }

    public VBox getFullPage() {
        VBox fullPage = new VBox();
        for (HomePageComponent section : sections) {
            Region root = section.getRoot();
            root.setPrefSize(section.getPrefWidth(), section.getPrefHeight());
            root.setMinSize(section.getPrefWidth(), section.getPrefHeight());
            fullPage.getChildren().add(root);
        }
        return fullPage;
    }

    // --- OPEN LOGIN PAGE ---
    private void openLoginPage() {
        LoginPage loginPage = new LoginPage(primaryStage);
        Scene loginScene = loginPage.getLoginScene();
        primaryStage.setScene(loginScene);
    }

    // --- OPEN MENU PAGE ---
    private void openMenu() {
        MenuPage menuPage = new MenuPage(primaryStage);
        primaryStage.setScene(menuPage.getMenuScene());
    }

    // --- ZOOM + FADE SLIDESHOW: 1.5s zoom → 5s hold → next ---
    private void startZoomSlideshow() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(6.5), e -> zoomToNextImage())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        zoomToNextImage(); // Start first transition
    }

    private void zoomToNextImage() {
        int nextIndex = (currentBgIndex + 1) % backgrounds.size();
        Image nextImage = backgrounds.get(nextIndex);

        ImageView nextImageView = new ImageView(nextImage);
        nextImageView.setPreserveRatio(true);
        nextImageView.setFitWidth(WIDTH);
        nextImageView.setFitHeight(HEIGHT);
        nextImageView.setOpacity(0.0);
        nextImageView.setScaleX(0.85);
        nextImageView.setScaleY(0.85);

        // Insert between bgView and content
        root.getChildren().add(root.getChildren().indexOf(bgView) + 1, nextImageView);

        ParallelTransition transition = new ParallelTransition(
                new FadeTransition(Duration.seconds(1.5), nextImageView),
                new ScaleTransition(Duration.seconds(1.5), nextImageView)
        );

        ((FadeTransition) transition.getChildren().get(0)).setFromValue(0.0);
        ((FadeTransition) transition.getChildren().get(0)).setToValue(1.0);

        ((ScaleTransition) transition.getChildren().get(1)).setFromX(0.9);
        ((ScaleTransition) transition.getChildren().get(1)).setFromY(0.9);
        ((ScaleTransition) transition.getChildren().get(1)).setToX(1.1);
        ((ScaleTransition) transition.getChildren().get(1)).setToY(1.1);

        transition.setOnFinished(e -> {
            bgView.setImage(nextImage);
            root.getChildren().remove(nextImageView);
            currentBgIndex = nextIndex;
        });

        transition.play();
    }
}