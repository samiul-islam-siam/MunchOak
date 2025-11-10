package com.example.view;

import javafx.animation.*;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.application.Application; // For fallback stylesheet

public class HomePage implements HomePageComponent {
    private final StackPane root;
    private final ImageView bgView;
    private final List<Image> backgrounds = new ArrayList<>();
    private int currentBgIndex = 0;
    private final Stage primaryStage;
    private final double WIDTH = 1000;
    private final double HEIGHT = 700;
    private final List<HomePageComponent> sections;

    // --- Side Panel Dashboard ---
    private VBox sidePanel;
    private Pane overlay;
    private boolean panelOpen = false;
    private boolean loggedIn = false;

    // References to background layers
    private BorderPane content;

    // ScrollPane reference for reset
    private ScrollPane scrollPane;

    public HomePage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // --- Load background images ---
        backgrounds.add(new Image(getClass().getResource("/com/example/view/images/bg1.png").toExternalForm()));
        backgrounds.add(new Image(getClass().getResource("/com/example/view/images/bg2.png").toExternalForm()));
        backgrounds.add(new Image(getClass().getResource("/com/example/view/images/bg3.png").toExternalForm()));
        backgrounds.add(new Image(getClass().getResource("/com/example/view/images/bg4.png").toExternalForm()));

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

        // --- HAMBURGER ICON ---
        Image hamburgerImg = new Image(getClass().getResource("/com/example/view/images/hamburger.png").toExternalForm());
        ImageView hamburgerIcon = new ImageView(hamburgerImg);
        hamburgerIcon.setFitWidth(24);
        hamburgerIcon.setFitHeight(24);
        hamburgerIcon.setPreserveRatio(true);

        Button menuIconBtn = new Button();
        menuIconBtn.setGraphic(hamburgerIcon);
        menuIconBtn.setPrefSize(40, 40);
        menuIconBtn.getStyleClass().addAll("top-button", "menu-icon-button");
        menuIconBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        // --- Style buttons ---
        homeBtn.getStyleClass().add("top-button");
        menuBtn.getStyleClass().add("top-button");
        fundBtn.getStyleClass().add("top-button");
        callNowBtn.getStyleClass().add("top-button");
        loginBtn.getStyleClass().addAll("top-button", "login-button");

        // --- Nav Bar ---
        HBox leftButtons = new HBox(20, homeBtn, menuBtn, fundBtn, callNowBtn);
        leftButtons.setAlignment(Pos.CENTER_LEFT);

        HBox rightButtons = new HBox(10, loginBtn, menuIconBtn);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);

        BorderPane navBar = new BorderPane();
        navBar.setLeft(leftButtons);
        navBar.setRight(rightButtons);
        navBar.getStyleClass().add("home-nav");
        navBar.setPadding(new Insets(5, 20, 5, 20));
        navBar.setStyle("-fx-background-color: transparent;");

        // --- Content overlay ---
        content = new BorderPane();
        content.setTop(navBar);
        content.setBackground(Background.EMPTY);

        // --- Root StackPane ---
        root = new StackPane();
        root.setPrefSize(WIDTH, HEIGHT);
        root.setStyle("-fx-background-color: black;");

        // --- Button Actions ---
        loginBtn.setOnAction(e -> openLoginPageDirectly());
        callNowBtn.setOnAction(e -> System.out.println("Calling now..."));
        homeBtn.setOnAction(e -> System.out.println("Home button clicked"));
        fundBtn.setOnAction(e -> System.out.println("Fund button clicked"));
        menuIconBtn.setOnAction(e -> toggleSidePanel());

        // --- Build UI ---
        createOverlay();
        createSidePanel();
        root.getChildren().setAll(bgView, content, overlay, sidePanel);

        // --- Initialize Sections ---
        this.sections = Arrays.asList(
                this,
                new HomePageThirdExtension(),
                new HomePageFourthExtension(),
                new HomePageFifthExtension(),
                new HomePageSixthExtension(primaryStage),
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
        fullPage.setStyle("-fx-background-color: transparent;");
        for (HomePageComponent section : sections) {
            Region sectionRoot = section.getRoot();
            sectionRoot.setPrefSize(section.getPrefWidth(), section.getPrefHeight());
            sectionRoot.setMinSize(section.getPrefWidth(), section.getPrefHeight());
            fullPage.getChildren().add(sectionRoot);
            if (section != this) {
                section.initialize();
            }
        }
        return fullPage;
    }

    private void openLoginPageDirectly() {
        Platform.runLater(() -> {
            LoginPage loginPage = new LoginPage(primaryStage);
            Scene loginScene = loginPage.getLoginScene();

            double w = primaryStage.getWidth();
            double h = primaryStage.getHeight();
            boolean fs = primaryStage.isFullScreen();
            boolean max = primaryStage.isMaximized();

            primaryStage.setScene(loginScene);
            primaryStage.setWidth(w);
            primaryStage.setHeight(h);

            if (fs) primaryStage.setFullScreen(true);
            else if (max) primaryStage.setMaximized(true);

            primaryStage.centerOnScreen();
        });
    }

    private void openReservationPageDirectly() {
        Platform.runLater(() -> {
            ReservationPage reservationPage = new ReservationPage(primaryStage);
            Scene reservationScene = reservationPage.getReservationScene();

            double w = primaryStage.getWidth();
            double h = primaryStage.getHeight();
            boolean fs = primaryStage.isFullScreen();
            boolean max = primaryStage.isMaximized();

            primaryStage.setScene(reservationScene);
            primaryStage.setWidth(w);
            primaryStage.setHeight(h);

            if (fs) primaryStage.setFullScreen(true);
            else if (max) primaryStage.setMaximized(true);

            primaryStage.centerOnScreen();
        });
    }

    private void openAboutUsPageDirectly() {
        Platform.runLater(() -> {
            AboutUsPage aboutUsPage = new AboutUsPage(primaryStage);
            Scene aboutUsScene = aboutUsPage.getAboutUsScene();

            double w = primaryStage.getWidth();
            double h = primaryStage.getHeight();
            boolean fs = primaryStage.isFullScreen();
            boolean max = primaryStage.isMaximized();

            primaryStage.setScene(aboutUsScene);
            primaryStage.setWidth(w);
            primaryStage.setHeight(h);

            if (fs) primaryStage.setFullScreen(true);
            else if (max) primaryStage.setMaximized(true);

            primaryStage.centerOnScreen();
        });
    }

    private void createOverlay() {
        overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.setVisible(false);
        overlay.setOnMouseClicked(e -> {
            if (panelOpen) {
                toggleSidePanel();
                e.consume();
            }
        });
        StackPane.setAlignment(overlay, Pos.TOP_LEFT);
    }

    private void createSidePanel() {
        Button closeBtn = new Button("X");
        closeBtn.getStyleClass().addAll("top-button", "login-button");
        closeBtn.setPrefSize(30, 30);
        closeBtn.setOnAction(e -> {
            e.consume();
            if (scrollPane != null) {
                scrollPane.setVvalue(0.0);
            }
            toggleSidePanel();
        });

        Button profileBtn = createSideButton("Profile");
        Button cartBtn = createSideButton("Cart");
        Button reserveBtn = createSideButton("Reservation");
        Button aboutBtn = createSideButton("About Us");

        reserveBtn.setOnAction(e -> {
            e.consume();
            if (scrollPane != null) {
                scrollPane.setVvalue(0.0);
            }
            toggleSidePanel();
            PauseTransition pause = new PauseTransition(Duration.millis(10));
            pause.setOnFinished(evt -> openReservationPageDirectly());
            pause.play();
        });

        aboutBtn.setOnAction(e -> {
            e.consume();
            if (scrollPane != null) {
                scrollPane.setVvalue(0.0);
            }
            toggleSidePanel();
            PauseTransition pause = new PauseTransition(Duration.millis(10));
            pause.setOnFinished(evt -> openAboutUsPageDirectly());
            pause.play();
        });

        Button authBtn = new Button(loggedIn ? "Log Out" : "Log In");
        authBtn.setPrefWidth(Double.MAX_VALUE);
        authBtn.getStyleClass().add("top-button");
        if (!loggedIn) authBtn.getStyleClass().add("login-button");

        authBtn.setOnAction(e -> {
            e.consume();
            if (scrollPane != null) {
                scrollPane.setVvalue(0.0);
            }
            if (loggedIn) {
                loggedIn = false;
                authBtn.setText("Log In");
                authBtn.getStyleClass().remove("login-button");
                System.out.println("Logged out");
                return;
            }

            toggleSidePanel();
            PauseTransition pause = new PauseTransition(Duration.millis(10));
            pause.setOnFinished(evt -> openLoginPageDirectly());
            pause.play();
        });

        VBox items = new VBox(18, profileBtn, cartBtn, reserveBtn, aboutBtn, authBtn);
        items.setAlignment(Pos.CENTER_LEFT);
        items.setFillWidth(true);

        HBox closeContainer = new HBox(closeBtn);
        closeContainer.setAlignment(Pos.TOP_RIGHT);
        closeContainer.setPrefHeight(30);

        VBox panelContent = new VBox(25, closeContainer, items);
        panelContent.setPadding(new Insets(30, 25, 30, 25));
        panelContent.setAlignment(Pos.TOP_LEFT);
        panelContent.setPrefWidth(280);

        sidePanel = new VBox(panelContent);
        sidePanel.setPrefWidth(280);
        sidePanel.setMaxWidth(280);
        sidePanel.getStyleClass().add("side-panel");
        sidePanel.setTranslateX(280);
        sidePanel.setVisible(false);
        sidePanel.setFillWidth(true);
        StackPane.setAlignment(sidePanel, Pos.CENTER_RIGHT);
    }

    private Button createSideButton(String text) {
        Button b = new Button(text);
        b.setPrefWidth(Double.MAX_VALUE);
        b.getStyleClass().add("top-button");
        b.setOnAction(e -> {
            e.consume();
            if (scrollPane != null) {
                scrollPane.setVvalue(0.0);
            }
            System.out.println(text + " clicked in dashboard");
        });
        return b;
    }

    private void toggleSidePanel() {
        if (panelOpen) {
            slideOut();
        } else {
            slideIn();
        }
        panelOpen = !panelOpen;
    }

    private void slideIn() {
        sidePanel.setVisible(true);
        overlay.setVisible(true);
        sidePanel.setTranslateX(280);
        bgView.setMouseTransparent(true);
        content.setMouseTransparent(true);

        overlay.toFront();
        sidePanel.toFront();

        TranslateTransition tt = new TranslateTransition(Duration.millis(10), sidePanel);
        tt.setToX(0);
        tt.play();
    }

    private void slideOut() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(10), sidePanel);
        tt.setToX(280);
        tt.setOnFinished(e -> {
            sidePanel.setVisible(false);
            overlay.setVisible(false);
            bgView.setMouseTransparent(false);
            content.setMouseTransparent(false);
            if (scrollPane != null) {
                scrollPane.setVvalue(0.0);
            }
        });
        tt.play();
    }

    private void startZoomSlideshow() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(6.5), e -> zoomToNextImage())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        zoomToNextImage();
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

    // FIXED: Robust CSS load with clear/fallback - prevents default style reversion on scene switch
    // TODO: Once global CSS is set in Home.java via setUserAgentStylesheet, REMOVE this entire method block
    public Scene getHomeScene() {
        VBox fullPage = getFullPage();

        scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0));

        // Bind height for dynamic stage resize
        scrollPane.prefHeightProperty().bind(primaryStage.heightProperty());

        double stageWidth = primaryStage.getWidth() > 0 ? primaryStage.getWidth() : WIDTH;
        double stageHeight = primaryStage.getHeight() > 0 ? primaryStage.getHeight() : HEIGHT;
        Scene scene = new Scene(scrollPane, stageWidth, stageHeight);

        // FIXED: Clear any old styles to avoid conflicts, then add CSS with fallback
        scene.getStylesheets().clear();
        var css = getClass().getResource("/com/example/view/styles/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
            System.out.println("✅ CSS loaded for new HomeScene: " + css.toExternalForm()); // Check this log on return from login
        } else {
            System.err.println("❌ CSS not found in HomeScene - buttons will use defaults! Path: /com/example/view/styles/style.css");
            // Fallback to Modena (built-in default) - buttons get basic styles
            scene.getStylesheets().add(Application.STYLESHEET_MODENA);
            System.out.println("🔄 Fallback to Modena stylesheet applied.");
        }

        return scene;
    }
}