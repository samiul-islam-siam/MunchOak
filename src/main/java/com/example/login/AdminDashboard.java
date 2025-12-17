package com.example.login;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import com.example.menu.MenuPage;
import com.example.view.HomePage;
import com.example.view.LoginPage;
import com.example.view.ProfilePage;
import com.example.view.AddCouponPopup;
import com.example.view.EditCouponPopup;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdminDashboard {

    private static Stage primaryStage = new Stage();

    public AdminDashboard(Stage stage) {
        primaryStage = stage;
    }

    private static HBox topBar;

    // Place this outside openAdminDashboard(), anywhere inside AdminDashboard class
    private static void showRequestPopup(String requestText) {
        Stage popup = new Stage();
        popup.setTitle("Additional Request");

        Label label = new Label(requestText == null || requestText.isBlank()
                ? "No additional request provided."
                : requestText);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        VBox box = new VBox(label);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        Scene scene = new Scene(box, 400, 200);
        popup.setScene(scene);
        popup.show();
    }

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
        Button profileBtn = new Button("Profile");
        Button reservationBtn = new Button("Reservation");   // ✅ NEW button
        Button addCouponBtn = new Button("Add Coupon");
        addCouponBtn.setOnAction(e -> AddCouponPopup.show(primaryStage));
        Button editCouponBtn = new Button("Edit Coupon");
        editCouponBtn.setOnAction(e -> EditCouponPopup.show(primaryStage));
        Button logoutBtn = new Button("Logout");

        // --- Button Styling ---
        for (Button btn : new Button[]{viewUsersBtn, manageMenuBtn, chatServerBtn, changePassBtn, profileBtn, reservationBtn, addCouponBtn, editCouponBtn, logoutBtn}) {
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
                viewUsersBtn, manageMenuBtn, chatServerBtn, changePassBtn, profileBtn, reservationBtn, addCouponBtn, editCouponBtn, logoutBtn
        );
        // --- Button Actions ---
        profileBtn.setOnAction(e -> {
            ProfilePage profilePage = new ProfilePage(primaryStage, primaryStage.getScene());
            primaryStage.setScene(profilePage.getProfileScene());
        });


        reservationBtn.setOnAction(e -> {

            TableView<FileStorage.ReservationRecord> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            Session.setReservationListener(() -> {
                table.getItems().setAll(FileStorage.loadReservations());
                table.refresh();
            });

            TableColumn<FileStorage.ReservationRecord, String> usernameCol = new TableColumn<>("Username");
            usernameCol.setStyle("-fx-alignment: CENTER_LEFT;");
            usernameCol.setPrefWidth(120);
            usernameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().username));

            TableColumn<FileStorage.ReservationRecord, String> userIdCol = new TableColumn<>("User ID");
            userIdCol.setStyle("-fx-alignment: CENTER_LEFT;");
            userIdCol.setPrefWidth(100);
            userIdCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().userId)));

            TableColumn<FileStorage.ReservationRecord, String> nameCol = new TableColumn<>("Name");
            nameCol.setStyle("-fx-alignment: CENTER_LEFT;");
            nameCol.setPrefWidth(90);
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name));

            TableColumn<FileStorage.ReservationRecord, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setStyle("-fx-alignment: CENTER_LEFT;");
            phoneCol.setPrefWidth(120);
            phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().phone));

            TableColumn<FileStorage.ReservationRecord, String> guestsCol = new TableColumn<>("Guests");
            guestsCol.setStyle("-fx-alignment: CENTER;");
            guestsCol.setPrefWidth(85);
            guestsCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().guests)));

            TableColumn<FileStorage.ReservationRecord, String> dateCol = new TableColumn<>("Date");
            dateCol.setStyle("-fx-alignment: CENTER_LEFT;");
            dateCol.setPrefWidth(105);
            dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().date));

            TableColumn<FileStorage.ReservationRecord, String> timeCol = new TableColumn<>("Time");
            timeCol.setStyle("-fx-alignment: CENTER;");
            timeCol.setPrefWidth(85);
            timeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().time));

            TableColumn<FileStorage.ReservationRecord, Void> reqCol = new TableColumn<>("Requests");
            reqCol.setStyle("-fx-alignment: CENTER;");
            reqCol.setPrefWidth(90);
            reqCol.setCellFactory(tc -> new TableCell<>() {
                private final Button viewBtn = new Button("View");

                {
                    viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
                    viewBtn.setOnAction(e -> {
                        FileStorage.ReservationRecord rec = getTableView().getItems().get(getIndex());
                        showRequestPopup(rec.request);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(viewBtn);
                    }
                }
            });

            TableColumn<FileStorage.ReservationRecord, String> statusCol = new TableColumn<>("Status");
            statusCol.setStyle("-fx-alignment: CENTER;");
            statusCol.setPrefWidth(110); // slightly wider
            statusCol.setCellValueFactory(data -> new SimpleStringProperty(
                    FileStorage.getReservationStatus(data.getValue().resId)
            ));

            TableColumn<FileStorage.ReservationRecord, Void> actionCol = new TableColumn<>("Action");
            actionCol.setPrefWidth(180);
            actionCol.setCellFactory(col -> new TableCell<>() {
                private final Button acceptBtn = new Button("Accept");
                private final Button rejectBtn = new Button("Reject");
                private final HBox box = new HBox(5, acceptBtn, rejectBtn);

                {
                    //box.setAlignment(Pos.CENTER);
                    box.setAlignment(Pos.CENTER);
                    box.setSpacing(5);
                    acceptBtn.setMinWidth(50);
                    rejectBtn.setMinWidth(50);

                    acceptBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                    rejectBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                    acceptBtn.setOnAction(evt -> handleDecision("Accepted"));
                    rejectBtn.setOnAction(evt -> handleDecision("Rejected"));
                }

                private void handleDecision(String decision) {
                    FileStorage.ReservationRecord rec = getTableView().getItems().get(getIndex());
                    FileStorage.setReservationStatus(rec.resId, decision);
                    String message;

                    if ("Accepted".equals(decision)) {
                        message =
                                "Hello " + rec.username + ",\n\n" +
                                        "Your reservation has been ACCEPTED successfully.\n\n" +
                                        "Reservation Details:\n" +
                                        "👥 Guests: " + rec.guests + "\n" +
                                        "📅 Date: " + rec.date + "\n" +
                                        "⏰ Time: " + rec.time + "\n\n" +
                                        "Thank you for choosing us.\n" +
                                        "We look forward to serving you.\n\n" +
                                        "— MUNCH-OAK Team";
                    } else {
                        message =
                                "Hello " + rec.username + ",\n\n" +
                                        "We are sorry to inform you that your reservation has been REJECTED.\n\n" +
                                        "Reservation Details:\n" +
                                        "👥 Guests: " + rec.guests + "\n" +
                                        "📅 Date: " + rec.date + "\n" +
                                        "⏰ Time: " + rec.time + "\n\n" +
                                        "Please try again with a different date or time.\n\n" +
                                        "— MUNCH-OAK Team";
                    }

                    // 🔒 SAVE ONLY FOR THIS USER
                    FileStorage.saveMessageForUser(
                            rec.userId,
                            "Admin",
                            message
                    );
                    Session.getMenuClient().sendMessageUpdate();
                    Session.getMenuClient().sendReservationUpdate();
                    getTableView().refresh();

                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        FileStorage.ReservationRecord rec = getTableView().getItems().get(getIndex());
                        String current = FileStorage.getReservationStatus(rec.resId);

                        if ("Accepted".equals(current)) {
                            acceptBtn.setDisable(true);
                            rejectBtn.setDisable(false);
                        } else if ("Rejected".equals(current)) {
                            rejectBtn.setDisable(true);
                            acceptBtn.setDisable(false);
                        } else {
                            // Pending or undecided
                            acceptBtn.setDisable(false);
                            rejectBtn.setDisable(false);
                        }
                        setGraphic(box);
                    }
                }
            });

            table.getColumns().addAll(
                    usernameCol, userIdCol, nameCol, phoneCol, guestsCol, dateCol, timeCol, reqCol, statusCol, actionCol
            );

            table.getItems().setAll(FileStorage.loadReservations());

            centerPane.setCenter(table);
        });


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
            idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[4]));
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

            centerPane.setCenter(table);
        });

        manageMenuBtn.setOnAction(e -> {
            MenuPage menuPage = new MenuPage(primaryStage);
            primaryStage.setScene(menuPage.getMenuScene());
        });

        AtomicBoolean isChatWindowOpen = new AtomicBoolean(false);
        chatServerBtn.setOnAction(event -> {
            HomePage homePage = new HomePage(primaryStage);
            if (!isChatWindowOpen.get()) {
                homePage.openChatWindow();
                isChatWindowOpen.set(true);
            }
        });

        changePassBtn.setOnAction(e ->
                ChangeAdminPasswordPage.show(primaryStage)
        );

        logoutBtn.setOnAction(e -> {
            Session.logout();
            HomePage.closeChatWindow();
            primaryStage.setScene(new LoginPage(primaryStage).getLoginScene());
        });
    }
}
