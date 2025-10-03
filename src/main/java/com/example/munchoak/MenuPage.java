package com.example.munchoak;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

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

    // Cart for the current user
    private Cart cart = new Cart("customer123");  // demo token

    public Node getView() {
        foodList = FXCollections.observableArrayList();
        foodContainer = new VBox(20);
        foodContainer.setPadding(new Insets(10));

        // ===== SCROLL PANE FOR FOOD ITEMS =====
        ScrollPane scrollPane = new ScrollPane(foodContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // ==== Input fields & form ====
        nameField = new TextField();
        detailsField = new TextField();
        priceField = new TextField();
        ratingsField = new TextField();
        imageFilenameLabel = new Label("No image selected");

        categoryBox = new ComboBox<>();
        loadCategories();
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

        // Category management buttons
        Button addCatBtn = new Button("Add Category");
        Button renameCatBtn = new Button("Rename Category");
        Button deleteCatBtn = new Button("Delete Category");
        HBox categoryButtons = new HBox(10, addCatBtn, renameCatBtn, deleteCatBtn);
        inputGrid.add(categoryButtons, 1, 5);

        addCatBtn.setOnAction(e -> addCategory());
        renameCatBtn.setOnAction(e -> renameCategory());
        deleteCatBtn.setOnAction(e -> deleteCategory());

        // Image selection
        Button browseBtn = new Button("Browse...");
        browseBtn.setOnAction(e -> chooseImage());
        HBox imageBox = new HBox(10, imageFilenameLabel, browseBtn);
        imageBox.setAlignment(Pos.CENTER_LEFT);
        inputGrid.add(new Label("Image:"), 0, 6);
        inputGrid.add(imageBox, 1, 6);

        // Add / Update button inside form
        addOrUpdateButton = new Button("Add");
        addOrUpdateButton.setOnAction(e -> {
            if (currentEditingFood == null) {
                addFoodItem();
            } else {
                updateFoodItem();
            }
        });

        VBox formBox = new VBox(10, inputGrid, addOrUpdateButton);
        formBox.setVisible(false);
        formBox.setManaged(false);

        // Cart buttons
        Button viewCartButton = new Button("View Cart");
        viewCartButton.setOnAction(e -> showCart());

        Button checkoutButton = new Button("Checkout");
        checkoutButton.setOnAction(e -> checkout());

        HBox cartButtons = new HBox(15, viewCartButton, checkoutButton);

        // ===== TOP "Add Food" BUTTON =====
        Button showAddFormBtn = new Button("Add Food");
        showAddFormBtn.setOnAction(e -> {
            if (formBox.isVisible()) {
                // Hide the form
                formBox.setVisible(false);
                formBox.setManaged(false);
            } else {
                // Show the form for adding new food
                clearFields(); // reset form
                formBox.setVisible(true);
                formBox.setManaged(true);
            }
        });


        // ===== MAIN LAYOUT ===== //
        VBox vbox = new VBox(15, showAddFormBtn, scrollPane, formBox, cartButtons);
        vbox.setPadding(new Insets(5));

        loadFoodItems();
        return vbox;
    }


    // ================== CATEGORY MANAGEMENT ===================

    private void loadCategories() {
        categoryBox.getItems().clear();
        String sql = "SELECT Category_Name FROM Categories ORDER BY Category_Name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categoryBox.getItems().add(rs.getString("Category_Name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Enter new category name:");
        dialog.setContentText("Category:");
        dialog.showAndWait().ifPresent(name -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO Categories (Category_Name) VALUES (?)")) {
                stmt.setString(1, name);
                stmt.executeUpdate();
                loadCategories();
                categoryBox.setValue(name);
            } catch (SQLException ex) {
                showAlert("Error", "Category already exists or invalid.");
            }
        });
    }

    private void renameCategory() {
        String selected = categoryBox.getValue();
        if (selected == null) {
            showAlert("Error", "Select a category first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected);
        dialog.setTitle("Rename Category");
        dialog.setHeaderText("Enter new name for category:");
        dialog.setContentText("New Name:");
        dialog.showAndWait().ifPresent(newName -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE Categories SET Category_Name=? WHERE Category_Name=?");
                stmt.setString(1, newName);
                stmt.setString(2, selected);
                stmt.executeUpdate();

                PreparedStatement stmt2 = conn.prepareStatement("UPDATE Details SET Category=? WHERE Category=?");
                stmt2.setString(1, newName);
                stmt2.setString(2, selected);
                stmt2.executeUpdate();

                loadCategories();
                categoryBox.setValue(newName);
                loadFoodItems();
            } catch (SQLException ex) {
                showAlert("Error", "Rename failed.");
            }
        });
    }

    private void deleteCategory() {
        String selected = categoryBox.getValue();
        if (selected == null) {
            showAlert("Error", "Select a category to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete category '" + selected + "'?\nAll foods in this category will also be deleted.",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM Details WHERE Category=?");
                    stmt1.setString(1, selected);
                    stmt1.executeUpdate();

                    PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM Categories WHERE Category_Name=?");
                    stmt2.setString(1, selected);
                    stmt2.executeUpdate();

                    loadCategories();
                    categoryBox.setValue(null);
                    loadFoodItems();
                } catch (SQLException ex) {
                    showAlert("Error", "Delete failed.");
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    // ================== FOOD ITEMS MANAGEMENT ===================

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

        // Load image
        String imagePath = "/images/" + food.getImagePath();
        Image image = null;
        try (InputStream is = getClass().getResourceAsStream(imagePath)) {
            if (is != null) {
                image = new Image(is);
            } else {
                String filePath = "file:src/main/resources/images/" + food.getImagePath();
                image = new Image(filePath);
            }
        } catch (Exception ignored) {}

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

        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.setOnAction(e -> {
            cart.addToCart(food.getId(), 1);

            Stage popup = new Stage();
            popup.initStyle(StageStyle.UNDECORATED);
            popup.setAlwaysOnTop(true);

            Label label = new Label(food.getName() + " added to cart!");
            label.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px;");

            VBox box = new VBox(label);
            box.setAlignment(Pos.CENTER);

            popup.setScene(new Scene(box));
            popup.show();

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e2 -> popup.close());
            delay.play();

        });

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> showEditDialog(food));

        HBox buttons = new HBox(10, addToCartBtn, editBtn);
        buttons.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imgView, name, desc, price, rating, buttons);
        return card;
    }


    private void addFoodItem() {
        if (categoryBox.getValue() == null || categoryBox.getValue().trim().isEmpty()) {
            showAlert("Error", "No category selected.");
            return;
        }
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Error", "Food name cannot be empty.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                showAlert("Error", "Price cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid price.");
            return;
        }

        double rating;
        try {
            rating = Double.parseDouble(ratingsField.getText().trim());
            if (rating < 0 || rating > 5) {
                showAlert("Error", "Ratings must be between 0 and 5.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid ratings.");
            return;
        }

        String imageFilename = selectedImageFile != null ? selectedImageFile.getName() : "";

        String sql = "INSERT INTO Details (Food_Name, Details, Price, Ratings, ImagePath, Category) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nameField.getText().trim());
            stmt.setString(2, detailsField.getText().trim());
            stmt.setDouble(3, price);
            stmt.setDouble(4, rating);
            stmt.setString(5, imageFilename);
            stmt.setString(6, categoryBox.getValue());

            stmt.executeUpdate();
            loadFoodItems();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add food item.");
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

            stmt.setString(1, nameField.getText().trim());
            stmt.setString(2, detailsField.getText().trim());
            stmt.setDouble(3, Double.parseDouble(priceField.getText().trim()));
            stmt.setDouble(4, Double.parseDouble(ratingsField.getText().trim()));
            stmt.setString(5, imageFilename);
            stmt.setString(6, categoryBox.getValue());
            stmt.setInt(7, currentEditingFood.getId());

            stmt.executeUpdate();
            loadFoodItems();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update food item.");
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

        // show form
        ((VBox) addOrUpdateButton.getParent()).setVisible(true);
        ((VBox) addOrUpdateButton.getParent()).setManaged(true);
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

        // hide form again
        ((VBox) addOrUpdateButton.getParent()).setVisible(false);
        ((VBox) addOrUpdateButton.getParent()).setManaged(false);
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

    // =================== CART ====================

    public static class CartItemView {
        private String name;
        private int quantity;
        private double price;
        private double total;

        public CartItemView(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.total = price * quantity;
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
    }

    private void showCart() {
        Map<Integer, FoodItems> foodMap = new HashMap<>();
        for (FoodItems item : foodList) {
            foodMap.put(item.getId(), item);
        }

        TableView<CartItemView> cartTable = new TableView<>();
        ObservableList<CartItemView> cartItems = FXCollections.observableArrayList();

        for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
            FoodItems food = foodMap.get(entry.getKey());
            if (food != null) {
                cartItems.add(new CartItemView(food.getName(), entry.getValue(), food.getPrice()));
            }
        }
        cartTable.setItems(cartItems);

        TableColumn<CartItemView, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));

        TableColumn<CartItemView, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("quantity"));

        TableColumn<CartItemView, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("price"));

        TableColumn<CartItemView, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("total"));

        cartTable.getColumns().addAll(nameCol, qtyCol, priceCol, totalCol);

        Stage stage = new Stage();
        stage.setTitle("Your Cart");

        VBox vbox = new VBox(10, cartTable);
        vbox.setPadding(new Insets(10));
        stage.setScene(new Scene(vbox, 400, 300));
        stage.show();
    }

    // ================== CHECKOUT ===================

    private void checkout() {
        // Step 1: Calculate total
        double total = 0;
        Map<Integer, FoodItems> foodMap = new HashMap<>();
        for (FoodItems food : foodList) {
            foodMap.put(food.getId(), food);
        }

        for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
            FoodItems food = foodMap.get(entry.getKey());
            if (food != null) {
                total += food.getPrice() * entry.getValue();
            }
        }

        // Step 2: Process payment (pass cart + foodMap so bill can be generated later)
        Payment payment = new Payment(total);
        payment.processPayment(cart, foodMap);
    }

    // ================== EDIT ===================

    private void showEditDialog(FoodItems food) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit " + food.getName());

        Button updateBtn = new Button("Update");
        updateBtn.setOnAction(e -> {
            populateFieldsForEdit(food);  // fill input fields
            dialog.close();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            deleteFoodItem(food);
            dialog.close();
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> dialog.close());

        VBox vbox = new VBox(15, new Label("Choose an action for " + food.getName()),
                updateBtn, deleteBtn, cancelBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        dialog.setScene(new Scene(vbox, 300, 200));
        dialog.show();
    }

}
