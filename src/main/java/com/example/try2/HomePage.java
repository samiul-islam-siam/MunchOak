package com.example.try2;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class HomePage {
    private final StackPane root;
    private final ImageView bgView;
    private final List<Image> backgrounds = new ArrayList<>();
    private int currentBgIndex = 0;

    public HomePage() {
        // Load background images
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

        // Buttons
        Button menuBtn = new Button("MENU");
        Button locationBtn = new Button("LOCATION");
        Button fundBtn = new Button("FUND");
        Button callNowBtn = new Button("CALL NOW 📞");
        Button loginBtn = new Button("Log In");
        Button signupBtn = new Button("Sign Up");

        menuBtn.getStyleClass().add("top-button");
        locationBtn.getStyleClass().add("top-button");
        fundBtn.getStyleClass().add("top-button");
        callNowBtn.getStyleClass().add("top-button");
        loginBtn.getStyleClass().addAll("top-button", "login-button");
        signupBtn.getStyleClass().addAll("top-button", "signup-button");

        // Left group of buttons (main navigation)
        HBox leftButtons = new HBox(20, menuBtn, locationBtn, fundBtn, callNowBtn);
        leftButtons.setAlignment(Pos.CENTER_LEFT);

        // Right group of buttons (Log In / Sign Up)
        HBox rightButtons = new HBox(10, loginBtn, signupBtn);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);

        // Navigation bar layout
        BorderPane navBar = new BorderPane();
        navBar.setLeft(leftButtons);
        navBar.setRight(rightButtons);
        navBar.setPadding(new Insets(5, 20, 5, 20));
        navBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        BorderPane content = new BorderPane();
        content.setTop(navBar);
        content.setBackground(new Background(
                new BackgroundFill(Color.color(0, 0, 0, 0.25), CornerRadii.EMPTY, Insets.EMPTY)
        ));

        root = new StackPane(bgView, content);
        root.setPrefSize(1366, 768);

        // Load CSS
        var css = getClass().getResource("/com/example/try2/style.css");
        if (css != null) root.getStylesheets().add(css.toExternalForm());

        startBackgroundSlideshow();

        // Button actions
        loginBtn.setOnAction(e -> System.out.println("Log In clicked"));
        signupBtn.setOnAction(e -> System.out.println("Sign Up clicked"));
        callNowBtn.setOnAction(e -> System.out.println("Calling now..."));
    }

    public StackPane getRoot() {
        return root;
    }

    public VBox getFullPage() {
        HomePageExtension extension = new HomePageExtension();
        VBox fullPage = new VBox();
        fullPage.getChildren().addAll(root, extension.getExtensionRoot());
        return fullPage;
    }

    private void startBackgroundSlideshow() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), e -> fadeToNextImage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void fadeToNextImage() {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), bgView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            currentBgIndex = (currentBgIndex + 1) % backgrounds.size();
            bgView.setImage(backgrounds.get(currentBgIndex));

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), bgView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }
}
