package com.example.login;

import com.example.munchoak.FileStorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Label statusLabel;
    @FXML private Button backButton;


//    private void handleRegister() {
//        String username = usernameField.getText().trim();
//        String email = emailField.getText().trim();
//        String password = passwordField.getText();
//        String confirmPassword = confirmPasswordField.getText();
//
//        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
//            statusLabel.setTextFill(Color.RED);
//            statusLabel.setText("All fields are required!");
//            return;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            statusLabel.setTextFill(Color.RED);
//            statusLabel.setText("Passwords do not match!");
//            return;
//        }
//
//        if (!email.contains("@") || !email.contains(".")) {
//            statusLabel.setTextFill(Color.RED);
//            statusLabel.setText("Invalid email address!");
//            return;
//        }
//
//        try {
//            File file = new File("src/main/resources/data/users.dat");
//            if (!file.exists()) file.getParentFile().mkdirs();
//
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split(",");
//                if (parts[0].equals(username) || parts[1].equals(email)) {
//                    statusLabel.setTextFill(Color.RED);
//                    statusLabel.setText("Username or email already exists!");
//                    reader.close();
//                    return;
//                }
//            }
//            reader.close();
//
//            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
//            writer.write(username + "," + email + "," + password);
//            writer.newLine();
//            writer.close();
//
//            statusLabel.setTextFill(Color.GREEN);
//            statusLabel.setText("Registration successful!");
//
//            usernameField.clear();
//            emailField.clear();
//            passwordField.clear();
//            confirmPasswordField.clear();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            statusLabel.setTextFill(Color.RED);
//            statusLabel.setText("File error occurred!");
//        }
//    }
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = "unknown"; // or get from a field

        if (username.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both username and password.");
            alert.showAndWait();
            return;
        }

        try {
            if (FileStorage.userExists(username)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Signup Failed");
                alert.setHeaderText(null);
                alert.setContentText("Username already exists!");
                alert.showAndWait();
                return;
            }

            FileStorage.appendUser(username, email, password); // handles binary writing automatically

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("User registered successfully!");
            alert.showAndWait();

//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/login/login.fxml"));
//            Scene scene = new Scene(fxmlLoader.load());
//            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//            stage.setScene(scene);
//            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while registering the user.");
            alert.showAndWait();
        }
    }


    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backButtonActionPerformed() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/welcome.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Welcome");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
