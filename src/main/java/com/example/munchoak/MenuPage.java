package com.example.munchoak;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.*;

public class MenuPage {

    private ObservableList<FoodItems> foodList;
    private VBox foodContainer;

    private TextField nameField, detailsField, priceField, ratingsField;
    private ComboBox<String> categoryBox;
    private Label imageFilenameLabel;
    private File selectedImageFile = null;
    private Button addOrUpdateButton;

    private FoodItems currentEditingFood = null;

    public Node getView() {
        foodList = FXCollections.observableArrayList();

        foodContainer = new VBox(20);
        foodContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(foodContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        nameField = new TextField();
        detailsField = new TextField();
        priceField = new TextField();
        ratingsField = new TextField();
        imageFilenameLabel = new Label("No image selected");

        categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Drinks", "Sweets", "Spicy Foods", "Main Course", "Appetizers");
        categoryBox.setPromptText("Select Category");

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
        inputGrid.add(new Label("Category:"), 0, 4);
        inputGrid.add(categoryBox, 1, 4);

        Button browseBtn = new Button("Browse...");
        browseBtn.setOnAction(e -> chooseImage());

        HBox imageBox = new HBox(10, imageFilenameLabel, browseBtn);
        imageBox.setAlignment(Pos.CENTER_LEFT);
        inputGrid.add(new Label("Image:"), 0, 5);
        inputGrid.add(imageBox, 1, 5);

        addOrUpdateButton = new Button("Add");
        addOrUpdateButton.setOnAction(e -> {
            if (currentEditingFood == null) {
                addFoodItem();
            } else {
                updateFoodItem();
            }
        });

        VBox vbox = new VBox(15, scrollPane, inputGrid, addOrUpdateButton);
        vbox.setPadding(new Insets(5));

        loadFoodItems();
        return vbox;
    }

    private void loadFoodItems() {
        foodList.clear();
        foodContainer.getChildren().clear();

        String sql = "SELECT * FROM Details ORDER BY Category";
        Map<String, FlowPane> categoryFlows = new LinkedHashMap<>();

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

                foodList.add(food);
                String category = food.getCategory();

                // If category section doesn't exist, create it
                if (!categoryFlows.containsKey(category)) {
                    Label categoryLabel = new Label(category);
                    categoryLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

                    Separator separator = new Separator();
                    separator.setPrefWidth(500);

                    FlowPane flow = new FlowPane(15, 15);
                    flow.setPadding(new Insets(5));

                    VBox section = new VBox(10);
                    section.getChildren().addAll(categoryLabel, separator, flow);
                    section.setPadding(new Insets(10, 5, 20, 5));

                    foodContainer.getChildren().add(section);
                    categoryFlows.put(category, flow);
                }

                // Add food card to its category section
                categoryFlows.get(category).getChildren().add(createFoodCard(food));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createFoodCard(FoodItems food) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: #f4f4f4; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #000;-fx-border-width: 2;");
        card.setPrefWidth(220);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(180);
        imgView.setFitHeight(120);
        imgView.setPreserveRatio(true);

        String imagePath = "/images/" + food.getImagePath();
        Image image = null;

        try (InputStream is = getClass().getResourceAsStream(imagePath)) {
            if (is != null) {
                image = new Image(is);
            } else {
                String filePath = "file:src/main/resources/images/" + food.getImagePath();
                image = new Image(filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (image == null || image.isError()) {
            try (InputStream placeholder = getClass().getResourceAsStream("/images/placeholder.png")) {
                if (placeholder != null) {
                    image = new Image(placeholder);
                }
            } catch (Exception ignored) {}
        }

        imgView.setImage(image);

        Label name = new Label(food.getName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label desc = new Label(food.getDetails());
        desc.setWrapText(true);
        Label price = new Label("Price: $" + food.getPrice());
        Label rating = new Label("⭐ " + food.getRatings());

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> deleteFoodItem(food));

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> populateFieldsForEdit(food));

        HBox buttons = new HBox(10, editBtn, deleteBtn);
        buttons.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imgView, name, desc, price, rating, buttons);
        return card;
    }

    private void addFoodItem() {
        String imageFilename = selectedImageFile != null ? selectedImageFile.getName() : "";

        String sql = "INSERT INTO Details (Food_Name, Details, Price, Ratings, ImagePath, Category) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nameField.getText());
            stmt.setString(2, detailsField.getText());
            stmt.setDouble(3, Double.parseDouble(priceField.getText()));
            stmt.setDouble(4, Double.parseDouble(ratingsField.getText()));
            stmt.setString(5, imageFilename);
            stmt.setString(6, categoryBox.getValue());

            stmt.executeUpdate();
            loadFoodItems();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFoodItem() {
        if (currentEditingFood == null) return;

        String imageFilename = currentEditingFood.getImagePath();
        if (selectedImageFile != null) {
            imageFilename = selectedImageFile.getName();
        }

        String sql = "UPDATE Details SET Food_Name=?, Details=?, Price=?, Ratings=?, ImagePath=?, Category=? WHERE Food_ID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nameField.getText());
            stmt.setString(2, detailsField.getText());
            stmt.setDouble(3, Double.parseDouble(priceField.getText()));
            stmt.setDouble(4, Double.parseDouble(ratingsField.getText()));
            stmt.setString(5, imageFilename);
            stmt.setString(6, categoryBox.getValue());
            stmt.setInt(7, currentEditingFood.getId());

            stmt.executeUpdate();
            loadFoodItems();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteFoodItem(FoodItems food) {
        String sql = "DELETE FROM Details WHERE Food_ID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, food.getId());
            stmt.executeUpdate();
            loadFoodItems();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateFieldsForEdit(FoodItems food) {
        currentEditingFood = food;
        nameField.setText(food.getName());
        detailsField.setText(food.getDetails());
        priceField.setText(String.valueOf(food.getPrice()));
        ratingsField.setText(String.valueOf(food.getRatings()));
        imageFilenameLabel.setText(food.getImagePath());
        categoryBox.setValue(food.getCategory());
        selectedImageFile = null;
        addOrUpdateButton.setText("Update");
    }

    private void clearFields() {
        nameField.clear();
        detailsField.clear();
        priceField.clear();
        ratingsField.clear();
        imageFilenameLabel.setText("No image selected");
        categoryBox.setValue(null);
        selectedImageFile = null;
        currentEditingFood = null;
        addOrUpdateButton.setText("Add");
    }

    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Food Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                File destDir = new File("src/main/resources/images/");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, file.getName());
                Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                selectedImageFile = file;
                imageFilenameLabel.setText(file.getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
