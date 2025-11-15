package com.example.menu;
import com.example.view.LoginPage;
import com.example.manager.Session;
import javafx.scene.Node;
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
        //BaseMenu baseMenu = new BaseMenu();
        BaseMenu menu;
        String username = com.example.manager.Session.getCurrentUsername();
        if ("admin".equalsIgnoreCase(username)) {
            menu = new AdminMenu();
            System.out.println("Admin Menu loaded in MenuPage");
        } else if("guest".equalsIgnoreCase(username))
        {
            menu = new guestMenu();
            System.out.println("Guest Menu loaded in MenuPage");
        }
        else {
            menu = new UserMenu();
            System.out.println("User Menu loaded in MenuPage");
        }
        Node menuView = menu.getView();
        root.setCenter(menuView);



// add it to your root layout, e.g., center of BorderPane
        root.setCenter(menuView);
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

        if(Session.getCurrentUsername().equalsIgnoreCase("admin"))
        {
            LoginPage loginpage = new LoginPage(primaryStage);
            backButton.setOnAction(e -> loginpage.openAdminDashboard());
        }else
        {
            backButton.setOnAction(e -> returnToHomePerfectly());
        }
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
