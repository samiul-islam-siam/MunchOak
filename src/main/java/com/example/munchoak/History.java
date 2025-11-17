package com.example.munchoak;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import com.example.view.HomePage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;
import java.util.List;
import java.lang.reflect.Field;

public class History {

    private final Stage primaryStage; // Reference to the main application window
    private ObservableList<HistoryRecord> historyData; // Observable list for TableView

    public History(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Main scene for displaying payment history in a table
    public Scene getScene() {
        TableView<HistoryRecord> historyTable = new TableView<>();
        historyTable.setPlaceholder(new Label("No payment history available.")); // Shown if no data
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        historyTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
        historyTable.setMaxWidth(700);

        historyData = FXCollections.observableArrayList();

        // ----- Table Columns -----

        // User ID column
        TableColumn<HistoryRecord, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Payment ID column
        TableColumn<HistoryRecord, Integer> paymentIdCol = new TableColumn<>("Payment ID");
        paymentIdCol.setCellValueFactory(new PropertyValueFactory<>("paymentId"));

        // Timestamp column
        TableColumn<HistoryRecord, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        // Total amount column
        TableColumn<HistoryRecord, Double> totalCol = new TableColumn<>("Total Amount");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Payment method column (Card/Cash)
        TableColumn<HistoryRecord, String> methodCol = new TableColumn<>("Payment Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        // View Bill column with a button
        TableColumn<HistoryRecord, Void> billCol = new TableColumn<>("Bill");
        billCol.setCellFactory(col -> new TableCell<>() {

            private final Button btn = new Button("View");

            {
                // When clicked, show the bill for that payment ID
                btn.setOnAction(e -> {
                    HistoryRecord record = getTableView().getItems().get(getIndex());
                    showBill(record);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn); // Hide button on empty rows
            }
        });

        // Add columns to table
        historyTable.getColumns().addAll(userIdCol, paymentIdCol, dateCol, totalCol, methodCol, billCol);
        historyTable.setItems(historyData);

        // Load data from storage
        loadHistory();

        // ----- Back Button -----
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-size: 14px;"
                + "-fx-padding: 6 15 6 15; -fx-background-radius: 8;");
        backBtn.setOnAction(e -> goBack());

        HBox backRow = new HBox(backBtn);
        backRow.setAlignment(Pos.TOP_LEFT);

        // ----- Title -----
        Label title = new Label("📜 Payment History");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #333;");
        HBox titleRow = new HBox(title);
        titleRow.setAlignment(Pos.CENTER);

        // ----- Main Layout -----
        VBox layout = new VBox(15, backRow, titleRow, historyTable);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        return new Scene(layout, 900, 600);
    }

    // Navigate back to Home Page
    private void goBack() {
        try {
            HomePage homePage = new HomePage(primaryStage);
            primaryStage.setScene(homePage.getHomeScene());
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    }

    // ------------------ Load User/Admin History ------------------
    private void loadHistory() {
        historyData.clear();

        // Load simplified stored records from FileStorage
        List<FileStorage.HistoryRecordSimple> list = FileStorage.loadPaymentHistory();

        int currentUserId = Session.getCurrentUserId();
        boolean isAdmin = Session.getCurrentUsername().equals("admin");

        // Admin sees all payments; users see only their own
        for (FileStorage.HistoryRecordSimple s : list) {
            if (!isAdmin && s.userId != currentUserId) continue;

            historyData.add(new HistoryRecord(
                    s.userId,
                    s.paymentId,
                    s.timestamp,
                    s.amount,
                    "Success",
                    s.paymentMethod
            ));
        }
    }

    // ------------------ Bill Popup Window ------------------
    private void showBill(HistoryRecord record) {

        // Load food info from storage
        Map<Integer, FoodItems> foodMap = FileStorage.loadFoodMap();

        // Rebuild the cart for this specific payment ID
        Cart cart = new Cart();
        Map<Integer, Integer> items = FileStorage.getCartItemsForPayment(record.getPaymentId());
        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
            cart.addToCart(e.getKey(), e.getValue());
        }

        // Create payment object using reflection to restore original stored values
        Payment payment = new Payment(record.getPaymentId(), record.getAmount());

        try {
            // Restore payment ID
            Field idField = Payment.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(payment, record.getPaymentId());

            // Restore timestamp
            Field timestampField = Payment.class.getDeclaredField("timestamp");
            timestampField.setAccessible(true);
            timestampField.set(payment, record.getTimestamp());

            // Restore success state
            Field successField = Payment.class.getDeclaredField("success");
            successField.setAccessible(true);
            successField.set(payment, record.getStatus().equalsIgnoreCase("Success"));

        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }

        // Generate bill text
        Bill bill = new Bill(cart, payment);
        String receipt = bill.generateReceipt(foodMap);

        // Show bill in separate popup window
        Stage billStage = new Stage();
        billStage.setTitle("Bill Receipt");

        TextArea receiptArea = new TextArea(receipt);
        receiptArea.setEditable(false);
        receiptArea.setStyle("-fx-font-size: 14px; -fx-font-family: monospace;");
        receiptArea.setPrefSize(500, 400);

        VBox billBox = new VBox(15, receiptArea);
        billBox.setPadding(new Insets(20));
        billBox.setAlignment(Pos.CENTER);

        billStage.setScene(new Scene(billBox));
        billStage.show();
    }

    // ------------------ Table Model for Each Row ------------------
    public static class HistoryRecord {
        private final int userId;
        private final int paymentId;
        private final String timestamp;
        private final double amount;
        private final String status;
        private final String paymentMethod;

        public HistoryRecord(int userId, int paymentId, String timestamp,
                             double amount, String status, String paymentMethod) {
            this.userId = userId;
            this.paymentId = paymentId;
            this.timestamp = timestamp;
            this.amount = amount;
            this.status = status;
            this.paymentMethod = paymentMethod;
        }

        public int getUserId() {
            return userId;
        }

        public int getPaymentId() {
            return paymentId;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public double getAmount() {
            return amount;
        }

        public String getStatus() {
            return status;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }
    }
}
