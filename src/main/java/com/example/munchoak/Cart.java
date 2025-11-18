package com.example.munchoak;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Cart implements Serializable {

    public static int numberOfCarts = 0;
    private final int id;
    private final HashMap<Integer, Integer> buyHistory;

    public Cart() {
        this.id = ++numberOfCarts;
        this.buyHistory = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public HashMap<Integer, Integer> getBuyHistory() {
        return buyHistory;
    }

    // ============================================================
    //                CART ITEM VIEW (INNER CLASS)
    // ============================================================
    public static class CartItemView {
        private final int id;
        private final String name;
        private int quantity;
        private final double price;
        private double total;

        public CartItemView(int id, String name, int quantity, double price) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.total = price * quantity;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public double getTotal() {
            return total;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
            this.total = this.price * quantity;
        }
    }

    // ============================================================
    //                CART OPERATIONS
    // ============================================================
    public void addToCart(Integer foodId, int count) {
        buyHistory.put(foodId, buyHistory.getOrDefault(foodId, 0) + count);
    }

    public void updateQuantity(int foodId, int newQty) {
        if (newQty <= 0) buyHistory.remove(foodId);
        else buyHistory.put(foodId, newQty);
    }

    public void removeFromCart(Integer foodId) {
        if (buyHistory.containsKey(foodId)) {
            int current = buyHistory.get(foodId);
            if (current > 1) buyHistory.put(foodId, current - 1);
            else buyHistory.remove(foodId);
        }
    }

    public void removeFromCartEntirely(Integer foodId) {
        buyHistory.remove(foodId);
    }

    public double getTotalPrice(Map<Integer, FoodItems> foodMap) {
        double total = 0.0;
        for (Map.Entry<Integer, Integer> entry : buyHistory.entrySet()) {
            FoodItems item = foodMap.get(entry.getKey());
            if (item != null) total += item.getPrice() * entry.getValue();
        }
        return total;
    }

    // ============================================================
    //                      SHOW CART UI
    // ============================================================
    public void showCart(ObservableList<FoodItems> foodList) {
        // Map all FoodItems by ID
        Map<Integer, FoodItems> foodMap = new HashMap<>();
        for (FoodItems item : foodList) {
            foodMap.put(item.getId(), item);
        }

        // Prepare UI cart list
        ObservableList<CartItemView> cartItems = FXCollections.observableArrayList();

        for (Map.Entry<Integer, Integer> entry : buyHistory.entrySet()) {
            FoodItems food = foodMap.get(entry.getKey());
            if (food != null) {
                cartItems.add(
                        new CartItemView(food.getId(), food.getName(), entry.getValue(), food.getPrice())
                );
            }
        }

        TableView<CartItemView> cartTable = new TableView<>(cartItems);

        // Columns
        TableColumn<CartItemView, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<CartItemView, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<CartItemView, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("%.2f", value));
            }
        });

        TableColumn<CartItemView, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("%.2f", value));
            }
        });

        Label totalLabel = new Label("Total: $" + String.format("%.2f", getTotalPrice(foodMap)));
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Action Column
        TableColumn<CartItemView, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn = new Button("Edit");
            private final Button removeBtn = new Button("Remove");
            private final HBox box = new HBox(5, editBtn, removeBtn);

            {
                // Edit button
                editBtn.setOnAction(e -> {
                    CartItemView item = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(String.valueOf(item.getQuantity()));
                    dialog.setTitle("Edit Quantity");
                    dialog.setHeaderText("Update quantity for " + item.getName());
                    dialog.setContentText("New quantity:");

                    dialog.showAndWait().ifPresent(value -> {
                        try {
                            int newQty = Integer.parseInt(value);

                            updateQuantity(item.getId(), newQty);

                            if (newQty <= 0) {
                                cartItems.remove(item);
                            } else {
                                item.setQuantity(newQty);
                                cartTable.refresh();
                            }

                            totalLabel.setText("Total: $" +
                                    String.format("%.2f", getTotalPrice(foodMap)));

                        } catch (NumberFormatException ex) {
                            new Alert(Alert.AlertType.ERROR,
                                    "Please enter a valid number").show();
                        }
                    });
                });

                // Remove button
                removeBtn.setOnAction(e -> {
                    CartItemView item = getTableView().getItems().get(getIndex());
                    removeFromCartEntirely(item.getId());
                    cartItems.remove(item);

                    totalLabel.setText("Total: $" +
                            String.format("%.2f", getTotalPrice(foodMap)));
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        cartTable.getColumns().addAll(nameCol, qtyCol, priceCol, totalCol, actionCol);

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> ((Stage) closeBtn.getScene().getWindow()).close());

        HBox bottomBar = new HBox(10, totalLabel, closeBtn);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(10));

        VBox vbox = new VBox(10, cartTable, bottomBar);
        vbox.setPadding(new Insets(10));

        Stage stage = new Stage();
        stage.setTitle("Your Cart");
        stage.setScene(new Scene(vbox, 600, 400));
        stage.show();
    }

    public int getTotalItems() {
        int total = 0;
        for (int qty : buyHistory.values()) {
            total += qty;
        }
        return total;
    }
    public void clearCart() {
        buyHistory.clear();
    }
}