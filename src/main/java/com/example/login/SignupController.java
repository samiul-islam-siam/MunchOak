package com.example.login;

import com.example.manager.FileStorage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignupController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both username and password.");
            alert.showAndWait();
            return;
        }

        try {
            // Check if user exists
            if (FileStorage.userExists(username)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Signup Failed");
                alert.setHeaderText(null);
                alert.setContentText("Username already exists!");
                alert.showAndWait();
                return;
            }

            // Append new user with "unknown" email
            FileStorage.appendUser(username, "unknown", password);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("User registered successfully!");
            alert.showAndWait();

            // Redirect to login
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/login/FXMLs/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to register user. Please try again.");
            alert.showAndWait();
        }
    }
}
