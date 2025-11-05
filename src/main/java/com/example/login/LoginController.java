package com.example.login;

import com.example.manager.FileStorage;
import com.example.munchoak.Dashboard;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Success");
                alert.setHeaderText(null);
                alert.setContentText("Welcome, " + username + "!");
                alert.showAndWait();

                Dashboard dashboard = new Dashboard();
                Stage stage = new Stage();
                dashboard.start(stage);

                Stage currentStage = (Stage) loginButton.getScene().getWindow();
                currentStage.close();
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
    private void registerButtonActionPerformed() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/FXMLs/register.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Register");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backButtonActionPerformed() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/FXMLs/welcome.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Welcome");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
