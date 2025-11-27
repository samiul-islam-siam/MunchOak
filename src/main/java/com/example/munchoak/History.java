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

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class History {

    private final Stage primaryStage;
    private final Cart cart;  // ADDED: To support reordering into current cart
    private ObservableList<HistoryRecord> historyData;

    // UPDATED: Overloaded constructor for backward compatibility (creates new Cart if not provided)
    public History(Stage primaryStage) {
        this(primaryStage, new Cart());
    }

    // Primary constructor
    public History(Stage primaryStage, Cart cart) {
        this.primaryStage = primaryStage;
        this.cart = cart;
    }

    public Scene getScene() {
        TableView<HistoryRecord> historyTable = new TableView<>();
        historyTable.setPlaceholder(new Label("No payment history available."));
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        historyTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
        historyTable.setMaxWidth(700); // keep table nicely centered

        historyData = FXCollections.observableArrayList();

        // --- Table Columns ---
        TableColumn<HistoryRecord, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<HistoryRecord, Integer> paymentIdCol = new TableColumn<>("Payment ID");
        paymentIdCol.setCellValueFactory(new PropertyValueFactory<>("paymentId"));

        TableColumn<HistoryRecord, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        TableColumn<HistoryRecord, Double> totalCol = new TableColumn<>("Total Amount");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<HistoryRecord, String> methodCol = new TableColumn<>("Payment Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        TableColumn<HistoryRecord, Void> billCol = new TableColumn<>("Bill");
        billCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("View");

            {
                btn.setOnAction(e -> {
                    HistoryRecord record = getTableView().getItems().get(getIndex());
                    showBill(record);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // ADDED: Reorder Column
        TableColumn<HistoryRecord, Void> reorderCol = new TableColumn<>("Reorder");
        reorderCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Reorder");

            {
                btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 4 8;");
                btn.setOnAction(e -> {
                    HistoryRecord record = getTableView().getItems().get(getIndex());
                    Map<Integer, Integer> items = FileStorage.getCartItemsForPayment(record.getPaymentId());
                    for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                        cart.addToCart(entry.getKey(), entry.getValue());
                    }
                    // Navigate to Cart page to view updated cart
                    primaryStage.setScene(new CartPage(primaryStage, cart).getScene());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        historyTable.getColumns().addAll(userIdCol, paymentIdCol, dateCol, totalCol, methodCol, billCol, reorderCol);
        historyTable.setItems(historyData);

        // Load payment data
        loadHistory();

        // --- Back Button ---
        Button backBtn = new Button("Back");
        // backBtn.setId("back-btn");
        backBtn.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-padding: 6 15 6 15; -fx-background-radius: 8;");
        backBtn.setOnAction(e -> goBack());

        HBox backRow = new HBox(backBtn);
        backRow.setAlignment(Pos.TOP_LEFT);

        // --- Title Row ---
        Label title = new Label("📜 Payment History");
        // title.setId("history-title");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #333;");
        HBox titleRow = new HBox(title);
        titleRow.setAlignment(Pos.CENTER);

        // --- Main Layout ---
        VBox layout = new VBox(15, backRow, titleRow, historyTable);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        return new Scene(layout, 900, 600);
    }

    private void goBack() {
        try {
            HomePage homePage = new HomePage(primaryStage);
            primaryStage.setScene(homePage.getHomeScene());
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    }

    // ------------------ Data Handling ------------------
    private void loadHistory() {
        historyData.clear();
        List<FileStorage.HistoryRecordSimple> list = FileStorage.loadPaymentHistory();
        int currentUserId = Session.getCurrentUserId();
        boolean isAdmin = Session.getCurrentUsername().equals("admin");

        double delivery = 7.99;
        double tax = 7.00;
        double service = 1.50;

        for (FileStorage.HistoryRecordSimple s : list) {
            if (!isAdmin && s.userId != currentUserId) continue;
            // FIXED: Load actual discount and tip for this payment to match Cart/Checkout totals
            double discountVal = FileStorage.getPaymentDiscount(s.paymentId);  // Assume FileStorage method loads saved discount for paymentId
            double tipVal = FileStorage.getPaymentTip(s.paymentId);  // Assume FileStorage method loads saved tip for paymentId
            double subtotal = s.amount;  // Assuming s.amount is the subtotal saved in storage
            double fullAmount = subtotal - discountVal + delivery + tipVal + service + tax;
            historyData.add(new HistoryRecord(s.userId, s.paymentId, s.timestamp, fullAmount, "Success", s.paymentMethod));
        }
    }

    // ------------------ Bill Popup ------------------
    private void showBill(HistoryRecord record) {
        Map<Integer, FoodItems> foodMap = FileStorage.loadFoodMap();

        Cart tempCart = new Cart();
        Map<Integer, Integer> items = FileStorage.getCartItemsForPayment(record.getPaymentId());
        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
            tempCart.addToCart(e.getKey(), e.getValue());
        }

        // FIXED: Load actual discount and tip to match totals in bill/receipt
        double discountVal = FileStorage.getPaymentDiscount(record.getPaymentId);
        double tipVal = FileStorage.getPaymentTip(record.getPaymentId);
        double subtotal = tempCart.getTotalPrice(foodMap);  // Recalculate subtotal from items for accuracy
        // FIXED: Create Payment with subtotal, then set discount/tip via setters for Bill to use in receipt breakdown
        Payment payment = new Payment(record.getPaymentId(), subtotal, record.getTimestamp());
        payment.setDiscount(discountVal);
        payment.setTip(tipVal);
        payment.setSuccess(record.getStatus().equalsIgnoreCase("Success"));
        payment.setPaymentMethod(record.getPaymentMethod());

        Bill bill = new Bill(tempCart, payment);
        String receipt = bill.generateReceipt(foodMap);  // Assume this now uses payment.getDiscount(), payment.getTip() for correct total in receipt

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

    // ------------------ Model ------------------
    public static class HistoryRecord {
        private final int userId;
        private final int paymentId;
        private final String timestamp;
        private final double amount;
        private final String status;
        private final String paymentMethod;
        public int getPaymentId;

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