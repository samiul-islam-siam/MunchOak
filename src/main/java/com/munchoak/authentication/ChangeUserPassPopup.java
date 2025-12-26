package com.munchoak.authentication;

import com.munchoak.manager.Session;
import com.munchoak.manager.UserStorage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChangeUserPassPopup {

    public static void show(Stage ownerStage) {
        Stage popup = new Stage();
        popup.initOwner(ownerStage);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Change Password");

        Label rulesLabel = new Label(PasswordPolicy.rulesText());
        rulesLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: black; -fx-font-weight: normal;");
        rulesLabel.setWrapText(true);
        rulesLabel.setMaxWidth(380);
        rulesLabel.setMinHeight(Region.USE_PREF_SIZE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Enter new password");

        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm new password");

        Label prefix = new Label("Password Strength:");
        prefix.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label strengthWord = new Label();
        strengthWord.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #B8860B;");

        HBox strengthBox = new HBox(5, prefix, strengthWord);
        strengthBox.setAlignment(Pos.CENTER);

        Label status = new Label();
        status.setStyle("-fx-font-size: 14px;");

        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #1b4fa8; -fx-text-fill: white; -fx-font-size: 14px;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 14px;");
        cancelBtn.setOnAction(e -> popup.close());

        newPassField.textProperty().addListener((obs, oldVal, newVal) ->
                strengthWord.setText(PasswordPolicy.strengthWord(newVal))
        );

        saveBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String newPass = newPassField.getText().trim();
            String confirmPass = confirmPassField.getText().trim();

            if (username.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                showTempStatus(status, "Fields cannot be empty. Please fill all the fields!", Color.RED);
                return;
            }

            if (!UserStorage.userExists(username)) {
                showTempStatus(status, "User not found!", Color.RED);
                return;
            }

            if (!PasswordPolicy.isValid(newPass)) {
                showTempStatus(status, "Invalid password format!", Color.RED);
                return;
            }
            if (!newPass.equals(confirmPass)) {
                showTempStatus(status, "Passwords do not match!", Color.RED);
                return;
            }

            try {
                PasswordStorage.updateUserPassword(Session.getCurrentUsername(), newPass);
                Session.getMenuClient().sendUserFileUpdate();

                status.setText("Password changed successfully!");
                status.setTextFill(Color.GREEN);

                javafx.animation.PauseTransition closeDelay =
                        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
                closeDelay.setOnFinished(ev -> popup.close());
                closeDelay.play();
            } catch (Exception ex) {
                status.setText("Error updating password! Please try again later.");
                status.setTextFill(Color.RED);
                System.err.println("IOException: " + ex.getMessage());
            }
        });

        VBox layout = new VBox(15, usernameField, newPassField, confirmPassField, rulesLabel, strengthBox, saveBtn, cancelBtn, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#20B2AA")),
                new Stop(1, Color.web("#40E0D0"))
        );
        layout.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        popup.setScene(new Scene(layout, 420, 460));
        popup.show();
    }

    private static void showTempStatus(Label status, String message, Color color) {
        status.setText(message);
        status.setTextFill(color);
        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        pause.setOnFinished(ev -> status.setText(""));
        pause.play();
    }
}