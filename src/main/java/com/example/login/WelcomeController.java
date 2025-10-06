package com.example.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class WelcomeController {

    @FXML
    private void adminLoginAction() {
        // TODO: Add admin login window later
        System.out.println("Admin login clicked");
    }

    @FXML
    private void userLoginAction(javafx.event.ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setTitle("User Login");
            stage.show();

            // Close the Welcome window
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registerAction(javafx.event.ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/login/register.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setTitle("Register");
            stage.show();

            // Close the Welcome window
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guestBrowseAction(javafx.event.ActionEvent event) {
        try {
            // Create and show RestaurantDashboard window
            com.example.munchoak.RestaurantDashboard dashboard = new com.example.munchoak.RestaurantDashboard();
            Stage stage = new Stage();
            dashboard.start(stage);

            // Close the Welcome window
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button guestBrowseBtn;
    @FXML
    private Button registerBtn;

    /* Called when Welcome screen is opened from Dashboard */
    public void openedFromDashboard() {
        System.out.println("Welcome page opened from Dashboard");

        // Customizations:
        welcomeLabel.setText("Welcome Back!");
        //guestBrowseBtn.setVisible(false); // hide
        guestBrowseBtn.setText("Continue as Guest");
        registerBtn.setStyle("-fx-background-color: #1abc9c;"); // visually highlight Register

    }
}
