package com.example.login;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import com.example.menu.MenuPage;
import com.example.view.HomePage;
import com.example.view.LoginPage;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class AdminDashboard {

    private static Stage primaryStage = new Stage();

    public AdminDashboard(Stage stage) {
        primaryStage = stage;
    }
    private static HBox topBar;

    public static void openAdminDashboard() {

        BorderPane dashboard = new BorderPane();

        dashboard.setStyle("-fx-background-color: lightyellow;");






        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);

        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 36px; -fx-text-fill: black; -fx-font-family: 'Arial Black'; -fx-font-weight: bold;");
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10, 0, 0, 0)); // Top spacing

        Label infoLabel = new Label("Select an action from the left menu.");
        infoLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Georgia';");

        VBox infoBox = new VBox(infoLabel);
        infoBox.setAlignment(Pos.CENTER); // Vertically center the instruction

        BorderPane centerPane = new BorderPane();
        centerPane.setTop(titleBox);
        centerPane.setCenter(infoBox);
        centerPane.setStyle("-fx-background-color: transparent;");



        // --- Left Menu ---
        VBox menuBox = new VBox(15);
        menuBox.setPadding(new Insets(30));

        menuBox.setStyle("-fx-background-color: transparent;");

        menuBox.setAlignment(Pos.CENTER);

        Button viewUsersBtn = new Button("View All Users");
        Button manageMenuBtn = new Button("Manage Menu");
        Button chatServerBtn = new Button("Chat With users");
        Button changePassBtn = new Button("Change Password");
        Button logoutBtn = new Button("Logout");

        // --- Button Styling ---
        for (Button btn : new Button[]{viewUsersBtn, manageMenuBtn, chatServerBtn, changePassBtn, logoutBtn}) {

            btn.setStyle(
                    "-fx-background-color: #b30000;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bolder;" +
                            "-fx-pref-width: 200;" +
                            "-fx-padding: 12 0;" +
                            "-fx-background-radius: 25;"
            );

            btn.setOnMouseEntered(e -> {
                btn.setStyle(
                        "-fx-background-color: #ff4d4d;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-size: 18px;" +          // keep same size
                                "-fx-font-weight: bolder;" +      // keep same boldness
                                "-fx-pref-width: 200;" +
                                "-fx-padding: 12 0;" +
                                "-fx-background-radius: 25;"
                );
                btn.setEffect(new DropShadow(10, Color.DARKRED));
            });

            btn.setOnMouseExited(e -> {
                btn.setStyle(
                        "-fx-background-color: #b30000;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 18px;" +
                                "-fx-font-weight: bolder;" +
                                "-fx-pref-width: 200;" +
                                "-fx-padding: 12 0;" +
                                "-fx-background-radius: 25;"
                );
                btn.setEffect(null);
            });

        }

        menuBox.getChildren().addAll(
                viewUsersBtn, manageMenuBtn, chatServerBtn, changePassBtn, logoutBtn
        );


        VBox leftPanel = new VBox(menuBox);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(0));
        leftPanel.setStyle("-fx-background-color: #b30000;");
        leftPanel.setPrefWidth(250); // Optional: controls left panel width
        dashboard.setLeft(leftPanel);

        dashboard.setCenter(centerPane);

        Scene scene = new Scene(dashboard, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
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

            //centerPane.getChildren().setAll(table);
            centerPane.setCenter(table);
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
