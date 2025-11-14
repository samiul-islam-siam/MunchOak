/*package com.example.login;

import com.example.manager.AdminFileStorage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ChangeAdminPasswordPage {

    public static void show(Stage stage) {
        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Enter new password");

        Button saveBtn = new Button("Save");
        Button backBtn = new Button("Back");

        Label status = new Label();

        saveBtn.setOnAction(e -> {
            String newPass = newPassField.getText().trim();
            if (newPass.isEmpty()) {
                status.setText("Password cannot be empty!");
                return;
            }
            try {
                AdminFileStorage.setAdminPassword(newPass);
                status.setText("Password changed successfully!");
            } catch (IOException ex) {
                status.setText("Error saving new password.");
                ex.printStackTrace();
            }
        });

        backBtn.setOnAction(e -> AdminDashboard.show(stage));

        VBox layout = new VBox(10, new Label("Change Admin Password"), newPassField, saveBtn, backBtn, status);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #F0F8FF;");

        stage.setScene(new Scene(layout, 400, 250));
    }
}
*/
package com.example.login;
import com.example.manager.AdminFileStorage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class ChangeAdminPasswordPage {
/*
    public static void show(Stage stage) {
        Label title = new Label("Change Admin Password");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Enter new password");
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm new password");

        Label strengthLabel = new Label(); // password strength
        strengthLabel.setStyle("-fx-font-weight: bold;");

        Label status = new Label();
        status.setStyle("-fx-font-weight: bold;");

        Button saveBtn = new Button("Save");
        Button backBtn = new Button("Back");

        // --- Password strength checker ---
        newPassField.textProperty().addListener((obs, oldText, newText) -> {
            strengthLabel.setText(getPasswordStrength(newText));
            if (newText.length() < 8) {
                strengthLabel.setText(strengthLabel.getText() + " (min 8 chars)");
                strengthLabel.setTextFill(Color.RED);
            }
        });

        // --- Save action ---
        saveBtn.setOnAction(e -> {
            String newPass = newPassField.getText().trim();
            String confirmPass = confirmPassField.getText().trim();

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                status.setText("Fields cannot be empty!");
                status.setTextFill(Color.RED);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                status.setText("Passwords do not match!");
                status.setTextFill(Color.RED);
                return;
            }

            if (newPass.length() < 8) {
                status.setText("Password must be at least 8 characters!");
                status.setTextFill(Color.RED);
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

        // --- Back button ---
        backBtn.setOnAction(e -> AdminDashboard.show(stage));

        VBox layout = new VBox(15, title, newPassField, confirmPassField, strengthLabel, saveBtn, backBtn, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #000428, #004e92);");

        stage.setScene(new Scene(layout, 400, 350));
    }
*/
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
