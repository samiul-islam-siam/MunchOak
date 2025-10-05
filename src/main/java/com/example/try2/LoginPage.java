package com.example.try2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginPage {

    public Scene getLoginScene(Stage primaryStage, HelloApplication app) {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #2c3e50;");

        // TOP bar with close button
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_RIGHT);

        Button closeButton = new Button("✖");
        closeButton.setStyle("-fx-font-size: 14px; -fx-background-color: transparent; -fx-text-fill: white;");
        closeButton.setOnAction(e -> primaryStage.setScene(app.getMainScene()));

        topBar.getChildren().add(closeButton);
        borderPane.setTop(topBar);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userName = new Label("Username:");
        userName.setTextFill(Color.WHITE);
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        pw.setTextFill(Color.WHITE);
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button loginBtn = new Button("Login");
        grid.add(loginBtn, 1, 3);

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);
        grid.add(messageLabel, 1, 4);

        // Return button
        Button returnBtn = new Button("Return");
        returnBtn.setStyle("-fx-background-color: #444; -fx-text-fill: white;");
        grid.add(returnBtn, 0, 3);

        returnBtn.setOnAction(e -> primaryStage.setScene(app.getMainScene()));

        loginBtn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();

            if (username.equals("admin") && password.equals("1234")) {
                messageLabel.setText("Login successful!");
                primaryStage.setScene(app.getMainScene());
            } else {
                messageLabel.setText("Invalid credentials");
            }
        });

        borderPane.setCenter(grid);

        return new Scene(borderPane, 1366, 768);
    }
}
