package com.example.munchoak;

import com.example.authentication.ChangeAdminPasswordPage;
import com.example.authentication.LoginPage;
import com.example.authentication.ProfilePage;
import com.example.homepage.HomePage;
import com.example.manager.*;
import com.example.menu.MenuPage;
import com.example.payment.History;
import com.example.payment.PaymentStorage;
import com.example.view.AddCouponPopup;
import com.example.view.EditCouponPopup;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


public class AdminHome {
    private static BorderPane centerPane;

    private static Stage primaryStage = new Stage();

    public AdminHome(Stage stage) {
        primaryStage = stage;
    }

    private static HBox topBar;

    private static void showDeleteCouponPopup(TableView<CouponStorage.Coupon> table) {
        Stage popup = new Stage();
        popup.setTitle("Delete Coupon");
        popup.initOwner(primaryStage);
        popup.initModality(Modality.WINDOW_MODAL);

        // --- Dropdown ---
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPrefWidth(250);
        comboBox.setPromptText("Coupon Code");
        comboBox.getItems().addAll(
                CouponStorage.loadCoupons().stream()
                        .map(c -> c.code)
                        .toList()
        );

        // --- Label ---
        Label label = new Label("Select a Coupon to Delete:");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        // --- Delete button ---
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle(
                "-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-pref-width: 120;" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 8;"
        );

        deleteBtn.setOnAction(e -> {
            String selected = comboBox.getValue();
            if (selected == null || selected.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please select a coupon.", ButtonType.OK).showAndWait();
                return;
            }

            boolean deleted = CouponStorage.deleteCoupon(selected);
            Session.getMenuClient().sendCouponUpdate();
            if (deleted) {
                new Alert(Alert.AlertType.INFORMATION, "Coupon '" + selected + "' deleted successfully.", ButtonType.OK).showAndWait();
                table.getItems().setAll(CouponStorage.loadCoupons());
                table.refresh();
                popup.close();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete coupon.", ButtonType.OK).showAndWait();
            }
        });

        // --- Layout ---
        VBox box = new VBox(15, label, comboBox, deleteBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setMaxWidth(300);

        // ✅ Matching background
        BackgroundFill bgFill = new BackgroundFill(
                Color.web("#E0F7FA"), CornerRadii.EMPTY, Insets.EMPTY
        );
        box.setBackground(new Background(bgFill));

        popup.setScene(new Scene(box, 340, 220));
        popup.show();
    }

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

        centerPane = new BorderPane();
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
        //   Button addCouponBtn = new Button("Add Coupon");
        // addCouponBtn.setOnAction(e -> AddCouponPopup.show(primaryStage));
        //  Button editCouponBtn = new Button("Edit Coupon");
        // editCouponBtn.setOnAction(e -> EditCouponPopup.show(primaryStage));
        // --- Coupons Button in Sidebar ---
        Button couponsBtn = new Button("Coupons");


        Button logoutBtn = new Button("Logout");
        Button historyBtn = new Button("Payment History");
        historyBtn.setStyle("-fx-background-color: #b30000; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bolder; -fx-pref-width: 200; -fx-padding: 12 0; -fx-background-radius: 25;");
        menuBox.getChildren().add(historyBtn);

        historyBtn.setOnAction(e -> openAdminHistory(dashboard));

        // --- Button Styling ---
        for (Button btn : new Button[]{viewUsersBtn, manageMenuBtn, historyBtn, chatServerBtn, changePassBtn, profileBtn, reservationBtn, couponsBtn, logoutBtn}) {
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
                viewUsersBtn, manageMenuBtn, chatServerBtn, changePassBtn, profileBtn, reservationBtn, couponsBtn, logoutBtn
        );
        // --- Button Actions ---
        profileBtn.setOnAction(e -> {
            ProfilePage profilePage = new ProfilePage(primaryStage, primaryStage.getScene());
            primaryStage.setScene(profilePage.getProfileScene());
        });
        couponsBtn.setOnAction(e -> {
            VBox layout = new VBox();
            layout.setPadding(new Insets(30, 10, 05, 10));
            layout.setSpacing(30); // spacing between title, buttons, and table
            layout.setAlignment(Pos.TOP_CENTER);
            layout.setStyle("-fx-background-color: lightyellow;"); // keep default background

            // --- Buttons: Add, Edit, Delete ---
            HBox btnBox = new HBox(30);
            btnBox.setAlignment(Pos.CENTER);

            Button addBtn = new Button("Add coupon");
            Button editBtn = new Button("Edit coupon");
            Button deleteBtn = new Button("Delete coupon");

            // 🎨 Individual button styles
            addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-pref-width: 140; -fx-padding: 10 0; -fx-background-radius: 8;");
            editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-pref-width: 140; -fx-padding: 10 0; -fx-background-radius: 8;");
            deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-pref-width: 140; -fx-padding: 10 0; -fx-background-radius: 8;");


            btnBox.getChildren().addAll(addBtn, editBtn, deleteBtn);
            layout.getChildren().add(btnBox);
            // Label tableLabel = new Label("Available coupons"); tableLabel.setStyle("-fx-font-size: 27px; -fx-font-weight: bold; -fx-text-fill: black;"); layout.getChildren().add(tableLabel);
            Label tableLabel = new Label("Available coupons");
            tableLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-font-family: 'Arial Black'; -fx-text-fill: black;");
            layout.getChildren().add(tableLabel);

            // --- Table ---
            TableView<CouponStorage.Coupon> table = new TableView<>();
            addBtn.setOnAction(ev -> AddCouponPopup.show(primaryStage, () -> {
                table.getItems().setAll(CouponStorage.loadCoupons());
                table.refresh();
            }));

            editBtn.setOnAction(ev -> EditCouponPopup.show(primaryStage, () -> {
                table.getItems().setAll(CouponStorage.loadCoupons());
                table.refresh();
            }));

            deleteBtn.setOnAction(ev -> showDeleteCouponPopup(table));

            table.setPrefWidth(Double.MAX_VALUE);     // ✅ full width
            table.setMaxWidth(Double.MAX_VALUE);
            table.setPrefHeight(1000);                 // ✅ adjustable height
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            VBox.setVgrow(table, Priority.ALWAYS);    // ✅ allow vertical stretch
            table.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 8;");

            TableColumn<CouponStorage.Coupon, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().code));
            TableColumn<CouponStorage.Coupon, String> discountCol = new TableColumn<>("Discount");
            discountCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().discount)));

            TableColumn<CouponStorage.Coupon, String> expiryCol = new TableColumn<>("Expiry Date");
            expiryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().expiry));
            TableColumn<CouponStorage.Coupon, String> usageCol = new TableColumn<>("Usage Limit");
            usageCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().usageLimit)));
            TableColumn<CouponStorage.Coupon, String> remainingCol = new TableColumn<>("Remaining");
            remainingCol.setCellValueFactory(data -> {
                CouponStorage.Coupon c = data.getValue();
                int remaining = Math.max(0, c.usageLimit - c.usedCount);
                return new SimpleStringProperty(String.valueOf(remaining));
            });

            table.getColumns().addAll(nameCol, discountCol, expiryCol, usageCol, remainingCol);
            table.getItems().setAll(CouponStorage.loadCoupons());
            ScrollPane scrollPane = new ScrollPane(table);
            scrollPane.setFitToWidth(true);
            // scrollPane.setFitToHeight(true);
            scrollPane.setPrefHeight(600);
            scrollPane.setStyle("-fx-background: transparent;");


            layout.getChildren().add(table);


            centerPane.setCenter(layout);
        });

        reservationBtn.setOnAction(e -> {

            TableView<ReservationStorage.ReservationRecord> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            Session.setReservationListener(() -> {
                table.getItems().setAll(ReservationStorage.loadReservations());
                table.refresh();
            });

            TableColumn<ReservationStorage.ReservationRecord, String> usernameCol = new TableColumn<>("Username");
            usernameCol.setStyle("-fx-alignment: CENTER_LEFT;");
            usernameCol.setPrefWidth(120);
            usernameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().username));

            TableColumn<ReservationStorage.ReservationRecord, String> userIdCol = new TableColumn<>("User ID");
            userIdCol.setStyle("-fx-alignment: CENTER_LEFT;");
            userIdCol.setPrefWidth(100);
            userIdCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().userId)));

            TableColumn<ReservationStorage.ReservationRecord, String> nameCol = new TableColumn<>("Name");
            nameCol.setStyle("-fx-alignment: CENTER_LEFT;");
            nameCol.setPrefWidth(90);
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name));

            TableColumn<ReservationStorage.ReservationRecord, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setStyle("-fx-alignment: CENTER_LEFT;");
            phoneCol.setPrefWidth(120);
            phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().phone));

            TableColumn<ReservationStorage.ReservationRecord, String> guestsCol = new TableColumn<>("Guests");
            guestsCol.setStyle("-fx-alignment: CENTER;");
            guestsCol.setPrefWidth(85);
            guestsCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().guests)));

            TableColumn<ReservationStorage.ReservationRecord, String> dateCol = new TableColumn<>("Date");
            dateCol.setStyle("-fx-alignment: CENTER_LEFT;");
            dateCol.setPrefWidth(105);
            dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().date));

            TableColumn<ReservationStorage.ReservationRecord, String> timeCol = new TableColumn<>("Time");
            timeCol.setStyle("-fx-alignment: CENTER;");
            timeCol.setPrefWidth(85);
            timeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().time));

            TableColumn<ReservationStorage.ReservationRecord, Void> reqCol = new TableColumn<>("Requests");
            reqCol.setStyle("-fx-alignment: CENTER;");
            reqCol.setPrefWidth(90);
            reqCol.setCellFactory(tc -> new TableCell<>() {
                private final Button viewBtn = new Button("View");

                {
                    viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
                    viewBtn.setOnAction(e -> {
                        ReservationStorage.ReservationRecord rec = getTableView().getItems().get(getIndex());
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

            TableColumn<ReservationStorage.ReservationRecord, String> statusCol = new TableColumn<>("Status");
            statusCol.setStyle("-fx-alignment: CENTER;");
            statusCol.setPrefWidth(110); // slightly wider
            statusCol.setCellValueFactory(data -> new SimpleStringProperty(
                    ReservationStorage.getReservationStatus(data.getValue().resId)
            ));

            TableColumn<ReservationStorage.ReservationRecord, Void> actionCol = new TableColumn<>("Action");
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
                    ReservationStorage.ReservationRecord rec = getTableView().getItems().get(getIndex());
                    ReservationStorage.setReservationStatus(rec.resId, decision);
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
                    MessageStorage.saveMessageForUser(
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
                        ReservationStorage.ReservationRecord rec = getTableView().getItems().get(getIndex());
                        String current = ReservationStorage.getReservationStatus(rec.resId);

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

            table.getItems().setAll(ReservationStorage.loadReservations());

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
            List<String[]> users = UserStorage.loadUsers();
            TableView<String[]> table = new TableView<>();

            TableColumn<String[], String> idCol = new TableColumn<>("User ID");
            idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[4]));
            idCol.setPrefWidth(100);

            TableColumn<String[], String> usernameCol = new TableColumn<>("Username");
            usernameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
            usernameCol.setPrefWidth(150);

            TableColumn<String[], String> userContactCol = new TableColumn<>("Contact No:");
            userContactCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
            userContactCol.setPrefWidth(200);

            TableColumn<String[], String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
            emailCol.setPrefWidth(200);

            TableColumn<String[], String> passwordCol = new TableColumn<>("Password");
            passwordCol.setCellValueFactory(data -> new SimpleStringProperty("********"));
            passwordCol.setPrefWidth(100);

            table.getColumns().addAll(idCol, usernameCol, userContactCol, emailCol, passwordCol);
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

    private static void openAdminHistory(BorderPane dashboard) {
        BorderPane centerPane = new BorderPane();

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));

        // --- Total Income ---
        double totalIncome = PaymentStorage.loadPaymentHistory()
                .stream().mapToDouble(r -> r.getAmount()).sum();
        Label totalIncomeLabel = new Label("Total Income: $" + String.format("%.2f", totalIncome));
        totalIncomeLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        totalIncomeLabel.setWrapText(true);

        VBox incomeBox = new VBox(totalIncomeLabel);
        incomeBox.setAlignment(Pos.CENTER_LEFT);
        incomeBox.setPadding(new Insets(10));
        incomeBox.setStyle("-fx-background-color: #f1f1f1; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;");
        incomeBox.setPrefWidth(300);

        // --- Daily Income Chart ---
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Income");
        BarChart<String, Number> dailyChart = new BarChart<>(xAxis, yAxis);
        dailyChart.setTitle("Daily Income");
        dailyChart.setLegendVisible(false);
        dailyChart.setPrefSize(600, 250);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
//        Map<String, Double> dailyIncome = FileStorage.loadPaymentHistory()
//                .stream()
//                .collect(Collectors.groupingBy(
//                        r -> r.timestamp.split("T")[0],
//                        Collectors.summingDouble(r -> r.amount)
//                ));
//        dailyIncome.forEach((date, income) -> series.getData().add(new XYChart.Data<>(date, income)));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

// Aggregate income by date
        Map<String, Double> incomeMap = PaymentStorage.loadPaymentHistory()
                .stream()
                .collect(Collectors.groupingBy(
                        r -> r.getTimestamp().split("T")[0],
                        Collectors.summingDouble(r -> r.getAmount())
                ));

// Get last 7 consecutive days (including today)
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);

            double income = incomeMap.getOrDefault(dateStr, 0.0);
            series.getData().add(new XYChart.Data<>(dateStr, income));
        }
        dailyChart.getData().add(series);
        dailyChart.setAnimated(false);
        dailyChart.setCategoryGap(20);
        dailyChart.setBarGap(5);

        VBox chartBox = new VBox(dailyChart);
        chartBox.setPadding(new Insets(10));
        chartBox.setStyle("-fx-background-color: #f1f1f1; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;");

        // --- Top HBox for Income + Chart ---
        HBox topBox = new HBox(20, incomeBox, chartBox);
        topBox.setAlignment(Pos.CENTER_LEFT);

        // --- History Table ---
        TableView<History.HistoryRecord> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(
                PaymentStorage.loadPaymentHistory().stream()
                        .map(s -> new History.HistoryRecord(
                                s.getUserId(), s.getPaymentId(), s.getTimestamp(), s.getAmount(), "Success", s.getPaymentMethod()))
                        .toList()
        ));

        TableColumn<History.HistoryRecord, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getUserId()).asObject());

        TableColumn<History.HistoryRecord, Integer> paymentIdCol = new TableColumn<>("Payment ID");
        paymentIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPaymentId()).asObject());

        TableColumn<History.HistoryRecord, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp()));

        TableColumn<History.HistoryRecord, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getAmount()).asObject());

        TableColumn<History.HistoryRecord, String> methodCol = new TableColumn<>("Payment Method");
        methodCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentMethod()));

        TableColumn<History.HistoryRecord, Void> billCol = new TableColumn<>("Bill");
        billCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("View");

            {
                btn.setOnAction(e -> {
                    History.HistoryRecord record = getTableView().getItems().get(getIndex());
                    new History(primaryStage).showBill(record);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(userIdCol, paymentIdCol, dateCol, amountCol, methodCol, billCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(400);

        // --- Layout ---
        mainLayout.getChildren().addAll(topBox, table);
        centerPane.setCenter(mainLayout);

        // Replace the center of the dashboard
        //dashboard.setCenter(centerPane);
        AdminHome.centerPane.setCenter(mainLayout);

    }
}
