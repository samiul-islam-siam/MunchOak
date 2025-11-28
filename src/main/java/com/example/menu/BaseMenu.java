package com.example.menu;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import com.example.munchoak.Cart;
import com.example.munchoak.FoodItems;
import javafx.animation.*;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseMenu {

    private ObservableList<FoodItems> foodList;
    private VBox foodContainer;

    private TextField nameField, detailsField, priceField, cuisineField, quantityField;
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
    protected Button buttonMenu;
    protected String searchKeyword = "";

    public void setSearchKeyword(String keyword) {
        this.searchKeyword = keyword == null ? "" : keyword.toLowerCase();
    }

    public void updateView() {
        // Remove guest empty message if it exists
        Node emptyMsg = mainLayout.lookup("#empty-message");
        if (emptyMsg != null) {
            ((Pane) emptyMsg.getParent()).getChildren().remove(emptyMsg);
        }

        // reload the full menu
        List<FoodItems> items = FileStorage.loadMenu();

        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            String key = searchKeyword.toLowerCase();
            items = items.stream()
                    .filter(i -> i.getName().toLowerCase().contains(key)
                            || i.getCategory().toLowerCase().contains(key)
                            || i.getDetails().toLowerCase().contains(key)
                            || i.getCuisine().toLowerCase().contains(key))
                    .collect(Collectors.toList());
        }

        foodList.setAll(items);
        loadFoodItems();

        if (items.isEmpty()) {
            foodContainer.getChildren().clear();

            VBox noResultBox = new VBox(10);
            noResultBox.setAlignment(Pos.CENTER);
            noResultBox.setPadding(new Insets(50));

            Label bigEmoji = new Label("🔎");
            bigEmoji.setStyle("-fx-font-size: 60px;");

            Label title = new Label("No Results Found");
            title.setStyle(
                    "-fx-font-size: 24px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #444;"
            );

            Label sub = new Label("Try searching with a different keyword.");
            sub.setStyle(
                    "-fx-font-size: 14px;" +
                            "-fx-text-fill: #777;"
            );

            noResultBox.getChildren().addAll(bigEmoji, title, sub);

            foodContainer.getChildren().add(noResultBox);

        }
    }

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
        foodContainer.setStyle("-fx-background-color: white;");
        foodList = FXCollections.observableArrayList();
        cartButtons = new HBox(20);
        cartButtons.setAlignment(Pos.CENTER);
        cartButtons.setPadding(new Insets(20, 0, 40, 0));


        // --- Assemble Layout ---
        mainLayout.getChildren().addAll(navBar, bannerSection, foodContainer, cartButtons);

        // --- Load Foods ---
        loadFoodItems();
    }

    // Cart for the current user
    int userId = Session.getCurrentUserId();

    private Cart cart = new Cart();
    //public void setSearchKeyword(String kw) {}

    public Node getView() {
        List<FoodItems> loaded = FileStorage.loadMenu();
        foodList.addAll(loaded);
        List<FoodItems> items = FileStorage.loadMenu();

        // APPLY SEARCH KEYWORD HERE
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            String key = searchKeyword.toLowerCase();
            items = items.stream()
                    .filter(i -> i.getName().toLowerCase().contains(key)
                            || i.getCategory().toLowerCase().contains(key)
                            || i.getDetails().toLowerCase().contains(key)
                            || i.getCuisine().toLowerCase().contains(key))
                    .collect(Collectors.toList());
        }
        foodList.clear();
        foodList.addAll(items);


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
        cuisineField = new TextField();
        quantityField = new TextField();
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
        inputGrid.add(new Label("Cuisine:"), 0, 3);
        inputGrid.add(cuisineField, 1, 3);
        inputGrid.add(new Label("Category:"), 0, 4);
        inputGrid.add(categoryBox, 1, 4);
        inputGrid.add(new Label("Quantity:"), 0, 5);
        inputGrid.add(quantityField, 1, 5);

        // Category management buttons
        Button addCatBtn = new Button("Add Category");
        Button renameCatBtn = new Button("Rename Category");
        Button deleteCatBtn = new Button("Delete Category");
        categoryButtons = new HBox(20, addCatBtn, renameCatBtn, deleteCatBtn);
        inputGrid.add(categoryButtons, 1, 6);
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
        inputGrid.add(new Label("Image:"), 0, 7);
        inputGrid.add(imageBox, 1, 7);

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

            adminButtons = new HBox(15, showAddFormBtn, buttonMenu);
            adminButtons.setAlignment(Pos.CENTER);
            adminButtons.setPadding(new Insets(10, 0, 10, 0));
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
                    // Broadcast to all clients
                    Session.getMenuClient().sendMenuUpdate();
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

    public void chooseMenu() {
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

                // Broadcast to all clients
                Session.getMenuClient().sendMenuUpdate();

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
                // Broadcast to all clients
                Session.getMenuClient().sendMenuUpdate();
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
                // Broadcast to all clients
                Session.getMenuClient().sendMenuUpdate();
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
                    // Broadcast to all clients
                    Session.getMenuClient().sendMenuUpdate();
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
        Map<String, HBox> categoryRows = new LinkedHashMap<>();

        for (FoodItems food : foodList) {
            String category = food.getCategory();

            // --- Create a new section for each category ---
            if (!categoryRows.containsKey(category)) {
                Label categoryLabel = new Label(category.toUpperCase());
                categoryLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #E53935; -fx-font-weight: bold;");
                Separator separator = new Separator();
                separator.setPrefWidth(800);

                // Horizontal row for food cards
                HBox foodRow = new HBox(20);
                foodRow.setPadding(new Insets(10));
                foodRow.setAlignment(Pos.CENTER_LEFT);

                // ScrollPane to hold row
                ScrollPane scrollPane = new ScrollPane(foodRow);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setFitToHeight(true);
                scrollPane.setPannable(true);
                scrollPane.setStyle("-fx-background-color: transparent;");

                // --- Left and right arrow buttons ---
                Button leftArrow = new Button("<");
                Button rightArrow = new Button(">");
                styleArrowButton(leftArrow);
                styleArrowButton(rightArrow);

                leftArrow.setOpacity(0); // Initially hidden
                rightArrow.setOpacity(0);

                // Arrow click actions — smooth scroll
                leftArrow.setOnAction(e -> smoothScroll(scrollPane, -0.3));
                rightArrow.setOnAction(e -> smoothScroll(scrollPane, 0.3));

                // StackPane overlays arrows on top of the scroll area
                StackPane scrollArea = new StackPane(scrollPane);
                StackPane.setAlignment(leftArrow, Pos.CENTER_LEFT);
                StackPane.setAlignment(rightArrow, Pos.CENTER_RIGHT);
                scrollArea.getChildren().addAll(leftArrow, rightArrow);
                scrollArea.setPadding(new Insets(0, 40, 0, 40));

                // --- Hover logic: fade arrows in/out ---
                scrollArea.setOnMouseEntered(e -> fadeArrows(leftArrow, rightArrow, 1.0));
                scrollArea.setOnMouseExited(e -> fadeArrows(leftArrow, rightArrow, 0.0));

                // Combine all into a category section
                VBox section = new VBox(10, categoryLabel, separator, scrollArea);
                section.setPadding(new Insets(15, 10, 30, 10));

                foodContainer.getChildren().add(section);
                categoryRows.put(category, foodRow);
            }

            // Add cards to their category row
            categoryRows.get(category).getChildren().add(createFoodCard(food));
        }
    }

    protected VBox createFoodCard(FoodItems food) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(12));
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(240);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
        );

        // --- IMAGE SETUP ---
        ImageView imgView = new ImageView();
        imgView.setFitWidth(210);
        imgView.setFitHeight(140);
        imgView.setPreserveRatio(true);

        // Load image safely
        String imagePath = "/images/" + food.getImagePath();
        Image image = null;
        try (InputStream is = getClass().getResourceAsStream(imagePath)) {
            if (is != null) {
                image = new Image(is);
            } else {
                String filePath = "file:src/main/resources/com/example/manager/images/" + food.getImagePath();
                image = new Image(filePath);
            }
        } catch (Exception ignored) {
        }

        // Fallback placeholder
        if (image == null || image.isError()) {
            try (InputStream placeholder = getClass().getResourceAsStream("/images/placeholder.png")) {
                if (placeholder != null) image = new Image(placeholder);
            } catch (Exception ignored) {
            }
        }

        imgView.setImage(image);

        // --- TEXT DETAILS ---
        Label name = new Label(food.getName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label desc = new Label(food.getDetails());
        desc.setWrapText(true);
        desc.setMaxWidth(200);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        Label price = new Label(String.format("Price: ৳ %.2f", food.getPrice()));
        price.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #E53935;");

        Label quantity = new Label("Quantity: " + food.getQuantity());
        quantity.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #E53935;");

        Label cuisine = new Label("⭐ " + food.getCuisine());
        cuisine.setStyle("-fx-font-size: 13px; -fx-text-fill: #FFA000;");

        // --- BUTTONS ---
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button addToCartBtn = null;
        Button editBtn = null;

        // Add to Cart button (only for user)
        if (!(this instanceof AdminMenu) && !(this instanceof guestMenu)) {
            addToCartBtn = new Button("Add to Cart");
            styleMainButton(addToCartBtn);

            addToCartBtn.setOnAction(e -> {
                if (food.getQuantity() <= 0) {
                    showAlert("Stock Empty", "This item is out of stock.");
                    return;
                }

                cart.addToCart(food.getId(), 1);

                // Popup notification
                Stage popup = new Stage();
                popup.initStyle(StageStyle.UNDECORATED);
                popup.setAlwaysOnTop(true);

                Label label = new Label(food.getName() + " added to cart!");
                label.setStyle(
                        "-fx-background-color: #E53935; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 20 10 20; " +
                                "-fx-background-radius: 10;"
                );

                VBox box = new VBox(label);
                box.setAlignment(Pos.CENTER);
                box.setStyle("-fx-background-color: transparent;");

                popup.setScene(new Scene(box));
                popup.show();

                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(e2 -> popup.close());
                delay.play();

            });
        }

        // Edit button (only for admin)
        if (!(this instanceof guestMenu) && !(this instanceof UserMenu)) {
            editBtn = new Button("Edit");
            styleMainButton(editBtn);
            editBtn.setOnAction(e -> showEditDialog(food));
        }

        // Combine buttons dynamically
        if (addToCartBtn != null) buttons.getChildren().add(addToCartBtn);
        if (editBtn != null) buttons.getChildren().add(editBtn);

        // --- ADD EVERYTHING TO CARD ---
        card.getChildren().addAll(imgView, name, desc, price, quantity, cuisine);
        if (!buttons.getChildren().isEmpty()) card.getChildren().add(buttons);

        // Click event to show detail popup
        card.setOnMouseClicked(e -> showFoodDetail(food, (VBox) e.getSource()));

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
        String cuisine = cuisineField.getText().trim();
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity < 0) {
                showAlert("Error", "Quantity cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Quantity.");
            return;
        }
        String imageFilename = selectedImageFile != null ? selectedImageFile.getName() : "";

        // compute next id from existing items to avoid ID collisions
        int nextId = 1;
        for (FoodItems f : FileStorage.loadMenu()) {
            if (f.getId() >= nextId) nextId = f.getId() + 1;
        }

        FoodItems newFood = new FoodItems(nextId, nameField.getText().trim(), detailsField.getText().trim(),
                price, cuisine, imageFilename, categoryBox.getValue(), quantity);

        try {
            FileStorage.appendMenuItem(newFood);
            // reload menu into list & UI
            foodList.setAll(FileStorage.loadMenu());
            loadFoodItems();
            clearFields();
            // Broadcast to all clients
            Session.getMenuClient().sendMenuUpdate();
        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
            showAlert("Error", "Failed to add food item.");
        }
    }

    protected void showFoodDetail(FoodItems food, VBox card) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        Stage owner = (Stage) card.getScene().getWindow();
        dialog.initOwner(owner);
        dialog.setTitle(food.getName());
        dialog.setWidth(500);
        dialog.setHeight(600);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Top: Title and Close button
        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(food.getName());
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Button closeBtn = new Button("X");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand;");
        closeBtn.setOnAction(ev -> dialog.close());
        HBox.setHgrow(title, Priority.ALWAYS);
        top.getChildren().addAll(title, closeBtn);
        root.setTop(top);

        // Center: Image on top, info below (changed from HBox to VBox)
        VBox center = new VBox(20);
        center.setAlignment(Pos.TOP_CENTER);

        // Large Image
        ImageView largeImgView = new ImageView();
        largeImgView.setFitWidth(200);
        largeImgView.setFitHeight(200);
        largeImgView.setPreserveRatio(true);

        // Load image safely (same as card)
        String imagePath = "/images/" + food.getImagePath();
        Image image = null;
        try (InputStream is = getClass().getResourceAsStream(imagePath)) {
            if (is != null) {
                image = new Image(is);
            } else {
                String filePath = "file:src/main/resources/com/example/manager/images/" + food.getImagePath();
                image = new Image(filePath);
            }
        } catch (Exception ignored) {
        }

        // Fallback placeholder
        if (image == null || image.isError()) {
            try (InputStream placeholder = getClass().getResourceAsStream("/images/placeholder.png")) {
                if (placeholder != null) image = new Image(placeholder);
            } catch (Exception ignored) {
            }
        }
        largeImgView.setImage(image);

        // Info section (below image, full width)
        VBox infoVBox = new VBox(15);
        infoVBox.setPrefWidth(400); // Wider to utilize full dialog width

        Label priceLabel = new Label("Tk " + String.format("%.2f", food.getPrice()));
        priceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #E53935;");

        Label descLabel = new Label(food.getDetails());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Label cuisineLabel = new Label("⭐ " + food.getCuisine());
        cuisineLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFA000;");

        Label quantityLabel = new Label("Quantity:"+ food.getQuantity());
        quantityLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFA000;");
        // Add On section (simple hardcoded example)
        VBox addOnSection = new VBox(10);
        Label addOnTitle = new Label("Add On");
        addOnTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        HBox addOnItem = new HBox(10);
        Label addOnName = new Label("Extra Patty");
        Label addOnPrice = new Label("+Tk 99");
        Button addOnPlus = new Button("+");
        addOnPlus.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5;");
        addOnItem.getChildren().addAll(addOnName, addOnPrice, addOnPlus);
        addOnSection.getChildren().addAll(addOnTitle, addOnItem);
        Label optionalLabel = new Label("Select up to 5 (optional)");
        optionalLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");

        // Quantity
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER);
        Button minusBtn = new Button("-");
        Label qtyLabel = new Label("1");
        Button plusBtn = new Button("+");
        minusBtn.setPrefSize(40, 40);
        plusBtn.setPrefSize(40, 40);
        minusBtn.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold;");
        plusBtn.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold;");
        qtyLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        quantityBox.getChildren().addAll(minusBtn, qtyLabel, plusBtn);

        final int[] currentQuantity = {1};
        minusBtn.setOnAction(ev -> {
            if (currentQuantity[0] > 1) {
                currentQuantity[0]--;
                qtyLabel.setText(String.valueOf(currentQuantity[0]));
            }
        });
        plusBtn.setOnAction(ev -> {
            currentQuantity[0]++;
            qtyLabel.setText(String.valueOf(currentQuantity[0]));
        });

        // Add to Cart button
        Button addToCartDetail = new Button("Add to cart");
        styleMainButton(addToCartDetail);
        addToCartDetail.setPrefWidth(Double.MAX_VALUE);
        addToCartDetail.setOnAction(ev -> {
            if (Session.getCurrentUsername().equals("guest")) {
                Stage notifyPopup = new Stage();
                notifyPopup.initStyle(StageStyle.UNDECORATED);
                notifyPopup.setAlwaysOnTop(true);
                Label notifyLabel = new Label("Please Login !");
                notifyLabel.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20 10 20; -fx-background-radius: 10;");
                VBox notifyBox = new VBox(notifyLabel);
                notifyBox.setAlignment(Pos.CENTER);
                notifyBox.setStyle("-fx-background-color: transparent;");
                notifyPopup.setScene(new Scene(notifyBox, 200, 50));
                notifyPopup.show();
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(e2 -> notifyPopup.close());
                delay.play();
                dialog.close();
            } else {

                if (food.getQuantity() <= 0) {
                    showAlert("Stock Empty", "This item is out of stock.");
                    return;
                }

                food.setQuantity(food.getQuantity() - 1);
                //List<FoodItems> updated = FileStorage.loadMenu();
                // save updated list to file
                cart.addToCart(food.getId(), currentQuantity[0]);
                // Popup notification
                Stage notifyPopup = new Stage();
                notifyPopup.initStyle(StageStyle.UNDECORATED);
                notifyPopup.setAlwaysOnTop(true);
                Label notifyLabel = new Label(food.getName() + " added to cart!");
                notifyLabel.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20 10 20; -fx-background-radius: 10;");
                VBox notifyBox = new VBox(notifyLabel);
                notifyBox.setAlignment(Pos.CENTER);
                notifyBox.setStyle("-fx-background-color: transparent;");
                notifyPopup.setScene(new Scene(notifyBox, 200, 50));
                notifyPopup.show();
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(e2 -> notifyPopup.close());
                delay.play();
                dialog.close();
                updateView();
            }
        });

        infoVBox.getChildren().addAll(priceLabel, descLabel, cuisineLabel, addOnSection, optionalLabel, quantityBox, addToCartDetail);

        center.getChildren().addAll(largeImgView, infoVBox);
        root.setCenter(center);

        root.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.show();
    }

    protected void updateFoodItem() {
        if (currentEditingFood == null) return;

        String imageFilename = currentEditingFood.getImagePath();
        System.out.println(imageFilename);
        if (selectedImageFile != null) {
            imageFilename = selectedImageFile.getName();
            System.out.println(imageFilename);
        }
        currentEditingFood.setName(nameField.getText().trim());
        currentEditingFood.setDetails(detailsField.getText().trim());
        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                showAlert("Error", "Price cannot be negative.");
                return;
            }
            currentEditingFood.setPrice(price);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid price.");
            return;
        }
        currentEditingFood.setCuisine(cuisineField.getText().trim());
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());

            if (quantity < 0) {
                showAlert("Error", "Quantity cannot be negative.");
                return;
            }
            currentEditingFood.setQuantity(quantity);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Quantity.");
            return;
        }

        currentEditingFood.setImagePath(imageFilename);
        currentEditingFood.setCategory(categoryBox.getValue());

        // ---- FIX: Update list item instance ----
        for (FoodItems f : foodList) {
            if (f.getId() == currentEditingFood.getId()) {
                f.setName(currentEditingFood.getName());
                f.setDetails(currentEditingFood.getDetails());
                f.setPrice(currentEditingFood.getPrice());
                f.setCuisine(currentEditingFood.getCuisine());
                f.setQuantity(currentEditingFood.getQuantity());
                f.setCategory(currentEditingFood.getCategory());
                f.setImagePath(currentEditingFood.getImagePath());
                break;
            }
        }

        try {
            // rewrite full menu file from in-memory list
            FileStorage.rewriteMenu(new ArrayList<>(foodList));
            foodList.setAll(FileStorage.loadMenu());
            loadFoodItems();
            System.out.println("Image UP");


            // 2. send the image ONLY if changed
            if (selectedImageFile != null) {
                Session.getMenuClient().sendImageUpdate(selectedImageFile);
            }

            // Broadcast to all clients
            Session.getMenuClient().sendMenuUpdate();

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
            // Broadcast to all clients
            Session.getMenuClient().sendMenuUpdate();
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
        cuisineField.setText(String.valueOf(food.getCuisine()));
        cuisineField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #333333;");
        quantityField.setText(String.valueOf(food.getQuantity()));
        quantityField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #333333;");
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
        cuisineField.clear();
        quantityField.clear();
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
                imageFilenameLabel.setText(selectedImageFile.getName());

                Session.getMenuClient().sendImageUpdate(destFile);
                //Session.getMenuClient().sendMenuUpdate();

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

    // Style the arrow buttons
    private void styleArrowButton(Button arrow) {
        arrow.setPrefSize(40, 40);
        arrow.setStyle("""
                    -fx-background-color: rgba(0,0,0,0.6);
                    -fx-text-fill: white;
                    -fx-font-size: 20px;
                    -fx-background-radius: 50%;
                    -fx-cursor: hand;
                """);

        arrow.setOnMouseEntered(e -> arrow.setOpacity(1));
        arrow.setOnMouseExited(e -> arrow.setOpacity(0.8));
    }

    // Smoothly scroll the scrollpane
    private void smoothScroll(ScrollPane scrollPane, double delta) {
        double newValue = Math.max(0, Math.min(1, scrollPane.getHvalue() + delta));
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(scrollPane.hvalueProperty(), newValue, Interpolator.EASE_BOTH))
        );
        timeline.play();
    }

    // Fade arrows in/out on hover
    private void fadeArrows(Button leftArrow, Button rightArrow, double targetOpacity) {
        Timeline fade = new Timeline(
                new KeyFrame(Duration.millis(250),
                        new KeyValue(leftArrow.opacityProperty(), targetOpacity, Interpolator.EASE_BOTH),
                        new KeyValue(rightArrow.opacityProperty(), targetOpacity, Interpolator.EASE_BOTH))
        );
        fade.play();
    }

    // near the cart field
    public Cart getCart() {
        return this.cart;
    }

    // New method to allow injecting a shared Cart instance (for navigation persistence)
    public void setCart(Cart cart) {
        this.cart = cart;
    }
}