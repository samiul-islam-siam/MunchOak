package com.example.login;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class ProfileController {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private Label loginCountLabel;
    @FXML
    private Label itemsPurchasedLabel;

    private String currentUsername = Session.getCurrentUsername();

    @FXML
    public void initialize() {
        currentUsername = Session.getCurrentUsername();
        loadUserData();
    }

    private void loadUserData() {
        if (currentUsername == null) return;

        List<String[]> users = FileStorage.loadUsers();
        for (String[] user : users) {
            if (user[0].equals(currentUsername)) {

                // user[] structure:
                // 0 = username
                // 1 = email
                // 2 = password
                // 3 = userId

                usernameLabel.setText(user[0]);
                emailLabel.setText(user[1]);

                // For fields that do not exist, set a placeholder
                nameLabel.setText(user[3]);
                addressLabel.setText("123 Oak street, Dhaka");
                phoneLabel.setText("+8801234567890");
                countryLabel.setText("Bangladesh");
                languageLabel.setText("English");
                loginCountLabel.setText("0");
                itemsPurchasedLabel.setText("0");

                break;
            }
        }
    }

}
