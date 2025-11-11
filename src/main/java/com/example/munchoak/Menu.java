package com.example.munchoak;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Menu {

    private ObservableList<FoodItems> foodList;
    private VBox foodContainer;

    private TextField nameField, detailsField, priceField, ratingsField;
    private ComboBox<String> categoryBox;
    private Label imageFilenameLabel;
    private File selectedImageFile = null;
    private Button addOrUpdateButton;

    private FoodItems currentEditingFood = null;

    // In-memory category list (backed by file)
    private List<String> categories = new ArrayList<>();

    // Cart for the current user
    int userId = Session.getCurrentUserId();

    private Cart cart = new Cart(userId, "guest");

    public Node getView() {
        foodList = FXCollections.observableArrayList();
        // load menu into foodList from files
        List<FoodItems> loaded = FileStorage.loadMenu();
        foodList.addAll(loaded);

        // load categories from files
        categories = FileStorage.loadCategories();

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
        categories = FileStorage.loadCategories();
        categoryBox.getItems().addAll(categories);
    }

    private void addCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Enter new category name:");
        dialog.setContentText("Category:");
        dialog.showAndWait().ifPresent(name -> {
            if (name == null || name.isBlank()) {
                showAlert("Error", "Invalid category name.");
                return;
            }
            try {
                FileStorage.addCategory(name);
                loadCategories();
                categoryBox.setValue(name);
            } catch (Exception ex) {
                ex.printStackTrace();
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
            if (newName.isBlank() || categories.contains(newName)) {
                showAlert("Error", "Invalid or duplicate category name.");
                return;
            }
            try {
                FileStorage.replaceCategory(selected, newName);
                loadCategories();
                categoryBox.setValue(newName);
                // reload menu and UI
                foodList.setAll(FileStorage.loadMenu());
                loadFoodItems();
            } catch (Exception ex) {
                ex.printStackTrace();
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
                try {
                    FileStorage.deleteCategory(selected);
                    loadCategories();
                    categoryBox.setValue(null);
                    foodList.setAll(FileStorage.loadMenu());
                    loadFoodItems();
                } catch (Exception ex) {
                    ex.printStackTrace();
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
        foodContainer.getChildren().clear();
        Map<String, FlowPane> categoryFlows = new LinkedHashMap<>();

        for (FoodItems food : foodList) {
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
            if (is != null) image = new Image(is);
            else {
                String filePath = "file:src/main/resources/com/example/manager/images/" + food.getImagePath();
                image = new Image(filePath);
            }
        } catch (Exception ignored) {
        }

        if (image == null || image.isError()) {
            try (InputStream placeholder = getClass().getResourceAsStream("/images/placeholder.png")) {
                if (placeholder != null) image = new Image(placeholder);
            } catch (Exception ignored) {
            }
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

        // compute next id from existing items to avoid ID collisions
        int nextId = 1;
        for (FoodItems f : FileStorage.loadMenu()) {
            if (f.getId() >= nextId) nextId = f.getId() + 1;
        }

        FoodItems newFood = new FoodItems(nextId, nameField.getText().trim(), detailsField.getText().trim(),
                price, rating, imageFilename, categoryBox.getValue());

        try {
            FileStorage.appendMenuItem(newFood);
            // reload menu into list & UI
            foodList.setAll(FileStorage.loadMenu());
            loadFoodItems();
            clearFields();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to add food item.");
        }
    }

    private void updateFoodItem() {
        if (currentEditingFood == null) return;

        String imageFilename = currentEditingFood.getImagePath();
        if (selectedImageFile != null) {
            imageFilename = selectedImageFile.getName();
        }

        currentEditingFood.setName(nameField.getText().trim());
        currentEditingFood.setDetails(detailsField.getText().trim());
        currentEditingFood.setPrice(Double.parseDouble(priceField.getText().trim()));
        currentEditingFood.setRatings(Double.parseDouble(ratingsField.getText().trim()));
        currentEditingFood.setImagePath(imageFilename);
        currentEditingFood.setCategory(categoryBox.getValue());

        try {
            // rewrite full menu file from in-memory list
            FileStorage.rewriteMenu(new ArrayList<>(foodList));
            foodList.setAll(FileStorage.loadMenu());
            loadFoodItems();
            clearFields();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to update food item.");
        }
    }

    private void deleteFoodItem(FoodItems food) {
        foodList.remove(food);
        try {
            FileStorage.rewriteMenu(new ArrayList<>(foodList));
            loadFoodItems();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete item.");
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
                File destDir = new File("src/main/resources/com/example/manager/images/");
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
        private int id;
        private String name;
        private int quantity;
        private double price;
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
            this.total = this.price * quantity; // update total dynamically
        }
    }

    private void showCart() {
        // Map all FoodItems by ID
        Map<Integer, FoodItems> foodMap = new HashMap<>();
        for (FoodItems item : foodList) {
            foodMap.put(item.getId(), item);
        }

        // Prepare cart data for TableView
        ObservableList<CartItemView> cartItems = FXCollections.observableArrayList();
        for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
            FoodItems food = foodMap.get(entry.getKey());
            if (food != null) {
                cartItems.add(new CartItemView(food.getId(), food.getName(), entry.getValue(), food.getPrice()));
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
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", value));
                }
            }
        });

        TableColumn<CartItemView, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", value));
                }
            }
        });

        Label totalLabel = new Label("Total: $" + String.format("%.2f", cart.getTotalPrice(foodMap)));
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<CartItemView, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button removeBtn = new Button("Remove");
            private final HBox box = new HBox(5, editBtn, removeBtn);

            {
                editBtn.setOnAction(e -> {
                    CartItemView item = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(String.valueOf(item.getQuantity()));
                    dialog.setTitle("Edit Quantity");
                    dialog.setHeaderText("Update quantity for " + item.getName());
                    dialog.setContentText("New quantity:");

                    dialog.showAndWait().ifPresent(value -> {
                        try {
                            int newQty = Integer.parseInt(value);
                            if (newQty <= 0) {
                                cart.removeFromCartEntirely(item.getId());
                                cartItems.remove(item);
                            } else {
                                cart.getBuyHistory().put(item.getId(), newQty);
                                item.setQuantity(newQty);
                                cartTable.refresh();
                            }
                            totalLabel.setText("Total: $" + String.format("%.2f", cart.getTotalPrice(foodMap)));
                        } catch (NumberFormatException ex) {
                            new Alert(Alert.AlertType.ERROR, "Please enter a valid number").show();
                        }
                    });
                });

                removeBtn.setOnAction(e -> {
                    CartItemView item = getTableView().getItems().get(getIndex());
                    cart.removeFromCartEntirely(item.getId());
                    cartItems.remove(item);
                    totalLabel.setText("Total: $" + String.format("%.2f", cart.getTotalPrice(foodMap)));
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) setGraphic(null);
                else setGraphic(box);
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

    // ================== CHECKOUT ===================

    private void checkout() {
        // Step 1: Calculate total
        double total = 0;
        Map<Integer, FoodItems> foodMap = new HashMap<>();
        for (FoodItems food : FileStorage.loadMenu()) {
            foodMap.put(food.getId(), food);
        }

        for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
            FoodItems food = foodMap.get(entry.getKey());
            if (food != null) {
                total += food.getPrice() * entry.getValue();
            }
        }

        if (cart.getBuyHistory().isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Your cart is empty!").show();
            return;
        }

        try {
            int userId = (cart.getUserId() == -1) ? 2025000 : cart.getUserId();

            int paymentId = FileStorage.createPaymentAndCart(userId, cart, foodMap, "Card");
            Payment payment = new Payment(paymentId, total);
            // open payment UI which will show bill and clear cart in Payment.processPayment
            payment.processPayment(cart, foodMap);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Checkout failed: " + e.getMessage()).show();
        }
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