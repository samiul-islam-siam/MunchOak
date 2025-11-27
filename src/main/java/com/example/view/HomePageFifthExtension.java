package com.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class HomePageFifthExtension implements HomePageComponent {
    private final AnchorPane extensionRoot;
    private final ImageView leftImageView;
    private final ImageView rightImageView;
    private final VBox middleMenu;

    private static final double PREF_WIDTH = 1000;
    private static final double PREF_HEIGHT = 700;
    private static final double PARALLAX_SPEED = 0.5;

    // Dynamic image limits
    private static final double MAX_IMAGE_PERCENT = 0.25; // Reduced to 25% to prevent overlap
    private static final double ASPECT_RATIO = 440.0 / 300.0; // H:W

    // Spacing
    private static final double EDGE_PADDING_PERCENT = 0.05; // Increased to 5% for better spacing

    public HomePageFifthExtension() {
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(PREF_WIDTH, PREF_HEIGHT);
        extensionRoot.setMinSize(PREF_WIDTH, PREF_HEIGHT);

        // --- RADIAL GRADIENT BACKGROUND ---
        extensionRoot.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        // --- IMAGES ---
        Image leftImg = new Image(getClass().getResource("/com/example/view/images/left_dish.png").toExternalForm());
        Image rightImg = new Image(getClass().getResource("/com/example/view/images/right_dish.png").toExternalForm());

        leftImageView = createImageView(leftImg);
        rightImageView = createImageView(rightImg);

        // --- MENU ---
        middleMenu = createMenuSection();

        // --- DYNAMIC LAYOUT ---
        extensionRoot.widthProperty().addListener((obs, old, newVal) -> updateLayout());
        extensionRoot.heightProperty().addListener((obs, old, newVal) -> updateLayout());

        // --- Add all ---
        extensionRoot.getChildren().addAll(leftImageView, rightImageView, middleMenu);

        initialize();
    }

    // --- Create image view with modern styling, no frame ---
    private ImageView createImageView(Image image) {
        ImageView iv = new ImageView(image);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);

        // Softer, more modern drop shadow
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.15)); // Lighter shadow color
        shadow.setRadius(25); // Softer blur
        shadow.setOffsetY(8); // Reduced offset for subtlety
        shadow.setSpread(0.1); // Slight spread for modern elevation effect
        iv.setEffect(shadow);

        return iv;
    }

    // --- Menu with smaller text ---
    private VBox createMenuSection() {
        VBox menuBox = new VBox(10);
        menuBox.setAlignment(Pos.CENTER);

        Label seaTitle = new Label("From the Sea");
        seaTitle.setStyle("-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");

        VBox seaItems = new VBox(2);
        seaItems.setAlignment(Pos.CENTER_LEFT);
        seaItems.getChildren().addAll(
                createMenuItem("Crab Cakes", "$8.5"),
                createMenuItem("Fish & Chips", "$7"),
                createMenuItem("Salmon Grill", "$10.5"),
                createMenuItem("Tuna Steak", "$11"),
                createMenuItem("plateau de fruits de mer", "$11")
        );

        Label mainTitle = new Label("Main Course");
        mainTitle.setStyle("-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");

        VBox mainItems = new VBox(2);
        mainItems.setAlignment(Pos.CENTER_LEFT);
        mainItems.getChildren().addAll(
                createMenuItem("Chicken Curry", "$5"),
                createMenuItem("Chicken Biriyani", "$6"),
                createMenuItem("Mutton Biriyani", "$8.5"),
                createMenuItem("Lasagna", "$8"),
                createMenuItem("Alfredo Pasta", "$7.5")
        );

        menuBox.getChildren().addAll(seaTitle, seaItems, mainTitle, mainItems);
        return menuBox;
    }

    private HBox createMenuItem(String name, String price) {
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-text-fill: white;");

        Label priceLabel = new Label(price);
        priceLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #ffdd00;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox item = new HBox(10, nameLabel, spacer, priceLabel);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    // --- UPDATE LAYOUT: PERFECT SPACING ---
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

        // --- IMAGE POSITION (closer to edges) ---
        double edgePadding = width * EDGE_PADDING_PERCENT; // 5%
        leftImageView.setLayoutX(edgePadding);
        leftImageView.setLayoutY((height - imageHeight) / 2);

        rightImageView.setLayoutX(width - imageWidth - edgePadding);
        rightImageView.setLayoutY((height - imageHeight) / 2);

        // --- MENU: CENTERED ---
        double menuWidth = width * 0.3; // Reduced to 30% to avoid overlap
        middleMenu.setPrefWidth(menuWidth);
        middleMenu.setMaxWidth(menuWidth);
        middleMenu.setLayoutX((width - menuWidth) / 2);
        middleMenu.setLayoutY((height - middleMenu.getHeight()) / 2);
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

    @Override
    public void initialize() {
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
}