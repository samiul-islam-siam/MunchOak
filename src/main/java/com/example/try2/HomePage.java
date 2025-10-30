package com.example.try2;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class HomePage {

    private final StackPane root;
    private final ImageView bgView;
    private final List<Image> backgrounds = new ArrayList<>();
    private int currentBgIndex = 0;
    private final Stage primaryStage;

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
        bgView.setFitWidth(1366);
        bgView.setFitHeight(768);

        // --- Navigation buttons ---
        Button homeBtn = new Button("HOME");
        Button menuBtn = new Button("MENU");
        Button fundBtn = new Button("FUND");
        Button callNowBtn = new Button("CALL NOW 📞");
        Button loginBtn = new Button("Log In");
        Button menuIconBtn = new Button("☰"); // Hamburger icon

        // --- Apply shared style class ---
        homeBtn.getStyleClass().add("top-button");
        menuBtn.getStyleClass().add("top-button");
        fundBtn.getStyleClass().add("top-button");
        callNowBtn.getStyleClass().add("top-button");
        loginBtn.getStyleClass().addAll("top-button", "login-button");
        menuIconBtn.getStyleClass().addAll("top-button", "menu-icon-button");

        // --- Square shape for hamburger button ---
        menuIconBtn.setPrefSize(40, 40);
        menuIconBtn.setMinSize(40, 40);
        menuIconBtn.setMaxSize(40, 40);
        menuIconBtn.setFont(javafx.scene.text.Font.font(18));

        // --- Layout setup ---
        HBox leftButtons = new HBox(20, homeBtn, menuBtn, fundBtn, callNowBtn);
        leftButtons.setAlignment(Pos.CENTER_LEFT);

        HBox rightButtons = new HBox(10, loginBtn, menuIconBtn);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);

        BorderPane navBar = new BorderPane();
        navBar.setLeft(leftButtons);
        navBar.setRight(rightButtons);
        navBar.setPadding(new Insets(5, 20, 5, 20));
        navBar.setStyle("-fx-background-color: transparent;");

        // --- Content overlay ---
        BorderPane content = new BorderPane();
        content.setTop(navBar);
        content.setBackground(new Background(
                new BackgroundFill(Color.color(0, 0, 0, 0.0), CornerRadii.EMPTY, Insets.EMPTY)
        ));

        // --- Root layout ---
        root = new StackPane(bgView, content);
        root.setPrefSize(1366, 768);

        // Load CSS
        var css = getClass().getResource("/com/example/try2/style.css");
        if (css != null) root.getStylesheets().add(css.toExternalForm());

        // Start background slideshow (wipe transition)
        startBackgroundSlideshow();

        // --- Button Actions ---
        loginBtn.setOnAction(e -> openLoginPage());
        menuIconBtn.setOnAction(e -> openMenu());
        callNowBtn.setOnAction(e -> System.out.println("Calling now..."));
        homeBtn.setOnAction(e -> System.out.println("Home button clicked"));
        menuBtn.setOnAction(e -> System.out.println("Menu button clicked"));
        fundBtn.setOnAction(e -> System.out.println("Fund button clicked"));
    }

    public StackPane getRoot() {
        return root;
    }

    public VBox getFullPage() {
        HomePageExtension extension1 = new HomePageExtension();
        HomePageSecondExtension extension2 = new HomePageSecondExtension();
        HomePageThirdExtension extension3 = new HomePageThirdExtension();
        HomePageFourthExtension extension4 = new HomePageFourthExtension();
        HomePageFifthExtension extension5 = new HomePageFifthExtension();    // NEW
        HomePageSixthExtension extension6 = new HomePageSixthExtension();    // NEW

        VBox fullPage = new VBox();
        fullPage.getChildren().addAll(
                root,
                extension1.getExtensionRoot(),
                extension2.getExtensionRoot(),
                extension3.getExtensionRoot(),
                extension4.getExtensionRoot(),
                extension5.getExtensionRoot(),  // appended
                extension6.getExtensionRoot()   // appended
        );
        return fullPage;
    }



    // --- Login page ---
    private void openLoginPage() {
        LoginPage loginPage = new LoginPage(primaryStage);
        Scene loginScene = loginPage.getLoginScene();
        primaryStage.setScene(loginScene);
    }

    // --- Menu placeholder ---
    private void openMenu() {
        System.out.println("☰ Menu button clicked — future logic goes here.");
    }

    // --- Background slideshow with wipe effect ---
    private void startBackgroundSlideshow() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), e -> wipeToNextImage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void wipeToNextImage() {
        int nextIndex = (currentBgIndex + 1) % backgrounds.size();

        ImageView nextImageView = new ImageView(backgrounds.get(nextIndex));
        nextImageView.setPreserveRatio(true);
        nextImageView.setFitWidth(1366);
        nextImageView.setFitHeight(768);

        root.getChildren().add(nextImageView);

        Rectangle clip = new Rectangle(0, 768);
        nextImageView.setClip(clip);

        Timeline wipeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(clip.widthProperty(), 0)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(clip.widthProperty(), 1366))
        );

        wipeTimeline.setOnFinished(e -> {
            bgView.setImage(backgrounds.get(nextIndex));
            root.getChildren().remove(nextImageView);
            currentBgIndex = nextIndex;
        });

        wipeTimeline.play();
    }
}
