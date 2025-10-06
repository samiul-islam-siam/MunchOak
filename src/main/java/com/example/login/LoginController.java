package com.example.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
    private void loginButtonActionPerformed() {
        String username = userNameField.getText();
        String password = passwordField.getText();

        if ("shahin".equalsIgnoreCase(username) && "shahin".equals(password)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Access Granted");
            alert.setHeaderText(null);
            alert.setContentText("Welcome, " + username + "!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Access Denied");
            alert.setHeaderText(null);
            alert.setContentText("Invalid credentials!");
            alert.showAndWait();
        }
    }

//    @FXML
//    private void registerButtonActionPerformed() {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register.fxml"));
//            Stage stage = new Stage();
//            stage.setScene(new Scene(fxmlLoader.load()));
//            stage.setTitle("Register");
//            stage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @FXML
//    private void registerButtonActionPerformed() {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register.fxml"));
//            Stage stage = (Stage) registerButton.getScene().getWindow(); // get current stage
//            stage.setScene(new Scene(fxmlLoader.load()));
//            stage.setTitle("Register");
//            stage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    private void registerButtonActionPerformed() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/register.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) registerButton.getScene().getWindow(); // ✅ reuse the same stage
            stage.setScene(scene);
            stage.setTitle("Register");
            stage.show();
        } catch (Exception e) {
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
