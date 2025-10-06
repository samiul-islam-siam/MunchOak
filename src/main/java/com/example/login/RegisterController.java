package com.example.login;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Label statusLabel;

    // Handle register button click
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("All fields are required!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Passwords do not match!");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Invalid email address!");
            return;
        }

        // ✅ Insert into DB
        try (java.sql.Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Users (Username, Email, Password) VALUES (?, ?, ?)";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setText("Registration successful!");

                // Clear fields
                usernameField.clear();
                emailField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Username or email already exists!");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Database error occurred!");
        }
    }

    // Handle "Login" link click
    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button backButton;

    @FXML
    private void backButtonActionPerformed() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/welcome.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow(); // Get current window
            stage.setScene(new Scene(root));
            stage.setTitle("Welcome");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
