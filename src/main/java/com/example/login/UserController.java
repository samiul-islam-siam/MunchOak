package com.example.login;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class UserController {

    @FXML private Button viewMenuButton;
    @FXML private Button placeOrderButton;
    @FXML private Button orderHistoryButton;

    @FXML private void handleViewMenu() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Menu");
        alert.setHeaderText(null);
        alert.setContentText("Here you can view the restaurant menu.");
        alert.showAndWait();
    }

    @FXML private void handlePlaceOrder() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Place Order");
        alert.setHeaderText(null);
        alert.setContentText("Here you can place an order.");
        alert.showAndWait();
    }

    @FXML private void handleOrderHistory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order History");
        alert.setHeaderText(null);
        alert.setContentText("Here you can view your past orders.");
        alert.showAndWait();
    }
}
