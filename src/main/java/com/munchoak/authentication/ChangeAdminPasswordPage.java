package com.munchoak.authentication;

import com.munchoak.manager.AdminStorage;
import com.munchoak.mainpage.AdminHome;

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
import javafx.stage.Stage;

import java.io.IOException;

public class ChangeAdminPasswordPage {

    public static void show(Stage ownerStage) {
        Stage popup = new Stage();
        popup.initOwner(ownerStage);
        popup.setTitle("Change Admin Password");

        // --- Title ---
        Label title = new Label("Change Admin Password");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: black;");

        // --- Admin ID Field ---
        TextField adminIDField = new TextField();
        adminIDField.setPromptText("Enter Admin ID");
        adminIDField.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px;");

        // --- Password Fields ---
        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Enter new password");
        newPassField.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px;");

        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm new password");
        confirmPassField.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px;");

        // --- Strength Labels (two-label approach) ---
        Label prefix = new Label("Password Strength:");
        prefix.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        Label strengthWord = new Label();
        strengthWord.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        HBox strengthBox = new HBox(5, prefix, strengthWord);
        strengthBox.setAlignment(Pos.CENTER);

        // --- Status Label ---
        Label status = new Label();
        status.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        // --- Buttons ---
        Button saveBtn = new Button("Save");
        Button backBtn = new Button("Back");
        saveBtn.setStyle("-fx-background-color: #00c9ff; -fx-text-fill: black; -fx-font-size: 14px;");
        backBtn.setStyle("-fx-background-color: #00c9ff; -fx-text-fill: black; -fx-font-size: 14px;");

        // --- Password Strength Listener ---
        newPassField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                // If field is empty, show nothing
                strengthWord.setText("");
            } else {
                strengthWord.setText(getPasswordStrength(newText));
            }
        });

        // --- Save Action ---
        saveBtn.setOnAction(e -> {
            String adminID = adminIDField.getText().trim();
            String newPass = newPassField.getText().trim();
            String confirmPass = confirmPassField.getText().trim();

            if (adminID.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                status.setText("Fields cannot be empty!");
                status.setStyle("-fx-text-fill: blue; -fx-font-weight: bold; -fx-font-size: 14px;");
                javafx.animation.PauseTransition clearError = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                clearError.setOnFinished(ev -> status.setText(""));
                clearError.play();
                return;
            }

            if (!"2104".equals(adminID)) {
                status.setText("Invalid Admin ID!");
                status.setStyle("-fx-text-fill: blue; -fx-font-weight: bold; -fx-font-size: 14px;");
                javafx.animation.PauseTransition clearError = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                clearError.setOnFinished(ev -> status.setText(""));
                clearError.play();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                status.setText("Passwords do not match!");
               // status.setStyle("-fx-text-fill: blue;");
                status.setStyle("-fx-text-fill: blue; -fx-font-weight: bold; -fx-font-size: 14px;");
                javafx.animation.PauseTransition clearError = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                clearError.setOnFinished(ev -> status.setText(""));
                clearError.play();
                return;
            }


            if (newPass.length() < 8 ||
                    !newPass.matches(".*[A-Z].*") ||
                    !newPass.matches(".*[a-z].*") ||
                    !newPass.matches(".*\\d.*") ||
                    !newPass.matches(".*[!@#$%^&*()-+=].*")) {

                status.setText(
                        "Password must be at least 8 characters\n" +
                                "and include uppercase, lowercase, numbers, and special characters!"
                );
                status.setStyle("-fx-text-fill: black; -fx-font-size: 13px; -fx-font-weight: bold;");
                status.setWrapText(true);
                status.setMaxWidth(380); // force wrapping
                status.setMinHeight(Region.USE_PREF_SIZE); // prevent vertical cutoff


                javafx.animation.PauseTransition clearError = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                clearError.setOnFinished(ev -> status.setText(""));
                clearError.play();
                return;
            }

            try {
                AdminStorage.setAdminPassword(newPass);
                status.setText("Password changed successfully!");
                javafx.animation.PauseTransition closeDelay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                closeDelay.setOnFinished(ev -> popup.close());
                closeDelay.play();

                status.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");


                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
                pause.setOnFinished(ev -> {
                    popup.close();
                    AdminHome dashboard = new AdminHome(ownerStage);
                    dashboard.openAdminDashboard();
                });
                pause.play();
            } catch (IOException ex) {
                status.setText("Error saving password.");
                status.setStyle("-fx-text-fill: red;");
                System.err.println("IOException: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> popup.close());

        // --- Layout ---
        VBox layout = new VBox(15, title, adminIDField, newPassField, confirmPassField, strengthBox, saveBtn, backBtn, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        // layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #add8e6, #87ceeb);");
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#20B2AA")),   // light sea green
                new Stop(1, Color.web("#40E0D0"))    // turquoise
        );
        layout.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        popup.setScene(new Scene(layout, 400, 400));
        popup.show();
    }

    private static String getPasswordStrength(String pwd) {
        if (pwd == null || pwd.isEmpty()) return ""; // empty → no text
        if (pwd.length() < 8) return "Weak";
        int score = 0;
        if (pwd.matches(".*[a-z].*")) score++;
        if (pwd.matches(".*[A-Z].*")) score++;
        if (pwd.matches(".*[0-9].*")) score++;
        if (pwd.matches(".*[!@#$%^&*()-+=].*")) score++;
        if (score <= 1) return "Weak";
        if (score == 2 || score == 3) return "Normal";
        return "Strong";
    }
}
