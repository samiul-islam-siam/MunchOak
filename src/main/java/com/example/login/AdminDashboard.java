package com.example.login;

import com.example.manager.Session;
import com.example.menu.MenuPage;
import com.example.manager.FileStorage;

import com.example.view.HomePage;
import com.example.view.LoginPage;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class AdminDashboard {

    private static Stage primaryStage = new Stage();

    public AdminDashboard(Stage stage) {
        primaryStage = stage;
    }

    public static void openAdminDashboard() {

        BorderPane dashboard = new BorderPane();
        dashboard.setStyle("-fx-background-color: linear-gradient(to right, #000428, #004e92);");

        // --- Top Bar ---
        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold;");
        HBox topBar = new HBox(title);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(20, 0, 20, 0));

        // --- Center Content ---
        Label infoLabel = new Label("Select an action from the left menu.");
        infoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        StackPane centerPane = new StackPane(infoLabel);

        // --- Left Menu ---
        VBox menuBox = new VBox(15);
        menuBox.setPadding(new Insets(30));
        menuBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-pref-width: 220;");
        menuBox.setAlignment(Pos.TOP_CENTER);

        Button viewUsersBtn = new Button("View All Users");
        Button manageMenuBtn = new Button("Manage Menu");
        Button chatServerBtn = new Button("Chat With users");
        Button changePassBtn = new Button("Change Password");
        Button logoutBtn = new Button("Logout");

        // --- Button Styling ---
        for (Button btn : new Button[]{viewUsersBtn, manageMenuBtn, chatServerBtn, changePassBtn, logoutBtn}) {
            btn.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-pref-width: 180; -fx-padding: 10 0; -fx-background-radius: 25;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #63B3ED; -fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; -fx-pref-width: 180; -fx-padding: 10 0; -fx-background-radius: 25;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-pref-width: 180; -fx-padding: 10 0; -fx-background-radius: 25;"));
        }

        menuBox.getChildren().addAll(
                viewUsersBtn, manageMenuBtn, chatServerBtn, changePassBtn, logoutBtn
        );

        dashboard.setTop(topBar);
        dashboard.setLeft(menuBox);
        dashboard.setCenter(centerPane);

        Scene scene = new Scene(dashboard, 1000, 700);
        primaryStage.setScene(scene);

        /* ------ Button Actions ------ */

        viewUsersBtn.setOnAction(e -> {
            List<String[]> users = FileStorage.loadUsers();
            TableView<String[]> table = new TableView<>();

            TableColumn<String[], String> idCol = new TableColumn<>("User ID");
            idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));
            idCol.setPrefWidth(100);

            TableColumn<String[], String> usernameCol = new TableColumn<>("Username");
            usernameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
            usernameCol.setPrefWidth(200);

            TableColumn<String[], String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
            emailCol.setPrefWidth(250);

            TableColumn<String[], String> passwordCol = new TableColumn<>("Password");
            passwordCol.setCellValueFactory(data -> new SimpleStringProperty("********"));
            passwordCol.setPrefWidth(150);

            table.getColumns().addAll(idCol, usernameCol, emailCol, passwordCol);
            table.getItems().addAll(users);
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

            centerPane.getChildren().setAll(table);
        });

        manageMenuBtn.setOnAction(e -> {
            MenuPage menuPage = new MenuPage(primaryStage);
            primaryStage.setScene(menuPage.getMenuScene());
        });

        chatServerBtn.setOnAction(event -> {
            HomePage homePage = new HomePage(primaryStage);
            homePage.openChatWindow();
        });

        changePassBtn.setOnAction(e ->
                ChangeAdminPasswordPage.show(primaryStage)
        );

        logoutBtn.setOnAction(e -> {
            Session.logout();
            primaryStage.setScene(new LoginPage(primaryStage).getLoginScene());
        });
    }
}
