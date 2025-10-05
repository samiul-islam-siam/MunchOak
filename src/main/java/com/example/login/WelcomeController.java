package com.example.login;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WelcomeController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        // You can add more logic here if needed
        welcomeLabel.setText("Welcome to FSKTM Restaurant!");
    }
}

