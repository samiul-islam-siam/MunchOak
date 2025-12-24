package com.example.menu;

import com.example.manager.*;
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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseMenu {

    // made protected so MenuEdit can access/update
    protected ObservableList<FoodItems> foodList;
    protected VBox foodContainer;

    // UI sections accessible to subclasses and MenuEdit
    protected VBox mainLayout;
    protected Button showAddFormBtn;
    protected HBox categoryButtons;
    protected VBox formBox;
    protected HBox cartButtons;
    protected Button buttonMenu;
    protected String searchKeyword = "";
    protected Button cartButton;

    // Edit delegate
    protected MenuEdit menuEdit;

    public void setCartButton(Button cartButton) {
        this.cartButton = cartButton;
    }

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
        List<FoodItems> items = MenuStorage.loadMenu();

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
            noResultBox.setId("empty-message");

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

        // create the MenuEdit delegate
        menuEdit = new MenuEdit(this);

        // --- Load Foods ---
        loadFoodItems();
    }

    // Cart for the current user
    int userId = Session.getCurrentUserId();

    private Cart cart = new Cart();

    public Node getView() {
        List<FoodItems> loaded = MenuStorage.loadMenu();
        foodList.addAll(loaded);
        List<FoodItems> items = MenuStorage.loadMenu();

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
        categories = CategoryStorage.loadCategories();

        foodContainer = new VBox(20);
        foodContainer.setPadding(new Insets(10));

        // ===== SCROLL PANE FOR FOOD ITEMS =====
        ScrollPane scrollPane = new ScrollPane(foodContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Create formBox from MenuEdit (admin editing form)
        formBox = menuEdit.getFormBox();

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
                    menuEdit.clearFields();
                    formBox.setVisible(true);
                    formBox.setManaged(true);
                }
            });

            buttonMenu = new Button("Add Menu");
            styleMainButton(buttonMenu);
            buttonMenu.setOnAction(e -> menuEdit.chooseMenu());

            Button deleteMenuButton = new Button("Delete Menu");
            styleMainButton(deleteMenuButton);
            deleteMenuButton.setOnAction(e -> menuEdit.deleteMenuFile());

            adminButtons = new HBox(15, showAddFormBtn, buttonMenu);
            adminButtons.setAlignment(Pos.CENTER);
            adminButtons.setPadding(new Insets(0, 0, 0, 0));
        }

        // --- Assemble final layout ---
        mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(5));

        if (adminButtons != null) mainLayout.getChildren().add(adminButtons); // top for admin
        mainLayout.getChildren().addAll(scrollPane, formBox, cartButtons);

        loadFoodItems();
        return mainLayout;
    }

    // ================== CATEGORY MANAGEMENT & EDIT DELEGATE ACCESS ===================
    protected void loadCategories(ComboBox<String> categoryBox) {
        categoryBox.getItems().clear();
        List<String> categories = CategoryStorage.loadCategories();
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

    // Exposed so MenuEdit can style buttons using same style
    protected void styleMainButton(Button button) {
        button.setStyle(
                "-fx-background-color: #FF6B00;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #E65C00;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #FF6B00;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;"
        ));
    }

    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    // ================== FOOD ITEMS MANAGEMENT (VIEW SIDE) ===================
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
        price.setStyle("-fx-font-size: 14px; -fx-font-weight:  bold; -fx-text-fill: #E53935;");

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
        if (!(this instanceof AdminMenu) && !(this instanceof GuestMenu)) {
            addToCartBtn = new Button("Add to Cart");
            styleMainButton(addToCartBtn);

            addToCartBtn.setOnAction(e -> {
                if (food.getQuantity() <= 0) {
                    showAlert("Stock Empty", "This item is out of stock.");
                    return;
                }

                cart.addToCart(food.getId(), 1, 0.0);  // No add-ons from quick add
                updateCartIcon();

                // Popup notification
                Stage popup = new Stage();
                popup.initStyle(StageStyle.TRANSPARENT);
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

                Scene popupScene = new Scene(box);
                popupScene.setFill(Color.TRANSPARENT);
                popup.setScene(popupScene);
                popup.show();

                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(e2 -> popup.close());
                delay.play();

            });
        }

        // Edit button (only for admin)
        if (!(this instanceof GuestMenu) && !(this instanceof UserMenu)) {
            editBtn = new Button("Edit");
            styleMainButton(editBtn);
            editBtn.setOnAction(e -> menuEdit.showEditDialog(food));
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

    protected void updateCartIcon() {
        int count = cart.getTotalItems();   // You already have this function

        if (cartButton == null || cartButton.getGraphic() == null) return;

        Node graphic = cartButton.getGraphic();
        if (graphic instanceof StackPane stack && stack.getChildren().size() > 1 && stack.getChildren().get(1) instanceof Label) {
            Label cartCountLabel = (Label) stack.getChildren().get(1);
            if (count > 0) {
                cartCountLabel.setText(String.valueOf(count));
                cartCountLabel.setVisible(true);
            } else {
                cartCountLabel.setVisible(false);
            }
        }
    }

    protected void showFoodDetail(FoodItems food, VBox card) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        Stage owner = (Stage) card.getScene().getWindow();
        dialog.initOwner(owner);
        dialog.setTitle(food.getName());
        dialog.setWidth(500);
        dialog.setHeight(700); // Increased height to accommodate scrolling content

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

        // Center: Scrollable content to prevent overflow and ensure button visibility via scroll
        VBox center = new VBox(20);
        center.setAlignment(Pos.TOP_CENTER);

        // Wrap center in ScrollPane for scrollable content if it exceeds dialog height
        ScrollPane scrollPane = new ScrollPane(center);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");

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

        // Base price
        final double basePrice = food.getPrice();
        double[] currentTotalPriceHolder = {basePrice};  // Use array for mutability in lambdas
        Label priceLabel = new Label("৳ " + String.format("%.2f", currentTotalPriceHolder[0]));
        priceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #E53935;");

        Label descLabel = new Label(food.getDetails());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Label cuisineLabel = new Label("⭐ " + food.getCuisine());
        cuisineLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFA000;");

        Label quantityLabel = new Label("Quantity: " + food.getQuantity());
        quantityLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFA000;");

        VBox addOnSection = new VBox(10);
        Label optionalLabel = new Label();
        if (!(food.getAddOne().isEmpty() && food.getAddTwo().isEmpty())) {
            // Add On section with multiple options

            Label addOnTitle = new Label("Add On");
            addOnTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FF6B00;"); // Orange text to match banner style
            addOnSection.getChildren().add(addOnTitle);

            // Define add-ons
            Map<String, Double> addOns;
            if (food.getAddOne().isEmpty()) {
                addOns = Map.of(
                        // food.getAddOne(), food.getAddOnePrice(),
                        food.getAddTwo(), food.getAddTwoPrice()
                );
            } else if (food.getAddTwo().isEmpty()) {
                addOns = Map.of(
                        food.getAddOne(), food.getAddOnePrice()
                        // food.getAddTwo(), food.getAddTwoPrice()
                );
            } else {
                addOns = Map.of(
                        food.getAddOne(), food.getAddOnePrice(),
                        food.getAddTwo(), food.getAddTwoPrice()
                );
            }

            // Counters for each add-on
            Map<String, int[]> counters = new LinkedHashMap<>();
            Map<String, Label> qtyLabels = new LinkedHashMap<>();

            for (Map.Entry<String, Double> entry : addOns.entrySet()) {
                String name = entry.getKey();
                double price = entry.getValue();

                // Outer HBox for the entire add-on row (to create a "block" feel)
                HBox addOnBlock = new HBox(8);
                addOnBlock.setPadding(new Insets(8, 12, 8, 12)); // Padding for block-like appearance
                addOnBlock.setStyle(
                        "-fx-background-color: #FAFAFA;" + // Light gray background for block
                                "-fx-background-radius: 8;" +     // Rounded corners
                                "-fx-border-color: #E0E0E0;" +    // Subtle border
                                "-fx-border-radius: 8;" +
                                "-fx-border-width: 1;"
                );
                addOnBlock.setAlignment(Pos.CENTER_LEFT);

                // Left: Name only
                Label addOnName = new Label(name);
                addOnName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;"); // Bold name
                addOnName.setPrefWidth(150); // Fixed width for alignment

                // Spacer to push right side to the right
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Right side: Price and Counter in a VBox or HBox for vertical alignment
                VBox rightSide = new VBox(2);
                rightSide.setAlignment(Pos.CENTER_RIGHT);

                // Price label above the counter
                Label addOnPrice = new Label("+" + String.format("৳ %.0f", price));
                addOnPrice.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #E53935;"); // Red price to match image

                // Counter HBox (centered horizontally within right side)
                HBox counterBox = new HBox(4);
                counterBox.setAlignment(Pos.CENTER);

                Button addOnMinus = new Button("-");
                addOnMinus.setPrefSize(30, 30); // Slightly larger square buttons
                addOnMinus.setStyle(
                        "-fx-background-color: #E53935;" + // Red background
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 4;" + // Slight rounding
                                "-fx-cursor: hand;"
                );
                addOnMinus.setOnMouseEntered(e -> addOnMinus.setStyle(
                        "-fx-background-color: #D32F2F;" + // Darker red on hover
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 4;"
                ));
                addOnMinus.setOnMouseExited(e -> addOnMinus.setStyle(
                        "-fx-background-color: #E53935;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 4;"
                ));

                Label extraQtyLabel = new Label("x0");
                extraQtyLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #666;"); // Bold and gray for visibility

                Button addOnPlus = new Button("+");
                addOnPlus.setPrefSize(30, 30);
                addOnPlus.setStyle(
                        "-fx-background-color: #E53935;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 4;" +
                                "-fx-cursor: hand;"
                );
                addOnPlus.setOnMouseEntered(e -> addOnPlus.setStyle(
                        "-fx-background-color: #D32F2F;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 4;"
                ));
                addOnPlus.setOnMouseExited(e -> addOnPlus.setStyle(
                        "-fx-background-color: #E53935;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 4;"
                ));

                counterBox.getChildren().addAll(addOnMinus, extraQtyLabel, addOnPlus);

                rightSide.getChildren().addAll(addOnPrice, counterBox);

                // Add name, spacer, and right side to the block
                addOnBlock.getChildren().addAll(addOnName, spacer, rightSide);

                int[] count = {0};
                counters.put(name, count);
                qtyLabels.put(name, extraQtyLabel);

                addOnSection.getChildren().add(addOnBlock);

                // Plus action
                addOnPlus.setOnAction(e -> {
                    int totalSelected = 0;
                    for (int[] c : counters.values()) {
                        totalSelected += c[0];
                    }
                    if (totalSelected < 5) {
                        count[0]++;
                        extraQtyLabel.setText("x" + count[0]);
                        // Update price (using array)
                        currentTotalPriceHolder[0] += price;
                        priceLabel.setText("৳ " + String.format("%.2f", currentTotalPriceHolder[0]));
                    }
                });

                // Minus action
                addOnMinus.setOnAction(e -> {
                    if (count[0] > 0) {
                        count[0]--;
                        extraQtyLabel.setText("x" + count[0]);
                        // Update price (using array)
                        currentTotalPriceHolder[0] -= price;
                        priceLabel.setText("৳ " + String.format("%.2f", currentTotalPriceHolder[0]));
                    }
                });
            }

            optionalLabel = new Label("Select up to 5 (optional)");
            optionalLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999; -fx-padding: 0 0 0 4;");
            optionalLabel.setAlignment(Pos.CENTER_LEFT);
            addOnSection.getChildren().add(optionalLabel); // Add the optional label inside the section for better grouping
        }


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
            if (Session.isGuest()) {
                Stage notifyPopup = new Stage();
                notifyPopup.initStyle(StageStyle.TRANSPARENT);
                notifyPopup.setAlwaysOnTop(true);
                Label notifyLabel = new Label("Please Login !");
                notifyLabel.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20 10 20; -fx-background-radius: 10;");
                VBox notifyBox = new VBox(notifyLabel);
                notifyBox.setAlignment(Pos.CENTER);
                notifyBox.setStyle("-fx-background-color: transparent;");
                Scene notifyScene = new Scene(notifyBox, 200, 50);
                notifyScene.setFill(Color.TRANSPARENT);
                notifyPopup.setScene(notifyScene);
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

                // Calculate per-item add-on total
                double addonPerItem = currentTotalPriceHolder[0] - basePrice;

                // Deduct actual selected quantity (not hardcoded -1)
                int selectedQty = currentQuantity[0];
                food.setQuantity(food.getQuantity() - selectedQty);

                cart.addToCart(food.getId(), selectedQty, addonPerItem);  // Pass add-on per item
                updateCartIcon();
                // Popup notification
                Stage notifyPopup = new Stage();
                notifyPopup.initStyle(StageStyle.TRANSPARENT);
                notifyPopup.setAlwaysOnTop(true);
                Label notifyLabel = new Label(food.getName() + " added to cart!");
                notifyLabel.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20 10 20; -fx-background-radius: 10;");
                VBox notifyBox = new VBox(notifyLabel);
                notifyBox.setAlignment(Pos.CENTER);
                notifyBox.setStyle("-fx-background-color: transparent;");
                Scene notifyScene = new Scene(notifyBox, 200, 50);
                notifyScene.setFill(Color.TRANSPARENT);
                notifyPopup.setScene(notifyScene);
                notifyPopup.show();
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(e2 -> notifyPopup.close());
                delay.play();
                dialog.close();
                updateView();
            }
        });

        // Info VBox
        VBox infoVBox = new VBox(15);
        infoVBox.setPrefWidth(400);
        infoVBox.getChildren().addAll(priceLabel, descLabel, cuisineLabel, addOnSection, quantityBox, addToCartDetail);

        center.getChildren().addAll(largeImgView, infoVBox);
        root.setCenter(scrollPane);

        root.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Scene scene = new Scene(root);
        dialog.setScene(scene);
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