package com.example.try2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane; // ✅ add this
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class LoginPage {

    private final Stage primaryStage;

    public LoginPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getLoginScene() {
        // Root layout, full-screen like homepage
        BorderPane root = new BorderPane();
        root.setPrefSize(1366, 768);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1f4037, #99f2c8);");

        // --- Top bar with cross (❌) button ---
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_RIGHT);

        Button closeBtn = new Button("❌");
        closeBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-cursor: hand;
        """);
        closeBtn.setOnAction(e -> returnToHome());

        topBar.getChildren().add(closeBtn);
        root.setTop(topBar);

        // --- Center login form ---
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));

        Label title = new Label("User Login");
        title.setStyle("-fx-font-size: 32px; -fx-text-fill: white; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("""
            -fx-background-color: #27ae60;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-cursor: hand;
            -fx-padding: 8 20 8 20;
            -fx-background-radius: 10;
        """);

        // Optional Return button (also returns to home)
        Button returnBtn = new Button("← Return");
        returnBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-cursor: hand;
            -fx-underline: true;
        """);

        // Actions
        loginBtn.setOnAction(e -> {
            // Dummy login validation
            String username = usernameField.getText();
            String password = passwordField.getText();
            System.out.println("Login attempt: " + username + " / " + password);
            // For now, just return to home after login
            returnToHome();
        });

        returnBtn.setOnAction(e -> returnToHome());

        formBox.getChildren().addAll(title, usernameField, passwordField, loginBtn, returnBtn);
        root.setCenter(formBox);

        return new Scene(root, 1366, 768);
    }

    // Return to homepage
    private void returnToHome() {
        HomePage homePage = new HomePage(primaryStage);
        VBox fullPage = homePage.getFullPage();

        ScrollPane scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene homeScene = new Scene(scrollPane, 1366, 768);
        primaryStage.setScene(homeScene);

    }
}
