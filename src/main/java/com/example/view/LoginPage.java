package com.example.view;

import com.example.munchoak.Dashboard;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
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

    private VBox loginPane;
    private VBox registerPane;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmField;
    private Label statusLabel;

    private static final double NORMAL_WIDTH = 1000;
    private static final double NORMAL_HEIGHT = 700;

    private Scene loginScene;           // Only one scene ever
    private BorderPane root;            // Reusable root

    public LoginPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getLoginScene() {
        if (loginScene == null) {
            root = buildRoot();                    // Build layout once
            loginScene = new Scene(root, NORMAL_WIDTH, NORMAL_HEIGHT);
            loginScene.getStylesheets().add(getClass().getResource("/com/example/view/styles/style.css").toExternalForm());
            attachMaximizedListener();
        }
        return loginScene;
    }

    private BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to right, #0f2027, #203a43, #2c5364);");

        // --- Close Button ---
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setStyle("-fx-background-color: transparent;");

        Button closeBtn = new Button("X");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand; -fx-font-weight: bold;");
        closeBtn.setOnAction(e -> returnToHome());
        topBar.getChildren().add(closeBtn);
        root.setTop(topBar);

        // --- LEFT PANE ---
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #a8e6cf, #dcedc1);");

        Pane doodlePane = new Pane();
        doodlePane.setMinSize(0, 0);
        leftPane.getChildren().add(doodlePane);
        VBox.setVgrow(doodlePane, Priority.ALWAYS);

        // --- RIGHT PANE ---
        StackPane rightContainer = new StackPane();
        rightContainer.setAlignment(Pos.CENTER);
        rightContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #283c86, #45a247);");

        loginPane = createLoginPane();
        registerPane = createRegisterPane();
        registerPane.setVisible(false);
        registerPane.setOpacity(0);
        rightContainer.getChildren().addAll(loginPane, registerPane);

        // --- Divider ---
        Pane divider = new Pane();
        divider.setPrefWidth(2);
        divider.setStyle("-fx-background-color: rgba(255,255,255,0.3);");

        // --- MAIN LAYOUT ---
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
        setupDoodles(doodlePane, leftPane);
        return root;
    }

    // --- PANES ---
    private VBox createLoginPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(40));

        Label brandTitle = new Label("WELCOME TO MUNCHOAK");
        brandTitle.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 2);");

        VBox buttonsVBox = new VBox(7);
        buttonsVBox.setAlignment(Pos.CENTER);

//------Button Logics-------------------------------------//
        Button adminBtn = createLoginButton("ADMIN");

        Button userBtn = createLoginButton("USER");
        userBtn.setOnAction(e -> {
            try {
                javafx.fxml.FXMLLoader loader =
                        new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/login/FXMLs/login.fxml"));
                javafx.scene.Parent welcomeRoot = loader.load();

                javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(welcomeRoot));
                stage.setTitle("Welcome");
                stage.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button registerBtn = createLoginButton("REGISTER");
        registerBtn.setOnAction(e -> showRegisterForm());

        Button guestBtn = createLoginButton("GUEST");
        guestBtn.setOnAction(e -> {
            try {
                // Launch the main Dashboard as guest
                com.example.munchoak.Dashboard dashboard = new com.example.munchoak.Dashboard();
                Stage stage = new Stage();
                dashboard.start(stage);

                // Close the Login window
                Stage currentStage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
                currentStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        buttonsVBox.getChildren().addAll(adminBtn, userBtn, registerBtn, guestBtn);
        pane.getChildren().addAll(brandTitle, buttonsVBox);
        return pane;
    }

    private VBox createRegisterPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(30, 40, 30, 40));
        pane.setMaxWidth(300);

        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        emailField = createStyledTextField("Email");
        passwordField = createStyledPasswordField("Password");
        confirmField = createStyledPasswordField("Confirm Password");

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 30; -fx-background-radius: 25; -fx-cursor: hand;");
        registerBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String pwd = passwordField.getText();
            String conf = confirmField.getText();

            if (email.isEmpty() || pwd.isEmpty() || conf.isEmpty()) {
                showStatus("All fields are required!", true);
                return;
            }
            if (!pwd.equals(conf)) {
                showStatus("Passwords do not match!", true);
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                showStatus("Invalid email format!", true);
                return;
            }

            showStatus("Registration successful!", false);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> showLoginForm());
                        }
                    }, 1500);
        });

        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 25; -fx-background-radius: 20; -fx-cursor: hand;");
        backBtn.setOnAction(e -> showLoginForm());

        VBox buttonBox = new VBox(10, registerBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setStyle("-fx-text-fill: #3498db; -fx-font-size: 13px;");
        loginLink.setOnAction(e -> showLoginForm());

        statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        statusLabel.setMinHeight(20);

        pane.getChildren().addAll(title, emailField, passwordField, confirmField, statusLabel, buttonBox, loginLink);
        return pane;
    }

    private void showRegisterForm() {
        loginPane.setVisible(false);
        registerPane.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), registerPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void showLoginForm() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), registerPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            registerPane.setVisible(false);
            loginPane.setVisible(true);
            emailField.clear();
            passwordField.clear();
            confirmField.clear();
            statusLabel.setText("");
        });
        fadeOut.play();
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError
                ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;"
                : "-fx-text-fill: #2ecc71; -fx-font-weight: bold;");

        FadeTransition fade = new FadeTransition(Duration.millis(3000), statusLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> statusLabel.setText(""));
        fade.play();
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: white; -fx-text-fill: #2c3e50; -fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 25; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 25; -fx-pref-height: 45;");
        field.setPrefWidth(280);
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: white; -fx-text-fill: #2c3e50; -fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 25; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 25; -fx-pref-height: 45;");
        field.setPrefWidth(280);
        return field;
    }

    private Button createLoginButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("top-button");
        btn.setPrefWidth(110);
        btn.setOnAction(e -> System.out.println(text + " clicked"));
        return btn;
    }

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
                    Circle d = doodles.get(i);
                    double dx = targetX[i] - d.getCenterX();
                    double dy = targetY[i] - d.getCenterY();
                    d.setCenterX(d.getCenterX() + dx * speeds[i]);
                    d.setCenterY(d.getCenterY() + dy * speeds[i]);
                }
            }
        };
        timer.start();
    }

    private void returnToHome() {
        HomePage homePage = new HomePage(primaryStage);
        VBox fullPage = homePage.getFullPage();
        ScrollPane scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        double w = primaryStage.isMaximized() ? primaryStage.getWidth() : NORMAL_WIDTH;
        double h = primaryStage.isMaximized() ? primaryStage.getHeight() : NORMAL_HEIGHT;

        Scene homeScene = new Scene(scrollPane, w, h);
        primaryStage.setScene(homeScene);
    }

    // --- FIXED: Resize existing scene instead of replacing root ---
    private void attachMaximizedListener() {
        ChangeListener<Boolean> listener = (obs, wasMax, isNowMax) -> {
            if (loginScene == null) return;

            double width = isNowMax ? primaryStage.getWidth() : NORMAL_WIDTH;
            double height = isNowMax ? primaryStage.getHeight() : NORMAL_HEIGHT;

            // Resize the *existing* scene
            loginScene.getWindow().setWidth(width);
            loginScene.getWindow().setHeight(height);

            // Optional: force layout
            root.requestLayout();
        };

        primaryStage.maximizedProperty().addListener(listener);

        // Also react when the scene is first shown
        primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == loginScene) {
                listener.changed(primaryStage.maximizedProperty(),
                        primaryStage.isMaximized(), primaryStage.isMaximized());
            }
        });
    }
}
