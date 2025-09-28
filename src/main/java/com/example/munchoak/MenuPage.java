package com.example.munchoak;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.*;

public class MenuPage {

    private TableView<FoodItems> tableView;
    private ObservableList<FoodItems> foodList;

    private TextField nameField, detailsField, priceField, ratingsField;

    public Node getView() {
        tableView = new TableView<>();
        foodList = FXCollections.observableArrayList();
        tableView.setItems(foodList);

        // Table columns
        TableColumn<FoodItems, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<FoodItems, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<FoodItems, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));

        TableColumn<FoodItems, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<FoodItems, Double> ratingsCol = new TableColumn<>("Ratings");
        ratingsCol.setCellValueFactory(new PropertyValueFactory<>("ratings"));

        tableView.getColumns().addAll(idCol, nameCol, detailsCol, priceCol, ratingsCol);

        // Input fields
        nameField = new TextField();
        detailsField = new TextField();
        priceField = new TextField();
        ratingsField = new TextField();

        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);

        inputGrid.add(new Label("Food Name:"), 0, 0);
        inputGrid.add(nameField, 1, 0);
        inputGrid.add(new Label("Details:"), 0, 1);
        inputGrid.add(detailsField, 1, 1);
        inputGrid.add(new Label("Price:"), 0, 2);
        inputGrid.add(priceField, 1, 2);
        inputGrid.add(new Label("Ratings:"), 0, 3);
        inputGrid.add(ratingsField, 1, 3);

        Button addButton = new Button("Add");
        Button updateButton = new Button("Update Selected");
        Button deleteButton = new Button("Delete Selected");

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton);
        VBox vbox = new VBox(10, tableView, inputGrid, buttonBox);
        vbox.setPadding(new Insets(10));

        // Load data from DB
        loadFoodItems();

        // Button actions
        addButton.setOnAction(e -> addFoodItem());
        updateButton.setOnAction(e -> updateFoodItem());
        deleteButton.setOnAction(e -> deleteFoodItem());

        // Selecting row fills input fields
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                detailsField.setText(newSelection.getDetails());
                priceField.setText(String.valueOf(newSelection.getPrice()));
                ratingsField.setText(String.valueOf(newSelection.getRatings()));
            }
        });

        return vbox;
    }

    private void loadFoodItems() {
        foodList.clear();
        String sql = "SELECT * FROM Details";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                foodList.add(new FoodItems(
                        rs.getInt("Food_ID"),
                        rs.getString("Food_Name"),
                        rs.getString("Details"),
                        rs.getDouble("Price"),
                        rs.getDouble("Ratings")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addFoodItem() {
        String sql = "INSERT INTO Details (Food_Name, Details, Price, Ratings) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nameField.getText());
            stmt.setString(2, detailsField.getText());
            stmt.setDouble(3, Double.parseDouble(priceField.getText()));
            stmt.setDouble(4, Double.parseDouble(ratingsField.getText()));

            stmt.executeUpdate();
            loadFoodItems();
            clearFields();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateFoodItem() {
        FoodItems selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String sql = "UPDATE Details SET Food_Name=?, Details=?, Price=?, Ratings=? WHERE Food_ID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nameField.getText());
            stmt.setString(2, detailsField.getText());
            stmt.setDouble(3, Double.parseDouble(priceField.getText()));
            stmt.setDouble(4, Double.parseDouble(ratingsField.getText()));
            stmt.setInt(5, selected.getId());

            stmt.executeUpdate();
            loadFoodItems();
            clearFields();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteFoodItem() {
        FoodItems selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String sql = "DELETE FROM Details WHERE Food_ID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();
            loadFoodItems();
            clearFields();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.clear();
        detailsField.clear();
        priceField.clear();
        ratingsField.clear();
    }
}

