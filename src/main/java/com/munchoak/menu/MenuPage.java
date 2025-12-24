package com.munchoak.menu;

import com.munchoak.manager.Session;
import com.munchoak.mainpage.AdminHome;
import com.munchoak.cart.Cart;
import com.munchoak.cart.CartPage;
import com.munchoak.server.MainClient;
import com.munchoak.reservation.AboutUsPage;
import com.munchoak.homepage.HomePage;
import com.munchoak.authentication.ProfilePage;
import com.munchoak.reservation.ReservationPage;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MenuPage {
    private final Stage primaryStage;
    private final Cart cart; // shared cart (nullable)
    private static final double NORMAL_WIDTH = 1000;
    private static final double NORMAL_HEIGHT = 700;
    public Scene menuScene;
    private BorderPane root;
    private String searchKeyword = "";
    public BaseMenu menu; // current menu instance

    // Default constructor (no external cart)
    public MenuPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.cart = null;
    }

    public void setSearchKeyword(String keyword) {
        this.searchKeyword = keyword == null ? "" : keyword.toLowerCase();
    }

    // Overloaded constructor (preserve cart)
    public MenuPage(Stage primaryStage, Cart cart) {
        this.primaryStage = primaryStage;
        this.cart = cart;
    }

    /**
     * Returns the menu scene, building it on first call.
     */
    public Scene getMenuScene() {
        if (menuScene == null) {
            double initWidth = primaryStage.isFullScreen() || primaryStage.isMaximized()
                    ? Math.max(primaryStage.getWidth(), NORMAL_WIDTH)
                    : NORMAL_WIDTH;
            double initHeight = primaryStage.isFullScreen() || primaryStage.isMaximized()
                    ? Math.max(primaryStage.getHeight(), NORMAL_HEIGHT)
                    : NORMAL_HEIGHT;

            root = buildRoot();
            menuScene = new Scene(root, initWidth, initHeight);

            // optional CSS — guard against missing resources
            try {
                var css1 = getClass().getResource("/com/munchoak/view/styles/style.css");
                if (css1 != null) menuScene.getStylesheets().add(css1.toExternalForm());
                var css2 = getClass().getResource("/com/munchoak/view/styles/menupage.css");
                if (css2 != null) menuScene.getStylesheets().add(css2.toExternalForm());
            } catch (Exception ignored) {
            }

            attachResizeListeners();
        }

        // Decide which menu to load (AdminMenu or UserMenu)
        String username = Session.getCurrentUsername();
        if (Session.isAdmin()) { // "admin".equalsIgnoreCase(username)
            menu = new AdminMenu();
            System.out.println("Admin Menu loaded in MenuPage");
        } else if (Session.isGuest()) {
            menu = new GuestMenu();
            System.out.println("Guest Menu loaded in MenuPage");
        } else {
            menu = new UserMenu();
            System.out.println("User Menu loaded in MenuPage");
        }
        menu.setSearchKeyword(searchKeyword);

        // ----------------- NEW: create / register MenuClient and attach menu -----------------
        try {
            // Try to reuse a MenuClient already stored in Session (so payment flow's Session.getMenuClient().sendMenuUpdate() stays valid)
            MainClient client = Session.getMenuClient();
            if (client == null) {
                // no client yet -> create, attach menu and store in Session
                client = new MainClient(menu);
                Session.setMenuClient(client); // your Session should expose this; expected by other code
            } else {
                // client exists (maybe created earlier) -> just update the menu reference so UI refresh works
                client.setMenu(menu);
            }
        } catch (NoSuchMethodError | NoClassDefFoundError ex) {
            // If Session.setMenuClient doesn't exist in your Session class, fallback to creating a local client:
            // (should rarely happen because your payment code used Session.getMenuClient())
            try {
                MainClient client = new MainClient(menu);
                // don't crash; we couldn't register into Session, but local client will still listen and call menu.updateView()
            } catch (Exception inner) {
                System.err.println("IOException: " + inner.getMessage());
            }
        } catch (Exception e) {
            // safe fallback: create client and attach menu
            try {
                MainClient tmp = new MainClient(menu);
                // won't be in Session, but will still listen/refresh this menu instance
            } catch (Exception ignored) {
            }
        }
        // ------------------------------------------------------------------------------------

        // Preserve cart if provided
        if (this.cart != null) {
            menu.setCart(this.cart);
        }

        // Place menu view into center
        Node menuView = menu.getView();
        root.setCenter(menuView);

        // Wire cart button (if present)
        Button cartButton = (Button) root.lookup("#cartButton");
        if (cartButton != null) {
            menu.setCartButton(cartButton);
            Cart currentCart = menu.getCart();
            cartButton.setOnAction(e -> {
                CartPage cp = new CartPage(primaryStage, currentCart);
                primaryStage.setScene(cp.getScene());
            });
        }
        // Dynamically update cart badge
        Cart currentCart = menu.getCart();
        //Button cartButton = (Button) root.lookup("#cartButton");
        if (cartButton != null && currentCart != null) {
            StackPane cartPane = (StackPane) cartButton.getGraphic();
            Label cartCountLabel = null;
            // Find cartCountLabel inside StackPane
            for (Node node : cartPane.getChildren()) {
                if (node instanceof Label && "cartCountLabel".equals(node.getId())) {
                    cartCountLabel = (Label) node;
                    break;
                }
            }

            if (cartCountLabel != null) {
                int count = currentCart.getTotalItems();
                if (count > 0) {
                    String countText = String.valueOf(count);
                    if (count > 99) {
                        countText = "99+";
                    }
                    cartCountLabel.setText(countText);
                    cartCountLabel.setVisible(true);

                    // Dynamic sizing based on digit count
                    double size;
                    if (countText.length() == 1) {
                        size = 18;
                    } else if (countText.length() == 2) {
                        size = 22;
                    } else {
                        size = 26;  // For 100+
                    }
                    cartCountLabel.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-text-fill: black;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-min-width: " + size + "px;" +
                                    "-fx-min-height: " + size + "px;" +
                                    "-fx-max-width: " + size + "px;" +
                                    "-fx-max-height: " + size + "px;" +
                                    "-fx-alignment: center;" +
                                    "-fx-background-radius: " + (size / 2) + "px;" +
                                    "-fx-border-color: black;" +
                                    "-fx-border-width: 1px;" +
                                    "-fx-border-radius: " + (size / 2) + "px;" +
                                    "-fx-text-overrun: clip;"
                    );
                } else {
                    cartCountLabel.setVisible(false);
                }
            }
        }

        return menuScene;
    }

    /**
     * Builds the root BorderPane with a top nav bar and a placeholder center.
     */
    private BorderPane buildRoot() {
        BorderPane pane = new BorderPane();

        // === NAV BAR ===
        HBox navBar = new HBox(12);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(12, 24, 12, 24));
        navBar.getStyleClass().add("nav-bar");
        navBar.setStyle("-fx-background-color: #FF6B00;");

        // --- Logo (circular) ---
        Image logoImage = null;
        try {
            var url = getClass().getResource("/com/munchoak/view/images/logo.png");
            if (url != null) logoImage = new Image(url.toExternalForm(), 40, 40, true, true);
        } catch (Exception ignored) {
        }
        ImageView logoView = new ImageView();
        if (logoImage != null) logoView.setImage(logoImage);
        logoView.setFitWidth(40);
        logoView.setFitHeight(40);

        // Clip into a circle
        Circle clip = new Circle(20, 20, 20);
        logoView.setClip(clip);

        // small border-style region behind logo for circular frame effect
        Region logoBorder = new Region();
        logoBorder.setPrefSize(44, 44);
        logoBorder.setStyle("-fx-border-radius: 22; -fx-background-radius: 22; -fx-border-color: transparent;");

        StackPane logoFrame = new StackPane(logoBorder, logoView);
        logoFrame.setPadding(new Insets(0, 8, 0, 0));

        // Title
        Label title = new Label("MunchOak");
        title.setFont(Font.font("Segoe UI", 22));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        // Left and right spacers for centering the search field
        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // --- Search field (centered) ---
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setMaxWidth(320);
        searchField.getStyleClass().add("search-field");
        searchField.setFocusTraversable(false);

        Label searchIcon = new Label("\uD83D\uDD0D"); // 🔍
        searchIcon.setFont(Font.font("Segoe UI Emoji", 14));
        searchIcon.setStyle("-fx-text-fill: gray;");

        // KEEP the keyword after refresh
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            searchField.setText(searchKeyword);
        }

        Button searchBtn = new Button("🔍");
        searchBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: maroon; -fx-font-size: 16px; -fx-cursor: hand;");
        searchBtn.setOnMouseEntered(e -> searchBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 16px; -fx-cursor: hand;"));
        searchBtn.setOnMouseExited(e -> searchBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: maroon; -fx-font-size: 16px; -fx-cursor: hand;"));


        searchBtn.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            // Re-open MenuPage with keyword applied
            MenuPage mp = new MenuPage(primaryStage, cart);
            mp.setSearchKeyword(keyword);
            primaryStage.setScene(mp.getMenuScene());
            mp.menu.updateView();

        });

        // live search listener
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (menu != null) {
                menu.setSearchKeyword(newText.trim().toLowerCase());
                menu.updateView();
            }
        });

        StackPane searchPane = new StackPane(searchField, searchBtn);
        StackPane.setAlignment(searchBtn, Pos.CENTER_RIGHT);
        StackPane.setMargin(searchBtn, new Insets(0, 10, 0, 0));
        searchPane.setMaxWidth(320);

        // --- Right-side nav buttons ---
        Label homeIcon = new Label("\uD83C\uDFE0"); // 🏠
        homeIcon.setFont(Font.font("Segoe UI Emoji", 22));
        homeIcon.setStyle("-fx-text-fill: white;");
        Button homeButton = new Button("Home");
        homeButton.setGraphic(homeIcon);
        homeButton.getStyleClass().add("top-button");
        homeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left;");

        if (Session.isAdmin()) {
            homeButton.setOnAction(e -> AdminHome.openAdminDashboard());
        } else {
            homeButton.setOnAction(e -> returnToHomePerfectly());
        }

        Label aboutIcon = new Label("\u2139"); // ℹ
        aboutIcon.setFont(Font.font("Segoe UI Emoji", 22));
        aboutIcon.setStyle("-fx-text-fill: white;");
        Button aboutButton = new Button("About Us");
        aboutButton.setGraphic(aboutIcon);
        aboutButton.getStyleClass().add("top-button");
        aboutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left;");
        aboutButton.setOnAction(e -> navigateToAboutUs());

        Label reservationIcon = new Label("\uD83D\uDDC5"); // 📅
        reservationIcon.setFont(Font.font("Segoe UI Emoji", 22));
        reservationIcon.setStyle("-fx-text-fill: white;");
        Button reservationButton = new Button("Reservation");
        reservationButton.setGraphic(reservationIcon);
        reservationButton.getStyleClass().add("top-button");
        reservationButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left;");
        reservationButton.setOnAction(e -> navigateToReservation());

        Label profileLabel = new Label("\uD83D\uDC64"); // 👤
        profileLabel.setFont(Font.font("Segoe UI Emoji", 22));
        profileLabel.setStyle("-fx-text-fill: white;");
        Button profileButton = new Button();
        profileButton.setGraphic(profileLabel);
        profileButton.getStyleClass().add("top-button");
        profileButton.setStyle("-fx-background-color: transparent;");

        profileButton.setOnAction(e -> {

            Scene currentScene = primaryStage.getScene();

            ProfilePage profilePage = new ProfilePage(primaryStage, currentScene);
            primaryStage.setScene(profilePage.getProfileScene());

        });

        Label cartLabel = new Label("\uD83D\uDED2"); // 🛒
        cartLabel.setFont(Font.font("Segoe UI Emoji", 22));
        cartLabel.setStyle("-fx-text-fill: white;");
        Label cartCountLabel = new Label("0");
        cartCountLabel.setId("cartCountLabel");
        cartCountLabel.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 12px;" +  // Reduced for better fit
                        "-fx-min-width: 18px;" +   // Slightly larger min for single digits
                        "-fx-min-height: 18px;" +
                        "-fx-max-width: 22px;" +   // Increased to fit 3 digits
                        "-fx-max-height: 22px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 11px;" +  // Adjusted radius for new size
                        "-fx-border-color: red;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 11px;" +
                        "-fx-text-overrun: clip;"   // Explicitly clip instead of ellipsis (optional but recommended)
        );
        cartCountLabel.setVisible(false);

        // STACK cart icon + badge
        StackPane cartPane = new StackPane(cartLabel, cartCountLabel);
        StackPane.setAlignment(cartCountLabel, Pos.TOP_RIGHT);

        // Move badge farther up-right
        cartCountLabel.setTranslateX(10);   // push more right
        cartCountLabel.setTranslateY(-10);  // push more up
        StackPane.setMargin(cartCountLabel, new Insets(-4, -4, 0, 0));

        Button cartButton = new Button();
        cartButton.setId("cartButton");
        cartButton.setGraphic(cartPane);
        cartButton.getStyleClass().add("top-button");
        cartButton.setStyle("-fx-background-color: transparent;");

        // Wrap right buttons in HBox for spacing
        HBox rightButtons=null;


        if(Session.isAdmin())
        {
            rightButtons = new HBox(8, homeButton, aboutButton, profileButton);
        }else
        {
            rightButtons = new HBox(8, homeButton, aboutButton, reservationButton, profileButton, cartButton);
        }
        rightButtons.setAlignment(Pos.CENTER_RIGHT);
        // Assemble nav bar: logo + title + leftSpacer + centered search + rightSpacer + nav buttons
        navBar.getChildren().addAll(
                logoFrame,
                title,
                leftSpacer,
                searchPane,
                rightSpacer,
                rightButtons
        );

        // BACK PANEL beneath (keeps previous behavior)
        VBox backPanel = new VBox();
        backPanel.setAlignment(Pos.TOP_LEFT);
        backPanel.setPadding(new Insets(6, 24, 0, 24));
        Label backLabel = new Label("\u2190");
        backLabel.getStyleClass().add("back-label");

        Button backButton = new Button();
        backButton.setGraphic(backLabel);
        backButton.getStyleClass().add("back-button");

        if (Session.isAdmin()) {
            backButton.setOnAction(e -> AdminHome.openAdminDashboard());
        } else {
            backButton.setOnAction(e -> returnToHomePerfectly());
        }

        backPanel.getChildren().add(backButton);

        VBox topSection = new VBox(navBar, backPanel);
        topSection.setAlignment(Pos.TOP_RIGHT);

        // Placeholder center before menu is inserted; it's fine because getMenuScene() replaces center with menu.getView()
        VBox placeholderCenter = new VBox();
        placeholderCenter.setPadding(new Insets(40));
        placeholderCenter.setAlignment(Pos.CENTER);
        Label placeholderLabel = new Label("Loading menu...");
        placeholderCenter.getChildren().add(placeholderLabel);

        // put them into root
        pane.setTop(topSection);
        pane.setCenter(placeholderCenter);

        return pane;
    }

    private void navigateToAboutUs() {
        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();
        boolean wasFullScreen = primaryStage.isFullScreen();
        boolean wasMaximized = primaryStage.isMaximized();

        AboutUsPage aboutPage = new AboutUsPage(primaryStage, cart);
        Scene aboutScene = aboutPage.getAboutUsScene();
        primaryStage.setScene(aboutScene);

        Platform.runLater(() -> {
            if (wasFullScreen) primaryStage.setFullScreen(true);
            else if (wasMaximized) primaryStage.setMaximized(true);
            else {
                primaryStage.setWidth(currentWidth);
                primaryStage.setHeight(currentHeight);
            }
        });
    }

    private void navigateToReservation() {
        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();
        boolean wasFullScreen = primaryStage.isFullScreen();
        boolean wasMaximized = primaryStage.isMaximized();

        ReservationPage resPage = new ReservationPage(primaryStage, cart);
        Scene resScene = resPage.getReservationScene();
        primaryStage.setScene(resScene);

        Platform.runLater(() -> {
            if (wasFullScreen) primaryStage.setFullScreen(true);
            else if (wasMaximized) primaryStage.setMaximized(true);
            else {
                primaryStage.setWidth(currentWidth);
                primaryStage.setHeight(currentHeight);
            }
        });
    }

    /**
     * Return to HomePage while preserving fullscreen/maximized state and current window size.
     */
    private void returnToHomePerfectly() {
        boolean wasFullScreen = primaryStage.isFullScreen();
        boolean wasMaximized = primaryStage.isMaximized();

        HomePage homePage = new HomePage(primaryStage, cart);
        VBox fullPage = homePage.getFullPage();

        ScrollPane scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        Scene homeScene = new Scene(scrollPane, currentWidth, currentHeight);
        try {
            var css = getClass().getResource("/com/munchoak/view/styles/style.css");
            if (css != null) homeScene.getStylesheets().add(css.toExternalForm());
        } catch (Exception ignored) {
        }

        Platform.runLater(() -> {
            primaryStage.setScene(homeScene);
            Platform.runLater(() -> {
                if (wasFullScreen) primaryStage.setFullScreen(true);
                else if (wasMaximized) primaryStage.setMaximized(true);
                else {
                    primaryStage.setWidth(currentWidth);
                    primaryStage.setHeight(currentHeight);
                }
            });
        });
    }

    /**
     * Basic listener to trigger layout requests when fullscreen/maximize changes.
     */
    private void attachResizeListeners() {
        ChangeListener<Boolean> resizeListener = (obs, oldVal, newVal) -> {
            if (menuScene != null && root != null) root.requestLayout();
        };
        primaryStage.fullScreenProperty().addListener(resizeListener);
        primaryStage.maximizedProperty().addListener(resizeListener);
    }
}