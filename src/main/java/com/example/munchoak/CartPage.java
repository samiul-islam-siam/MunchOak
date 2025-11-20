package com.example.munchoak;

import com.example.manager.FileStorage;
import com.example.manager.Session;
import com.example.menu.MenuPage;
import com.example.view.HomePage;
import com.example.view.LoginPage;
import com.example.view.ProfilePage;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CartPage {
    private final Stage primaryStage;
    private final Cart cart;
    private double disCount;
    private double total;
    public void setDisCount(double disCount)
    {
        this.disCount = disCount;
    }

    public double getTotal()
    {
        return total;
    }

    public double getDisCount()
    {
        return disCount;
    }

    public double getDeliveryAmount()
    {
        return 7.99;
    }
    public double getTaxAmount()
    {
        return 7.00;
    }
    public double getServiceFeeAmount()
    {
        return 1.50;
    }

    public CartPage(Stage primaryStage, Cart cart) {
        this.primaryStage = primaryStage;
        this.cart = cart;
    }

    private Map<Integer, FoodItems> buildFoodMap() {
        List<FoodItems> loaded = FileStorage.loadMenu();
        Map<Integer, FoodItems> map = new HashMap<>();
        for (FoodItems f : loaded) map.put(f.getId(), f);
        return map;
    }

    public Scene getScene() {
        Map<Integer, FoodItems> foodMap = buildFoodMap();

        // PAGE BACKGROUND
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #FFDAB9;");  // Peach yellow background

        // TOP CONTAINER FOR NAVBAR AND BREADCRUMB
        VBox topContainer = new VBox();
        topContainer.setSpacing(0);

        // NAVIGATION BAR
        HBox navBar = new HBox();
        navBar.setPadding(new Insets(10, 20, 10, 20));
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setStyle("-fx-background-color: #FF6B00; -fx-background-radius: 0 0 10 10;");

        // LEFT GROUP: LOGO WITH ROUND FRAME AND TITLE
        HBox leftGroup = new HBox(10);
        leftGroup.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = new ImageView();
        logo.setFitWidth(60);
        logo.setFitHeight(60);
        logo.setPreserveRatio(true);
        Image logoImg = null;
        String logoPath = "/images/logo.png";  // Assume logo path
        try (InputStream is = getClass().getResourceAsStream(logoPath)) {
            if (is != null) logoImg = new Image(is);
        } catch (Exception ignored) {}
        if (logoImg != null) {
            logo.setImage(logoImg);
        }
        Circle clip = new Circle(30, 30, 30);
        logo.setClip(clip);

        Label titleLabel = new Label("MunchOak");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        leftGroup.getChildren().addAll(logo, titleLabel);

        // SEARCH BAR IN THE MIDDLE
        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER);
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 8 12; -fx-background-color: white; -fx-border-radius: 20;");
        Button searchBtn = new Button("🔍");
        searchBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        searchBtn.setOnMouseEntered(e -> searchBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;"));
        searchBtn.setOnMouseExited(e -> searchBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;"));
        // TODO: Add search action, e.g., searchBtn.setOnAction(e -> { /* perform search */ });

        searchContainer.getChildren().addAll(searchField, searchBtn);

        // SPACERS
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // BUTTONS HBOX ON THE RIGHT
        HBox buttonsHBox = new HBox(20);
        buttonsHBox.setAlignment(Pos.CENTER_RIGHT);

        Button homeBtn = new Button("Home");
        homeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        homeBtn.setOnMouseEntered(e -> homeBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        homeBtn.setOnMouseExited(e -> homeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        // TODO: Add navigation action, e.g., homeBtn.setOnAction(e -> primaryStage.setScene(new HomePage(primaryStage).getScene()));
        homeBtn.setOnAction(e -> primaryStage.setScene(new HomePage(primaryStage).getHomeScene()));

        Button menuBtn = new Button("Menu");
        menuBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        menuBtn.setOnMouseEntered(e -> menuBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        menuBtn.setOnMouseExited(e -> menuBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        // TODO: Add navigation action, e.g., menuBtn.setOnAction(e -> primaryStage.setScene(new MenuPage(primaryStage, cart).getScene()));
        menuBtn.setOnAction(e -> primaryStage.setScene(new MenuPage(primaryStage, cart).getMenuScene()));

        Button cartBtn = new Button("Cart");
        cartBtn.setStyle("-fx-background-color: white; -fx-text-fill: #FF6B00; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-cursor: hand;");  // Active state
        cartBtn.setOnAction(e -> primaryStage.setScene(getScene()));

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        // TODO: Add navigation action, e.g., loginBtn.setOnAction(e -> primaryStage.setScene(new LoginPage(primaryStage).getScene()));
        loginBtn.setOnAction(e -> primaryStage.setScene(new LoginPage(primaryStage).getLoginScene()));

        Button profileBtn = new Button("Profile");
        profileBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        profileBtn.setOnMouseEntered(e -> profileBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        profileBtn.setOnMouseExited(e -> profileBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        profileBtn.setOnAction(e -> {
            ProfilePage profilePage = new ProfilePage(primaryStage);
            primaryStage.setScene(profilePage.getProfileScene());
        });

        buttonsHBox.getChildren().addAll(homeBtn, menuBtn, cartBtn, loginBtn, profileBtn);

        navBar.getChildren().addAll(leftGroup, spacer1, searchContainer, spacer2, buttonsHBox);

        // BREADCRUMB STEPS
        HBox breadcrumb = new HBox(20);
        breadcrumb.setAlignment(Pos.CENTER);
        breadcrumb.setPadding(new Insets(10, 20, 10, 20));

        Label cartStep = new Label("Cart");
        cartStep.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label sep1 = new Label(" > ");
        sep1.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");

        Label shippingStep = new Label("Shipping");
        shippingStep.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");

        Label sep2 = new Label(" > ");
        sep2.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");

        Label paymentStep = new Label("Payment");
        paymentStep.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");

        breadcrumb.getChildren().addAll(cartStep, sep1, shippingStep, sep2, paymentStep);

        topContainer.getChildren().addAll(navBar, breadcrumb);
        root.setTop(topContainer);

        // MAIN CONTENT HBOX WITH BACKGROUND
        HBox mainContent = new HBox(20);
        mainContent.setStyle("-fx-background-color: #FFDAB9;");  // Ensure main content area has peach yellow

        // LEFT SIDE - CART ITEMS
        VBox leftColumn = new VBox(20);
        leftColumn.setPadding(new Insets(25, 30, 25, 30));
        leftColumn.setStyle("-fx-background-color: transparent;");

        Label title = new Label("My Cart");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333;");
        leftColumn.getChildren().add(title);

        VBox itemList = new VBox(18);
        itemList.setAlignment(Pos.TOP_CENTER);

        // EMPTY CART
        if (cart.getBuyHistory().isEmpty()) {
            Label empty = new Label("Your cart is empty.");
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: #666;");
            itemList.getChildren().add(empty);

        } else {
            for (Map.Entry<Integer, Integer> e : cart.getBuyHistory().entrySet()) {
                int foodId = e.getKey();
                int qty = e.getValue();
                FoodItems item = foodMap.get(foodId);
                if (item == null) continue;

                HBox card = new HBox(20);
                card.setPadding(new Insets(18));
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 12;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8,0,0,2);"
                );

                // IMAGE
                ImageView iv = new ImageView();
                iv.setFitWidth(90);
                iv.setFitHeight(90);
                iv.setPreserveRatio(true);

                Image img = null;
                String resourcePath = "/images/" + item.getImagePath();
                try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                    if (is != null) img = new Image(is);
                    else {
                        String filePath = "file:src/main/resources/com/example/manager/images/" + item.getImagePath();
                        img = new Image(filePath);
                    }
                } catch (Exception ignored) {}

                if (img != null) iv.setImage(img);

                // INFO COLUMN
                VBox info = new VBox(6);
                Label name = new Label(item.getName());
                name.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: black;");
                Label details = new Label(item.getDetails());
                details.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
                details.setMaxWidth(300);
                details.setWrapText(true);

                Label priceEach = new Label(String.format("৳ %.2f", item.getPrice()));
                priceEach.setStyle("-fx-font-size: 15px; -fx-text-fill: #333;");

                info.getChildren().addAll(name, details, priceEach);

                // SPACER
                Region spacer3 = new Region();
                HBox.setHgrow(spacer3, Priority.ALWAYS);

                // QUANTITY BOX
                HBox qtyBox = new HBox(8);
                qtyBox.setAlignment(Pos.CENTER);
                Button minus = new Button("-");
                minus.setStyle(buttonStyleSmall());
                Label qtyLabel = new Label(String.valueOf(qty));
                qtyLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
                Button plus = new Button("+");
                plus.setStyle(buttonStyleSmall());

                minus.setOnAction(evt -> {
                    cart.removeFromCart(foodId);
                    primaryStage.setScene(getScene());
                });
                plus.setOnAction(evt -> {
                    cart.addToCart(foodId, 1);
                    primaryStage.setScene(getScene());
                });

                qtyBox.getChildren().addAll(minus, qtyLabel, plus);

                // REMOVE BUTTON
                Button remove = new Button("Remove");
                remove.setStyle("-fx-background-color: transparent; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                remove.setOnAction(evt -> {
                    cart.removeFromCartEntirely(foodId);
                    primaryStage.setScene(getScene());
                });

                VBox sideControls = new VBox(6, qtyBox, remove);
                sideControls.setAlignment(Pos.CENTER_RIGHT);

                // LINE TOTAL
                Label lineTotal = new Label(String.format("৳ %.2f", item.getPrice() * qty));
                lineTotal.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: black;");

                card.getChildren().addAll(iv, info, spacer3, sideControls, lineTotal);

                // ANIMATION
                FadeTransition ft = new FadeTransition(Duration.millis(230), card);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();

                itemList.getChildren().add(card);
            }
        }

        leftColumn.getChildren().add(itemList);

        // NEW SECTION: RELATED FOODS (below cart items) - Made as class fields for listener access
        Label relatedTitle = new Label("You Might Also Like");
        relatedTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 20 0 10 0;");

        // Get all menu items not already in cart
        Set<Integer> inCartIds = cart.getBuyHistory().keySet();
        List<FoodItems> allMenuItems = FileStorage.loadMenu();
        List<FoodItems> suggestedItems;

        if (cart.getBuyHistory().isEmpty()) {
            // If cart is empty, suggest first 6 items
            suggestedItems = allMenuItems.stream()
                    .limit(6)
                    .collect(Collectors.toList());
        } else {
            // Collect categories from cart items
            Set<String> cartCategories = new HashSet<>();
            for (Integer foodId : inCartIds) {
                FoodItems cartItem = foodMap.get(foodId);
                if (cartItem != null) {
                    cartCategories.add(cartItem.getCategory());  // Assuming FoodItems has getCategory() returning String
                }
            }

            // Suggest items from the same categories, not in cart
            suggestedItems = allMenuItems.stream()
                    .filter(item -> !inCartIds.contains(item.getId()))
                    .filter(item -> cartCategories.contains(item.getCategory()))
                    .limit(6)
                    .collect(Collectors.toList());

            // If no category matches, fall back to first 6 non-cart items
            if (suggestedItems.isEmpty()) {
                suggestedItems = allMenuItems.stream()
                        .filter(item -> !inCartIds.contains(item.getId()))
                        .limit(6)
                        .collect(Collectors.toList());
            }
        }

        VBox relatedSection = new VBox(5);
        FlowPane relatedPane = new FlowPane();
        relatedPane.setAlignment(Pos.TOP_CENTER);
        relatedPane.setHgap(15);
        relatedPane.setVgap(15);

        if (suggestedItems.isEmpty()) {
            Label noSuggestions = new Label("No more items to suggest.");
            noSuggestions.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            relatedSection.getChildren().add(noSuggestions);
        } else {
            for (FoodItems item : suggestedItems) {
                VBox suggestionCard = new VBox(10);
                suggestionCard.setPrefWidth(180);  // Larger base size, like extension of cart items
                suggestionCard.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 12;" +  // Matching cart radius
                                "-fx-padding: 12;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8,0,0,2);"  // Matching cart shadow
                );
                suggestionCard.setAlignment(Pos.CENTER);

                // Larger image, similar to cart
                ImageView suggestionIv = new ImageView();
                suggestionIv.setFitWidth(100);  // Increased, closer to cart's 90
                suggestionIv.setFitHeight(100);
                suggestionIv.setPreserveRatio(true);
                Image suggestionImg = null;
                String suggestionResourcePath = "/images/" + item.getImagePath();
                try (InputStream is = getClass().getResourceAsStream(suggestionResourcePath)) {
                    if (is != null) suggestionImg = new Image(is);
                    else {
                        String filePath = "file:src/main/resources/com/example/manager/images/" + item.getImagePath();
                        suggestionImg = new Image(filePath);
                    }
                } catch (Exception ignored) {}
                if (suggestionImg != null) suggestionIv.setImage(suggestionImg);

                // Name with larger font
                Label suggestionName = new Label(item.getName());
                suggestionName.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: black;");  // Larger
                suggestionName.setWrapText(true);
                suggestionName.setMaxWidth(160);
                suggestionName.setAlignment(Pos.CENTER);

                // Price
                Label suggestionPrice = new Label(String.format("৳ %.2f", item.getPrice()));
                suggestionPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");  // Larger
                suggestionPrice.setAlignment(Pos.CENTER);

                // Add button, larger and matching style
                Button addSuggestionBtn = new Button("Add to Cart");
                addSuggestionBtn.setStyle(
                        "-fx-background-color: #FF6B00; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 13px; " +  // Larger
                                "-fx-font-weight: bold; " +
                                "-fx-background-radius: 20; " +  // Rounded like nav buttons
                                "-fx-padding: 8 16; " +
                                "-fx-cursor: hand;"
                );
                addSuggestionBtn.setMaxWidth(Double.MAX_VALUE);
                addSuggestionBtn.setOnMouseEntered(e -> addSuggestionBtn.setStyle(
                        "-fx-background-color: #e55a00; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 13px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-background-radius: 20; " +
                                "-fx-padding: 8 16; " +
                                "-fx-cursor: hand;"
                ));
                addSuggestionBtn.setOnMouseExited(e -> addSuggestionBtn.setStyle(
                        "-fx-background-color: #FF6B00; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 13px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-background-radius: 20; " +
                                "-fx-padding: 8 16; " +
                                "-fx-cursor: hand;"
                ));
                addSuggestionBtn.setOnAction(evt -> {
                    if(Session.getCurrentUsername().equals("guest"))
                    {
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

                    }else
                    {
                        cart.addToCart(item.getId(), 1);
                        primaryStage.setScene(getScene());  // Refresh the scene to update cart
                    }

                });

                suggestionCard.getChildren().addAll(suggestionIv, suggestionName, suggestionPrice, addSuggestionBtn);
                relatedPane.getChildren().add(suggestionCard);
            }

            relatedSection.getChildren().add(relatedPane);
        }

        VBox relatedWrapper = new VBox(10, relatedTitle, relatedSection);
        leftColumn.getChildren().add(relatedWrapper);

        // ---- RIGHT COLUMN (ORDER SUMMARY BOX) ----
        VBox rightColumn = new VBox(20);
        rightColumn.setPadding(new Insets(25));
        rightColumn.setPrefWidth(350);
        rightColumn.setStyle("-fx-background-color: transparent;");  // Ensure transparency

        // COUPON BOX
        VBox couponBox = new VBox(10);
        Label couponLabel = new Label("Coupons");
        couponLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        HBox couponInput = new HBox(8);
        TextField couponField = new TextField();
        couponField.setPromptText("Coupon code");
        Button applyBtn = new Button("APPLY NOW");
        applyBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        couponInput.getChildren().addAll(couponField, applyBtn);
        couponBox.getChildren().addAll(couponLabel, couponInput);
        couponBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");

        // SUMMARY BOX
        VBox summaryBox = new VBox(15);
        summaryBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20;");

        Label yourOrder = new Label("Your Order");
        yourOrder.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

        double subtotal = cart.getTotalPrice(foodMap);
        boolean isEmptyCart = cart.getBuyHistory().isEmpty();

        Label subtotalLabel = new Label("Subtotal: ৳" + String.format("%.2f", subtotal));
        subtotalLabel.setStyle("-fx-text-fill: black;");

        AtomicReference<Double> discount = new AtomicReference<>(0.0);
        Label discountLabel;

        VBox deliveryBox = null;
        Label tipLabel = null;
        HBox tipRow = null;
        ToggleGroup tipGroup = null;
        RadioButton tip2;
        RadioButton tip4;
        RadioButton tip7;
        AtomicReference<Double> tip = new AtomicReference<>(0.0);
        Label feeLabel = new Label("Service Fee: ৳1.50");
        feeLabel.setStyle("-fx-text-fill: black;");
        Label taxLabel = new Label("Tax: ৳7.00");
        taxLabel.setStyle("-fx-text-fill: black;");

        double taxAmount = getTaxAmount();
        double serviceFeeAmount = getServiceFeeAmount();
        double deliveryAmount = getDeliveryAmount();

        if (!isEmptyCart) {
            discountLabel = new Label("Discount: -৳0.00");
            discountLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");

            deliveryBox = new VBox(6);
            Label delLabel = new Label("Delivery: ৳"+deliveryAmount);
            delLabel.setStyle("-fx-text-fill: black;");
            deliveryBox.getChildren().add(delLabel);

            tipLabel = new Label("Tip");
            tipLabel.setStyle("-fx-text-fill: black;");
            tipGroup = new ToggleGroup();

            tip2 = new RadioButton("৳2.00");
            tip2.setStyle("-fx-text-fill: black;");
            tip2.setToggleGroup(tipGroup);
            tip4 = new RadioButton("৳4.00");
            tip4.setStyle("-fx-text-fill: black;");
            tip4.setToggleGroup(tipGroup);
            tip7 = new RadioButton("৳7.00");
            tip7.setStyle("-fx-text-fill: black;");
            tip7.setToggleGroup(tipGroup);

            tipRow = new HBox(12, tip2, tip4, tip7);
        } else {
            tip7 = null;
            tip4 = null;
            tip2 = null;
            discountLabel = null;
        }

        double currentTotal = subtotal - discount.get() + (isEmptyCart ? 0.0 : taxAmount + serviceFeeAmount + deliveryAmount + tip.get());

        Label totalLabel = new Label("Total Payable: ৳" + String.format("%.2f", currentTotal));
        totalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: black;");

        Button checkoutBtn = new Button("PROCEED TO CHECKOUT");
        checkoutBtn.setStyle("-fx-background-color: #FF6B00; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 15px; -fx-padding: 10;");
        checkoutBtn.setMaxWidth(Double.MAX_VALUE);
        // TODO: Add checkout action, e.g., checkoutBtn.setOnAction(e -> primaryStage.setScene(new ShippingPage(primaryStage, cart).getScene()));
        checkoutBtn.setOnAction(e -> primaryStage.setScene(new CheckoutPage(primaryStage, cart).getScene()));
        // Coupon validation logic
        applyBtn.setOnAction(e -> {
            if (isEmptyCart) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Add items to your cart to apply a coupon!");
                alert.showAndWait();
                return;
            }
            String code = couponField.getText().trim().toUpperCase();
            if (code.isEmpty()) {
                return;
            }
            boolean valid = false;
            if ("SAVE10".equals(code)) {
                discount.set(subtotal * 0.10);
                valid = true;
            } else if ("SAVE20".equals(code)) {
                discount.set(20.0);  // Fixed $20 discount
                valid = true;
            }
            // Add more coupons as needed
            if (valid) {
                discountLabel.setText(String.format("Discount: -৳%.2f", discount.get()));
                double newTotal = subtotal - discount.get() + taxAmount + serviceFeeAmount + deliveryAmount + tip.get();
                totalLabel.setText("Total Payable: ৳" + String.format("%.2f", newTotal));
                couponField.clear();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Coupon applied successfully!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Invalid coupon code. Please try again.");
                alert.showAndWait();
            }
        });

        if (!isEmptyCart) {
            tipGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                tip.set(0.0);
                if (newValue == tip2) {
                    tip.set(2.00);
                } else if (newValue == tip4) {
                    tip.set(4.00);
                } else if (newValue == tip7) {
                    tip.set(7.00);
                }

               // this.disCount = discount.get();
                setDisCount(discount.get());



                double newTotal = subtotal - discount.get() + taxAmount + serviceFeeAmount + deliveryAmount + tip.get();
                this.total = newTotal;
                totalLabel.setText("Total Payable: ৳" + String.format("%.2f", newTotal));
            });
        }

        summaryBox.getChildren().addAll(yourOrder, subtotalLabel);

        if (!isEmptyCart) {
            summaryBox.getChildren().add(discountLabel);
            summaryBox.getChildren().addAll(
                    deliveryBox,
                    new Label(""),
                    tipLabel,
                    tipRow,
                    feeLabel,
                    taxLabel,
                    new Separator(),
                    totalLabel,
                    checkoutBtn
            );
        } else {
            summaryBox.getChildren().add(totalLabel);
        }

        rightColumn.getChildren().addAll(couponBox, summaryBox);

        // Add columns to main content
        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        mainContent.getChildren().addAll(leftColumn, rightColumn);

        // SCROLLPANE WITH TRANSPARENT BACKGROUND
        ScrollPane scroll = new ScrollPane(mainContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        root.setCenter(scroll);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());

        // RESPONSIVE LISTENER - Now after building relatedPane, with access to variables
        ChangeListener<Number> widthListener = (obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();
            double availableLeftWidth = width - rightColumn.getPrefWidth() - 40;  // Approximate: total width - right column - padding
            int numCols = (int) Math.max(1, Math.floor(availableLeftWidth / 200));  // Dynamic columns based on space
            double cardWidth = Math.max(150, (availableLeftWidth / numCols) - 20);  // Adjust card width dynamically

            if (width < 768) {  // Mobile: vertical stack or 1-2 cols
                searchContainer.setVisible(false);
                searchContainer.setManaged(false);
                buttonsHBox.setSpacing(5);
                titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
                leftGroup.setSpacing(5);
                logo.setFitWidth(40);
                logo.setFitHeight(40);
                clip.setRadius(20);
                logo.setClip(clip);
                breadcrumb.setSpacing(5);
                cartStep.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #333;");
                sep1.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
                shippingStep.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
                sep2.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
                paymentStep.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");

                // Mobile suggestions: smaller, more vertical
                relatedTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 10 0 5 0;");
                relatedPane.setHgap(10);
                relatedPane.setVgap(10);
                updateSuggestionCards(relatedPane, cardWidth, 60, 12, 11, 10, "6 12");  // Smaller sizes
                rightColumn.setPrefWidth(300);  // Shrink right on mobile
            } else if (width < 1024) {  // Tablet: 2-3 cols
                searchContainer.setVisible(true);
                searchContainer.setManaged(true);
                searchField.setPrefWidth(200);
                buttonsHBox.setSpacing(10);
                titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
                leftGroup.setSpacing(8);
                logo.setFitWidth(50);
                logo.setFitHeight(50);
                clip.setRadius(25);
                logo.setClip(clip);
                breadcrumb.setSpacing(10);
                cartStep.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
                sep1.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
                shippingStep.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
                sep2.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
                paymentStep.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");

                // Tablet suggestions
                relatedTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 15 0 8 0;");
                relatedPane.setHgap(12);
                relatedPane.setVgap(12);
                updateSuggestionCards(relatedPane, cardWidth, 80, 13, 12, 11, "7 14");
                rightColumn.setPrefWidth(320);
            } else {  // Desktop: full size, 3-4+ cols
                searchContainer.setVisible(true);
                searchContainer.setManaged(true);
                searchField.setPrefWidth(250);
                buttonsHBox.setSpacing(20);
                titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
                leftGroup.setSpacing(10);
                logo.setFitWidth(60);
                logo.setFitHeight(60);
                clip.setRadius(30);
                logo.setClip(clip);
                breadcrumb.setSpacing(20);
                cartStep.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
                sep1.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
                shippingStep.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
                sep2.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
                paymentStep.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");

                // Desktop suggestions: larger
                relatedTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 20 0 10 0;");
                relatedPane.setHgap(15);
                relatedPane.setVgap(15);
                updateSuggestionCards(relatedPane, cardWidth, 100, 15, 14, 13, "8 16");
                rightColumn.setPrefWidth(350);
            }
        };
        primaryStage.widthProperty().addListener(widthListener);

        // Initial responsive check
        widthListener.changed(null, null, primaryStage.getWidth());

        return scene;
    }

    private void updateSuggestionCards(FlowPane pane, double cardWidth, double imgSize, double nameFont, double priceFont, double btnFont, String btnPadding) {
        if (pane == null) return;
        for (javafx.scene.Node node : pane.getChildren()) {
            if (node instanceof VBox card) {
                card.setPrefWidth(cardWidth);
                for (javafx.scene.Node child : card.getChildren()) {
                    if (child instanceof ImageView iv) {
                        iv.setFitWidth(imgSize);
                        iv.setFitHeight(imgSize);
                    } else if (child instanceof Label lbl) {
                        String currentStyle = lbl.getStyle();
                        if (currentStyle.contains("-fx-font-size: ")) {
                            // Simple replace for font size (assumes format is consistent)
                            if (lbl.getText().contains("৳")) {  // Price label
                                lbl.setStyle(currentStyle.replaceAll("-fx-font-size: \\d+(?:\\.\\d+)?px;", "-fx-font-size: " + priceFont + "px;"));
                            } else {  // Name label
                                lbl.setStyle(currentStyle.replaceAll("-fx-font-size: \\d+(?:\\.\\d+)?px;", "-fx-font-size: " + nameFont + "px;"));
                            }
                            lbl.setMaxWidth(cardWidth - 20);
                        }
                    } else if (child instanceof Button btn) {
                        String currentStyle = btn.getStyle();
                        String newStyle = currentStyle.replaceAll("-fx-font-size: \\d+(?:\\.\\d+)?px;", "-fx-font-size: " + btnFont + "px;")
                                .replaceAll("-fx-padding: [^;]+;", "-fx-padding: " + btnPadding + ";");
                        btn.setStyle(newStyle);
                        // Also update hover styles if needed, but for simplicity, update base
                        btn.setOnMouseEntered(e -> btn.setStyle(newStyle.replace("-fx-background-color: #FF6B00;", "-fx-background-color: #e55a00;")));
                        btn.setOnMouseExited(e -> btn.setStyle(newStyle));
                    }
                }
            }
        }
    }

    private String buttonStyleSmall() {
        return "-fx-background-color: #f0f0f0; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 14px;";
    }
}