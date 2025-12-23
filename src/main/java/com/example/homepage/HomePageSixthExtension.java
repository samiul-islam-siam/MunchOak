package com.example.homepage;

import com.example.menu.MenuPage;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HomePageSixthExtension implements HomePageComponent {
    private final AnchorPane extensionRoot;
    private final ImageView leftImageView;
    private final ImageView rightImageView;
    private final VBox middleMenu;
    private final Button enterMenuBtn;
    private final String originalStyle;

    private static final double PREF_WIDTH = 1000;
    private static final double PREF_HEIGHT = 700;
    private static final double PARALLAX_SPEED = 0.5;

    private static final double MAX_IMAGE_PERCENT = 0.25; // Reduced to prevent overlap
    private static final double ASPECT_RATIO = 440.0 / 300.0; // H:W
    private static final double EDGE_PADDING_PERCENT = 0.05; // Increased for better spacing
    private static final double MENU_TOP_PADDING = 200; // Increased further to move text more downward

    public HomePageSixthExtension(Stage primaryStage) {
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(PREF_WIDTH, PREF_HEIGHT);
        extensionRoot.setMinSize(PREF_WIDTH, PREF_HEIGHT);

        // --- RADIAL GRADIENT BACKGROUND (UPSIDE DOWN: DARK CENTER, LIGHT EDGES) ---
        extensionRoot.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));

        // --- LEFT & RIGHT IMAGES ---
        Image leftImg = new Image(getClass().getResource("/com/example/view/images/left_dish2.png").toExternalForm());
        Image rightImg = new Image(getClass().getResource("/com/example/view/images/right_dish2.png").toExternalForm());
        leftImageView = createImageView(leftImg);
        rightImageView = createImageView(rightImg);

        // --- MIDDLE MENU: TOP-ALIGNED ---
        middleMenu = createMenuSection();

        // --- ENTER MENU BUTTON ---
        enterMenuBtn = new Button("ENTER MENU");
        originalStyle = "-fx-background-color: transparent; -fx-text-fill: black; -fx-border-color: black; -fx-border-width: 2; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 25; -fx-effect: dropshadow(three-pass-box, rgba(255,255,255,0.6), 20, 0, 0, 0);";
        enterMenuBtn.setStyle(originalStyle);

        // Add hover effects
        enterMenuBtn.setOnMouseEntered((MouseEvent e) -> {
            enterMenuBtn.setScaleX(1.05);
            enterMenuBtn.setScaleY(1.05);
            enterMenuBtn.setStyle(originalStyle.replace("rgba(255,255,255,0.6)", "rgba(255,255,255,0.8)") + " -fx-background-color: rgba(255,255,255,0.1);");
        });
        enterMenuBtn.setOnMouseExited((MouseEvent e) -> {
            enterMenuBtn.setScaleX(1.0);
            enterMenuBtn.setScaleY(1.0);
            enterMenuBtn.setStyle(originalStyle);
        });

        // Button click action: Navigate to MenuPage
        enterMenuBtn.setOnAction(e -> navigateToMenu(primaryStage));

        StackPane buttonContainer = new StackPane(enterMenuBtn);
        buttonContainer.setAlignment(Pos.BOTTOM_CENTER);
        buttonContainer.setPadding(new Insets(0, 0, 100, 0)); // Increased bottom padding to move button upward
        AnchorPane.setLeftAnchor(buttonContainer, 0.0);
        AnchorPane.setRightAnchor(buttonContainer, 0.0);
        AnchorPane.setBottomAnchor(buttonContainer, 0.0);

        // --- DYNAMIC LAYOUT ---
        extensionRoot.widthProperty().addListener((obs, old, newVal) -> updateLayout());
        extensionRoot.heightProperty().addListener((obs, old, newVal) -> updateLayout());

        // --- Add all ---
        extensionRoot.getChildren().addAll(leftImageView, rightImageView, middleMenu, buttonContainer);

        initialize();
    }

    // --- Navigate to MenuPage ---
    private void navigateToMenu(Stage stage) {
        MenuPage menuPage = new MenuPage(stage);
        stage.setScene(menuPage.getMenuScene());
        stage.setTitle("MunchOak - Menu");
    }

    // --- Create image view without frame ---
    private ImageView createImageView(Image image) {
        ImageView iv = new ImageView(image);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);

        // Softer, more modern drop shadow
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        shadow.setRadius(18);
        shadow.setOffsetY(10);
        iv.setEffect(shadow);

        return iv;
    }

    // --- Create menu section (TOP-ALIGNED) ---
    private VBox createMenuSection() {
        VBox menuBox = new VBox(10);
        menuBox.setAlignment(Pos.TOP_LEFT);

        Label appTitle = new Label("Appetizers");
        appTitle.setStyle("-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: black;");

        VBox appItems = new VBox(2);
        appItems.setAlignment(Pos.CENTER_LEFT);
        appItems.getChildren().addAll(
                createMenuItem("Samosa", "৳20"),
                createMenuItem("Fries", "৳25"),
                createMenuItem("Spring Rolls", "৳40"),
                createMenuItem("Garlic Bread", "৳220"),
                createMenuItem("Onion Rings", "৳120")
        );

        Label soupTitle = new Label("Soup");
        soupTitle.setStyle("-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: black;");

        VBox soupItems = new VBox(2);
        soupItems.setAlignment(Pos.CENTER_LEFT);
        soupItems.getChildren().addAll(
                createMenuItem("Mushroom Soup", "৳499"),
                createMenuItem("Hot Soup", "৳350"),
                createMenuItem("Clam Chowder", "৳650"),
                createMenuItem("Miso Soup", "৳300")
        );

        menuBox.getChildren().addAll(appTitle, appItems, soupTitle, soupItems);
        return menuBox;
    }

    // --- Create menu item ---
    private HBox createMenuItem(String name, String price) {
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-text-fill: black;");

        Label priceLabel = new Label(price);
        priceLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #ffdd00;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox item = new HBox(10, nameLabel, spacer, priceLabel);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    // --- UPDATE LAYOUT ---
    private void updateLayout() {
        double width = extensionRoot.getWidth();
        double height = extensionRoot.getHeight();

        // --- IMAGE SIZE ---
        double maxImageWidth = width * MAX_IMAGE_PERCENT;
        double baseImageWidth = 300 * (width / PREF_WIDTH);
        double imageWidth = Math.min(baseImageWidth, maxImageWidth);
        double imageHeight = imageWidth * ASPECT_RATIO;
        if (imageHeight > height * 0.8) {
            imageHeight = height * 0.8;
            imageWidth = imageHeight / ASPECT_RATIO;
        }

        // --- IMAGE SIZES ---
        leftImageView.setFitWidth(imageWidth);
        leftImageView.setFitHeight(imageHeight);
        rightImageView.setFitWidth(imageWidth);
        rightImageView.setFitHeight(imageHeight);

        // --- POSITION IMAGES ---
        double edgePadding = width * EDGE_PADDING_PERCENT;
        leftImageView.setLayoutX(edgePadding);
        leftImageView.setLayoutY((height - imageHeight) / 2);
        rightImageView.setLayoutX(width - imageWidth - edgePadding);
        rightImageView.setLayoutY((height - imageHeight) / 2);

        // --- MIDDLE MENU ---
        double menuWidth = width * 0.3; // Reduced to avoid overlap
        double menuX = (width - menuWidth) / 2;
        double menuTopY = MENU_TOP_PADDING * (width / PREF_WIDTH);

        middleMenu.setLayoutX(menuX);
        middleMenu.setPrefWidth(menuWidth);
        middleMenu.setMaxWidth(menuWidth);
        middleMenu.setLayoutY(menuTopY);
    }

    @Override
    public void initialize() {
        // --- Fade-in button ---
        FadeTransition fade = new FadeTransition(Duration.seconds(1.2), enterMenuBtn);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        // --- Parallax ---
        extensionRoot.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent instanceof ScrollPane scrollPane) {
                bindParallax(scrollPane);
            } else if (newParent instanceof VBox vbox) {
                vbox.parentProperty().addListener((o, op, np) -> {
                    if (np instanceof ScrollPane sp) {
                        bindParallax(sp);
                    }
                });
            }
        });

        updateLayout();
    }

    private void bindParallax(ScrollPane scrollPane) {
        extensionRoot.translateYProperty().bind(
                scrollPane.vvalueProperty().multiply(-PREF_HEIGHT * PARALLAX_SPEED)
        );
    }

    @Override
    public AnchorPane getRoot() {
        return extensionRoot;
    }

    @Override
    public double getPrefWidth() {
        return PREF_WIDTH;
    }

    @Override
    public double getPrefHeight() {
        return PREF_HEIGHT;
    }
}