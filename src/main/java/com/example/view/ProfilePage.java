package com.example.view;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

public class ProfilePage {

    private final Stage primaryStage;
    private final Scene previousScene;

    public ProfilePage(Stage stage, Scene previousScene) {
        this.primaryStage = stage;
        this.previousScene = previousScene;
    }

    public Scene getProfileScene() {
        String username = Session.getCurrentUsername();
        String email = Session.getCurrentEmail();
        int userId = Session.getCurrentUserId();
        String contactNo = Session.getCurrentContactNo();

        if ("guest".equals(username)) {
            Label title = new Label("You are browsing \nas guest");
            title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: black;");

            Label passLabel = new Label("Default guest Id: " + userId);
            passLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #1b4fa8;");

            Button backBtn = new Button("⬅ Back");
            backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-size: 16px;");
            backBtn.setOnAction(e -> primaryStage.setScene(previousScene));

            VBox card = new VBox(15, title, passLabel, backBtn);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(30));
            card.setMaxWidth(400);
            card.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 15;");

            StackPane root = new StackPane(card);
            root.setPadding(new Insets(50));
            LinearGradient gradient = new LinearGradient(
                    0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#49bad8")),
                    new Stop(1, Color.web("#1c71bd"))
            );
            root.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

            return new Scene(root, 1000, 700);
        }

        Label title = new Label("My Profile");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label userLabel = new Label("Username: " + username);
        Label emailLabel = new Label("Email: " + email);
        Label passLabel = new Label("Your Id: " + userId);
        Label contactLabel = new Label("Contact No: " + contactNo);

        userLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #1b4fa8;");
        emailLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #1b4fa8;");
        passLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #1b4fa8;");
        contactLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #1b4fa8;");

        Button editBtn = new Button("Edit Profile");
        editBtn.setStyle("-fx-background-color: #1b4fa8; -fx-text-fill: white; -fx-padding: 8 16; -fx-font-size: 14px;");
        //editBtn.setOnAction(e -> EditProfilePopup.show(primaryStage));
        editBtn.setOnAction(e -> EditProfilePopup.show(primaryStage, () -> {
            // Refresh ProfilePage with updated Session values
            primaryStage.setScene(new ProfilePage(primaryStage, previousScene).getProfileScene());
        }));

        Button backBtn = new Button("⬅ Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-size: 16px;");
        backBtn.setOnAction(e -> primaryStage.setScene(previousScene));

        VBox card = new VBox(15, title, userLabel, emailLabel, contactLabel, passLabel, editBtn, backBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setMaxWidth(400);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 15;");

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(50));
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#49bad8")),
                new Stop(1, Color.web("#1c71bd"))
        );
        root.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        return new Scene(root, 1000, 700);
    }
}
