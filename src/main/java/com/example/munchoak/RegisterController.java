package com.example.munchoak;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/munchoak/Login.fxml"));
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

