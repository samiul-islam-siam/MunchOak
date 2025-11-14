package com.example.login;

import com.example.manager.FileStorage;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewUsersPage {

    public static void show(Stage stage) {
        TableView<String[]> table = new TableView<>();

        TableColumn<String[], String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));

        TableColumn<String[], String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> idCol = new TableColumn<>("User ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));

        table.getColumns().addAll(usernameCol, emailCol, idCol);
        table.setItems(FXCollections.observableArrayList(FileStorage.loadUsers()));

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> AdminDashboard.show(stage));

        VBox layout = new VBox(10, table, backBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #F9F9F9;");
        stage.setScene(new Scene(layout, 500, 400));
    }
}

