// TODO : admin page and control
package com.example.login;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class AdminController {

    @FXML private Button viewOrdersButton;
    @FXML private Button manageMenuButton;
    @FXML private Button viewUsersButton;

    @FXML private void handleViewOrders() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Orders");
        alert.setHeaderText(null);
        alert.setContentText("Here you will see all orders.");
        alert.showAndWait();
    }

    @FXML private void handleManageMenu() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Menu Management");
        alert.setHeaderText(null);
        alert.setContentText("Here you can manage the menu items.");
        alert.showAndWait();
    }

    @FXML private void handleViewUsers() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Users");
        alert.setHeaderText(null);
        alert.setContentText("Here you will see all registered users.");
        alert.showAndWait();
    }
}