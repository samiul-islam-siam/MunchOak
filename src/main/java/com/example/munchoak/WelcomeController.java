
package com.example.munchoak;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WelcomeController {

    @FXML
    private ImageView background;

    @FXML
    private StackPane stackPane;

    @FXML
    public void initialize() {
        // Bind background size to the StackPane → always fills the window
        background.fitWidthProperty().bind(stackPane.widthProperty());
        background.fitHeightProperty().bind(stackPane.heightProperty());
    }

    @FXML
    private void adminLoginAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/munchoak/AdminLogin.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            stage.setTitle("Admin Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void userLoginAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/munchoak/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            stage.setTitle("User Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registerAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/munchoak/register.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            stage.setTitle("Register");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guestBrowseAction() {
        System.out.println("Guest browsing...");
    }
}
