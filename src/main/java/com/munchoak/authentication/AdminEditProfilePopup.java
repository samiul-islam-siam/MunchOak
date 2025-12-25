package com.munchoak.authentication;

import com.munchoak.manager.AdminStorage;
import com.munchoak.manager.Session;
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

import java.io.IOException;

public class AdminEditProfilePopup {

    public static void show(Stage owner, Runnable onProfileUpdated) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Edit Admin Profile");

        VBox layout = new VBox(12);
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setPadding(new Insets(30));
        layout.setMaxWidth(400);
        layout.setPrefWidth(400);

        // Common fields
        Label usernameLabel = new Label("Username:");
        Label emailLabel = new Label("Email:");
        Label contactLabel = new Label("Contact No:");
        Label newPassLabel = new Label("New Password:");
        Label confirmPassLabel = new Label("Confirm Password:");

        for (Label label : new Label[]{usernameLabel, emailLabel, contactLabel, newPassLabel, confirmPassLabel}) {
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black;");
            label.setPrefWidth(120);
        }

        TextField usernameField = new TextField(Session.getCurrentUsername());
        TextField emailField = new TextField(Session.getCurrentEmail());
        TextField contactField = new TextField(Session.getCurrentContactNo());
        PasswordField newPassField = new PasswordField();
        PasswordField confirmPassField = new PasswordField();

        newPassField.setPromptText("Enter new password");
        confirmPassField.setPromptText("Confirm new password");

        Label status = new Label();
        status.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        status.setAlignment(Pos.CENTER);
        status.setMaxWidth(Double.MAX_VALUE);

        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #1b4fa8; -fx-text-fill: white; -fx-font-size: 14px;");
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 14px;");
        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonRow = new HBox(20, saveBtn, cancelBtn);
        buttonRow.setAlignment(Pos.CENTER);

        saveBtn.setOnAction(e -> {
            String newUser = usernameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newContact = contactField.getText().trim();
            String newPass = newPassField.getText().trim();
            String confirmPass = confirmPassField.getText().trim();

            // Preserve old values if blank
            if (newUser.isEmpty()) newUser = Session.getCurrentUsername();
            if (newEmail.isEmpty()) newEmail = Session.getCurrentEmail();
            if (newContact.isEmpty()) newContact = Session.getCurrentContactNo();

            // Password update only if provided
            if (!newPass.isEmpty() || !confirmPass.isEmpty()) {
                if (!newPass.equals(confirmPass)) {
                    showTempStatus(status, "Passwords do not match!", Color.BLUE);
                    return;
                }
                // In AdminEditProfilePopup.java
                AdminStorage.updateAdminPassword(AdminStorage.ADMIN_ID, newPass);

                showTempStatus(status, "Password updated successfully!", Color.WHITE);

                javafx.animation.PauseTransition closeDelay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                closeDelay.setOnFinished(ev -> popup.close());
                closeDelay.play();

            }

            // Update admin profile info safely
            AdminStorage.updateAdminInfo(newUser, newEmail, newContact);
            Session.getMenuClient().sendAdminFileUpdate();

            // Update Session
            try {
                Session.refreshAdminFromFile();
            } catch (IOException ex) {
                showTempStatus(status, "Failed to refresh admin session!", Color.RED);
                return;
            }

            showTempStatus(status, "Admin profile updated successfully!", Color.WHITE);
            if (onProfileUpdated != null) onProfileUpdated.run();
        });

        layout.getChildren().addAll(
                usernameLabel, usernameField,
                emailLabel, emailField,
                contactLabel, contactField,
                newPassLabel, newPassField,
                confirmPassLabel, confirmPassField,
                buttonRow, status
        );

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#20B2AA")),
                new Stop(1, Color.web("#40E0D0"))
        );
        layout.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        popup.setScene(new Scene(layout, 450, 600));
        popup.show();
    }

    private static void showTempStatus(Label status, String message, Color color) {
        status.setText(message);
        status.setTextFill(color);
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
        pause.setOnFinished(ev -> status.setText(""));
        pause.play();
    }
}

