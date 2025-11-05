package com.example.try2;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoginPage {
    private final Stage primaryStage;
    private final Random random = new Random();

    // UI Components
    private VBox loginPane;
    private VBox registerPane;

    public LoginPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getLoginScene() {
        BorderPane root = new BorderPane();
        root.setPrefSize(1000, 700);

        // --- Load CSS ---
        root.getStylesheets().add(getClass().getResource("/com/example/try2/style.css").toExternalForm());

        // --- Background Gradient ---
        root.setStyle("""
            -fx-background-color: linear-gradient(to right, #0f2027, #203a43, #2c5364);
        """);

        // --- Transparent Close Button ---
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setStyle("-fx-background-color: transparent;");

        Button closeBtn = new Button("X");
        closeBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-cursor: hand;
            -fx-font-weight: bold;
        """);
        closeBtn.setOnAction(e -> returnToHome());
        topBar.getChildren().add(closeBtn);
        root.setTop(topBar);

        // --- LEFT PANE: 60% (Dynamic) ---
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #a8e6cf, #dcedc1);");

        // --- Doodle Pane ---
        Pane doodlePane = new Pane();
        doodlePane.setMinSize(0, 0);
        leftPane.getChildren().add(doodlePane);
        VBox.setVgrow(doodlePane, Priority.ALWAYS);

        // --- RIGHT PANE: 40% (Dynamic) ---
        StackPane rightContainer = new StackPane();
        rightContainer.setAlignment(Pos.CENTER);
        rightContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #283c86, #45a247);");

        // --- LOGIN PANE (Default) ---
        loginPane = createLoginPane();
        loginPane.setVisible(true);

        // --- REGISTER PANE (Hidden initially) ---
        registerPane = createRegisterPane();
        registerPane.setVisible(false);
        registerPane.setOpacity(0);

        rightContainer.getChildren().addAll(loginPane, registerPane);

        // --- Divider ---
        Pane divider = new Pane();
        divider.setPrefWidth(2);
        divider.setStyle("-fx-background-color: rgba(255,255,255,0.3);");

        // --- MAIN LAYOUT: DYNAMIC 60%-40% ---
        HBox mainLayout = new HBox();
        mainLayout.getChildren().addAll(leftPane, divider, rightContainer);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightContainer, Priority.ALWAYS);

        mainLayout.widthProperty().addListener((obs, old, newVal) -> {
            if (newVal.doubleValue() > 0) {
                double total = newVal.doubleValue();
                leftPane.setPrefWidth(total * 0.6);
                rightContainer.setPrefWidth(total * 0.4);
            }
        });

        root.setCenter(mainLayout);

        // --- DOODLES: Follow Mouse ---
        setupDoodles(doodlePane, leftPane);

        return new Scene(root, 1000, 700);
    }

    // --- Create Login Pane ---
    private VBox createLoginPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(40));

        Label brandTitle = new Label("WELCOME TO MUNCHOAK");
        brandTitle.setStyle("""
            -fx-font-size: 26px;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 2);
        """);

        VBox buttonsVBox = new VBox(7);
        buttonsVBox.setAlignment(Pos.CENTER);

        Button adminBtn = createLoginButton("ADMIN");
        Button userBtn = createLoginButton("USER");
        Button registerBtn = createLoginButton("REGISTER");
        registerBtn.setOnAction(e -> showRegisterForm());
        Button guestBtn = createLoginButton("GUEST");

        buttonsVBox.getChildren().addAll(adminBtn, userBtn, registerBtn, guestBtn);
        pane.getChildren().addAll(brandTitle, buttonsVBox);
        return pane;
    }

    // --- Create Register Pane ---
    private VBox createRegisterPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(30, 40, 30, 40));
        pane.setMaxWidth(300);

        Label title = new Label("Create Account");
        title.setStyle("""
            -fx-font-size: 28px;
            -fx-text-fill: #2c3e50;
            -fx-font-weight: bold;
        """);

        // --- Text Fields ---
        TextField emailField = createStyledTextField("Email");
        PasswordField passwordField = createStyledPasswordField("Password");
        PasswordField confirmField = createStyledPasswordField("Confirm Password");

        // --- Buttons ---
        Button registerBtn = new Button("Register");
        registerBtn.setStyle("""
            -fx-background-color: #27ae60;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 16px;
            -fx-padding: 10 30;
            -fx-background-radius: 25;
            -fx-cursor: hand;
        """);
        registerBtn.setOnAction(e -> {
            System.out.println("Register clicked");
            // Add registration logic here
        });

        Button backBtn = new Button("Back");
        backBtn.setStyle("""
            -fx-background-color: #16a085;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 14px;
            -fx-padding: 8 25;
            -fx-background-radius: 20;
            -fx-cursor: hand;
            -fx-margin-top: 10;
        """);
        backBtn.setOnAction(e -> showLoginForm());

        VBox buttonBox = new VBox(10, registerBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // --- Login Link ---
        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setStyle("-fx-text-fill: #3498db; -fx-font-size: 13px;");
        loginLink.setOnAction(e -> showLoginForm());

        pane.getChildren().addAll(title, emailField, passwordField, confirmField, buttonBox, loginLink);
        return pane;
    }

    // --- Show Register Form ---
    private void showRegisterForm() {
        loginPane.setVisible(false);
        registerPane.setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), registerPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    // --- Show Login Form ---
    private void showLoginForm() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), registerPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            registerPane.setVisible(false);
            loginPane.setVisible(true);
        });
        fadeOut.play();
    }

    // --- Helper: Styled Text Field ---
    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("""
            -fx-background-color: white;
            -fx-text-fill: #2c3e50;
            -fx-font-size: 14px;
            -fx-padding: 12;
            -fx-background-radius: 25;
            -fx-border-color: #27ae60;
            -fx-border-width: 2;
            -fx-border-radius: 25;
            -fx-pref-height: 45;
        """);
        field.setPrefWidth(280);
        return field;
    }

    // --- Helper: Styled Password Field ---
    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle("""
            -fx-background-color: white;
            -fx-text-fill: #2c3e50;
            -fx-font-size: 14px;
            -fx-padding: 12;
            -fx-background-radius: 25;
            -fx-border-color: #27ae60;
            -fx-border-width: 2;
            -fx-border-radius: 25;
            -fx-pref-height: 45;
        """);
        field.setPrefWidth(280);
        return field;
    }

    // --- Helper: Login Button ---
    private Button createLoginButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("top-button");
        btn.setPrefWidth(110);
        btn.setOnAction(e -> System.out.println(text + " clicked"));
        return btn;
    }

    // --- Doodle Animation ---
    private void setupDoodles(Pane doodlePane, VBox leftPane) {
        List<Circle> doodles = new ArrayList<>();
        double[] speeds = {0.02, 0.03, 0.025, 0.015, 0.035};
        double[] targetX = new double[5];
        double[] targetY = new double[5];

        for (int i = 0; i < 5; i++) {
            double radius = 3 + random.nextDouble() * 7;
            Circle doodle = new Circle(radius, Color.rgb(255, 255, 255, 0.6 + random.nextDouble() * 0.4));
            doodle.setCenterX(100);
            doodle.setCenterY(100);
            doodlePane.getChildren().add(doodle);
            doodles.add(doodle);
        }

        leftPane.setOnMouseMoved(e -> {
            double mx = e.getX();
            double my = e.getY();
            for (int i = 0; i < 5; i++) {
                targetX[i] = mx;
                targetY[i] = my;
            }
        });

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
    }

    // --- Return to Home ---
    private void returnToHome() {
        HomePage homePage = new HomePage(primaryStage);
        VBox fullPage = homePage.getFullPage();
        ScrollPane scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");
        Scene homeScene = new Scene(scrollPane, 1000, 700);
        primaryStage.setScene(homeScene);
    }
}