package com.example.view;
import com.example.manager.Session;
import com.example.menu.MenuPage;
import javafx.stage.Modality;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.paint.Color;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Background;
import javafx.geometry.Insets;
import com.example.manager.FileStorage;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.manager.AdminFileStorage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    private static final double NORMAL_WIDTH = 1000;
    private static final double NORMAL_HEIGHT = 700;

    private Scene loginScene;           // Only one scene ever
    private BorderPane root;            // Reusable root

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
            loginScene.getStylesheets().add(getClass().getResource("/com/example/view/styles/style.css").toExternalForm());
            attachResizeListeners(); // Renamed and fixed to handle full screen too
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
        userLoginPane = createUserLoginPane();
        adminLoginPane = createAdminLoginPane();
        registerPane.setVisible(false);
        userLoginPane.setVisible(false);
        adminLoginPane.setVisible(false);
        registerPane.setOpacity(0);
        rightContainer.getChildren().addAll(loginPane, registerPane, userLoginPane, adminLoginPane);

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
        adminBtn.setOnAction(e -> showAdminLoginForm());

        Button userBtn = createLoginButton("USER");
        userBtn.setOnAction(e -> showUserLoginForm());

        Button registerBtn = createLoginButton("REGISTER");
        registerBtn.setOnAction(e -> showRegisterForm());

        Button guestBtn = createLoginButton("GUEST");
        guestBtn.setOnAction(e -> {
            returnToHome();
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

        usernameField = createStyledTextField("Username");
        emailField = createStyledTextField("Email");
        passwordField = createStyledPasswordField("Password");
        confirmField = createStyledPasswordField("Confirm Password");

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 30; -fx-background-radius: 25; -fx-cursor: hand;");
        registerBtn.setOnAction(e -> {

            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String pwd = passwordField.getText();
            String conf = confirmField.getText();

            if (username.isEmpty() || email.isEmpty() || pwd.isEmpty() || conf.isEmpty()) {
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

                showStatus("Registration successful!", false);

            } catch (IOException err) {
                err.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while registering the user.");
                alert.showAndWait();
            }

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

        statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        statusLabel.setMinHeight(20);

        pane.getChildren().addAll(title, usernameField, emailField, passwordField, confirmField, statusLabel, buttonBox, loginLink);
        return pane;
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
                Session.setCurrentUsername(usernameField.getText());
                showStatus("Login successful!", false);
                returnToHome();
            } catch (Exception ex) {
                ex.printStackTrace();
                showStatus("Error logging in!", true);
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

        Label title = new Label("Admin Login");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        TextField adminIDField = createStyledTextField("Enter Admin ID");
        PasswordField adminPasswordField = createStyledPasswordField("Enter Admin Password");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 10 30;");

        // ✅ Login Action
        loginBtn.setOnAction(e -> {
            String idEntered = adminIDField.getText().trim();
            String passEntered = adminPasswordField.getText();

            try {
                if (!AdminFileStorage.verifyAdminPassword(idEntered, passEntered)) {
                    showStatus("Incorrect ID or password!", true);
                    return;
                }
                Session.setCurrentUsername("admin");
                showStatus("Admin login successful!", false);
                openAdminDashboard();
            } catch (IOException ex) {
                ex.printStackTrace();
                showStatus("Error verifying password!", true);
            }
        });

        // ✅ Forgot Password Button (for admin only)
        Hyperlink forgotLink = new Hyperlink("Forgot Password?");
        forgotLink.setOnAction(e -> openForgotPasswordWindow(true)); // true = admin mode
        forgotLink.setStyle("-fx-text-fill: #3498db; -fx-font-size: 13px;");

        // ✅ Back Button
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 8 25;");
        backBtn.setOnAction(e -> switchPane(adminLoginPane, loginPane)); // Go back to main login

        VBox btnBox = new VBox(10, loginBtn, forgotLink, backBtn);
        btnBox.setAlignment(Pos.CENTER);

        pane.getChildren().addAll(title, adminIDField, adminPasswordField, btnBox);
        return pane;
    }

    // shopwing register form
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

    public void openAdminDashboard() {
        BorderPane dashboard = new BorderPane();
        dashboard.setStyle("-fx-background-color: linear-gradient(to right, #000428, #004e92);");

        // --- Top Bar ---
        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold;");
        HBox topBar = new HBox(title);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(20, 0, 20, 0));

        // --- Center Content Placeholder ---
        Label infoLabel = new Label("Select an action from the left menu.");
        infoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        StackPane centerPane = new StackPane(infoLabel);

        // --- Left Menu ---
        VBox menuBox = new VBox(15);
        menuBox.setPadding(new Insets(30));
        menuBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-pref-width: 220;");
        menuBox.setAlignment(Pos.TOP_CENTER);

        Button viewUsersBtn = new Button("View All Users");
        Button countUsersBtn = new Button("Count Users");
        Button manageMenuBtn = new Button("Manage Menu");
        Button changePassBtn = new Button("Change Password");
        Button logoutBtn = new Button("Logout");

        for (Button btn : new Button[]{viewUsersBtn, countUsersBtn, manageMenuBtn, changePassBtn, logoutBtn}) {
            btn.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-pref-width: 180; -fx-padding: 10 0; -fx-background-radius: 25;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #63B3ED; -fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; -fx-pref-width: 180; -fx-padding: 10 0; -fx-background-radius: 25;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-pref-width: 180; -fx-padding: 10 0; -fx-background-radius: 25;"));
        }

        menuBox.getChildren().addAll(viewUsersBtn, countUsersBtn, manageMenuBtn, changePassBtn, logoutBtn);

        dashboard.setTop(topBar);
        dashboard.setLeft(menuBox);
        dashboard.setCenter(centerPane);

        Scene scene = new Scene(dashboard, 1000, 700);
        primaryStage.setScene(scene);

        // --- Button Functionalities ---

        viewUsersBtn.setOnAction(e -> {
            List<String[]> users = FileStorage.loadUsers();
            TableView<String[]> table = new TableView<>();
            // Column for User ID (last element)
            TableColumn<String[], String> idCol = new TableColumn<>("User ID");
            idCol.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty(data.getValue()[3])
            );
            idCol.setPrefWidth(100);

            // Column for Username (first element)
            TableColumn<String[], String> usernameCol = new TableColumn<>("Username");
            usernameCol.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty(data.getValue()[0])
            );
            usernameCol.setPrefWidth(200);

            // Column for Email / Main (second element)
            TableColumn<String[], String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty(data.getValue()[1])
            );
            emailCol.setPrefWidth(250);

            // Column for Password (third element, masked)
            TableColumn<String[], String> passwordCol = new TableColumn<>("Password");
            passwordCol.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty("********")
            );
            passwordCol.setPrefWidth(150);

            table.getColumns().addAll(idCol, usernameCol, emailCol, passwordCol);

            // Add users to the table
            table.getItems().addAll(users);

            // Make table fill centerPane
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            centerPane.getChildren().setAll(table);



        });

        manageMenuBtn.setOnAction(e -> {
//            Label msg = new Label("Manage Menu (Coming soon...)");
//            msg.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
//            centerPane.getChildren().setAll(msg);
            MenuPage menuPage = new MenuPage(primaryStage);
            primaryStage.setScene(menuPage.getMenuScene());
        });

        changePassBtn.setOnAction(e -> com.example.login.ChangeAdminPasswordPage.show(primaryStage));

        logoutBtn.setOnAction(e -> {
            // Go back to LoginPage
            primaryStage.setScene(getLoginScene());
        });
    }

    private void openUserDashboard() {
        Label label = new Label("Welcome, User!");
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        BorderPane pane = new BorderPane(label);
        pane.setStyle("-fx-background-color: linear-gradient(to right, #56ab2f, #a8e063);");
        primaryStage.setScene(new Scene(pane, 1000, 700));
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

        PasswordField newPass = new PasswordField();
        newPass.setPromptText("Enter new password");

        Label status = new Label();


        if (isAdmin) {
            TextField adminIDField = new TextField();
            adminIDField.setPromptText("Enter Admin ID");

            Label strengthLabel = new Label("Password Strength: ");
            strengthLabel.setStyle("-fx-font-weight: bold;");

            // ✅ Update strength as user types
            newPass.textProperty().addListener((obs, oldText, newText) -> {
                strengthLabel.setText("Password Strength: " + getPasswordStrength(newText));
                switch (getPasswordStrength(newText)) {
                    case "Weak" -> strengthLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    case "Normal" -> strengthLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    case "Strong" -> strengthLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
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

        popup.setScene(new Scene(box, 350, 250));
        popup.show();
    }

    // Helper: ask for username when user resets password
    private String askForUsername() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username Required");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter your username:");
        return dialog.showAndWait().orElse(null);
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
