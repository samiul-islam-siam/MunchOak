package com.example.munchoak;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class AdminLoginController {

    @FXML
    private TextField adminUserField;

    @FXML
    private PasswordField adminPasswordField;

    @FXML
    private Button loginButton, registerButton;

    @FXML
    private void handleAdminLogin() {
        String username = adminUserField.getText();
        String password = adminPasswordField.getText();

        if ("admin".equalsIgnoreCase(username) && "admin".equals(password)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Admin Access Granted");
            alert.setHeaderText(null);
            alert.setContentText("Welcome, " + username + "!");
            alert.showAndWait();

            // Close admin login window
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();

            // TODO: Open admin dashboard window
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Access Denied");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Admin credentials!");
            alert.showAndWait();

            adminUserField.clear();
            adminPasswordField.clear();
        }
    }

    @FXML
    private void handleAdminRegister() {
        // Optional: open admin register page if needed
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Register");
        alert.setHeaderText(null);
        alert.setContentText("Admin registration is not available yet!");
        alert.showAndWait();
    }
}

