package com.example.munchoak;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class HistoryPage {

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

        // Load data from DB
        loadHistory();

        VBox layout = new VBox(15, new Label("📜 Payment History"), historyTable);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    private void loadHistory() {
        historyData.clear();

        String sql = "SELECT Payment_ID, User_ID, TotalAmount, PaymentMethod, PaymentDate " +
                "FROM PaymentHistory ORDER BY Payment_ID DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int paymentId = rs.getInt("Payment_ID");
                int userId = rs.getInt("User_ID");
                double amount = rs.getDouble("TotalAmount");
                String date = rs.getString("PaymentDate");
                String method = rs.getString("PaymentMethod");
                String status = "Success"; // temporary placeholder

                historyData.add(new HistoryRecord(userId, paymentId, date, amount, status, method));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
private void showBill(HistoryRecord record) {
    Map<Integer, FoodItems> foodMap = loadFoodMap();

    // Create a cart for displaying history
    Cart cart = new Cart(record.getUserId(), "historyCart");

    String sql = """
        SELECT ci.Food_ID, ci.Quantity
        FROM CartItems ci
        JOIN Cart c ON ci.Cart_ID = c.Cart_ID
        WHERE c.Payment_ID = ?
        """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, record.getPaymentId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int foodId = rs.getInt("Food_ID");
            int qty = rs.getInt("Quantity");
            cart.addToCart(foodId, qty);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    Payment payment = new Payment(record.paymentId, record.getAmount());
    try {
        java.lang.reflect.Field idField = Payment.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(payment, record.getPaymentId());

        java.lang.reflect.Field successField = Payment.class.getDeclaredField("success");
        successField.setAccessible(true);
        successField.set(payment, record.getStatus().equals("Success"));

        java.lang.reflect.Field timestampField = Payment.class.getDeclaredField("timestamp");
        timestampField.setAccessible(true);
        timestampField.set(payment, record.getTimestamp());
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    Bill bill = new Bill(cart, payment);
    String receipt = bill.generateReceipt(foodMap);

    Stage stage = new Stage();
    stage.setTitle("Bill Receipt - " + record.getPaymentId());

    TextArea area = new TextArea(receipt);
    area.setEditable(false);
    area.setStyle("-fx-font-size: 14px; -fx-font-family: monospace;");

    VBox box = new VBox(area);
    box.setPadding(new Insets(15));

    stage.setScene(new Scene(box, 500, 400));
    stage.show();
}


    private Map<Integer, FoodItems> loadFoodMap() {
        Map<Integer, FoodItems> map = new HashMap<>();
        String sql = "SELECT * FROM Details";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                FoodItems food = new FoodItems(
                        rs.getInt("Food_ID"),
                        rs.getString("Food_Name"),
                        rs.getString("Details"),
                        rs.getDouble("Price"),
                        rs.getDouble("Ratings"),
                        rs.getString("ImagePath"),
                        rs.getString("Category")
                );
                map.put(food.getId(), food);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
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