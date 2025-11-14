package com.example.menu;

import com.example.view.HomePage;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class MenuPage {
    private final Stage primaryStage;
    private static final double NORMAL_WIDTH = 1000;
    private static final double NORMAL_HEIGHT = 700;
    private Scene menuScene;
    private BorderPane root;
    private VBox categoryExtensionsBox;

    public MenuPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

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

            menuScene.getStylesheets().addAll(
                    getClass().getResource("/com/example/view/styles/style.css").toExternalForm(),
                    getClass().getResource("/com/example/view/styles/menupage.css").toExternalForm()
            );

            attachResizeListeners();
        }
        return menuScene;
    }

    private BorderPane buildRoot() {
        BorderPane root = new BorderPane();

        // === TOP NAV BAR ===
        HBox navBar = new HBox(15);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(15, 30, 15, 30));
        navBar.getStyleClass().add("nav-bar");

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.getStyleClass().add("search-field");

        Label searchIcon = new Label("\uD83D\uDD0D"); // 🔍
        searchIcon.setFont(Font.font("Segoe UI Emoji", 16));
        searchIcon.setStyle("-fx-text-fill: gray;");

        StackPane searchPane = new StackPane(searchField, searchIcon);
        StackPane.setAlignment(searchIcon, Pos.CENTER_RIGHT);
        StackPane.setMargin(searchIcon, new Insets(0, 10, 0, 0));
        searchPane.setMaxWidth(200);
        // --- SPACER to push navigation buttons to right ---
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

// --- HOME, ABOUT US, RESERVATION buttons ---
        Button homeButton = new Button("Home");
        homeButton.getStyleClass().add("nav-text-button");
        homeButton.setOnAction(e -> returnToHomePerfectly());

        Button aboutButton = new Button("About Us");
        aboutButton.getStyleClass().add("nav-text-button");
// You can later link this to an AboutUs page
        aboutButton.setOnAction(e -> System.out.println("About Us clicked"));

        Button reservationButton = new Button("Reservation");
        reservationButton.getStyleClass().add("nav-text-button");
// Later, you can open your reservation page here
        reservationButton.setOnAction(e -> System.out.println("Reservation clicked"));
        Label profileLabel = new Label("\uD83D\uDC64"); // 👤
        profileLabel.setFont(Font.font("Segoe UI Emoji", 26));
        Button profileButton = new Button();
        profileButton.setGraphic(profileLabel);
        profileButton.getStyleClass().add("top-button");

        Label cartLabel = new Label("\uD83D\uDED2"); // 🛒
        cartLabel.setFont(Font.font("Segoe UI Emoji", 26));
        Button cartButton = new Button();
        cartButton.setGraphic(cartLabel);
        cartButton.getStyleClass().add("top-button");
        navBar.getChildren().addAll(
                searchPane,
                spacer,
                homeButton, aboutButton, reservationButton,
                profileButton, cartButton
        );


        // BACK BUTTON
        VBox backPanel = new VBox();
        backPanel.setAlignment(Pos.TOP_LEFT);
        backPanel.setPadding(new Insets(10, 30, 0, 30));

        Label backLabel = new Label("\u2190");
        backLabel.getStyleClass().add("back-label");

        Button backButton = new Button();
        backButton.setGraphic(backLabel);
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> returnToHomePerfectly());
        backPanel.getChildren().add(backButton);

        VBox topSection = new VBox(navBar, backPanel);
        topSection.setAlignment(Pos.TOP_RIGHT);

        // === CENTER CONTENT ===
        VBox content = new VBox(50);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(80, 60, 60, 60));

        // === Restored Banner Layering ===
        // === Atmospheric Behind-Logo Layering ===
        Image bannerImage = new Image(getClass().getResource("/com/example/view/images/menu_banner.png").toExternalForm());
        ImageView bannerView = new ImageView(bannerImage);
        bannerView.setPreserveRatio(true);
        bannerView.setSmooth(true);
        bannerView.setFitWidth(300);

        Image overlayImage = new Image(getClass().getResource("/com/example/view/images/overlay_logo.png").toExternalForm());
        ImageView overlayView = new ImageView(overlayImage);
        overlayView.setPreserveRatio(true);
        overlayView.setSmooth(true);
        overlayView.setFitWidth(60);
        overlayView.setOpacity(0.75);

// Place overlay *behind* the banner
        StackPane imageStack = new StackPane(overlayView, bannerView);
        imageStack.setAlignment(Pos.TOP_CENTER);
        StackPane.setMargin(overlayView, new Insets(80, 0, 0, 0));

// === Soft blur & glow pulse ===
        javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(8);
        javafx.scene.effect.ColorAdjust glow = new javafx.scene.effect.ColorAdjust();
        // Combine blur + glow
        javafx.scene.effect.Blend combinedEffect = new javafx.scene.effect.Blend();
        combinedEffect.setMode(javafx.scene.effect.BlendMode.SRC_OVER);
        combinedEffect.setBottomInput(blur);
        combinedEffect.setTopInput(glow);
        overlayView.setEffect(combinedEffect);


// Glow pulse animation
        Timeline glowPulse = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(glow.brightnessProperty(), -0.2)),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(glow.brightnessProperty(), 0.3))
        );
        glowPulse.setAutoReverse(true);
        glowPulse.setCycleCount(Animation.INDEFINITE);
        glowPulse.play();

// Zoom animation (breathing motion)
        ScaleTransition zoom = new ScaleTransition(Duration.seconds(8.0), overlayView);
        zoom.setFromX(0.6);
        zoom.setFromY(0.4);
        zoom.setToX(8.0);
        zoom.setToY(6.0);
        zoom.setCycleCount(ScaleTransition.INDEFINITE);
        zoom.setAutoReverse(true);
        imageStack.layoutBoundsProperty().addListener((obs, old, newVal) -> {
            if (zoom.getStatus() != Animation.Status.RUNNING) zoom.play();
        });

        content.getChildren().add(imageStack);

        // === MENU TITLE ===
        Label title = new Label("OUR SPECIALS MENU");
        title.getStyleClass().add("menu-title");
        content.getChildren().add(title);

        // === MENU GRID ===
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(30);
        grid.setAlignment(Pos.CENTER);

        grid.add(createMenuBox("SOUP"), 0, 0);
        grid.add(createMenuBox("FROM THE SEA"), 1, 0);
        grid.add(createMenuBox("DRINKS"), 2, 0);
        grid.add(createMenuBox("SWEETS"), 0, 1);
        grid.add(createMenuBox("MAIN COURSE"), 1, 1);
        grid.add(createMenuBox("APPETIZERS"), 2, 1);
        grid.add(createMenuBox("FAST FOOD"), 1, 2);

        content.getChildren().add(grid);

        // === CATEGORY EXTENSION AREA ===
        categoryExtensionsBox = new VBox(40);
        categoryExtensionsBox.setAlignment(Pos.CENTER);
        categoryExtensionsBox.setPadding(new Insets(50, 0, 0, 0));
        content.getChildren().add(categoryExtensionsBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll-pane");

        root.setTop(topSection);
        root.setCenter(scrollPane);
        return root;
    }

    // --- Category button setup ---
    private Button createMenuBox(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("menu-button");
        button.setOnAction(e -> toggleCategory(text));
        return button;
    }

    // --- Animated expand/collapse ---
    private void toggleCategory(String category) {
        VBox existing = null;
        for (var node : categoryExtensionsBox.getChildren()) {
            if (node instanceof VBox box && category.equals(box.getUserData())) {
                existing = box;
                break;
            }
        }

        if (existing != null) {
            // collapse animation
            collapseSection(existing);
        } else {
            VBox section = createCategorySection(category);
            expandSection(section);
        }
    }

    private VBox createCategorySection(String category) {
        VBox section = new VBox(10);
        section.setUserData(category);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: #fff8f8; -fx-border-color: red; -fx-border-width: 2; -fx-border-radius: 10;");

        Label sectionTitle = new Label(category);
        sectionTitle.setFont(Font.font("Arial", 24));
        sectionTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");

        VBox itemsBox = new VBox(10);
        itemsBox.setAlignment(Pos.CENTER);

        List<String> foods = switch (category) {
            case "SOUP" -> List.of("Tomato Soup", "Chicken Soup", "Mushroom Soup");
            case "FROM THE SEA" -> List.of("Grilled Salmon", "Fish & Chips", "Shrimp Cocktail", "Lobster Thermidor");
            case "DRINKS" -> List.of("Cola", "Orange Juice", "Mojito", "Lemonade", "Iced Tea");
            case "SWEETS" -> List.of("Brownie", "Cheesecake", "Chocolate Mousse", "Ice Cream", "Cupcakes");
            case "MAIN COURSE" -> List.of("Steak", "Pasta", "Pizza", "Grilled Chicken", "Lasagna");
            case "APPETIZERS" -> List.of("Spring Rolls", "Fries", "Garlic Bread");
            case "FAST FOOD" -> List.of("Burger", "Hotdog", "Nuggets");
            default -> List.of();
        };

        for (String food : foods) {
            Button itemBtn = new Button(food);
            itemBtn.setFont(Font.font(16));
            itemBtn.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: red; -fx-border-width: 1.5; " +
                            "-fx-background-radius: 6; -fx-padding: 8 16; " +
                            "-fx-text-fill: black; -fx-cursor: hand;"
            );
            itemBtn.setOnAction(ev -> System.out.println("Selected: " + food));
            itemsBox.getChildren().add(itemBtn);
        }

        section.getChildren().addAll(sectionTitle, itemsBox);
        return section;
    }

    private void expandSection(VBox section) {
        section.setOpacity(0);
        section.setScaleY(0.3);
        categoryExtensionsBox.getChildren().add(section);

        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.millis(350),
                        new KeyValue(section.opacityProperty(), 1),
                        new KeyValue(section.scaleYProperty(), 1))
        );
        fadeIn.play();
    }

    private void collapseSection(VBox section) {
        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.millis(350),
                        new KeyValue(section.opacityProperty(), 0),
                        new KeyValue(section.scaleYProperty(), 0.3))
        );
        fadeOut.setOnFinished(e -> categoryExtensionsBox.getChildren().remove(section));
        fadeOut.play();
    }

    // === Return to Home ===
    private void returnToHomePerfectly() {
        boolean wasFullScreen = primaryStage.isFullScreen();
        boolean wasMaximized = primaryStage.isMaximized();

        HomePage homePage = new HomePage(primaryStage);
        VBox fullPage = homePage.getFullPage();

        ScrollPane scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");

        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        Scene homeScene = new Scene(scrollPane, currentWidth, currentHeight);
//        homeScene.getStylesheets().addAll(
//                getClass().getResource("resources/com/example/view/styles/style.css").toExternalForm(),
//                getClass().getResource("resources/com/example/view/menupage.css").toExternalForm()
//        );
        // ✅ Reapply global stylesheet here
        var css = getClass().getResource("/com/example/view/styles/style.css");
        if (css != null) {
            homeScene.getStylesheets().add(css.toExternalForm());
            System.out.println("✅ CSS reapplied successfully: " + css);
        } else {
            System.out.println("❌ CSS not found! Check file path.");
        }

        javafx.application.Platform.runLater(() -> {
            primaryStage.setScene(homeScene);
            javafx.application.Platform.runLater(() -> {
                if (wasFullScreen) primaryStage.setFullScreen(true);
                else if (wasMaximized) primaryStage.setMaximized(true);
                else {
                    primaryStage.setWidth(currentWidth);
                    primaryStage.setHeight(currentHeight);
                }
            });
        });
    }

    private void attachResizeListeners() {
        ChangeListener<Boolean> resizeListener = (obs, oldVal, newVal) -> {
            if (menuScene != null && root != null) root.requestLayout();
        };
        primaryStage.fullScreenProperty().addListener(resizeListener);
        primaryStage.maximizedProperty().addListener(resizeListener);
    }
}
