package com.example.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class RegisterController {

    @FXML
    private TextField regUserNameField;

    @FXML
    private PasswordField regPasswordField;

    @FXML
    private Button backButton;

    @FXML
    private StackPane rootPane;

    @FXML
    private void backToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/Login.fxml"));
            rootPane.getChildren().clear();
            rootPane.getChildren().add(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registerConfirmAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registered");
        alert.setHeaderText(null);
        alert.setContentText("User " + regUserNameField.getText() + " registered successfully!");
        alert.showAndWait();
    }
}

