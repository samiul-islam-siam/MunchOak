package com.example.view;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HomePageSixthExtension implements HomePageComponent {
    private final AnchorPane extensionRoot;
    private final ImageView leftImageView;
    private final ImageView rightImageView;
    private final VBox middleMenu;
    private final Button enterMenuBtn;

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
        RadialGradient gradient = new RadialGradient(
                0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#004aad")),
                new Stop(1.0, Color.web("#5de0e6"))
        );
        extensionRoot.setBackground(new Background(new BackgroundFill(
                gradient, CornerRadii.EMPTY, Insets.EMPTY
        )));

        // --- LEFT & RIGHT IMAGES ---
        Image leftImg = new Image(getClass().getResource("/com/example/view/images/left_dish2.png").toExternalForm());
        Image rightImg = new Image(getClass().getResource("/com/example/view/images/right_dish2.png").toExternalForm());
        leftImageView = createFramedImage(leftImg);
        rightImageView = createFramedImage(rightImg);

        // --- MIDDLE MENU: TOP-ALIGNED ---
        middleMenu = createMenuSection();

        // --- ENTER MENU BUTTON ---
        enterMenuBtn = new Button("ENTER MENU");
        enterMenuBtn.getStyleClass().add("top-button");

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

    // --- Create framed image ---
    private ImageView createFramedImage(Image image) {
        ImageView iv = new ImageView(image);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        StackPane frame = new StackPane(iv);
        frame.setStyle("-fx-background-color: white; -fx-border-color: white; -fx-border-width: 6; -fx-border-radius: 12; -fx-background-radius: 12;");
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        shadow.setRadius(18);
        shadow.setOffsetY(10);
        frame.setEffect(shadow);
        return iv;
    }

    // --- Create menu section (TOP-ALIGNED) ---
    private VBox createMenuSection() {
        VBox menuBox = new VBox(10);
        menuBox.setAlignment(Pos.TOP_LEFT);

        Label appTitle = new Label("Appetizers");
        appTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        appTitle.setTextFill(Color.WHITE);

        VBox appItems = new VBox(2);
        appItems.setAlignment(Pos.CENTER_LEFT);
        appItems.getChildren().addAll(
                createMenuItem("Samosa", "$2"),
                createMenuItem("Fries", "$2.5"),
                createMenuItem("Spring Rolls", "$2.8"),
                createMenuItem("Garlic Bread", "$2.2"),
                createMenuItem("Onion Rings", "$2.5")
        );

        Label soupTitle = new Label("Soup");
        soupTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        soupTitle.setTextFill(Color.WHITE);

        VBox soupItems = new VBox(2);
        soupItems.setAlignment(Pos.CENTER_LEFT);
        soupItems.getChildren().addAll(
                createMenuItem("Mushroom Soup", "$5"),
                createMenuItem("Hot Soup", "$3.5"),
                createMenuItem("Clam Chowder", "$6.5"),
                createMenuItem("Miso Soup", "$3")
        );

        menuBox.getChildren().addAll(appTitle, appItems, soupTitle, soupItems);
        return menuBox;
    }

    // --- Create menu item ---
    private HBox createMenuItem(String name, String price) {
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", 16));
        nameLabel.setTextFill(Color.WHITE);

        Label priceLabel = new Label(price);
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        priceLabel.setTextFill(Color.web("#ffdd00"));

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