package com.example.authentication;

import com.example.manager.Session;
import com.example.manager.UserStorage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditProfilePopup {

    public static void show(Stage owner, Runnable onProfileUpdated) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Edit Profile");

        VBox layout = new VBox(12);
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setPadding(new Insets(30));
        layout.setMaxWidth(400);
        layout.setPrefWidth(400);

        // Common fields
        Label usernameLabel = new Label("Username:");
        Label emailLabel = new Label("Email:");
        Label contactLabel = new Label("Contact No:");
        Label newPassLabel = new Label("New Password:");
        Label confirmPassLabel = new Label("Confirm Password:");

        for (Label label : new Label[]{usernameLabel, emailLabel, contactLabel, newPassLabel, confirmPassLabel}) {
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black;");
            label.setPrefWidth(150);
        }

        TextField usernameField = new TextField(Session.getCurrentUsername());
        TextField emailField = new TextField(Session.getCurrentEmail());
        TextField contactField = new TextField(Session.getCurrentContactNo());
        PasswordField newPassField = new PasswordField();
        PasswordField confirmPassField = new PasswordField();

        newPassField.setPromptText("Enter new password");
        confirmPassField.setPromptText("Confirm new password");

        // Strength + rules
        Label prefix = new Label("Password Strength:");
        prefix.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");
        Label strengthWord = new Label();
        strengthWord.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: blue;");
        HBox strengthBox = new HBox(5, prefix, strengthWord);
        strengthBox.setAlignment(Pos.CENTER_LEFT);

        Label rulesLabel = new Label(
                "Password must be at least 8 characters long,\n" +
                        "and include uppercase, lowercase, numbers, and special characters."
        );
        rulesLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: black; -fx-font-weight: bold;");
        rulesLabel.setWrapText(true);
        rulesLabel.setMaxWidth(500);

        Label status = new Label();
        status.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        status.setAlignment(Pos.CENTER);
        status.setMaxWidth(Double.MAX_VALUE);

        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #1b4fa8; -fx-text-fill: white; -fx-font-size: 14px;");
        saveBtn.setDefaultButton(true); // ✅ Enter key will trigger this button

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 14px;");
        saveBtn.setDefaultButton(true); // ✅ Enter key will trigger this button

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonRow = new HBox(20, saveBtn, cancelBtn);
        buttonRow.setAlignment(Pos.CENTER);

        newPassField.textProperty().addListener((obs, oldVal, newVal) -> updateStrengthWord(strengthWord, newVal));

        // Save logic (user only)
        saveBtn.setOnAction(e -> {
            String newUser = usernameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newContact = contactField.getText().trim();
            String newPass = newPassField.getText().trim();
            String confirmPass = confirmPassField.getText().trim();

            // Preserve old values if blank
            if (newUser.isEmpty()) newUser = Session.getCurrentUsername();
            if (newEmail.isEmpty()) newEmail = Session.getCurrentEmail();
            if (newContact.isEmpty()) newContact = Session.getCurrentContactNo();

            // Validate only non-empty fields
            if (!newUser.isEmpty() && (!newUser.matches(".*[a-zA-Z].*") || newUser.length() < 3)) {
                showTempStatus(status, "Invalid username!", Color.BLUE);
                return;
            }
            if (!newEmail.isEmpty() && !newEmail.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
                showTempStatus(status, "Email must be a valid @gmail.com address.", Color.BLUE);
                return;
            }
            if (!newContact.isEmpty() && !newContact.matches("01\\d{9}")) {
                showTempStatus(status, "Contact must be 11 digits starting with 01.", Color.BLUE);
                return;
            }

            // Password validation only if provided
            if (!newPass.isEmpty() || !confirmPass.isEmpty()) {
                if (newPass.length() < 8 ||
                        !newPass.matches(".*[A-Z].*") ||
                        !newPass.matches(".*[a-z].*") ||
                        !newPass.matches(".*\\d.*") ||
                        !newPass.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
                    showTempStatus(status, "Invalid password format!", Color.BLUE);
                    return;
                }
                if (!newPass.equals(confirmPass)) {
                    showTempStatus(status, "Passwords do not match!", Color.BLUE);
                    return;
                }
                PasswordStorage.updateUserPassword(Session.getCurrentUsername(), newPass);
            }

            // Update user profile info
            UserStorage.updateUserInfo(Session.getCurrentUserId(), newUser, newEmail, newContact);

            // Update Session
            Session.setCurrentUser(newUser);
            Session.setCurrentEmail(newEmail);
            Session.setCurrentContactNo(newContact);

            showTempStatus(status, "Profile updated successfully!", Color.WHITE);
            if (onProfileUpdated != null) onProfileUpdated.run();
        });

        layout.getChildren().addAll(
                usernameLabel, usernameField,
                emailLabel, emailField,
                contactLabel, contactField,
                newPassLabel, newPassField,
                confirmPassLabel, confirmPassField,
                strengthBox, rulesLabel,
                buttonRow, status
        );

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#20B2AA")),
                new Stop(1, Color.web("#40E0D0"))
        );
        layout.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        popup.setScene(new Scene(layout, 450, 620));
        popup.show();
    }

    private static void updateStrengthWord(Label strengthWord, String password) {
        if (password == null || password.isEmpty()) {
            strengthWord.setText("");
            return;
        }
        int score = 0;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) score++;

        if (password.length() < 8) {
            strengthWord.setText("Weak");
            return;
        }
        switch (score) {
            case 4 -> strengthWord.setText("Strong");
            case 2, 3 -> strengthWord.setText("Normal");
            default -> strengthWord.setText("Weak");
        }
    }

    private static void showTempStatus(Label status, String message, Color color) {
        status.setText(message);
        status.setTextFill(color);
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
        pause.setOnFinished(ev -> status.setText(""));
        pause.play();
    }
}
