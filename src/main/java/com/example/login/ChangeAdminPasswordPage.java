package com.example.login;

import com.example.manager.AdminFileStorage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class ChangeAdminPasswordPage {

    public static void show(Stage ownerStage) {
        Stage popup = new Stage();
        popup.initOwner(ownerStage); // links it to main dashboard
        popup.setTitle("Change Admin Password");

        Label title = new Label("Change Admin Password");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Enter new password");
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm new password");

        Label strengthLabel = new Label();
        strengthLabel.setStyle("-fx-font-weight: bold;");

        Label status = new Label();
        status.setStyle("-fx-font-weight: bold;");

        Button saveBtn = new Button("Save");
        Button backBtn = new Button("Back");

        // Password strength listener
        newPassField.textProperty().addListener((obs, oldText, newText) -> {
            strengthLabel.setText(getPasswordStrength(newText));
            if (newText.length() < 8) {
                strengthLabel.setText(strengthLabel.getText() + " (min 8 chars)");
                strengthLabel.setTextFill(Color.RED);
            }
        });

        saveBtn.setOnAction(e -> {
            String newPass = newPassField.getText().trim();
            String confirmPass = confirmPassField.getText().trim();

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                status.setText("Fields cannot be empty!");
                status.setTextFill(Color.GREEN);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                status.setText("Passwords do not match!");
                status.setTextFill(Color.GREEN);
                return;
            }

            if (newPass.length() < 8) {
                status.setText("Password must be at least 8 characters!");
                status.setTextFill(Color.GREEN);
                return;
            }

            try {
                AdminFileStorage.setAdminPassword(newPass);
                status.setText("Password changed successfully!");
                status.setTextFill(Color.LIMEGREEN);
                newPassField.clear();
                confirmPassField.clear();
                strengthLabel.setText("");
            } catch (IOException ex) {
                status.setText("Error saving password.");
                status.setTextFill(Color.RED);
                ex.printStackTrace();
            }
        });

        backBtn.setOnAction(e -> popup.close()); // Just close popup

        VBox layout = new VBox(15, title, newPassField, confirmPassField, strengthLabel, saveBtn, backBtn, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #000428, #004e92);");

        popup.setScene(new Scene(layout, 400, 350));
        popup.show();
    }

    private static String getPasswordStrength(String pwd) {
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
