package com.example.munchoak;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

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
    private StackPane rootPane;

    @FXML
    public void initialize() {
        userNameField.setText("");
        passwordField.setText("");
    }

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

    @FXML
    private void registerButtonActionPerformed() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/munchoak/Register.fxml"));
            rootPane.getChildren().clear();
            rootPane.getChildren().add(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

