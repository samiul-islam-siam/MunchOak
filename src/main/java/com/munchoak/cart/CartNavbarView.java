package com.munchoak.cart;

import com.munchoak.authentication.LoginPage;
import com.munchoak.authentication.ProfilePage;
import com.munchoak.homepage.HomePage;
import com.munchoak.mainpage.FoodItems;
import com.munchoak.manager.MenuStorage;
import com.munchoak.manager.Session;
import com.munchoak.menu.MenuPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class CartNavbarView {

    public record NavbarNodes(
            VBox topContainer,
            VBox searchResultsWrapper,
            FlowPane searchResultsPane,
            HBox searchContainer,
            TextField searchField,
            Label titleLabel,
            HBox buttonsHBox,
            HBox leftGroup,
            ImageView logo,
            Circle clip,
            HBox breadcrumb,
            Label cartStep,
            Label sep1,
            Label shippingStep,
            Label sep2,
            Label paymentStep
    ) {}

    public static NavbarNodes build(Stage primaryStage, Cart cart, Runnable refreshCartScene) {

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
        String logoPath = "/images/logo.png";
        try (InputStream is = CartNavbarView.class.getResourceAsStream(logoPath)) {
            if (is != null) logoImg = new Image(is);
        } catch (Exception ignored) {}
        if (logoImg != null) logo.setImage(logoImg);

        Circle clip = new Circle(30, 30, 30);
        logo.setClip(clip);

        Label titleLabel = new Label("MunchOak");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        leftGroup.getChildren().addAll(logo, titleLabel);

        // SEARCH RESULTS WRAPPER
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

        searchBtn.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            MenuPage menuPage = new MenuPage(primaryStage, cart);
            menuPage.setSearchKeyword(keyword);
            primaryStage.setScene(menuPage.getMenuScene());
            menuPage.menu.updateView();
        });

        // Initially hide
        searchResultsWrapper.setVisible(false);
        searchResultsWrapper.setManaged(false);

        // Live search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue.trim().toLowerCase();
            searchResultsPane.getChildren().clear();

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
                    return;
                }

                for (FoodItems item : results) {
                    VBox card = CartSearchCardFactory.build(primaryStage, cart, item, refreshCartScene);
                    searchResultsPane.getChildren().add(card);
                }
            } else {
                searchResultsWrapper.setVisible(false);
                searchResultsWrapper.setManaged(false);
            }
        });

        searchField.setOnAction(e -> searchBtn.fire());
        searchContainer.getChildren().addAll(searchField, searchBtn);

        // SPACERS
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // BUTTONS HBOX ON THE RIGHT
        HBox buttonsHBox = new HBox(20);
        buttonsHBox.setAlignment(Pos.CENTER_RIGHT);

        Button homeBtn = navButton("Home");
        homeBtn.setOnAction(e -> primaryStage.setScene(new HomePage(primaryStage, cart).getHomeScene()));

        Button menuBtn = navButton("Menu");
        menuBtn.setOnAction(e -> primaryStage.setScene(new MenuPage(primaryStage, cart).getMenuScene()));

        Button cartBtn = new Button("Cart");
        cartBtn.setStyle("-fx-background-color: white; -fx-text-fill: #FF6B00; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-cursor: hand;");
        cartBtn.setOnAction(e -> refreshCartScene.run());

        boolean loggedIn = (Session.getCurrentUsername() != null && !Session.isGuest())
                && (Session.getCurrentEmail() != null && !Session.getCurrentEmail().isEmpty());

        Button authBtn = navButton(loggedIn ? "Log Out" : "Log In");
        authBtn.setOnAction(e -> {
            if (loggedIn) Session.logout();
            primaryStage.setScene(new LoginPage(primaryStage).getLoginScene());
        });

        Button profileBtn = navButton("Profile");
        profileBtn.setOnAction(e -> {
            Scene currentScene = primaryStage.getScene();
            ProfilePage profilePage = new ProfilePage(primaryStage, currentScene);
            primaryStage.setScene(profilePage.getProfileScene());
        });

        buttonsHBox.getChildren().addAll(homeBtn, menuBtn, cartBtn, authBtn, profileBtn);

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

        return new NavbarNodes(
                topContainer,
                searchResultsWrapper,
                searchResultsPane,
                searchContainer,
                searchField,
                titleLabel,
                buttonsHBox,
                leftGroup,
                logo,
                clip,
                breadcrumb,
                cartStep,
                sep1,
                shippingStep,
                sep2,
                paymentStep
        );
    }

    private static Button navButton(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;"));
        return b;
    }
}