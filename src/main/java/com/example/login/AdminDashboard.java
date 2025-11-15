package com.example.login;

import com.example.manager.AdminFileStorage;
import com.example.view.LoginPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AdminDashboard {

    public static void show(Stage stage)
    {
        Label title = new Label("Welcome to the Admin Dashboard!");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: white; -fx-font-weight: bold;");

        // --- Buttons ---
        Button viewUsersBtn = new Button("View All Users");
        Button countUsersBtn = new Button("Count Users");
        Button manageMenuBtn = new Button("Manage Menu");
        Button changePassBtn = new Button("Change Admin Password");
        Button changeIdBtn = new Button("Change Admin ID");
        Button viewHistoryBtn = new Button("View History");
        Button logoutBtn = new Button("Logout");

        // --- Styling all buttons consistently ---
        Button[] buttons = {viewUsersBtn, countUsersBtn, manageMenuBtn, changePassBtn, changeIdBtn, viewHistoryBtn, logoutBtn};
        for (Button btn : buttons) {
            btn.setStyle("""
                -fx-font-size: 14px;
                -fx-pref-width: 220px;
                -fx-background-color: #ffffff;
                -fx-text-fill: #001F3F;
                -fx-background-radius: 8;
                -fx-font-weight: bold;
            """);
        }

        // --- Button Actions ---
        viewUsersBtn.setOnAction(e -> {
            List<String> users = AdminFileStorage.getAllUsers();
            TextArea area = new TextArea();
            area.setEditable(false);
            area.setText(users.isEmpty() ? "No users found." : String.join("\n", users));
            Button back = new Button("Back");
            back.setOnAction(ev -> show(stage));
            VBox layout = new VBox(15, new Label("All Registered Users:"), area, back);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));
            stage.setScene(new Scene(layout, 500, 400));
        });

        countUsersBtn.setOnAction(e -> {
            int count = AdminFileStorage.countUsers();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Total Users: " + count);
            alert.showAndWait();
        });

        manageMenuBtn.setOnAction(e -> ManageMenuPage.show(stage));
        changePassBtn.setOnAction(e -> ChangeAdminPasswordPage.show(stage));

        changeIdBtn.setOnAction(e -> showChangeAdminID(stage));
        changeIdBtn.setStyle("-fx-font-size: 14px; -fx-pref-width: 220px; -fx-background-color: white; -fx-text-fill: #001F3F;");

        viewHistoryBtn.setOnAction(e -> {
            List<String> logs = AdminFileStorage.readLog();
            TextArea area = new TextArea();
            area.setEditable(false);
            area.setText(logs.isEmpty() ? "No history found." : String.join("\n", logs));
            Button back = new Button("Back");
            back.setOnAction(ev -> show(stage));
            VBox layout = new VBox(15, new Label("Admin Action History:"), area, back);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));
            stage.setScene(new Scene(layout, 600, 400));
        });

        logoutBtn.setOnAction(e -> {
            LoginPage loginPage = new LoginPage(stage);
            stage.setScene(loginPage.getLoginScene());
        });

        // --- Layout ---
        VBox layout = new VBox(15, title, viewUsersBtn, countUsersBtn, manageMenuBtn,
                changePassBtn, changeIdBtn, viewHistoryBtn, logoutBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: linear-gradient(to right, #001F3F, #0074D9);");

        Scene scene = new Scene(layout, 800, 650);
        stage.setTitle("Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    // --- Change Admin ID page ---
    private static void showChangeAdminID(Stage stage) {
        List<String> users = AdminFileStorage.getAllUsers();

        if (users.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("No users found.");
            alert.showAndWait();
            return;
        }

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Change User Unique ID");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ComboBox<String> userCombo = new ComboBox<>();
        userCombo.getItems().addAll(users);
        userCombo.setPromptText("Select User");

        TextField newIdField = new TextField();
        newIdField.setPromptText("Enter new Unique ID");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");

        Button saveBtn = new Button("Save");
        Button backBtn = new Button("Back");

        saveBtn.setOnAction(e -> {
            String selectedUser = userCombo.getValue();
            String newId = newIdField.getText().trim();

            if (selectedUser == null || newId.isEmpty()) {
                statusLabel.setText("Please select a user and enter a new ID.");
                return;
            }

            try {
                boolean success = AdminFileStorage.updateAdminUniqueID(selectedUser, newId);
                if (success) {
                    statusLabel.setStyle("-fx-text-fill: green;");
                    statusLabel.setText("Unique ID updated successfully!");
                } else {
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText("Error updating Unique ID.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Error updating Unique ID.");
            }
        });

        backBtn.setOnAction(e -> show(stage));

        root.getChildren().addAll(title, userCombo, newIdField, saveBtn, backBtn, statusLabel);
        stage.setScene(new Scene(root, 400, 300));
    }
}
