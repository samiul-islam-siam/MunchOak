package com.example.menu;

import com.example.manager.FileStorage;
import com.example.munchoak.Cart;
import com.example.munchoak.FoodItems;
import com.example.munchoak.Payment;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;

import com.example.manager.Session;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class BaseMenu {

    private ObservableList<FoodItems> foodList;
    private VBox foodContainer;

    private TextField nameField, detailsField, priceField, ratingsField;
    private ComboBox<String> categoryBox;
    private Label imageFilenameLabel;
    private File selectedImageFile = null;
    private Button addOrUpdateButton;

    private FoodItems currentEditingFood = null;
    protected Button deleteMenuButton;

    // ===== make UI sections accessible to subclasses =====
    protected VBox mainLayout;
    protected Button showAddFormBtn;
    protected HBox categoryButtons;
    protected VBox formBox;
    protected HBox cartButtons;
    protected Button viewCartButton;
    protected Button checkoutButton;
    protected Button addToCartBtn;
    protected Button buttonMenu;
    protected Button editBtn;

    // In-memory category list (backed by file)
    private List<String> categories = new ArrayList<>();

    public BaseMenu() {
        mainLayout = new VBox();
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setSpacing(0);
        mainLayout.setStyle("-fx-background-color: white;");

        // --- Navigation Bar ---
        HBox navBar = createNavBar();

        // --- Banner Section ---
        StackPane bannerSection = createBannerSection();

        // --- Food Grid Section ---
        foodContainer = new VBox();
        foodContainer.setAlignment(Pos.TOP_CENTER);
        foodContainer.setPadding(new Insets(20, 40, 40, 40));
//        foodContainer.Hgap(30);
//        foodContainer.setVgap(30);
        foodContainer.setStyle("-fx-background-color: white;");
        foodList = FXCollections.observableArrayList();
        // --- Cart Buttons Section ---
        cartButtons = new HBox(20);
        cartButtons.setAlignment(Pos.CENTER);
        cartButtons.setPadding(new Insets(20, 0, 40, 0));

        Button viewCart = new Button("View Cart");
        Button checkout = new Button("Checkout");
        styleMainButton(viewCart);
        styleMainButton(checkout);

        cartButtons.getChildren().addAll(viewCart, checkout);

        // --- Assemble Layout ---
        mainLayout.getChildren().addAll(navBar, bannerSection, foodContainer, cartButtons);

        // --- Load Foods ---
        loadFoodItems();
    }

    // Cart for the current user
    int userId = Session.getCurrentUserId();

    private final Cart cart = new Cart();

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

//        for (Node node : inputGrid.getChildren()) {
//            if (node instanceof Label lbl) {
//                lbl.setStyle("-fx-text-fill:#E53935;");
//            }
//        }

        // Category management buttons
        Button addCatBtn = new Button("Add Category");
        Button renameCatBtn = new Button("Rename Category");
        Button deleteCatBtn = new Button("Delete Category");
        categoryButtons = new HBox(10, addCatBtn, renameCatBtn, deleteCatBtn);
        inputGrid.add(categoryButtons, 1, 5);
        categoryButtons.setSpacing(12);
        categoryButtons.setPadding(new Insets(10, 0, 10, 0));

        for (Node n : categoryButtons.getChildren()) {
            if (n instanceof Button b) styleMainButton(b);
        }
        addCatBtn.setOnAction(e -> addCategory());
        renameCatBtn.setOnAction(e -> renameCategory());
        deleteCatBtn.setOnAction(e -> deleteCategory());

        // Image selection
        Button browseBtn = new Button("Browse...");
        styleMainButton(browseBtn);
        browseBtn.setOnAction(e -> chooseImage());
        HBox imageBox = new HBox(10, imageFilenameLabel, browseBtn);
        imageBox.setAlignment(Pos.CENTER_LEFT);
        inputGrid.add(new Label("Image:"), 0, 6);
        inputGrid.add(imageBox, 1, 6);

        for (Node node : inputGrid.getChildren()) {
            if (node instanceof Label lbl) {
                lbl.setStyle("-fx-text-fill:#E53935;");
            }
        }
        // Add / Update button inside form
        addOrUpdateButton = new Button("Add");
        styleMainButton(addOrUpdateButton);
        addOrUpdateButton.setOnAction(e -> {
            if (currentEditingFood == null) {
                addFoodItem();
            } else {
                updateFoodItem();
            }
        });


        formBox = new VBox(10, inputGrid, addOrUpdateButton);
        formBox.setVisible(false);
        formBox.setManaged(false);

        // Inside BaseMenu.java — where you initialize admin buttons


        // Cart buttons
        viewCartButton = new Button("View Cart");
        viewCartButton.setOnAction(e -> cart.showCart(foodList));

        checkoutButton = new Button("Checkout");

        checkoutButton.setOnAction(e -> Payment.checkout(cart));
        styleMainButton(viewCartButton);
        styleMainButton(checkoutButton);
        cartButtons = new HBox(15, viewCartButton, checkoutButton);


        HBox adminButtons = null;
        if (this instanceof AdminMenu) {
            // --- Top buttons for admin ---
            showAddFormBtn = new Button("Add Food");
            styleMainButton(showAddFormBtn);
            showAddFormBtn.setOnAction(e -> {
                if (formBox.isVisible()) {
                    formBox.setVisible(false);
                    formBox.setManaged(false);
                } else {
                    clearFields();
                    formBox.setVisible(true);
                    formBox.setManaged(true);
                }
            });

            buttonMenu = new Button("Add Menu");
            styleMainButton(buttonMenu);
            buttonMenu.setOnAction(e -> chooseMenu());

            deleteMenuButton = new Button("Delete Menu");
            styleMainButton(deleteMenuButton);
            deleteMenuButton.setOnAction(e -> deleteMenuFile());

            adminButtons = new HBox(15, showAddFormBtn, buttonMenu, deleteMenuButton);
            adminButtons.setAlignment(Pos.CENTER);
            adminButtons.setPadding(new Insets(10, 0, 10, 0));

            //mainLayout.getChildren().add(adminButtons); // add at top
            // Add admin buttons **before other sections**
            //mainLayout.getChildren().add(0, adminButtons);
        }

        // --- Assemble final layout ---
        mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(5));

        if (adminButtons != null) mainLayout.getChildren().add(adminButtons); // top for admin
        mainLayout.getChildren().addAll(scrollPane, formBox, cartButtons);

        loadFoodItems();
        return mainLayout;

    }

    protected void deleteMenuFile() {
        File menuFile = FileStorage.getMenuFile();

        if (!menuFile.exists()) {
            showAlert("Menu file not found.", "There’s no menu file to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Menu File");
        confirm.setContentText("Are you sure you want to delete the entire menu file?\nThis action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean deleted = menuFile.delete();

                if (deleted) {
                    foodList.clear();
                    loadFoodItems();
                    showAlert("Menu Deleted", "The menu file has been deleted successfully.");
                } else {
                    showAlert("Error", "Failed to delete the menu file.");
                }
            }
        });
    }

    // ================== CATEGORY MANAGEMENT ===================
    protected void loadCategories() {
        categoryBox.getItems().clear();
        categories = FileStorage.loadCategories();
        categoryBox.getItems().addAll(categories);
    }


    private StackPane createBannerSection() {
        StackPane banner = new StackPane();
        banner.setPrefHeight(250);

        URL bannerUrl = getClass().getResource("/com/example/images/menu_banner.png");
        ImageView bannerImage = new ImageView();
        if (bannerUrl != null)
            bannerImage.setImage(new Image(bannerUrl.toExternalForm()));
        bannerImage.setFitHeight(250);
        bannerImage.setPreserveRatio(true);

        URL logoUrl = getClass().getResource("/com/example/images/overlay_logo.png");
        ImageView logo = new ImageView();
        if (logoUrl != null)
            logo.setImage(new Image(logoUrl.toExternalForm()));
        logo.setFitWidth(150);
        logo.setPreserveRatio(true);

        banner.getChildren().addAll(bannerImage, logo);
        StackPane.setAlignment(logo, Pos.CENTER);

        return banner;
    }

    private HBox createNavBar() {
        HBox nav = new HBox(30);
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(20, 0, 20, 0));
        nav.setStyle("-fx-background-color: #E53935;");

        String[] buttons = {"Home", "About Us", "Reservation", "Cart", "Profile"};
        for (String label : buttons) {
            Button btn = new Button(label);
            //btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");
            btn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;"
            );
            nav.getChildren().add(btn);
        }
        return nav;
    }

    protected void chooseMenu() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Menu File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Data Files", "*.dat")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                // 1. Copy the file to local resource folder
                File destDir = new File("src/main/resources/com/example/manager/data/");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, file.getName());
                Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                FileStorage.setMenuFile(file);

                // 2. Load FoodItems from selected file
                List<FoodItems> importedItems = FileStorage.loadMenu();

                if (importedItems.isEmpty()) {
                    showAlert("Info", "The selected menu file is empty or invalid.");
                    return;
                }
                foodList.setAll(importedItems);
                loadFoodItems();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Menu items imported successfully!");
                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("IOException: " + e.getMessage());
                showAlert("Error", "Failed to import menu file.");
            }
        }
    }

    private void styleMainButton(Button button) {
        button.setStyle(
                "-fx-background-color: #E53935;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #C62828;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #E53935;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;"
        ));
    }

    protected void addCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Enter new category name:");
        dialog.setContentText("Category:");
        dialog.showAndWait().ifPresent(name -> {
            if (name.isBlank()) {
                showAlert("Error", "Invalid category name.");
                return;
            }
            try {
                FileStorage.addCategory(name);
                loadCategories();
                categoryBox.setValue(name);
            } catch (Exception e) {
                System.err.println("IOException: " + e.getMessage());
                showAlert("Error", "Category already exists or invalid.");
            }
        });
    }

    protected void renameCategory() {
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
            } catch (Exception e) {
                System.err.println("IOException: " + e.getMessage());
                showAlert("Error", "Rename failed.");
            }
        });
    }

    protected void deleteCategory() {
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
                } catch (Exception e) {
                    System.err.println("IOException: " + e.getMessage());
                    showAlert("Error", "Delete failed.");
                }
            }
        });
    }

    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    // ================== FOOD ITEMS MANAGEMENT ===================

    protected void loadFoodItems() {
        foodContainer.getChildren().clear();
        Map<String, FlowPane> categoryFlows = new LinkedHashMap<>();

        for (FoodItems food : foodList) {
            String category = food.getCategory();
            if (!categoryFlows.containsKey(category)) {
                Label categoryLabel = new Label(category);
                categoryLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #E53935; -fx-font-weight: bold;");
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

    protected VBox createFoodCard(FoodItems food) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.TOP_CENTER);
        //card.setStyle("-fx-background-color: #fff; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        card.setStyle(
                "-fx-background-color: #fff;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);"
        );

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
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Label desc = new Label(food.getDetails());
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        Label price = new Label("Price: $" + food.getPrice());
        price.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #E53935;");
        Label rating = new Label("⭐ " + food.getRatings());
        rating.setStyle("-fx-font-size: 13px; -fx-text-fill: #FFA000;");

        if (!(this instanceof guestMenu) && !(this instanceof AdminMenu)) { // only create Add to Cart if NOT admin
            addToCartBtn = new Button("Add to Cart");
            styleMainButton(addToCartBtn);
            addToCartBtn.setOnAction(e -> {
                cart.addToCart(food.getId(), 1);

                Stage popup = new Stage();
                popup.initStyle(StageStyle.UNDECORATED);
                popup.setAlwaysOnTop(true);

                Label label = new Label(food.getName() + " added to cart!");
                label.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;");

                VBox box = new VBox(label);
                box.setAlignment(Pos.CENTER);

                popup.setScene(new Scene(box));
                popup.show();

                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(e2 -> popup.close());
                delay.play();

            });
        }

        editBtn = null;
        if (!(this instanceof guestMenu) && !(this instanceof UserMenu)) {
            editBtn = new Button("Edit");
            styleMainButton(editBtn);
            editBtn.setOnAction(e -> showEditDialog(food));
        }

        HBox buttons;
        if (addToCartBtn != null && editBtn != null) buttons = new HBox(10, addToCartBtn, editBtn);
        else if (addToCartBtn != null) buttons = new HBox(10, addToCartBtn);
        else if (editBtn != null) buttons = new HBox(10, editBtn);
        else buttons = new HBox();

        buttons.setAlignment(Pos.CENTER);
        card.getChildren().addAll(imgView, name, desc, price, rating, buttons);
        return card;
    }

    protected void addFoodItem() {
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
        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
            showAlert("Error", "Failed to add food item.");
        }
    }


    protected void updateFoodItem() {
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
        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
            showAlert("Error", "Failed to update food item.");
        }
    }

    protected void deleteFoodItem(FoodItems food) {
        foodList.remove(food);
        try {
            FileStorage.rewriteMenu(new ArrayList<>(foodList));
            loadFoodItems();
        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
            showAlert("Error", "Failed to delete item.");
        }
    }

    protected void populateFieldsForEdit(FoodItems food) {
        currentEditingFood = food;
        nameField.setText(food.getName());
        nameField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #333333; -fx-padding: 5;");
        detailsField.setText(food.getDetails());
        detailsField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #333333;");
        priceField.setText(String.valueOf(food.getPrice()));
        priceField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #333333;");
        ratingsField.setText(String.valueOf(food.getRatings()));
        ratingsField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #333333;");
        imageFilenameLabel.setText(food.getImagePath());
        categoryBox.setValue(food.getCategory());
        categoryBox.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #798965;");
        selectedImageFile = null;
        addOrUpdateButton.setText("Update");

        // show form
        addOrUpdateButton.getParent().setVisible(true);
        addOrUpdateButton.getParent().setManaged(true);
        styleMainButton(addOrUpdateButton);
    }

    protected void clearFields() {
        nameField.clear();
        detailsField.clear();
        priceField.clear();
        ratingsField.clear();
        imageFilenameLabel.setText("No image selected");
        imageFilenameLabel.setStyle("-fx-text-fill:#E53935;");
        categoryBox.setValue(null);
        selectedImageFile = null;
        currentEditingFood = null;
        addOrUpdateButton.setText("Add");

        // hide form again
        addOrUpdateButton.getParent().setVisible(false);
        addOrUpdateButton.getParent().setManaged(false);
    }

    protected void chooseImage() {
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
                System.err.println("IOException: " + e.getMessage());
            }
        }
    }

    // ================== EDIT ===================

    protected void showEditDialog(FoodItems food) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit " + food.getName());

        Button updateBtn = new Button("Update");
        styleMainButton(updateBtn);
        updateBtn.setOnAction(e -> {
            populateFieldsForEdit(food);  // fill input fields
            dialog.close();
        });

        Button deleteBtn = new Button("Delete");
        styleMainButton(deleteBtn);
        deleteBtn.setOnAction(e -> {
            deleteFoodItem(food);
            dialog.close();
        });

        Button cancelBtn = new Button("Cancel");
        styleMainButton(cancelBtn);
        cancelBtn.setOnAction(e -> dialog.close());

        VBox vbox = new VBox(15, new Label("Choose an action for " + food.getName()),
                updateBtn, deleteBtn, cancelBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        dialog.setScene(new Scene(vbox, 300, 200));
        dialog.show();
    }
}