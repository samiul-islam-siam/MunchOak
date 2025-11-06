package com.example.login;

import com.example.manager.FileStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class ProfileController {

    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label nameLabel;
    @FXML private Label addressLabel;
    @FXML private Label phoneLabel;
    @FXML private Label countryLabel;
    @FXML private Label languageLabel;
    @FXML private Label loginCountLabel;
    @FXML private Label itemsPurchasedLabel;

    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        loadUserData(); // update profile as soon as username is set
    }

    @FXML
    public void initialize() {
        // Can leave empty; data loads when setCurrentUsername is called
    }

    private void loadUserData() {
        if (currentUsername == null) return;

        List<String[]> users = FileStorage.loadUsers(); // Replace with real storage
        for (String[] user : users) {
            if (user[0].equals(currentUsername)) { // user[0] is username
                usernameLabel.setText(user[0]);
                emailLabel.setText(user[1]);
                nameLabel.setText(user[2]);
                addressLabel.setText(user[3]);
                phoneLabel.setText(user[4]);
                countryLabel.setText(user[5]);
                languageLabel.setText(user[6]);
                loginCountLabel.setText(user[7]);
                itemsPurchasedLabel.setText(user[8]);
                break;
            }
        }
    }
}
