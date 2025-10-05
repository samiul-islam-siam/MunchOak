package com.example.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePageController {

    @FXML
    void onExploreClicked(ActionEvent event) {
        // Just show a message — no login required
        System.out.println("Exploring menu (demo mode).");
    }

    @FXML
    void onLoginClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/Login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    @FXML
    void onSignupClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/Signup.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }
}

