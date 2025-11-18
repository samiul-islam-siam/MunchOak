
package com.example.view;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

public class ProfilePage {

    private final Stage primaryStage;

    public ProfilePage(Stage stage) {
        this.primaryStage = stage;
    }

    public Scene getProfileScene() {
        // --- Get session info ---
        String username = Session.getCurrentUsername();
        String email = Session.getCurrentEmail();
        String password = Session.getCurrentPassword();
        int userId = Session.getCurrentUserId();

        if (username == null) username = "Guest";
        if (email == null) email = "N/A";
        //if (password == null) password = "N/A";


        // --- Title ---
        Label title = new Label("My Profile");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: black;");

        // --- User info labels ---
        Label userLabel = new Label("Username: " + username);
        Label emailLabel = new Label("Email: " + email);
        //Label passLabel = new Label("Password: " + password);
        Label passLabel = new Label("Your Id: " + userId);

        userLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #1b4fa8;");
        emailLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #1b4fa8;");
        passLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #1b4fa8;");

        // --- Password change field ---
        PasswordField newPass = new PasswordField();
        newPass.setPromptText("Enter new password");

        // Strength label
        Label strengthLabel = new Label("Password Strength: ");
        strengthLabel.setStyle("-fx-font-size: 14px;");

        // Status label
        Label status = new Label();
        status.setStyle("-fx-font-size: 14px;");

        // Update button
        Button updateBtn = new Button("Change Password");
        updateBtn.setStyle("-fx-background-color: #1b4fa8; -fx-text-fill: white; -fx-padding: 8 16; -fx-font-size: 14px;");

        // --- Real-time password strength ---
        newPass.textProperty().addListener((obs, oldVal, newVal) -> {
            updateStrengthLabel(strengthLabel, newVal);
        });

        // --- Update button action ---
        updateBtn.setOnAction(e -> {
            String newPw = newPass.getText().trim();

            if (newPw.isEmpty()) {
                status.setText("Password cannot be empty!");
                status.setStyle("-fx-text-fill: red;");
                return;
            }

            if (newPw.length() < 8) {
                status.setText("Password must be at least 8 characters!");
                status.setStyle("-fx-text-fill: red;");
                return;
            }

            // Update password in file
            FileStorage.updateUserPassword(Session.getCurrentUsername(), newPw);

            // Refresh session
            Session.setCurrentUser(Session.getCurrentUsername());
            passLabel.setText("Password: " + Session.getCurrentPassword());

            status.setText("Password updated successfully!");
            status.setStyle("-fx-text-fill: lime;");
            newPass.clear();
            strengthLabel.setText("Password Strength: ");
        });

        // --- Back button ---
        Button backBtn = new Button("⬅ Back to Home");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-size: 16px;");
        backBtn.setOnAction(e -> primaryStage.setScene(new HomePage(primaryStage).getHomeScene()));

        // --- Card layout ---
        VBox card = new VBox(15, title, userLabel, emailLabel, passLabel,
                new Label("Change Your Password:"), newPass, strengthLabel, updateBtn, status, backBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setMaxWidth(400);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 15;");

        // --- Root gradient background ---
        StackPane root = new StackPane(card);
        root.setPadding(new Insets(50));
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#49bad8")),
                new Stop(1, Color.web("#1c71bd"))
        );
        root.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        return new Scene(root, 1000, 700);
    }

    // --- Password strength helper ---
    private void updateStrengthLabel(Label label, String password) {
        int score = 0;

        if (password.length() < 8) {
            label.setText("Password Strength: Too Short");
            label.setTextFill(Color.RED);
            return;
        }

        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) score++;

        switch (score) {
            case 4 -> { label.setText("Password Strength: Strong"); label.setTextFill(Color.GREEN); }
            case 3 -> { label.setText("Password Strength: Normal"); label.setTextFill(Color.ORANGE); }
            default -> { label.setText("Password Strength: Weak"); label.setTextFill(Color.RED); }
        }
    }
}
