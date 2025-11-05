package com.example.munchoak;

import com.example.manager.FileStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;
import java.util.List;

public class History {

    private TableView<HistoryRecord> historyTable;
    private ObservableList<HistoryRecord> historyData;

    public VBox getView() {
        historyTable = new TableView<>();
        historyData = FXCollections.observableArrayList();

        // Columns
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

        historyTable.getColumns().addAll(userIdCol, paymentIdCol, dateCol, totalCol, methodCol, billCol);
        historyTable.setItems(historyData);

        // Load data from file storage
        loadHistory();

        VBox layout = new VBox(15, new Label("📜 Payment History"), historyTable);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    private void loadHistory() {
        historyData.clear();
        List<FileStorage.HistoryRecordSimple> list = FileStorage.loadPaymentHistory();
        for (FileStorage.HistoryRecordSimple s : list) {
            // status defaults to "Success" here (FileStorage doesn't store status)
            historyData.add(new HistoryRecord(s.userId, s.paymentId, s.timestamp, s.amount, "Success", s.paymentMethod));
        }
    }

    private void showBill(HistoryRecord record) {
        Map<Integer, FoodItems> foodMap = FileStorage.loadFoodMap();

        // Create a cart for displaying history
        Cart cart = new Cart(record.getUserId(), "historyCart");

        Map<Integer, Integer> items = FileStorage.getCartItemsForPayment(record.getPaymentId());
        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
            cart.addToCart(e.getKey(), e.getValue());
        }

        Payment payment = new Payment(record.getPaymentId(), record.getAmount());
        // set private fields using reflection (timestamp and success/status)
        try {
            java.lang.reflect.Field idField = Payment.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(payment, record.getPaymentId());

            java.lang.reflect.Field timestampField = Payment.class.getDeclaredField("timestamp");
            timestampField.setAccessible(true);
            timestampField.set(payment, record.getTimestamp());

            java.lang.reflect.Field successField = Payment.class.getDeclaredField("success");
            successField.setAccessible(true);
            successField.set(payment, record.getStatus().equalsIgnoreCase("Success"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Bill bill = new Bill(cart, payment);
        String receipt = bill.generateReceipt(foodMap);

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

        public int getUserId() { return userId; }
        public int getPaymentId() { return paymentId; }
        public String getTimestamp() { return timestamp; }
        public double getAmount() { return amount; }
        public String getStatus() { return status; }
        public String getPaymentMethod() { return paymentMethod; }
    }
}