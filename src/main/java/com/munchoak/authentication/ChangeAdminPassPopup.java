package com.munchoak.authentication;

import com.munchoak.mainpage.AdminHome;
import com.munchoak.manager.AdminStorage;
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
import javafx.stage.Stage;

import java.io.IOException;

public class ChangeAdminPassPopup {

    public static void show(Stage ownerStage) {
        Stage popup = new Stage();
        popup.initOwner(ownerStage);
        popup.setTitle("Change Admin Password");

        Label title = new Label("Change Admin Password");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: black;");

        TextField adminIDField = new TextField();
        adminIDField.setPromptText("Enter Admin ID");
        adminIDField.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px;");

        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Enter new password");
        newPassField.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px;");

        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm new password");
        confirmPassField.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px;");

        Label prefix = new Label("Password Strength:");
        prefix.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        Label strengthWord = new Label();
        strengthWord.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        HBox strengthBox = new HBox(5, prefix, strengthWord);
        strengthBox.setAlignment(Pos.CENTER);

        Label rulesLabel = new Label(PasswordPolicy.rulesText());
        rulesLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: black; -fx-font-weight: bold;");
        rulesLabel.setWrapText(true);
        rulesLabel.setMaxWidth(380);
        rulesLabel.setMinHeight(Region.USE_PREF_SIZE);

        Label status = new Label();
        status.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        Button saveBtn = new Button("Save");
        Button backBtn = new Button("Cancel");
        saveBtn.setStyle("-fx-background-color: #00c9ff; -fx-text-fill: black; -fx-font-size: 14px;");
        backBtn.setStyle("-fx-background-color: #00c9ff; -fx-text-fill: black; -fx-font-size: 14px;");

        newPassField.textProperty().addListener((obs, oldText, newText) ->
                strengthWord.setText(PasswordPolicy.strengthWord(newText))
        );

        saveBtn.setOnAction(e -> {
            String adminID = adminIDField.getText().trim();
            String newPass = newPassField.getText().trim();
            String confirmPass = confirmPassField.getText().trim();

            if (adminID.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                showTemp(status, "Fields cannot be empty!", "-fx-text-fill: blue; -fx-font-weight: bold; -fx-font-size: 14px;", 1);
                return;
            }

            if (!AdminStorage.ADMIN_ID.equals(adminID)) {
                showTemp(status, "Invalid Admin ID!", "-fx-text-fill: blue; -fx-font-weight: bold; -fx-font-size: 14px;", 1);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                showTemp(status, "Passwords do not match!", "-fx-text-fill: blue; -fx-font-weight: bold; -fx-font-size: 14px;", 1);
                return;
            }

            if (!PasswordPolicy.isValid(newPass)) {
                status.setText(PasswordPolicy.rulesText());
                status.setStyle("-fx-text-fill: black; -fx-font-size: 13px; -fx-font-weight: bold;");
                status.setWrapText(true);
                status.setMaxWidth(380);
                status.setMinHeight(Region.USE_PREF_SIZE);
                clearAfter(status, 1);
                return;
            }

            try {
                AdminStorage.setAdminPassword(newPass);

                status.setText("Password changed successfully!");
                status.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

                javafx.animation.PauseTransition pause =
                        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
                pause.setOnFinished(ev -> {
                    popup.close();
                });
                pause.play();
            } catch (IOException ex) {
                status.setText("Error saving password! Please try again later.");
                status.setStyle("-fx-text-fill: red;");
                System.err.println("IOException: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> popup.close());

        VBox layout = new VBox(15, title, adminIDField, newPassField, confirmPassField, strengthBox, rulesLabel, saveBtn, backBtn, status);
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

    private static void showTemp(Label label, String text, String style, int seconds) {
        label.setText(text);
        label.setStyle(style);
        clearAfter(label, seconds);
    }

    private static void clearAfter(Label label, int seconds) {
        javafx.animation.PauseTransition clear =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(seconds));
        clear.setOnFinished(ev -> label.setText(""));
        clear.play();
    }
}