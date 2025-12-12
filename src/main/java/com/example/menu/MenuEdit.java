package com.example.menu;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import com.example.munchoak.FoodItems;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class MenuEdit {

    private final BaseMenu owner;

    // Form fields and controls (moved from BaseMenu)
    private TextField nameField, detailsField, priceField, cuisineField, quantityField, addOneField, addOnePriceField, addTwoField, addTwoPriceField;
    private ComboBox<String> categoryBox;
    private Label imageFilenameLabel;
    private File selectedImageFile = null;
    private Button addOrUpdateButton;
    private VBox formBox;

    private FoodItems currentEditingFood = null;

    public MenuEdit(BaseMenu owner) {
        this.owner = owner;
        buildForm();
    }

    public VBox getFormBox() {
        // ensure visibility defaults are consistent with previous behavior
        if (formBox == null) buildForm();
        return formBox;
    }

    private void buildForm() {
        nameField = new TextField();
        detailsField = new TextField();
        priceField = new TextField();
        cuisineField = new TextField();
        quantityField = new TextField();
        addOneField = new TextField();
        addOnePriceField = new TextField();
        addTwoField = new TextField();
        addTwoPriceField = new TextField();

        imageFilenameLabel = new Label("No image selected");

        categoryBox = new ComboBox<>();
        owner.loadCategories(categoryBox);
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
        inputGrid.add(new Label("Add-on-1"), 0, 6);
        inputGrid.add(addOneField, 1, 6);
        inputGrid.add(new Label("Add-on-1-Price"), 0, 7);
        inputGrid.add(addOnePriceField, 1, 7);
        inputGrid.add(new Label("Add-on-2"), 0, 8);
        inputGrid.add(addTwoField, 1, 8);
        inputGrid.add(new Label("Add-on-2-Price"), 0, 9);
        inputGrid.add(addTwoPriceField, 1, 9);

        // Category management buttons
        Button addCatBtn = new Button("Add Category");
        Button renameCatBtn = new Button("Rename Category");
        Button deleteCatBtn = new Button("Delete Category");
        HBox categoryButtons = new HBox(20, addCatBtn, renameCatBtn, deleteCatBtn);
        inputGrid.add(categoryButtons, 1, 10);
        categoryButtons.setSpacing(12);
        categoryButtons.setPadding(new Insets(10, 0, 10, 0));

        for (Node n : categoryButtons.getChildren()) {
            if (n instanceof Button b) owner.styleMainButton(b);
        }
        addCatBtn.setOnAction(e -> addCategory());
        renameCatBtn.setOnAction(e -> renameCategory());
        deleteCatBtn.setOnAction(e -> deleteCategory());

        // Image selection
        Button browseBtn = new Button("Browse...");
        owner.styleMainButton(browseBtn);
        browseBtn.setOnAction(e -> chooseImage());
        HBox imageBox = new HBox(10, imageFilenameLabel, browseBtn);
        imageBox.setAlignment(Pos.CENTER_LEFT);
        inputGrid.add(new Label("Image:"), 0, 11);
        inputGrid.add(imageBox, 1, 11);

        for (Node node : inputGrid.getChildren()) {
            if (node instanceof Label lbl) {
                lbl.setStyle("-fx-text-fill:#E53935;");
            }
        }
        // Add / Update button inside form
        addOrUpdateButton = new Button("Add");
        owner.styleMainButton(addOrUpdateButton);
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
    }

    // ---------------- Menu File Management (moved) ----------------
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
                    owner.showAlert("Info", "The selected menu file is empty or invalid.");
                    return;
                }
                owner.foodList.setAll(importedItems);
                owner.loadFoodItems();

                // Broadcast to all clients
                Session.getMenuClient().sendMenuUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Menu items imported successfully!");
                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("IOException: " + e.getMessage());
                owner.showAlert("Error", "Failed to import menu file.");
            }
        }
    }

    public void deleteMenuFile() {
        File menuFile = FileStorage.getMenuFile();

        if (!menuFile.exists()) {
            owner.showAlert("Menu file not found.", "There’s no menu file to delete.");
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
                    owner.foodList.clear();
                    owner.loadFoodItems();
                    owner.showAlert("Menu Deleted", "The menu file has been deleted successfully.");
                    // Broadcast to all clients
                    Session.getMenuClient().sendMenuUpdate();
                } else {
                    owner.showAlert("Error", "Failed to delete the menu file.");
                }
            }
        });
    }

    // ================== CATEGORY MANAGEMENT ===================
    protected void loadCategories() {
        owner.loadCategories(categoryBox);
    }

    protected void addCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Enter new category name:");
        dialog.setContentText("Category:");
        dialog.showAndWait().ifPresent(name -> {
            if (name.isBlank()) {
                owner.showAlert("Error", "Invalid category name.");
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
                owner.showAlert("Error", "Category already exists or invalid.");
            }
        });
    }

    protected void renameCategory() {
        String selected = categoryBox.getValue();
        if (selected == null) {
            owner.showAlert("Error", "Select a category first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected);
        dialog.setTitle("Rename Category");
        dialog.setHeaderText("Enter new name for category:");
        dialog.setContentText("New Name:");
        dialog.showAndWait().ifPresent(newName -> {
            if (newName.isBlank() || FileStorage.loadCategories().contains(newName)) {
                owner.showAlert("Error", "Invalid or duplicate category name.");
                return;
            }
            try {
                FileStorage.replaceCategory(selected, newName);
                loadCategories();
                categoryBox.setValue(newName);
                // reload menu and UI
                owner.foodList.setAll(FileStorage.loadMenu());
                owner.loadFoodItems();
                // Broadcast to all clients
                Session.getMenuClient().sendMenuUpdate();
            } catch (Exception e) {
                System.err.println("IOException: " + e.getMessage());
                owner.showAlert("Error", "Rename failed.");
            }
        });
    }

    protected void deleteCategory() {
        String selected = categoryBox.getValue();
        if (selected == null) {
            owner.showAlert("Error", "Select a category to delete.");
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
                    owner.foodList.setAll(FileStorage.loadMenu());
                    owner.loadFoodItems();
                    // Broadcast to all clients
                    Session.getMenuClient().sendMenuUpdate();
                } catch (Exception e) {
                    System.err.println("IOException: " + e.getMessage());
                    owner.showAlert("Error", "Delete failed.");
                }
            }
        });
    }

    // ================== FOOD ITEMS MANAGEMENT (EDIT SIDE) ===================
    protected void addFoodItem() {
        if (categoryBox.getValue() == null || categoryBox.getValue().trim().isEmpty()) {
            owner.showAlert("Error", "No category selected.");
            return;
        }
        if (nameField.getText().trim().isEmpty()) {
            owner.showAlert("Error", "Food name cannot be empty.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                owner.showAlert("Error", "Price cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            owner.showAlert("Error", "Invalid price.");
            return;
        }
        String cuisine = cuisineField.getText().trim();
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity < 0) {
                owner.showAlert("Error", "Quantity cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            owner.showAlert("Error", "Invalid Quantity.");
            return;
        }
//        double addOnePrice;
//        try {
//            addOnePrice = Double.parseDouble(addOnePriceField.getText().trim());
//            if (addOnePrice < 0) {
//                owner.showAlert("Error", "Price cannot be negative.");
//                return;
//            }
//        } catch (NumberFormatException e) {
//            owner.showAlert("Error", "Invalid add-on price.");
//            return;
//        }
//        double addTwoPrice;
//        try {
//            addTwoPrice = Double.parseDouble(addTwoPriceField.getText().trim());
//            if (addTwoPrice < 0) {
//                owner.showAlert("Error", "Price cannot be negative.");
//                return;
//            }
//        } catch (NumberFormatException e) {
//            owner.showAlert("Error", "Invalid add-on price.");
//            return;
//        }
        double addOnePrice = 0;
        String addOneText = addOnePriceField.getText().trim();
        if (!addOneText.isEmpty()) {
            try {
                addOnePrice = Double.parseDouble(addOneText);
                if (addOnePrice < 0) {
                    owner.showAlert("Error", "Add-on 1 price cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                owner.showAlert("Error", "Invalid Add-on 1 price.");
                return;
            }
        }

        double addTwoPrice = 0;
        String addTwoText = addTwoPriceField.getText().trim();
        if (!addTwoText.isEmpty()) {
            try {
                addTwoPrice = Double.parseDouble(addTwoText);
                if (addTwoPrice < 0) {
                    owner.showAlert("Error", "Add-on 2 price cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                owner.showAlert("Error", "Invalid Add-on 2 price.");
                return;
            }
        }
        String imageFilename = selectedImageFile != null ? selectedImageFile.getName() : "";

        // compute next id from existing items to avoid ID collisions
        int nextId = 1;
        for (FoodItems f : FileStorage.loadMenu()) {
            if (f.getId() >= nextId) nextId = f.getId() + 1;
        }

        FoodItems newFood = new FoodItems(nextId, nameField.getText().trim(), detailsField.getText().trim(),
                price, cuisine, imageFilename, categoryBox.getValue(), quantity, addOneField.getText().trim(), addOnePrice, addTwoField.getText().trim(), addTwoPrice);

        try {
            FileStorage.appendMenuItem(newFood);
            // reload menu into list & UI
            owner.foodList.setAll(FileStorage.loadMenu());
            owner.loadFoodItems();
            clearFields();
            // Broadcast to all clients
            Session.getMenuClient().sendMenuUpdate();
        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
            owner.showAlert("Error", "Failed to add food item.");
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
        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                owner.showAlert("Error", "Price cannot be negative.");
                return;
            }
            currentEditingFood.setPrice(price);
        } catch (NumberFormatException e) {
            owner.showAlert("Error", "Invalid price.");
            return;
        }
        currentEditingFood.setCuisine(cuisineField.getText().trim());
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());

            if (quantity < 0) {
                owner.showAlert("Error", "Quantity cannot be negative.");
                return;
            }
            currentEditingFood.setQuantity(quantity);
        } catch (NumberFormatException e) {
            owner.showAlert("Error", "Invalid Quantity.");
            return;
        }

        currentEditingFood.setImagePath(imageFilename);
        currentEditingFood.setCategory(categoryBox.getValue());

        currentEditingFood.setAddOne(addOneField.getText().trim());
        currentEditingFood.setAddTwo(addTwoField.getText().trim());
//        double addOnePrice;
//        try {
//            addOnePrice = Double.parseDouble(addOnePriceField.getText().trim());
//            if (addOnePrice < 0) {
//                owner.showAlert("Error", "Price cannot be negative.");
//                return;
//            }
//            currentEditingFood.setAddOnePrice(addOnePrice);
//        } catch (NumberFormatException e) {
//            owner.showAlert("Error", "Invalid price.");
//            return;
//        }
//        double addTwoPrice;
//        try {
//            addTwoPrice = Double.parseDouble(addTwoPriceField.getText().trim());
//            if (addTwoPrice < 0) {
//                owner.showAlert("Error", "Price cannot be negative.");
//                return;
//            }
//            currentEditingFood.setAddTwoPrice(addTwoPrice);
//        } catch (NumberFormatException e) {
//            owner.showAlert("Error", "Invalid price.");
//            return;
//        }
        double addOnePrice = 0;
        String addOneText = addOnePriceField.getText().trim();
        if (!addOneText.isEmpty()) {
            try {
                addOnePrice = Double.parseDouble(addOneText);
                if (addOnePrice < 0) {
                    owner.showAlert("Error", "Add-on 1 price cannot be negative.");
                    return;
                }
                currentEditingFood.setAddOnePrice(addOnePrice);
            } catch (NumberFormatException e) {
                owner.showAlert("Error", "Invalid Add-on 1 price.");
                return;
            }
        }else
        {
            currentEditingFood.setAddOnePrice(0);
        }

        double addTwoPrice = 0;
        String addTwoText = addTwoPriceField.getText().trim();
        if (!addTwoText.isEmpty()) {
            try {
                addTwoPrice = Double.parseDouble(addTwoText);
                if (addTwoPrice < 0) {
                    owner.showAlert("Error", "Add-on 2 price cannot be negative.");
                    return;
                }
                currentEditingFood.setAddTwoPrice(addTwoPrice);
            } catch (NumberFormatException e) {
                owner.showAlert("Error", "Invalid Add-on 2 price.");
                return;
            }
        }else
        {
            currentEditingFood.setAddTwoPrice(0);
        }


        // ---- Update list item instance ----
        for (FoodItems f : owner.foodList) {
            if (f.getId() == currentEditingFood.getId()) {
                f.setName(currentEditingFood.getName());
                f.setDetails(currentEditingFood.getDetails());
                f.setPrice(currentEditingFood.getPrice());
                f.setCuisine(currentEditingFood.getCuisine());
                f.setQuantity(currentEditingFood.getQuantity());
                f.setCategory(currentEditingFood.getCategory());
                f.setImagePath(currentEditingFood.getImagePath());
                f.setAddOne(currentEditingFood.getAddOne());
                f.setAddOnePrice(currentEditingFood.getAddOnePrice());
                f.setAddTwo(currentEditingFood.getAddTwo());
                f.setAddTwoPrice(currentEditingFood.getAddTwoPrice());
                break;
            }
        }

        try {
            // rewrite full menu file from in-memory list
            FileStorage.rewriteMenu(new ArrayList<>(owner.foodList));
            owner.foodList.setAll(FileStorage.loadMenu());
            owner.loadFoodItems();

            // 2. send the image ONLY if changed
            if (selectedImageFile != null) {
                Session.getMenuClient().sendImageUpdate(selectedImageFile);
            }

            // Broadcast to all clients
            Session.getMenuClient().sendMenuUpdate();

            clearFields();
        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
            owner.showAlert("Error", "Failed to update food item.");
        }
    }

    protected void deleteFoodItem(FoodItems food) {
        owner.foodList.remove(food);
        try {
            FileStorage.rewriteMenu(new ArrayList<>(owner.foodList));
            owner.loadFoodItems();
            // Broadcast to all clients
            Session.getMenuClient().sendMenuUpdate();
        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
            owner.showAlert("Error", "Failed to delete item.");
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
        addOneField.setText(String.valueOf(food.getAddOne()));
        addOneField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #333333;");
        addTwoField.setText(String.valueOf(food.getAddTwo()));
        addTwoField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #333333;");
        addOnePriceField.setText(String.valueOf(food.getAddOnePrice()));
        addOnePriceField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #333333;");
        addTwoPriceField.setText(String.valueOf(food.getAddTwoPrice()));
        addTwoPriceField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #333333;");
        imageFilenameLabel.setText(food.getImagePath());
        categoryBox.setValue(food.getCategory());
        categoryBox.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #798965;");
        selectedImageFile = null;
        addOrUpdateButton.setText("Update");

        // show form
        formBox.setVisible(true);
        formBox.setManaged(true);
        owner.styleMainButton(addOrUpdateButton);
    }

    protected void clearFields() {
        nameField.clear();
        detailsField.clear();
        priceField.clear();
        cuisineField.clear();
        quantityField.clear();
        addOneField.clear();
        addOnePriceField.clear();
        addTwoField.clear();
        addTwoPriceField.clear();
        imageFilenameLabel.setText("No image selected");
        imageFilenameLabel.setStyle("-fx-text-fill:#E53935;");
        categoryBox.setValue(null);
        selectedImageFile = null;
        currentEditingFood = null;
        addOrUpdateButton.setText("Add");

        // hide form again
        formBox.setVisible(false);
        formBox.setManaged(false);
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

                selectedImageFile = destFile; // store dest file as the selected image
                imageFilenameLabel.setText(selectedImageFile.getName());

                Session.getMenuClient().sendImageUpdate(destFile);

            } catch (Exception e) {
                System.err.println("IOException: " + e.getMessage());
            }
        }
    }

    // ================== Edit Dialog for admin actions ===================
    protected void showEditDialog(FoodItems food) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit " + food.getName());

        Button updateBtn = new Button("Update");
        owner.styleMainButton(updateBtn);
        updateBtn.setOnAction(e -> {
            populateFieldsForEdit(food);  // fill input fields
            dialog.close();
        });

        Button deleteBtn = new Button("Delete");
        owner.styleMainButton(deleteBtn);
        deleteBtn.setOnAction(e -> {
            deleteFoodItem(food);
            dialog.close();
        });

        Button cancelBtn = new Button("Cancel");
        owner.styleMainButton(cancelBtn);
        cancelBtn.setOnAction(e -> dialog.close());

        VBox vbox = new VBox(15, new Label("Choose an action for " + food.getName()),
                updateBtn, deleteBtn, cancelBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        dialog.setScene(new Scene(vbox, 300, 200));
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner.mainLayout.getScene() != null ? (Stage) owner.mainLayout.getScene().getWindow() : null);
        dialog.show();
    }
}