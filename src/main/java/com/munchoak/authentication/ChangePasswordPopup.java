package com.munchoak.authentication;

import com.munchoak.manager.Session;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChangePasswordPopup {

    public static void show(Stage ownerStage) {
        Stage popup = new Stage();
        popup.initOwner(ownerStage);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Change Password");
        // --- Permanent password rules label ---
        Label rulesLabel = new Label(
                "Password must be at least 8 characters long,\n" +
                        "and include uppercase, lowercase, numbers, and special characters."
        );
        rulesLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: black; -fx-font-weight: normal;");
        rulesLabel.setWrapText(true);
        rulesLabel.setMaxWidth(380); // ✅ ensures wrapping
        rulesLabel.setMinHeight(Region.USE_PREF_SIZE); // ✅ prevents vertical cutoff
        rulesLabel.setAlignment(Pos.CENTER_LEFT); // ✅ avoids center misalignment
        VBox.setVgrow(rulesLabel, Priority.NEVER); // ✅ prevents VBox from squashing it

        // --- Fields ---
        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Enter new password");

        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm new password");

        // --- Strength Labels (two-label approach) ---
        Label prefix = new Label("Password Strength:");
        prefix.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label strengthWord = new Label(); // dynamic part
        strengthWord.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #B8860B;"); // dark yellow

        HBox strengthBox = new HBox(5, prefix, strengthWord);
        strengthBox.setAlignment(Pos.CENTER);

        // --- Status Label ---
        Label status = new Label();
        status.setStyle("-fx-font-size: 14px;");

        // --- Buttons ---
        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #1b4fa8; -fx-text-fill: white; -fx-font-size: 14px;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 14px;");
        cancelBtn.setOnAction(e -> popup.close());

        // --- Real-time strength update ---
        newPassField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateStrengthWord(strengthWord, newVal);
        });

        // --- Save Action ---
        saveBtn.setOnAction(e -> {
            String newPass = newPassField.getText().trim();
            String confirmPass = confirmPassField.getText().trim();

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                showTempStatus(status, "Fields cannot be empty!", Color.RED);
                return;
            }
            if (newPass.length() < 8) {
                showTempStatus(status, "Invalid Password", Color.RED);
                return;
            }
            if (!newPass.matches(".*[A-Z].*") ||
                    !newPass.matches(".*[a-z].*") ||
                    !newPass.matches(".*\\d.*") ||
                    !newPass.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
                showTempStatus(status, "Password must include uppercase, lowercase, number & special character!", Color.RED);
                return;
            }
            if (!newPass.equals(confirmPass)) {
                showTempStatus(status, "Passwords do not match!", Color.RED);
                return;
            }

            // Update password in file
            PasswordStorage.updateUserPassword(Session.getCurrentUsername(), newPass);
            Session.setCurrentUser(Session.getCurrentUsername());
            Session.getMenuClient().sendUserFileUpdate();

            status.setText("Password changed successfully!");
            javafx.animation.PauseTransition closeDelay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
            closeDelay.setOnFinished(ev -> popup.close());
            closeDelay.play();

            status.setTextFill(Color.GREEN);

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
            pause.setOnFinished(ev -> popup.close());
            pause.play();
        });

        // --- Layout ---
        VBox layout = new VBox(15, newPassField, confirmPassField, rulesLabel, strengthBox, saveBtn, cancelBtn, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        // --- Background Gradient ---
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#20B2AA")),   // light sea green
                new Stop(1, Color.web("#40E0D0"))    // turquoise
        );
        layout.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        popup.setScene(new Scene(layout, 420, 320));
        popup.show();
    }

    // --- Error Message Helper ---
    private static void showTempStatus(Label status, String message, Color color) {
        status.setText(message);
        status.setTextFill(color);
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        pause.setOnFinished(ev -> status.setText(""));
        pause.play();
    }

    // --- Strength Word Updater ---
    private static void updateStrengthWord(Label strengthWord, String password) {
        if (password == null || password.isEmpty()) {
            strengthWord.setText(""); // empty → no word
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
}
