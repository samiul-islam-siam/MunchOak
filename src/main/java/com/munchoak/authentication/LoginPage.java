package com.munchoak.authentication;

import com.munchoak.homepage.HomePage;
import com.munchoak.mainpage.AdminHome;
import com.munchoak.manager.AdminStorage;
import com.munchoak.manager.Session;
import com.munchoak.manager.UserStorage;
import com.munchoak.menu.BaseMenu;
import com.munchoak.server.MainClient;
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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class LoginPage {
    private final Stage primaryStage;

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
    public BaseMenu menu; // current menu instance

    public LoginPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getLoginScene() {
        if (loginScene == null) {
            double initWidth = primaryStage.isFullScreen() || primaryStage.isMaximized()
                    ? Math.max(primaryStage.getWidth(), NORMAL_WIDTH)
                    : NORMAL_WIDTH;
            double initHeight = primaryStage.isFullScreen() || primaryStage.isMaximized()
                    ? Math.max(primaryStage.getHeight(), NORMAL_HEIGHT)
                    : NORMAL_HEIGHT;

            root = buildRoot();
            loginScene = new Scene(root, initWidth, initHeight);

            try {
                loginScene.getStylesheets().add(
                        Objects.requireNonNull(getClass().getResource("/com/munchoak/view/styles/style.css"))
                                .toExternalForm()
                );
            } catch (NullPointerException npe) {
                System.err.println("Exception: " + npe.getMessage());
            }

            attachResizeListeners();

            // ----------------- create / register MenuClient and attach menu -----------------
            try {
                MainClient client = Session.getMenuClient();
                if (client == null) {
                    client = new MainClient(menu);
                    Session.setMenuClient(client);
                } else {
                    client.setMenu(menu);
                }
            } catch (NoSuchMethodError | NoClassDefFoundError ex) {
                try {
                    new MainClient(menu);
                } catch (Exception inner) {
                    System.err.println("IOException: " + inner.getMessage());
                }
            } catch (Exception e) {
                try {
                    new MainClient(menu);
                } catch (Exception ignored) {
                }
            }
        }
        return loginScene;
    }

    private BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #FFFFE0;");

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER);
        topBar.setStyle("-fx-background-color: red;");

        HBox titleSection = new HBox(10);
        titleSection.setAlignment(Pos.CENTER);

        Image logoImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/com/munchoak/view/images/logo.png")
        ));
        ImageView logo = new ImageView(logoImage);
        logo.setFitWidth(40);
        logo.setFitHeight(40);
        logo.setClip(new Circle(20, 20, 20));
        titleSection.getChildren().add(logo);

        Label title = new Label("MUNCHOAK");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: black;");
        titleSection.getChildren().add(title);

        topBar.getChildren().add(titleSection);
        root.setTop(topBar);

        StackPane rightContainer = new StackPane();
        rightContainer.setAlignment(Pos.CENTER);
        rightContainer.setStyle("-fx-background-color: #FFFFE0;");

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

    // ------------------ Validation Helpers (non-password) --------------------

    private boolean isValidContact(String contact) {
        return contact != null && contact.matches("01\\d{9}");
    }

    private boolean isValidUsername(String username) {
        if (username == null) return false;
        boolean hasLetter = username.matches(".*[a-zA-Z].*");
        boolean minLength = username.length() >= 3;
        return hasLetter && minLength;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$");
    }

    // ------------------ UI Helpers --------------------

    private static void clearLater(Label label, double seconds) {
        PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
        pause.setOnFinished(ev -> label.setText(""));
        pause.play();
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

            // clear register fields
            if (usernameField != null) usernameField.clear();
            if (emailField != null) emailField.clear();
            if (passwordField != null) passwordField.clear();
            if (confirmField != null) confirmField.clear();

            if (statusLabel != null) statusLabel.setText("");
        });
        fadeOut.play();
    }

    private void showUserLoginForm() {
        switchPane(loginPane, userLoginPane);
    }

    private void showAdminLoginForm() {
        switchPane(loginPane, adminLoginPane);
    }

    private StackPane createStyledPasswordFieldWithEye(String prompt, PasswordField[] pfOut, TextField[] tfOut) {
        double fieldWidth = 280;
        double fieldHeight = 45;

        PasswordField pwField = createStyledPasswordField(prompt);
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setStyle(pwField.getStyle());
        textField.setVisible(false);
        textField.setManaged(false);

        pwField.setPrefWidth(fieldWidth);
        pwField.setPrefHeight(fieldHeight);
        textField.setPrefWidth(fieldWidth);
        textField.setPrefHeight(fieldHeight);

        pwField.textProperty().bindBidirectional(textField.textProperty());

        Label eyeIcon = new Label("\uD83D\uDC41");
        eyeIcon.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-text-fill: #888;" +
                        "-fx-cursor: hand; " +
                        "-fx-background-color: transparent; " +
                        "-fx-label-padding: 0 10 0 0;"
        );

        StackPane.setAlignment(eyeIcon, Pos.CENTER_RIGHT);
        StackPane.setMargin(eyeIcon, new Insets(0, 16, 0, 0));

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

        StackPane fieldPane = new StackPane(pwField, textField, eyeIcon);
        fieldPane.setPrefWidth(fieldWidth);
        fieldPane.setPrefHeight(fieldHeight);

        if (pfOut != null && pfOut.length > 0) pfOut[0] = pwField;
        if (tfOut != null && tfOut.length > 0) tfOut[0] = textField;

        return fieldPane;
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
        btn.setStyle("-fx-text-fill: black; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
        return btn;
    }

    // ------------------ PANES --------------------

    private VBox createLoginPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(40));

        Label subtitle = new Label("Fresh flavors, just one login away.");
        subtitle.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-family: 'Comic Sans MS', cursive;");
        subtitle.setAlignment(Pos.CENTER);
        subtitle.setOpacity(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), subtitle);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setInterpolator(Interpolator.EASE_IN);
        fadeIn.play();

        Label brandTitle = new Label("WELCOME TO MUNCHOAK");
        brandTitle.setStyle("-fx-font-size: 26px; -fx-text-fill: black; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 2);");

        Label loginAsLabel = new Label("Login as:");
        loginAsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black; -fx-font-weight: bold;");

        VBox buttonsVBox = new VBox(7);
        buttonsVBox.setAlignment(Pos.CENTER);

        Button adminBtn = createLoginButton("ADMIN");
        adminBtn.setOnAction(e -> showAdminLoginForm());

        Button userBtn = createLoginButton("USER");
        userBtn.setOnAction(e -> showUserLoginForm());

        Button registerBtn = createLoginButton("REGISTER");
        registerBtn.setOnAction(e -> showRegisterForm());

        Button guestBtn = createLoginButton("GUEST");
        guestBtn.setOnAction(e -> returnToHome());

        Button backToHomeBtn = createLoginButton("BACK");
        backToHomeBtn.setOnAction(e -> returnToHome());

        buttonsVBox.getChildren().addAll(adminBtn, userBtn, registerBtn, guestBtn, backToHomeBtn);
        pane.getChildren().addAll(subtitle, brandTitle, loginAsLabel, buttonsVBox);
        return pane;
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
        TextField contactField = createStyledTextField("Contact No");

        PasswordField[] pf1 = new PasswordField[1], pf2 = new PasswordField[1];
        TextField[] tf1 = new TextField[1], tf2 = new TextField[1];
        StackPane passwordFieldBox = createStyledPasswordFieldWithEye("Password", pf1, tf1);
        StackPane confirmFieldBox = createStyledPasswordFieldWithEye("Confirm Password", pf2, tf2);
        passwordField = pf1[0];
        confirmField = pf2[0];

        // ✅ use shared rules text
        Label passwordRulesLabel = new Label(PasswordPolicy.rulesText());
        passwordRulesLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px; -fx-font-family: 'Arial Black';");
        passwordRulesLabel.setWrapText(true);
        passwordRulesLabel.setMaxWidth(280);
        passwordRulesLabel.setMinHeight(Region.USE_PREF_SIZE);

        registerStrengthLabel = new Label("Password Strength: ");
        registerStrengthLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px; -fx-font-family: 'Arial Black';");
        registerStrengthLabel.setMinHeight(20);

        registerStatusLabel = new Label("");
        registerStatusLabel.setStyle("-fx-text-fill: black; -fx-font-size: 13px; -fx-font-weight: bold;");
        registerStatusLabel.setMinHeight(20);

        // ✅ strength update uses PasswordPolicy
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            String strength = PasswordPolicy.strengthWord(newVal);
            updateStrengthLabel(strength);
        });

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 30; -fx-background-radius: 25; -fx-cursor: hand;");

        registerBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String contactNo = contactField.getText().trim();
            String pwd = passwordField.getText();
            String conf = confirmField.getText();

            boolean invalidUser = !isValidUsername(username);
            boolean invalidEmail = !isValidEmail(email);
            boolean invalidContact = !isValidContact(contactNo);
            boolean invalidPassword = !PasswordPolicy.isValid(pwd);
            boolean invalidConfirm = !pwd.equals(conf);

            if (!username.isEmpty() && !email.isEmpty() && !contactNo.isEmpty() && !pwd.isEmpty() && !conf.isEmpty()
                    && invalidUser && invalidEmail && invalidContact && invalidPassword && invalidConfirm) {
                showRegisterStatus("All fields are invalid!", true);
                return;
            }

            if (username.isEmpty() || email.isEmpty() || contactNo.isEmpty() || pwd.isEmpty() || conf.isEmpty()) {
                showRegisterStatus("Please fill up the required fields!", true);
                return;
            }

            if (!isValidUsername(username)) {
                showRegisterStatus("Invalid username!\n(At least 3 characters and include a letter)", true);
                return;
            }

            if (!isValidEmail(email)) {
                showRegisterStatus("Invalid email format!", true);
                return;
            }

            if (!isValidContact(contactNo)) {
                showRegisterStatus("Invalid contact number!\n(Must be 11 digits starting with 01)", true);
                return;
            }

            if (!PasswordPolicy.isValid(pwd)) {
                showRegisterStatus("Invalid Password", true);
                return;
            }

            if (!pwd.equals(conf)) {
                showRegisterStatus("Passwords do not match!", true);
                return;
            }

            try {
                if (UserStorage.userExists(username)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Signup Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Username already exists!");
                    alert.showAndWait();
                    return;
                }

                Session.getMenuClient().sendRegister(username, email, contactNo, pwd);
                showRegisterStatus("Registering...", false);
            } catch (Exception err) {
                System.err.println("IOException: " + err.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while registering the user.");
                alert.showAndWait();
                return;
            }

            confirmField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) registerBtn.fire();
            });

            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> showLoginForm());
                }
            }, 1500);
        });

        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 25; -fx-background-radius: 20; -fx-cursor: hand;");
        backBtn.setOnAction(e -> showLoginForm());

        VBox buttonBox = new VBox(10, registerBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setStyle("-fx-text-fill: #3498db; -fx-font-size: 13px;");
        loginLink.setOnAction(e -> showLoginForm());

        pane.getChildren().addAll(
                title,
                usernameField, emailField, contactField,
                passwordFieldBox, confirmFieldBox,
                passwordRulesLabel,
                registerStrengthLabel,
                registerStatusLabel,
                buttonBox,
                loginLink
        );

        return pane;
    }

    private void updateStrengthLabel(String strength) {
        if (passwordField == null || passwordField.getText().isEmpty()) {
            registerStrengthLabel.setText("Password Strength: ");
            registerStrengthLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px; -fx-font-family: 'Arial Black';");
            return;
        }

        // Keep your existing colors (all orange except invalid/empty)
        switch (strength) {
            case "" -> {
                registerStrengthLabel.setText("Password Strength: ");
                registerStrengthLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px; -fx-font-family: 'Arial Black';");
            }
            case "Weak" -> {
                registerStrengthLabel.setText("Password Strength: Weak");
                registerStrengthLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 12px; -fx-font-family: 'Arial Black';");
            }
            case "Normal" -> {
                registerStrengthLabel.setText("Password Strength: Normal");
                registerStrengthLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 12px; -fx-font-family: 'Arial Black';");
            }
            case "Strong" -> {
                registerStrengthLabel.setText("Password Strength: Strong");
                registerStrengthLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 12px; -fx-font-family: 'Arial Black';");
            }
            default -> {
                registerStrengthLabel.setText("Password Strength: " + strength);
                registerStrengthLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 12px; -fx-font-family: 'Arial Black';");
            }
        }
    }

    private void showRegisterStatus(String message, boolean isError) {
        registerStatusLabel.setText(message);
        registerStatusLabel.setWrapText(true);
        registerStatusLabel.setMaxWidth(280);
        registerStatusLabel.setMinHeight(Region.USE_PREF_SIZE);
        registerStatusLabel.setStyle(isError
                ? "-fx-text-fill:black; -fx-font-weight: bold;"
                : "-fx-text-fill: black; -fx-font-weight: bold;"
        );
        clearLater(registerStatusLabel, 2);
    }

    private VBox createUserLoginPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(30));
        pane.setMaxWidth(300);

        Label title = new Label("User Login");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        TextField usernameField = createStyledTextField("Username");

        PasswordField[] pf = new PasswordField[1];
        TextField[] tf = new TextField[1];
        StackPane passwordFieldBox = createStyledPasswordFieldWithEye("Password", pf, tf);
        PasswordField passwordField = pf[0];

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 10 30;");

        loginBtn.setOnAction(e -> {
            try {
                if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                    showStatus("All fields required!", true);
                    return;
                }

                if (!UserStorage.userExists(usernameField.getText())) {
                    showStatus("User not found!", true);
                    return;
                }

                if (!PasswordStorage.verifyUserPassword(usernameField.getText(), passwordField.getText())) {
                    showStatus("Incorrect password!", true);
                    return;
                }

                Session.setCurrentUser(usernameField.getText());
                showStatus("Login successful!", false);

                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(ev -> returnToHome());
                pause.play();
            } catch (Exception ex) {
                System.err.println("IOException: " + ex.getMessage());
                showStatus("Error logging in!", true);
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) loginBtn.fire();
        });

        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: black; -fx-background-radius: 25;");
        backBtn.setOnAction(e -> switchPane(userLoginPane, loginPane));

        Hyperlink forgotLink = new Hyperlink("Forgot Password?");
        forgotLink.setStyle("-fx-text-fill: #3498db; -fx-font-size: 13px;");
        forgotLink.setOnAction(e -> ChangeUserPassPopup.show(primaryStage));

        VBox btnBox = new VBox(10, loginBtn, backBtn, forgotLink);
        btnBox.setAlignment(Pos.CENTER);

        statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: black; -fx-font-size: 13px; -fx-font-weight: bold;");
        statusLabel.setMinHeight(20);

        pane.getChildren().addAll(title, usernameField, passwordFieldBox, statusLabel, btnBox);
        return pane;
    }

    private VBox createAdminLoginPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(30));
        pane.setMaxWidth(300);

        adminStatusLabel = new Label("");
        adminStatusLabel.setStyle("-fx-text-fill: black; -fx-font-size: 13px; -fx-font-weight: bold;");
        adminStatusLabel.setMinHeight(20);

        Label title = new Label("Admin Login");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        TextField adminIDField = createStyledTextField("Enter Admin ID");

        PasswordField[] pf = new PasswordField[1];
        TextField[] tf = new TextField[1];
        StackPane adminPasswordFieldBox = createStyledPasswordFieldWithEye("Enter Admin Password", pf, tf);
        PasswordField adminPasswordField = pf[0];

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 10 30;");

        loginBtn.setOnAction(e -> {
            String idEntered = adminIDField.getText().trim();
            String passEntered = adminPasswordField.getText();

            try {
                if (idEntered.isEmpty() || passEntered.isEmpty()) {
                    showAdminStatus("Please fill up all the fields!", true);
                    return;
                }

                // ✅ use AdminStorage constant, not hard-coded "2104"
                if (!AdminStorage.ADMIN_ID.equals(idEntered)) {
                    showAdminStatus("Invalid Admin ID!", true);
                    return;
                }

                if (!AdminStorage.verifyAdminPassword(idEntered, passEntered)) {
                    showAdminStatus("Incorrect password!", true);
                    return;
                }

                // (Removed redundant double-check block)

                Session.setAdminUser();
                showAdminStatus("Admin login successful!", false);

                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(ev -> {
                    AdminHome dashboard = new AdminHome(primaryStage);
                    dashboard.openAdminDashboard();
                });
                pause.play();

            } catch (IOException ex) {
                System.err.println("IOException: " + ex.getMessage());
                showAdminStatus("Error verifying password!", true);
            }
        });

        Hyperlink forgotLink = new Hyperlink("Forgot Password?");
        forgotLink.setOnAction(e -> ChangeAdminPassPopup.show(primaryStage));
        forgotLink.setStyle("-fx-text-fill: #3498db; -fx-font-size: 13px;");

        adminPasswordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) loginBtn.fire();
        });

        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 8 25;");
        backBtn.setOnAction(e -> switchPane(adminLoginPane, loginPane));

        VBox btnBox = new VBox(10, loginBtn, backBtn, forgotLink);
        btnBox.setAlignment(Pos.CENTER);

        pane.getChildren().addAll(title, adminIDField, adminPasswordFieldBox, adminStatusLabel, btnBox);
        return pane;
    }

    private void showAdminStatus(String message, boolean isError) {
        adminStatusLabel.setText(message);
        adminStatusLabel.setStyle(isError
                ? "-fx-text-fill: black; -fx-font-weight: 900; -fx-font-size: 13px;"
                : "-fx-text-fill: orange; -fx-font-weight: 900; -fx-font-size: 13px;"
        );
        if (isError) clearLater(adminStatusLabel, 1);
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError
                ? "-fx-text-fill: black; -fx-font-weight: bolder; -fx-font-size: 13px;"
                : "-fx-text-fill: orange; -fx-font-weight: bolder; -fx-font-size: 13px;"
        );
        clearLater(statusLabel, 1);
    }

    // ------------------ Navigation --------------------

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

        var css = getClass().getResource("/com/munchoak/view/styles/style.css");
        if (css != null) {
            homeScene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS not found! Check file path.");
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

    private void attachResizeListeners() {
        ChangeListener<Boolean> fullScreenListener = (obs, wasFull, isNowFull) -> {
            if (loginScene != null) root.requestLayout();
        };
        primaryStage.fullScreenProperty().addListener(fullScreenListener);

        ChangeListener<Boolean> maximizedListener = (obs, wasMax, isNowMax) -> {
            if (loginScene != null) root.requestLayout();
        };
        primaryStage.maximizedProperty().addListener(maximizedListener);

        primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == loginScene && (primaryStage.isFullScreen() || primaryStage.isMaximized())) {
                root.requestLayout();
            }
        });
    }
}