package com.munchoak.payment;

import com.munchoak.authentication.LoginPage;
import com.munchoak.authentication.ProfilePage;
import com.munchoak.cart.Cart;
import com.munchoak.cart.CartPage;
import com.munchoak.coupon.CouponStorage;
import com.munchoak.homepage.HomePage;
import com.munchoak.mainpage.FoodItems;
import com.munchoak.manager.MenuStorage;
import com.munchoak.manager.Session;
import com.munchoak.menu.MenuPage;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CheckoutPage {
    private final Stage primaryStage;
    private final Cart cart;
    private final double discount; // FIXED: Added field for passed discount
    private final double tip; // FIXED: Added field for passed tip
    private final String appliedCouponCode;

    // FIXED: Updated constructor to accept discount and tip
    public CheckoutPage(Stage primaryStage, Cart cart, double discount, double tip, String appliedCouponCode) {
        this.primaryStage = primaryStage;
        this.cart = cart;
        this.discount = discount;
        this.tip = tip;
        this.appliedCouponCode = appliedCouponCode;
    }

    private Map<Integer, FoodItems> buildFoodMap() {
        List<FoodItems> loaded = MenuStorage.loadMenu();
        Map<Integer, FoodItems> map = new HashMap<>();
        for (FoodItems f : loaded) map.put(f.getId(), f);
        return map;
    }

    // New method to show feedback dialog after payment
    private void showFeedbackDialog(Window owner) {
        Stage feedbackStage = new Stage();
        feedbackStage.setTitle("Your Feedback");
        feedbackStage.initModality(Modality.APPLICATION_MODAL);
        feedbackStage.initOwner(owner);
        feedbackStage.setResizable(false);
        feedbackStage.setWidth(400);
        feedbackStage.setHeight(350);

        VBox feedbackVBox = new VBox(15);
        feedbackVBox.setPadding(new Insets(20));
        feedbackVBox.setAlignment(Pos.CENTER);
        feedbackVBox.setStyle("-fx-background-color: #f9f9f9;");

        Label titleLabel = new Label("How was your experience with MunchOak?");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label ratingLabel = new Label("Rate us:");
        ratingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        // Star Rating Component
        HBox starsHBox = new HBox(5);
        starsHBox.setAlignment(Pos.CENTER);
        AtomicInteger currentRating = new AtomicInteger(5); // Default rating
        List<Label> stars = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Label star = new Label(currentRating.get() >= i ? "★" : "☆");
            star.setStyle("-fx-font-size: 24px; -fx-cursor: hand; -fx-text-fill: gold;");
            // Initial glow for filled stars
            if (currentRating.get() >= i) {
                Glow glow = new Glow(0.5);
                star.setEffect(glow);
            } else {
                star.setEffect(null);
            }
            final int rating = i;
            // Click event with pulse animation
            star.setOnMouseClicked(e -> {
                currentRating.set(rating);
                updateStars(stars, currentRating.get());
                // Pulse animation on click
                ScaleTransition pulse = new ScaleTransition(Duration.millis(150), star);
                pulse.setToX(1.3);
                pulse.setToY(1.3);
                pulse.setAutoReverse(true);
                pulse.setCycleCount(2);
                pulse.play();
            });
            // Enter event with scale up animation and enhanced glow
            star.setOnMouseEntered(e -> {
                updateStars(stars, rating, true);
                // Scale up animation
                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), star);
                scaleUp.setToX(1.2);
                scaleUp.setToY(1.2);
                scaleUp.play();
                // Enhance glow on hover
                Glow hoverGlow = new Glow(0.8);
                star.setEffect(hoverGlow);
            });
            // Exit event with scale down animation and restore glow
            star.setOnMouseExited(e -> {
                updateStars(stars, currentRating.get(), false);
                // Scale down animation
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), star);
                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);
                scaleDown.play();
                // Restore glow based on rating
                if (currentRating.get() >= rating) {
                    Glow glow = new Glow(0.5);
                    star.setEffect(glow);
                } else {
                    star.setEffect(null);
                }
            });
            stars.add(star);
            starsHBox.getChildren().add(star);
        }

        Label commentLabel = new Label("Optional comments:");
        commentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Tell us what you liked or how we can improve...");
        commentArea.setPrefHeight(80);
        commentArea.setWrapText(true);
        commentArea.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4;");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color: #FF6B00; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 8 16;");
        submitBtn.setOnMouseEntered(e -> submitBtn.setStyle("-fx-background-color: #e55a00; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 8 16;"));
        submitBtn.setOnMouseExited(e -> submitBtn.setStyle("-fx-background-color: #FF6B00; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 8 16;"));
        submitBtn.setOnAction(e -> {
            int rating = currentRating.get();
            String comment = commentArea.getText().trim();
            System.out.println("Feedback submitted: Rating=" + rating + ", Comment=" + comment); // Placeholder for now
            feedbackStage.close();
        });

        Button skipBtn = new Button("Skip");
        skipBtn.setStyle("-fx-background-color: #ddd; -fx-text-fill: #666; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 8 16;");
        skipBtn.setOnMouseEntered(e -> skipBtn.setStyle("-fx-background-color: #ccc; -fx-text-fill: #666; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 8 16;"));
        skipBtn.setOnMouseExited(e -> skipBtn.setStyle("-fx-background-color: #ddd; -fx-text-fill: #666; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 8 16;"));
        skipBtn.setOnAction(e -> feedbackStage.close());

        buttonBox.getChildren().addAll(skipBtn, submitBtn);

        feedbackVBox.getChildren().addAll(titleLabel, ratingLabel, starsHBox, commentLabel, commentArea, buttonBox);

        Scene feedbackScene = new Scene(feedbackVBox);
        feedbackStage.setScene(feedbackScene);
        feedbackStage.showAndWait();
    }

    // Helper method to update star visuals
    private void updateStars(List<Label> stars, int rating) {
        updateStars(stars, rating, false);
    }

    private void updateStars(List<Label> stars, int rating, boolean isHover) {
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.get(i).setText("★");
                stars.get(i).setStyle("-fx-font-size: 24px; -fx-cursor: hand; -fx-text-fill: " + (isHover ? "#FFD700" : "gold") + ";");
                // Apply glow to filled stars
                Glow glow = new Glow(isHover ? 0.8 : 0.5);
                stars.get(i).setEffect(glow);
            } else {
                stars.get(i).setText("☆");
                stars.get(i).setStyle("-fx-font-size: 24px; -fx-cursor: hand; -fx-text-fill: #ddd;");
                // Remove effect for empty stars
                stars.get(i).setEffect(null);
            }
        }
    }

    public Scene getScene() {
        Map<Integer, FoodItems> foodMap = buildFoodMap();

        // FIXED: Calculate base subtotal and total add-ons to match CartPage
        double baseSubtotal = 0.0;
        double totalAddons = 0.0;
        for (Map.Entry<Integer, Integer> e : cart.getBuyHistory().entrySet()) {
            int foodId = e.getKey();
            int qty = e.getValue();
            FoodItems item = foodMap.get(foodId);
            if (item != null) {
                baseSubtotal += item.getPrice() * qty;
                double addonPer = cart.getAddonPerItem(foodId);
                totalAddons += addonPer * qty;
            }
        }
        final double TOTAL = totalAddons;
        double subtotal = baseSubtotal + totalAddons;
        boolean isEmptyCart = cart.getBuyHistory().isEmpty();
        double taxAmount = 7.00; // Fixed from Cart
        double serviceFeeAmount = 1.50; // Fixed from Cart
        double deliveryAmount = 7.99; // Fixed from Cart
        //  double totalPayable = isEmptyCart ? 0.0 : (subtotal - discount + deliveryAmount + tip + serviceFeeAmount + taxAmount);
        double discountAmount = subtotal * discount; // discount is percentage (e.g. 0.1 = 10%)
        double discountedSubtotal = subtotal - discountAmount;
        double totalPayable = discountedSubtotal + deliveryAmount + tip + serviceFeeAmount + taxAmount;

        // PAGE BACKGROUND
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #FFDAB9;"); // Peach yellow background

        // TOP CONTAINER FOR NAVBAR AND BREADCRUMB
        VBox topContainer = new VBox();
        topContainer.setSpacing(0);

        // NAVIGATION BAR (same as CartPage)
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
        String logoPath = "/images/logo.png"; // Assume logo path
        try (InputStream is = getClass().getResourceAsStream(logoPath)) {
            if (is != null) logoImg = new Image(is);
        } catch (Exception ignored) {
        }

        if (logoImg != null) {
            logo.setImage(logoImg);
        }

        Circle clip = new Circle(30, 30, 30);
        logo.setClip(clip);

        Label titleLabel = new Label("MUNCHOAK");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        leftGroup.getChildren().addAll(logo, titleLabel);

        VBox searchResultsWrapper = new VBox(10);
        searchResultsWrapper.setPadding(new Insets(20, 0, 10, 0));


        Label searchResultsTitle = new Label("Search Results");
        searchResultsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        FlowPane searchResultsPane = new FlowPane();
        searchResultsPane.setHgap(15);
        searchResultsPane.setVgap(15);
        searchResultsPane.setAlignment(Pos.TOP_CENTER);

        searchResultsWrapper.getChildren().addAll(searchResultsTitle, searchResultsPane);

        // SEARCH BAR IN THE MIDDLE
        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER);


        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 8 12; -fx-background-color: white; -fx-border-radius: 20;");
        searchField.setFocusTraversable(false);

        Button searchBtn = new Button("🔍");
        searchBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        searchBtn.setOnMouseEntered(e -> searchBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;"));
        searchBtn.setOnMouseExited(e -> searchBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;"));

        searchContainer.getChildren().addAll(searchField, searchBtn);

        searchBtn.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            MenuPage menuPage = new MenuPage(primaryStage, cart);

            // Pass search keyword to MenuPage
            menuPage.setSearchKeyword(keyword);

            primaryStage.setScene(menuPage.getMenuScene());
            searchField.textProperty().set(keyword); // triggers listener
            menuPage.menu.updateView();
        });

        // Initially hide the search results wrapper
        searchResultsWrapper.setVisible(false);
        searchResultsWrapper.setManaged(false);
        // Live search: triggers on every text change
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue.trim().toLowerCase();
            searchResultsPane.getChildren().clear(); // clear previous results

            if (!keyword.isEmpty()) {
                searchResultsWrapper.setVisible(true);
                searchResultsWrapper.setManaged(true);
                List<FoodItems> results = MenuStorage.loadMenu().stream()
                        .filter(i -> i.getName().toLowerCase().contains(keyword)
                                || i.getCategory().toLowerCase().contains(keyword)
                                || i.getDetails().toLowerCase().contains(keyword)
                                || i.getCuisine().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());

                if (results.isEmpty()) {
                    Label noResult = new Label("No matching food items");
                    noResult.setStyle("-fx-padding: 10; -fx-text-fill: red; -fx-font-size: 14px;");

                    searchResultsPane.getChildren().add(noResult);
                    //resultsContainer.getChildren().setAll(noResult);
                    return;
                }

                for (FoodItems item : results) {
                    VBox card = new VBox(10);
                    card.setPrefWidth(180);
                    card.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 12;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8,0,0,2);"
                    );
                    card.setAlignment(Pos.CENTER);

                    // IMAGE
                    ImageView iv = new ImageView();
                    iv.setFitWidth(100);
                    iv.setFitHeight(100);
                    iv.setPreserveRatio(true);
                    try (InputStream is = getClass().getResourceAsStream("/images/" + item.getImagePath())) {
                        if (is != null) iv.setImage(new Image(is));
                        else
                            iv.setImage(new Image("file:src/main/resources/com/munchoak/manager/images/" + item.getImagePath()));
                    } catch (Exception ignored) {
                    }

                    // NAME
                    Label name = new Label(item.getName());
                    name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: black;");
                    name.setWrapText(true);
                    name.setMaxWidth(160);
                    name.setAlignment(Pos.CENTER);

                    // PRICE
                    Label price = new Label(String.format("৳ %.2f", item.getPrice()));
                    price.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                    price.setAlignment(Pos.CENTER);

                    // ADD BUTTON
                    Button addBtn = new Button("Add to Cart");
                    addBtn.setStyle(
                            "-fx-background-color: #FF6B00; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;" +
                                    "-fx-background-radius: 20; -fx-padding: 8 16; -fx-cursor: hand;"
                    );
                    addBtn.setMaxWidth(Double.MAX_VALUE);
                    addBtn.setOnAction(evt -> {
                        if (Session.isGuest()) {
                            // Show login popup
                        } else {
                            cart.addToCart(item.getId(), 1);
                            primaryStage.setScene(getScene());  // Refresh to update cart
                        }
                    });

                    card.getChildren().addAll(iv, name, price, addBtn);
                    searchResultsPane.getChildren().add(card);
                }
            } else {
                // Hide the wrapper when field is empty
                searchResultsWrapper.setVisible(false);
                searchResultsWrapper.setManaged(false);
            }
        });


        searchField.setOnAction(e -> searchBtn.fire());
        //searchContainer.getChildren().addAll(searchField, searchBtn);


        // SPACERS
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // BUTTONS HBOX ON THE RIGHT (same as CartPage)
        HBox buttonsHBox = new HBox(20);
        buttonsHBox.setAlignment(Pos.CENTER_RIGHT);

        Button homeBtn = new Button("Home");
        homeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        homeBtn.setOnMouseEntered(e -> homeBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        homeBtn.setOnMouseExited(e -> homeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        // FIXED: Pass cart to HomePage to preserve state during navigation
        homeBtn.setOnAction(e -> primaryStage.setScene(new HomePage(primaryStage, cart).getHomeScene()));

        Button menuBtn = new Button("Menu");
        menuBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        menuBtn.setOnMouseEntered(e -> menuBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        menuBtn.setOnMouseExited(e -> menuBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        menuBtn.setOnAction(e -> primaryStage.setScene(new MenuPage(primaryStage, cart).getMenuScene()));

        Button cartBtn = new Button("Cart");
        cartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        cartBtn.setOnMouseEntered(e -> cartBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        cartBtn.setOnMouseExited(e -> cartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        cartBtn.setOnAction(e -> primaryStage.setScene(new CartPage(primaryStage, cart).getScene()));

        boolean loggedIn = (Session.getCurrentUsername() != null &&
                !Session.isGuest()) &&
                (Session.getCurrentEmail() != null &&
                        !Session.getCurrentEmail().isEmpty());

        Button authBtn = new Button(loggedIn ? "Log Out" : "Log In");
        authBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        authBtn.setOnMouseEntered(e ->
                authBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        authBtn.setOnMouseExited(e ->
                authBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));

        authBtn.setOnAction(e ->
        {
            if (loggedIn) {
                Session.logout();
                primaryStage.setScene(new LoginPage(primaryStage).getLoginScene());
            } else {
                primaryStage.setScene(new LoginPage(primaryStage).getLoginScene());
            }
        });

        Button profileBtn = new Button("Profile");
        profileBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        profileBtn.setOnMouseEntered(e -> profileBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        profileBtn.setOnMouseExited(e -> profileBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        profileBtn.setOnAction(e -> {
            Scene currentScene = primaryStage.getScene();
            ProfilePage profilePage = new ProfilePage(primaryStage, currentScene);
            primaryStage.setScene(profilePage.getProfileScene());
        });

        buttonsHBox.getChildren().addAll(homeBtn, menuBtn, cartBtn, authBtn, profileBtn);

        navBar.getChildren().addAll(leftGroup, spacer1, searchContainer, spacer2, buttonsHBox);

        // BREADCRUMB STEPS (updated for payment active)
        HBox breadcrumb = new HBox(20);
        breadcrumb.setAlignment(Pos.CENTER);
        breadcrumb.setPadding(new Insets(10, 20, 10, 20));

        Label cartStep = new Label("Cart");
        cartStep.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");

        Label sep1 = new Label(" > ");
        sep1.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");

        Label shippingStep = new Label("Shipping");
        shippingStep.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");

        Label sep2 = new Label(" > ");
        sep2.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");

        Label paymentStep = new Label("Payment");
        paymentStep.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;"); // Active

        breadcrumb.getChildren().addAll(cartStep, sep1, shippingStep, sep2, paymentStep);

        topContainer.getChildren().addAll(navBar, breadcrumb);
        root.setTop(topContainer);

        // RESPONSIVE LISTENER (same as CartPage, but simplified for checkout)
        ChangeListener<Number> widthListener = (obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();
            if (width < 768) {
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
                cartStep.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
                sep1.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
                shippingStep.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
                sep2.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
                paymentStep.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #333;");
            } else if (width < 1024) {
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
                cartStep.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
                sep1.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
                shippingStep.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
                sep2.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
                paymentStep.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
            } else {
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
                cartStep.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
                sep1.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
                shippingStep.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
                sep2.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
                paymentStep.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
            }
        };
        primaryStage.widthProperty().addListener(widthListener);

        // MAIN CONTENT HBOX WITH BACKGROUND
        HBox mainContent = new HBox(20);
        mainContent.setStyle("-fx-background-color: #FFDAB9;");

        // LEFT SIDE - ORDER SUMMARY (slightly different: no qty controls, just list)
        VBox leftColumn = new VBox(20);
        leftColumn.setPadding(new Insets(25, 30, 25, 30));
        leftColumn.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Order Summary");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333;");
        leftColumn.getChildren().add(title);

        VBox orderList = new VBox(18);
        orderList.setAlignment(Pos.TOP_CENTER);

        double orderSubtotal = 0.0;
        if (!cart.getBuyHistory().isEmpty()) {
            for (Map.Entry<Integer, Integer> e : cart.getBuyHistory().entrySet()) {
                int foodId = e.getKey();
                int qty = e.getValue();
                FoodItems item = foodMap.get(foodId);
                if (item == null) continue;

                double addonPerItem = cart.getAddonPerItem(foodId);
                orderSubtotal += (item.getPrice() + addonPerItem) * qty;

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
                        String filePath = "file:src/main/resources/com/munchoak/manager/images/" + item.getImagePath();
                        img = new Image(filePath);
                    }
                } catch (Exception ignored) {
                }

                if (img != null) iv.setImage(img);

                // INFO COLUMN (no qty controls, include add-on if applicable)
                VBox info = new VBox(6);
                Label name = new Label(item.getName() + " (x" + qty + ")");
                name.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: black;");
                Label details = new Label(item.getDetails());
                details.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
                details.setMaxWidth(300);
                details.setWrapText(true);

                // Add-on label if applicable
                if (addonPerItem > 0) {
                    Label addonLabel = new Label("Add-ons: +৳" + String.format("%.2f", addonPerItem));
                    addonLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #E53935; -fx-font-weight: bold;");
                    info.getChildren().addAll(name, details, addonLabel);
                } else {
                    info.getChildren().addAll(name, details);
                }

                // SPACER
                Region spacer3 = new Region();
                HBox.setHgrow(spacer3, Priority.ALWAYS);

                // LINE TOTAL (includes add-ons)
                double lineTotalValue = (item.getPrice() + addonPerItem) * qty;
                Label lineTotal = new Label(String.format("৳ %.2f", lineTotalValue));
                lineTotal.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: black;");

                card.getChildren().addAll(iv, info, spacer3, lineTotal);
                orderList.getChildren().add(card);
            }
        }
        leftColumn.getChildren().add(orderList);
        leftColumn.getChildren().add(0, searchResultsWrapper);


        // RIGHT COLUMN - PAYMENT FORM WITH TOTAL SUMMARY
        VBox rightColumn = new VBox(20);
        rightColumn.setPadding(new Insets(25));
        rightColumn.setPrefWidth(350);
        rightColumn.setStyle("-fx-background-color: transparent;");

        // FIXED: Updated Order Summary Box to match CartPage (breakdown with add-ons)
        if (!isEmptyCart) {
            VBox summaryBox = new VBox(15);
            summaryBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-margin: 0 0 20 0;");

            Label yourOrder = new Label("Your Order Summary");
            yourOrder.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

            Label baseSubtotalLabel = new Label("Subtotal: ৳" + String.format("%.2f", baseSubtotal));
            baseSubtotalLabel.setStyle("-fx-text-fill: black;");

            Label addonsLabel = new Label("Add-ons: ৳" + String.format("%.2f", totalAddons));
            addonsLabel.setStyle("-fx-text-fill: black;");

            Label subtotalLabel = new Label("Subtotal: ৳" + String.format("%.2f", subtotal));
            subtotalLabel.setStyle("-fx-text-fill: black;");

            Label discountLabel = new Label("Discount: -৳" + String.format("%.2f", discountAmount));
            discountLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");

            Label deliveryLabel = new Label("Delivery: ৳" + String.format("%.2f", deliveryAmount));
            deliveryLabel.setStyle("-fx-text-fill: black;");

            Label tipLabel = new Label("Tip: ৳" + String.format("%.2f", tip));
            tipLabel.setStyle("-fx-text-fill: black;");

            Label serviceFeeLabel = new Label("Service Fee: ৳" + String.format("%.2f", serviceFeeAmount));
            serviceFeeLabel.setStyle("-fx-text-fill: black;");

            Label taxLabel = new Label("Tax: ৳" + String.format("%.2f", taxAmount));
            taxLabel.setStyle("-fx-text-fill: black;");

            Separator separator = new Separator();
            Label totalLabel = new Label("Total Payable: ৳" + String.format("%.2f", totalPayable));
            totalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: black;");

            summaryBox.getChildren().addAll(yourOrder, baseSubtotalLabel, addonsLabel, subtotalLabel, discountLabel, deliveryLabel, tipLabel, serviceFeeLabel, taxLabel, separator, totalLabel);
            rightColumn.getChildren().add(summaryBox);
        }

        // PAYMENT BOX
        VBox paymentBox = new VBox(15);
        paymentBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20;");

        Label paymentTitle = new Label("Payment Details");
        paymentTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Card Number
        Label cardLabel = new Label("Card Number");
        cardLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextField cardField = new TextField();
        cardField.setPromptText("Card Number (16 digits)");
        cardField.setStyle("-fx-background-radius: 6; -fx-padding: 10; -fx-background-color: #f5f5f5;");

        // Expiry and CVV
        HBox expiryCvv = new HBox(10);
        Label expiryLabel = new Label("Expiry Date");
        expiryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextField expiryField = new TextField();
        expiryField.setPromptText("MM/YY");
        expiryField.setPrefWidth(100);
        expiryField.setStyle("-fx-background-radius: 6; -fx-padding: 10; -fx-background-color: #f5f5f5;");

        Label cvvLabel = new Label("CVV");
        cvvLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextField cvvField = new TextField();
        cvvField.setPromptText("3 digits");
        cvvField.setPrefWidth(80);
        cvvField.setStyle("-fx-background-radius: 6; -fx-padding: 10; -fx-background-color: #f5f5f5;");

        VBox expiryCvvContainer = new VBox(5, expiryLabel, expiryField);
        VBox cvvContainer = new VBox(5, cvvLabel, cvvField);
        expiryCvv.getChildren().addAll(expiryCvvContainer, cvvContainer);


        Label paymentStatus = new Label();
        paymentStatus.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        paymentStatus.setAlignment(Pos.CENTER);

        // FIXED: Updated pay button to reference totalPayable in alert
        Button payBtn = new Button("PAY NOW (৳" + String.format("%.2f", totalPayable) + ")");
        payBtn.setStyle("-fx-background-color: #FF6B00; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12;");
        payBtn.setMaxWidth(Double.MAX_VALUE);
        payBtn.setOnMouseEntered(e -> payBtn.setStyle("-fx-background-color: #e55a00; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12;"));
        payBtn.setOnMouseExited(e -> payBtn.setStyle("-fx-background-color: #FF6B00; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12;"));

        payBtn.setOnAction(e -> {
            if (cardField.getText().isEmpty() || expiryField.getText().isEmpty() || cvvField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Incomplete Details");
                alert.setContentText("Please fill in all payment fields.");
                alert.showAndWait();
                return;
            }

            String cardNumber = cardField.getText().trim();
            String expiry = expiryField.getText().trim();
            String cvv = cvvField.getText().trim();

            // --- Card number validation ---
            if (!cardNumber.matches("\\d{16}")) {
                paymentStatus.setText("Card number must be 16 digits!");
                paymentStatus.setTextFill(javafx.scene.paint.Color.RED);
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(evt -> paymentStatus.setText(""));
                delay.play();
                return;
            }

            // --- Expiry validation (MM/YY) ---
            if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                paymentStatus.setText("Expiry must be in MM/YY format!");
                paymentStatus.setTextFill(javafx.scene.paint.Color.RED);
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(evt -> paymentStatus.setText(""));
                delay.play();
                return;
            } else {
                // check if expiry is in the future
                String[] parts = expiry.split("/");
                int month = Integer.parseInt(parts[0]);
                int year = Integer.parseInt(parts[1]) + 2000; // convert YY to YYYY
                java.time.YearMonth expDate = java.time.YearMonth.of(year, month);
                if (!expDate.isAfter(java.time.YearMonth.now())) {
                    paymentStatus.setText("Card has expired!");
                    paymentStatus.setTextFill(javafx.scene.paint.Color.RED);
                    PauseTransition delay = new PauseTransition(Duration.seconds(2));
                    delay.setOnFinished(evt -> paymentStatus.setText(""));
                    delay.play();
                    return;
                }
            }

            // --- CVV validation ---
            if (!cvv.matches("\\d{3}")) {
                paymentStatus.setText("CVV must be 3 digits!");
                paymentStatus.setTextFill(javafx.scene.paint.Color.RED);
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(evt -> paymentStatus.setText(""));
                delay.play();
                return;
            }

            try {

                List<FoodItems> menuList = MenuStorage.loadMenu();
                Map<Integer, FoodItems> menuMap = menuList.stream()
                        .collect(Collectors.toMap(FoodItems::getId, f -> f));

                // Check stock availability
                for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
                    int foodId = entry.getKey();
                    int qtyRequested = entry.getValue();
                    FoodItems menuItem = menuMap.get(foodId);

                    if (menuItem == null || menuItem.getQuantity() < qtyRequested) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("Insufficient Stock");
                        alert.setContentText("Sorry, " + (menuItem != null ? menuItem.getName() : "an item")
                                + " is out of stock or doesn't have enough quantity.");
                        alert.showAndWait();
                        return; // Stop payment
                    }
                }

                // Deduct quantities
                for (Map.Entry<Integer, Integer> entry : cart.getBuyHistory().entrySet()) {
                    int foodId = entry.getKey();
                    int qtyRequested = entry.getValue();
                    FoodItems menuItem = menuMap.get(foodId);
                    menuItem.setQuantity(menuItem.getQuantity() - qtyRequested);
                }

                // Update menu file
                MenuStorage.rewriteMenu(new ArrayList<>(menuMap.values()));

                /*------------------------------SERVER PART----------------------------------------*/
                // Broadcast to all clients
                Session.getMenuClient().sendMenuUpdate();

                Payment.checkout(cart);
                int paymentId = PaymentStorage.createPaymentAndCart(
                        Session.getCurrentUserId(),
                        cart,
                        foodMap,
                        "card",
                        totalPayable
                );

                PaymentStorage.savePaymentBreakdown(paymentId, subtotal, TOTAL, discountAmount, tip, deliveryAmount, taxAmount, serviceFeeAmount, totalPayable, Session.getCurrentUserId(), Session.getCurrentUsername());
                // AFTER Payment.checkout(cart);
                if (discount > 0) {
                    CouponStorage.consumeCoupon(appliedCouponCode, Session.getCurrentUserId());
                }

                Session.getMenuClient().sendCouponUpdate();
                Session.getMenuClient().sendPaymentFileUpdate();
                Session.getMenuClient().sendCartFilesUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Payment Successful!");
                alert.setContentText("Your order has been placed for ৳" + String.format("%.2f", totalPayable) + ". Thank you for shopping with MUNCHOAK! You can view your receipt in Payment History.");
                alert.showAndWait();

                // Show feedback dialog
                showFeedbackDialog(primaryStage);

                // Clear cart and navigate to home
                cart.clearCart();
                primaryStage.setScene(new HomePage(primaryStage, cart).getHomeScene());
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Payment Failed");
                alert.setContentText("An error occurred: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        // Add to payment box
        paymentBox.getChildren().addAll(paymentTitle, cardLabel, cardField, new Label(""), new Label("Expiry Date / CVV"), expiryCvv, new Separator(), paymentStatus, payBtn);

        rightColumn.getChildren().add(paymentBox);

        // Add columns to main content
        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        mainContent.getChildren().addAll(leftColumn, rightColumn);

        // SCROLLPANE WITH TRANSPARENT BACKGROUND
        ScrollPane scroll = new ScrollPane(mainContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        root.setCenter(scroll);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());

        // Initial responsive check
        widthListener.changed(null, null, primaryStage.getWidth());

        return scene;
    }
}