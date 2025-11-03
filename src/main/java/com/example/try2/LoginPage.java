package com.example.try2;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoginPage {

    private final Stage primaryStage;

    public LoginPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getLoginScene() {
        BorderPane root = new BorderPane();
        root.setPrefSize(1366, 768);

        // --- Load CSS ---
        root.getStylesheets().add(getClass().getResource("/com/example/try2/style.css").toExternalForm());

        // --- Background Gradient ---
        root.setStyle("""
            -fx-background-color: linear-gradient(to right, #0f2027, #203a43, #2c5364);
        """);

        // --- Transparent Navigation Bar ---
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setStyle("-fx-background-color: transparent;"); // Transparent and colorless

        Button closeBtn = new Button("❌");
        closeBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-cursor: hand;
        """);
        closeBtn.setOnAction(e -> returnToHome());
        topBar.getChildren().add(closeBtn);
        root.setTop(topBar);

        // --- Left section (60%) - Lighter, less green ---
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(40));
        leftPane.setSpacing(20);
        leftPane.setPrefWidth(1366 * 0.6);
        leftPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #a8e6cf, #dcedc1);"); // Lighter pastel colors

        // --- Doodle Pane for moving doodles ---
        Pane doodlePane = new Pane();
        doodlePane.setPrefSize(1366 * 0.6, 768);
        leftPane.getChildren().add(doodlePane);

        // --- Create doodles (small circles) ---
        List<Circle> doodles = new ArrayList<>();
        Random random = new Random();
        double[] speeds = {0.02, 0.03, 0.025, 0.015, 0.035}; // Different lag speeds
        double[] targetX = new double[5];
        double[] targetY = new double[5];

        for (int i = 0; i < 5; i++) {
            double radius = 3 + random.nextDouble() * 7; // Random size 3-10
            Circle doodle = new Circle(radius, Color.rgb(255, 255, 255, 0.6 + random.nextDouble() * 0.4)); // Semi-transparent white
            doodle.setCenterX(0);
            doodle.setCenterY(0);
            doodlePane.getChildren().add(doodle);
            doodles.add(doodle);
        }

        // --- Mouse listener for left pane ---
        leftPane.setOnMouseMoved(e -> {
            double mx = e.getX();
            double my = e.getY();
            for (int i = 0; i < 5; i++) {
                targetX[i] = mx;
                targetY[i] = my;
            }
        });

        // --- Animation for doodles following mouse ---
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < 5; i++) {
                    Circle doodle = doodles.get(i);
                    double dx = targetX[i] - doodle.getCenterX();
                    double dy = targetY[i] - doodle.getCenterY();
                    doodle.setCenterX(doodle.getCenterX() + dx * speeds[i]);
                    doodle.setCenterY(doodle.getCenterY() + dy * speeds[i]);
                }
            }
        };
        timer.start();

        // --- Divider Line ---
        Pane divider = new Pane();
        divider.setPrefWidth(2);
        divider.setStyle("-fx-background-color: rgba(255,255,255,0.3);");

        // --- Right section (40%) - Original gradient ---
        VBox rightPane = new VBox(20);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(40));
        rightPane.setPrefWidth(1366 * 0.4);
        rightPane.setStyle("""
            -fx-background-color: linear-gradient(to bottom right, #283c86, #45a247);
        """); // Keep as before

        Label brandTitle = new Label("WELCOME TO MUNCHOAK");
        brandTitle.setStyle("""
            -fx-font-size: 36px;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 2);
        """);

        // --- 4 Buttons: ADMIN, USER, REGISTER, GUEST (Vertical, equal size) ---
        VBox buttonsVBox = new VBox(10);
        buttonsVBox.setAlignment(Pos.CENTER);

        Button adminBtn = new Button("ADMIN");
        adminBtn.getStyleClass().add("top-button");
        adminBtn.setPrefWidth(150); // Equal width
        adminBtn.setOnAction(e -> {
            System.out.println("ADMIN clicked");
            // Navigate to Admin page
        });

        Button userBtn = new Button("USER");
        userBtn.getStyleClass().add("top-button");
        userBtn.setPrefWidth(150); // Equal width
        userBtn.setOnAction(e -> {
            System.out.println("USER clicked");
            // Proceed with user login
        });

        Button registerBtn = new Button("REGISTER");
        registerBtn.getStyleClass().add("top-button");
        registerBtn.setPrefWidth(150); // Equal width
        registerBtn.setOnAction(e -> {
            System.out.println("REGISTER clicked");
            // Navigate to Register page
        });

        Button guestBtn = new Button("GUEST");
        guestBtn.getStyleClass().add("top-button");
        guestBtn.setPrefWidth(150); // Equal width
        guestBtn.setOnAction(e -> {
            System.out.println("GUEST clicked");
            // Proceed as guest
        });

        buttonsVBox.getChildren().addAll(adminBtn, userBtn, registerBtn, guestBtn);

        rightPane.getChildren().addAll(brandTitle, buttonsVBox);

        // --- Combine both halves ---
        HBox mainLayout = new HBox(leftPane, divider, rightPane);
        root.setCenter(mainLayout);

        return new Scene(root, 1366, 768);
    }

    private void returnToHome() {
        HomePage homePage = new HomePage(primaryStage);
        VBox fullPage = homePage.getFullPage();

        ScrollPane scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene homeScene = new Scene(scrollPane, 1366, 768);
        primaryStage.setScene(homeScene);
    }
}