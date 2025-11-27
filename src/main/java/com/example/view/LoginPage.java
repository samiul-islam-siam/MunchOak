package com.example.view;

import com.example.login.AdminDashboard;
import com.example.manager.AdminFileStorage;
import com.example.manager.FileStorage;
import com.example.manager.Session;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LoginPage {
    private final Stage primaryStage;
    private final Random random = new Random();

    private VBox loginPane;
    private VBox registerPane;
    private VBox userLoginPane;
    private VBox adminLoginPane;

    private TextField usernameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmField;
    private Label statusLabel;
    private Label registerStatusLabel;
    private Label registerStrengthLabel;
    private Label adminStatusLabel;
    private static final double NORMAL_WIDTH = 1000;
    private static final double NORMAL_HEIGHT = 700;

    private Scene loginScene; // Only one scene ever
    private BorderPane root; // Reusable root

    public LoginPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getLoginScene() {
        if (loginScene == null) {
            // Use current stage size if already in full screen/maximized, else normal
            double initWidth = primaryStage.isFullScreen() || primaryStage.isMaximized() ? Math.max(primaryStage.getWidth(), NORMAL_WIDTH) : NORMAL_WIDTH;
            double initHeight = primaryStage.isFullScreen() || primaryStage.isMaximized() ? Math.max(primaryStage.getHeight(), NORMAL_HEIGHT) : NORMAL_HEIGHT;

            root = buildRoot(); // Build layout once
            loginScene = new Scene(root, initWidth, initHeight);
            try {
                loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/view/styles/style.css")).toExternalForm());
            } catch (NullPointerException npe) {
                System.err.println("Exception: " + npe.getMessage());
            }
            attachResizeListeners(); // Renamed and fixed to handle full screen too
        }
        return loginScene;
    }

    private BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to right, #0f2027, #203a43, #2c5364);");

        // --- Top Bar with Logo and Title (Centered, No Navigation) ---
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER);

        // Logo and Title Section
        HBox titleSection = new HBox(10);
        titleSection.setAlignment(Pos.CENTER);

        // Load logo with updated path
        Image logoImage = new Image(getClass().getResourceAsStream("/com/example/view/images/logo.png"));
        ImageView logo = new ImageView(logoImage);
        logo.setFitWidth(40);
        logo.setFitHeight(40);
        Circle clip = new Circle(20, 20, 20);
        logo.setClip(clip);
        titleSection.getChildren().add(logo);

        Label title = new Label("MUNCHOAK");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleSection.getChildren().add(title);

        topBar.getChildren().add(titleSection);
        root.setTop(topBar);

        // --- RIGHT PANE (Full Center) ---
        StackPane rightContainer = new StackPane();
        rightContainer.setAlignment(Pos.CENTER);
        rightContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #283c86, #45a247);");

        loginPane = createLoginPane();
        registerPane = createRegisterPane();
        userLoginPane = createUserLoginPane();
        adminLoginPane = createAdminLoginPane();
        registerPane.setVisible(false);
        userLoginPane.setVisible(false);
        adminLoginPane.setVisible(false);
        registerPane.setOpacity(0);
        rightContainer.getChildren().addAll(loginPane, registerPane, userLoginPane, adminLoginPane);

        root.setCenter(rightContainer);
        return root;
    }

    // ------------------ PANES --------------------
    private VBox createLoginPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(40));

        // Subtitle at the top
        Label subtitle = new Label("Fresh flavors, just one login away.");
        subtitle.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-family: 'Comic Sans MS', cursive;");
        subtitle.setAlignment(Pos.CENTER);
        subtitle.setOpacity(0.0);

        // Transition effect for subtitle
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), subtitle);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setInterpolator(Interpolator.EASE_IN);
        fadeIn.play();

        Label brandTitle = new Label("WELCOME TO MUNCHOAK");
        brandTitle.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 2);");

        VBox buttonsVBox = new VBox(7);
        buttonsVBox.setAlignment(Pos.CENTER);

        //------Button Logics-------------------------------------//
        Button adminBtn = createLoginButton("ADMIN");
        adminBtn.setOnAction(e -> showAdminLoginForm());

        Button userBtn = createLoginButton("USER");
        userBtn.setOnAction(e -> showUserLoginForm());

        Button registerBtn = createLoginButton("REGISTER");
        registerBtn.setOnAction(e -> showRegisterForm());

        Button guestBtn = createLoginButton("GUEST");
        guestBtn.setOnAction(e -> {
            returnToHome();
        });

        Button backToHomeBtn = createLoginButton("BACK");
        backToHomeBtn.setOnAction(e -> {
            returnToHome();
        });

        buttonsVBox.getChildren().addAll(adminBtn, userBtn, registerBtn, guestBtn, backToHomeBtn);
        pane.getChildren().addAll(subtitle, brandTitle, buttonsVBox);
        return pane;
    }

    private StackPane createStyledPasswordFieldWithEye(String prompt, PasswordField[] pfOut, TextField[] tfOut) {
        double fieldWidth = 280;  // Whatever your fields use
        double fieldHeight = 45;

        // Create password and visible text fields, match their style
        PasswordField pwField = createStyledPasswordField(prompt); // Use your existing styled method!
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setStyle(pwField.getStyle()); // Ensures visual match
        textField.setVisible(false);
        textField.setManaged(false);

        pwField.setPrefWidth(fieldWidth);
        pwField.setPrefHeight(fieldHeight);
        textField.setPrefWidth(fieldWidth);
        textField.setPrefHeight(fieldHeight);

        // Keep text in sync
        pwField.textProperty().bindBidirectional(textField.textProperty());

        // The eye icon label
        Label eyeIcon = new Label("\uD83D\uDC41");
        eyeIcon.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-text-fill: #888;" +              // gray eye
                        "-fx-cursor: hand; " +
                        "-fx-background-color: transparent; " +
                        "-fx-label-padding: 0 10 0 0;"
        );
        // Overlay: stays within the field bounds
        StackPane.setAlignment(eyeIcon, Pos.CENTER_RIGHT);
        StackPane.setMargin(eyeIcon, new Insets(0, 16, 0, 0)); // Adjust right margin to match your field padding

        final boolean[] showing = {false};
        eyeIcon.setOnMouseClicked(event -> {
            showing[0] = !showing[0];
            if (showing[0]) {
                pwField.setVisible(false);
                pwField.setManaged(false);
                textField.setVisible(true);
                textField.setManaged(true);
            } else {
                textField.setVisible(false);
                textField.setManaged(false);
                pwField.setVisible(true);
                pwField.setManaged(true);
            }
        });

        // Arrange layers: field below, eye icon on top right inside box
        StackPane fieldPane = new StackPane(pwField, textField, eyeIcon);
        fieldPane.setPrefWidth(fieldWidth);
        fieldPane.setPrefHeight(fieldHeight);

        // Output for logic use:
        if (pfOut != null && pfOut.length > 0) pfOut[0] = pwField;
        if (tfOut != null && tfOut.length > 0) tfOut[0] = textField;

        return fieldPane;
    }

    private VBox createRegisterPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(30, 40, 30, 40));
        pane.setMaxWidth(300);

        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        usernameField = createStyledTextField("Username");
        emailField = createStyledTextField("Email");

        PasswordField[] pf1 = new PasswordField[1], pf2 = new PasswordField[1];
        TextField[] tf1 = new TextField[1], tf2 = new TextField[1];

        StackPane passwordFieldBox = createStyledPasswordFieldWithEye("Password", pf1, tf1);
        StackPane confirmFieldBox  = createStyledPasswordFieldWithEye("Confirm Password", pf2, tf2);

        passwordField = pf1[0];
        confirmField = pf2[0];

        // Password strength label
        registerStrengthLabel = new Label("Password Strength: ");
        registerStrengthLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        registerStrengthLabel.setMinHeight(20);

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 30; -fx-background-radius: 25; -fx-cursor: hand;");

        registerBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String pwd = passwordField.getText();
            String conf = confirmField.getText();

            String strength = getPasswordStrength(pwd);

            if (!"Strong".equals(strength)) {
                showRegisterStatus("Invalid password!", true);
                return;
            }
            if (username.isEmpty() || email.isEmpty() || pwd.isEmpty() || conf.isEmpty()) {
                showRegisterStatus("All fields are required!", true);
                return;
            }
            if (!pwd.equals(conf)) {
                showRegisterStatus("Passwords do not match!", true);
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                showRegisterStatus("Invalid email format!", true);
                return;
            }

            try {
                if (FileStorage.userExists(username)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Signup Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Username already exists!");
                    alert.showAndWait();
                    return;
                }

                FileStorage.appendUser(username, email, pwd); // handles binary writing automatically

                showRegisterStatus("Registration successful!", false);

            } catch (IOException err) {
                System.err.println("IOException: " + err.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while registering the user.");
                alert.showAndWait();
            }

            confirmField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    registerBtn.fire();   // Trigger Register
                }
            });

            new java.util.Timer().schedule(new java.util.TimerTask() {
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

        registerStatusLabel = new Label("");
        registerStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        registerStatusLabel.setMinHeight(20);

        // Listen for changes in password field
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            String strength = getPasswordStrength(newVal);
            updateStrengthLabel(strength);
        });

        pane.getChildren().addAll(title, usernameField, emailField, passwordFieldBox, confirmFieldBox,registerStrengthLabel, registerStatusLabel, buttonBox, loginLink);
        return pane;
    }

    private void updateStrengthLabel(String strength) {
        // You can color and text per state, using switch statement (Java 17+ switch expression)
        switch (strength) {
            case "Invalid" -> {
                registerStrengthLabel.setText("Password Strength: Invalid");
                registerStrengthLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
            }
            case "Weak" -> {
                registerStrengthLabel.setText("Password Strength: Weak");
                registerStrengthLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
            }
            case "Normal" -> {
                registerStrengthLabel.setText("Password Strength: Normal");
                registerStrengthLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
            }
            case "Strong" -> {
                registerStrengthLabel.setText("Password Strength: Strong");
                registerStrengthLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
            }
        }
    }

    private void showRegisterStatus(String message, boolean isError) {
        registerStatusLabel.setText(message);
        registerStatusLabel.setStyle(isError ? "-fx-text-fill:black; -fx-font-weight: bold;"
                : "-fx-text-fill: black; -fx-font-weight: bold;");

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(ev -> registerStatusLabel.setText(""));
        pause.play();

    }
    private VBox createUserLoginPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(30));
        pane.setMaxWidth(300);

        Label title = new Label("User Login");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        // --- Input Fields ---
        TextField usernameField = createStyledTextField("Username");
        PasswordField passwordField = createStyledPasswordField("Password");

        // --- Buttons ---
        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 10 30;");
        loginBtn.setOnAction(e -> {
            try {
                if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                    showStatus("All fields required!", true);
                    return;
                }
                if (!FileStorage.userExists(usernameField.getText())) {
                    showStatus("User not found!", true);
                    return;
                }
                if (!FileStorage.verifyUserPassword(usernameField.getText(), passwordField.getText())) {
                    showStatus("Incorrect password!", true);
                    return;
                }
                Session.setCurrentUser(usernameField.getText());
                Session.setCurrentUser(usernameField.getText());
                showStatus("Login successful!", false);
                returnToHome();
            } catch (Exception ex) {
                System.err.println("IOException: " + ex.getMessage());
                showStatus("Error logging in!", true);
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginBtn.fire();   // Trigger Login
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-background-radius: 25;");
        backBtn.setOnAction(e -> switchPane(userLoginPane, loginPane));

        Hyperlink forgotLink = new Hyperlink("Forgot Password?");
        forgotLink.setStyle("-fx-text-fill: #3498db; -fx-font-size: 13px;");
        forgotLink.setOnAction(e -> showUserForgotPasswordPopup(primaryStage));

        VBox btnBox = new VBox(10, loginBtn, backBtn, forgotLink);
        btnBox.setAlignment(Pos.CENTER);

        statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        statusLabel.setMinHeight(20);

        pane.getChildren().addAll(title, usernameField, passwordField, statusLabel, btnBox);

        return pane;
    }

    private VBox createAdminLoginPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(30));
        pane.setMaxWidth(300);
        adminStatusLabel = new Label("");
        adminStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        adminStatusLabel.setMinHeight(20);
        Label title = new Label("Admin Login");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        TextField adminIDField = createStyledTextField("Enter Admin ID");
        PasswordField adminPasswordField = createStyledPasswordField("Enter Admin Password");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 10 30;");

        loginBtn.setOnAction(e -> {
            String idEntered = adminIDField.getText().trim();
            String passEntered = adminPasswordField.getText();

            try {
                if (!AdminFileStorage.verifyAdminPassword(idEntered, passEntered)) {
                    showAdminStatus("Incorrect ID or password!", true); // <<< Use the right method!
                    return;
                }
                Session.setCurrentUser("admin");
                showAdminStatus("Admin login successful!", false);
                AdminDashboard dashboard = new AdminDashboard(primaryStage);
                dashboard.openAdminDashboard();
            } catch (IOException ex) {
                System.err.println("IOException: " + ex.getMessage());
                showAdminStatus("Error verifying password!", true);
            }
        });
        // ✅ Forgot Password Button (for admin only)
        Hyperlink forgotLink = new Hyperlink("Forgot Password?");
        forgotLink.setOnAction(e -> openForgotPasswordWindow(true)); // true = admin mode
        forgotLink.setStyle("-fx-text-fill: #3498db; -fx-font-size: 13px;");

        adminPasswordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginBtn.fire();   // Trigger Admin Login
            }
        });

        // ✅ Back Button
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 8 25;");
        backBtn.setOnAction(e -> switchPane(adminLoginPane, loginPane)); // Go back to main login

        VBox btnBox = new VBox(10, loginBtn, forgotLink, backBtn);
        btnBox.setAlignment(Pos.CENTER);

        pane.getChildren().addAll(title, adminIDField, adminPasswordField, adminStatusLabel,btnBox);
        return pane;
    }
    private void showAdminStatus(String message, boolean isError) {
        adminStatusLabel.setText(message);
        adminStatusLabel.setStyle(isError ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;"
                : "-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        FadeTransition fade = new FadeTransition(Duration.millis(3000), adminStatusLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> {
            adminStatusLabel.setText("");
            adminStatusLabel.setOpacity(1.0); // reset for future shows
        });
        fade.play();
    }

    // showing register form
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
            usernameField.clear();
            emailField.clear();
            passwordField.clear();
            confirmField.clear();
            statusLabel.setText("");
        });
        fadeOut.play();
    }

    private void showUserLoginForm() {
        switchPane(loginPane, userLoginPane);
    }

    private void showAdminLoginForm() {
        switchPane(loginPane, adminLoginPane);
    }

    private void switchPane(VBox from, VBox to) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), from);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            from.setVisible(false);
            to.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), to);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void showUserForgotPasswordPopup(Stage parentStage) {
        Stage popup = new Stage();
        popup.initOwner(parentStage);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Reset Password");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Gradient background
        Stop[] stops = new Stop[]{new Stop(0, Color.web("#36D1DC")), new Stop(1, Color.web("#5B86E5"))};
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        root.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(12), Insets.EMPTY)));

        Label title = new Label("Reset Your Password");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Username field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        // Password fields
        PasswordField newPass = new PasswordField();
        newPass.setPromptText("New Password (min 8 chars)");
        PasswordField confirmPass = new PasswordField();
        confirmPass.setPromptText("Confirm Password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold;");

        // Buttons
        Button saveBtn = new Button("Save");
        Button backBtn = new Button("Back");
        HBox btnBox = new HBox(15, saveBtn, backBtn);
        btnBox.setAlignment(Pos.CENTER);

        saveBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String np = newPass.getText().trim();
            String cp = confirmPass.getText().trim();

            if (username.isEmpty() || np.isEmpty() || cp.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }

            if (!FileStorage.userExists(username)) {
                errorLabel.setText("User not found!");
                return;
            }

            if (np.length() < 8) {
                errorLabel.setText("Password must be at least 8 characters.");
                return;
            }

            if (!np.equals(cp)) {
                errorLabel.setText("Passwords do not match!");
                return;
            }

            try {
                FileStorage.updateUserPassword(username, np);
                errorLabel.setStyle("-fx-text-fill: lightgreen;");
                errorLabel.setText("Password updated successfully!");
            } catch (Exception ex) {
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText("Error updating password.");
                ex.printStackTrace();
            }
        });

        backBtn.setOnAction(e -> popup.close());

        root.getChildren().addAll(
                title,
                new Label("Username:"), usernameField,
                new Label("New Password:"), newPass,
                new Label("Confirm Password:"), confirmPass,
                btnBox, errorLabel
        );

        Scene scene = new Scene(root, 400, 400);
        popup.setScene(scene);
        popup.showAndWait();
    }

    // Put these at the class level, not inside a method
    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:'\",.<>/?".indexOf(ch) >= 0);
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private String getPasswordStrength(String password) {
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.chars().anyMatch(Character::isUpperCase)) score++;
        if (password.chars().anyMatch(Character::isLowerCase)) score++;
        if (password.chars().anyMatch(Character::isDigit)) score++;
        if (password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:'\",.<>/?".indexOf(ch) >= 0)) score++;

        return switch (score) {
            case 0, 1, 2 -> "Weak";
            case 3, 4 -> "Normal";
            case 5 -> "Strong";
            default -> "Weak";
        };
    }

    private void openForgotPasswordWindow(boolean isAdmin) {
        Stage popup = new Stage();
        popup.setTitle(isAdmin ? "Admin Password Reset" : "User Password Reset");

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        // ---- Gradient Background START ----
        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#36D1DC")),
                new Stop(1, Color.web("#5B86E5"))
        };
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops
        );
        box.setBackground(new Background(
                new BackgroundFill(gradient, new CornerRadii(16), Insets.EMPTY)
        ));
        // ---- Gradient Background END ----

        PasswordField newPass = new PasswordField();
        newPass.setPromptText("Enter new password");

        Label status = new Label();

        if (isAdmin) {
            TextField adminIDField = new TextField();
            adminIDField.setPromptText("Enter Admin ID");

            Label strengthLabel = new Label("Password Strength: ");
            strengthLabel.setStyle("-fx-font-weight: bold;");

            newPass.textProperty().addListener((obs, oldText, newText) -> {
                strengthLabel.setText("Password Strength: " + getPasswordStrength(newText));
                switch (getPasswordStrength(newText)) {
                    case "Weak" -> strengthLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    case "Normal" -> strengthLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    case "Strong" -> strengthLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    default -> strengthLabel.setStyle("-fx-text-fill: gray; -fx-font-weight: bold;");
                }
            });

            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(ev -> {
                String idEntered = adminIDField.getText().trim();
                String np = newPass.getText().trim();

                if (idEntered.isEmpty() || !idEntered.equals(AdminFileStorage.ADMIN_ID)) {
                    status.setText("❌ Invalid Admin ID!");
                    status.setStyle("-fx-text-fill: red;");
                    return;
                }

                if (np.isEmpty()) {
                    status.setText("❌ Password cannot be empty!");
                    status.setStyle("-fx-text-fill: red;");
                    return;
                }

                if (!isValidPassword(np)) {
                    status.setText("❌ Password must be at least 8 chars, include upper, lower, number & special!");
                    status.setStyle("-fx-text-fill: red;");
                    return;
                }

                try {
                    AdminFileStorage.setAdminPassword(np);
                    status.setText("✅ Admin password reset successfully!");
                    status.setStyle("-fx-text-fill: green;");
                } catch (IOException e) {
                    status.setText("❌ Error saving password!");
                    status.setStyle("-fx-text-fill: red;");
                    e.printStackTrace();
                }
            });

            box.getChildren().addAll(
                    new Label("Enter Admin ID:"), adminIDField,
                    new Label("Enter New Password:"), newPass,
                    strengthLabel,
                    saveBtn,
                    status
            );
        }

        Scene scene = new Scene(box, 400, 280); // Slightly bigger for nice padding
        popup.setScene(scene);

        // --- Popup will always close with main window ---
        popup.initOwner(primaryStage);
        popup.initModality(Modality.WINDOW_MODAL);

        // This line ensures all popups close when main window closes:
        primaryStage.setOnCloseRequest(e -> {
            popup.close();
            // Optionally exit the application entirely:
            // Platform.exit();
        });

        popup.showAndWait();
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;" : "-fx-text-fill: #2ecc71; -fx-font-weight: bold;");

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
        // btn.setOnAction(e -> System.out.println(text + " clicked"));
        return btn;
    }

    public void returnToHome() {
        boolean wasFullScreen = primaryStage.isFullScreen();
        boolean wasMaximized = primaryStage.isMaximized();
        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        HomePage homePage = new HomePage(primaryStage);
        VBox fullPage = homePage.getFullPage();
        ScrollPane scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene homeScene = new Scene(scrollPane, currentWidth, currentHeight);

        // ✅ Reapply global stylesheet here
        var css = getClass().getResource("/com/example/view/styles/style.css");
        if (css != null) {
            homeScene.getStylesheets().add(css.toExternalForm());
            // System.out.println("✅ CSS reapplied successfully: " + css);
        } else {
            System.out.println("❌ CSS not found! Check file path.");
        }

        primaryStage.setScene(homeScene);

        if (wasFullScreen) {
            primaryStage.setFullScreen(true);
        } else if (wasMaximized) {
            primaryStage.setMaximized(true);
        }

        primaryStage.setTitle("Home Page + Extension");
        primaryStage.show();
    }


    // --- FIXED: Handle both full screen and maximized without manual resizing ---
    private void attachResizeListeners() {
        // Full screen listener: No manual resize needed; just force layout refresh
        ChangeListener<Boolean> fullScreenListener = (obs, wasFull, isNowFull) -> {
            if (loginScene != null) {
                root.requestLayout(); // Ensures layout adapts instantly
            }
        };
        primaryStage.fullScreenProperty().addListener(fullScreenListener);

        // Maximized listener: Same as above
        ChangeListener<Boolean> maximizedListener = (obs, wasMax, isNowMax) -> {
            if (loginScene != null) {
                root.requestLayout(); // Ensures layout adapts instantly
            }
        };
        primaryStage.maximizedProperty().addListener(maximizedListener);

        // Scene attach listener: Trigger layout if scene is set while in special mode
        primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == loginScene && (primaryStage.isFullScreen() || primaryStage.isMaximized())) {
                root.requestLayout();
            }
        });
    }
}