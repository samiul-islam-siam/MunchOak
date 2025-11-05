/*package com.example.login;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

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

    // Instance field instead of static
    private String currentUsername;

    // Call this after FXMLLoader.load()
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        // Update UI immediately if already loaded
        if (usernameLabel != null) {
            populateUserData();
        }
    }

    @FXML
    public void initialize() {
        // Initialize with default or dummy data
        if (currentUsername == null) currentUsername = "Unknown";
        populateUserData();
    }

    private void populateUserData() {
        // You can replace this with real FileStorage data later
        usernameLabel.setText(currentUsername);
        emailLabel.setText(currentUsername + "@example.com");
        nameLabel.setText("Shuch Example");
        addressLabel.setText("123 Main St, City");
        phoneLabel.setText("+880123456789");
        countryLabel.setText("Bangladesh");
        languageLabel.setText("English");
        loginCountLabel.setText("5");
        itemsPurchasedLabel.setText("12");
    }
}
*/
package com.example.login;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.example.manager.FileStorage;

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
