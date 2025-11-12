package com.example.login;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import com.example.view.*;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;

public class LoginController {

    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button backButton;

    @FXML
    private void loginButtonActionPerformed() {
        String username = userNameField.getText().trim();
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
            boolean found = false;
            for (String[] user : FileStorage.loadUsers()) {
                if (user[0].equals(username) && user[2].equals(password)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                int userId = FileStorage.getUserId(username);  // get the user's unique ID
                Session.setCurrentUsername(username);
                Session.setCurrentUserId(userId);              // store it in the session
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Success");
                alert.setHeaderText(null);
                alert.setContentText("Welcome, " + username + "!");
                alert.showAndWait();

                // ✅ Use the existing stage instead of creating a new one
                Stage primaryStage = (Stage) loginButton.getScene().getWindow();

                // ✅ Create LoginPage with the same stage and call returnToHome
                LoginPage mainApp = new LoginPage(primaryStage);
                mainApp.returnToHome();


            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password!");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backButtonActionPerformed() {
        try {
            // Get current stage
            Stage currentStage = (Stage) backButton.getScene().getWindow();

            // Create and show LoginPage again
            com.example.view.LoginPage loginPage = new com.example.view.LoginPage(currentStage);
            Scene loginScene = loginPage.getLoginScene();

            currentStage.setScene(loginScene);
            currentStage.setTitle("Login");
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
