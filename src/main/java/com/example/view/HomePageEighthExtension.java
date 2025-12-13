package com.example.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HomePageEighthExtension implements HomePageComponent {

    private final AnchorPane extensionRoot;

    public HomePageEighthExtension() {

        extensionRoot = new AnchorPane();
        extensionRoot.setStyle("-fx-background-color: #000000;");

        // MAIN RESPONSIVE CONTAINER
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(30);
        container.setPadding(new Insets(20));
        container.maxWidthProperty().set(900); // stops left-right movement
        container.setFillWidth(true);

        AnchorPane.setTopAnchor(container, 0.0);
        AnchorPane.setLeftAnchor(container, 0.0);
        AnchorPane.setRightAnchor(container, 0.0);
        AnchorPane.setBottomAnchor(container, 0.0);

        // Bind container spacing and padding to root width for responsiveness
        container.spacingProperty().bind(Bindings.createDoubleBinding(() -> Math.max(15, extensionRoot.getWidth() * 0.03), extensionRoot.widthProperty()));
        container.paddingProperty().bind(Bindings.createObjectBinding(() -> new Insets(extensionRoot.getWidth() * 0.02), extensionRoot.widthProperty()));

        // ======================================================
        // SECTION 1 — HEADINGS
        // ======================================================
        Label headline1 = new Label("Subscribe to MUNCHOAK Newsletter");
        headline1.setTextFill(Color.WHITE);
        headline1.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("Arial", FontWeight.BOLD, Math.max(18, extensionRoot.getWidth() / 35)),
                extensionRoot.widthProperty()));

        Label headline2 = new Label("Get Updates on Our Latest Offers");
        headline2.setTextFill(Color.web("#bb86fc")); // purple
        headline2.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("Arial", FontWeight.BOLD, Math.max(18, extensionRoot.getWidth() / 35)),
                extensionRoot.widthProperty()));

        Label subText = new Label(
                "Get 20% off on your first order at MUNCHOAK by subscribing to our newsletter."
        );
        subText.setTextFill(Color.web("#cccccc"));
        subText.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("Arial", Math.max(12, extensionRoot.getWidth() / 60)),
                extensionRoot.widthProperty()));
        subText.setWrapText(true);

        VBox headingBox = new VBox(headline1, headline2, subText);
        headingBox.setSpacing(8);
        headingBox.setAlignment(Pos.CENTER);
        headingBox.setFillWidth(true);

        // ======================================================
        // SECTION 2 — EMAIL INPUT + SUBSCRIBE BUTTON
        // ======================================================
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email Address");
        emailField.prefWidthProperty().bind(Bindings.createDoubleBinding(() ->
                Math.max(250, extensionRoot.getWidth() * 0.35), extensionRoot.widthProperty()));
        emailField.setStyle(
                "-fx-background-radius: 20; -fx-border-radius: 20;" +
                        "-fx-font-size: 14px; -fx-prompt-text-fill: #888;" +
                        "-fx-background-color: #111111; -fx-text-fill: white;" +
                        "-fx-border-color: #444444; -fx-padding: 10;"
        );
        emailField.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("Arial", Math.max(12, extensionRoot.getWidth() / 60)),
                extensionRoot.widthProperty()));

        Button subscribeBtn = new Button("Subscribe");
        subscribeBtn.prefHeightProperty().bind(Bindings.createDoubleBinding(() ->
                Math.max(35, extensionRoot.getWidth() / 25), extensionRoot.widthProperty()));
        subscribeBtn.setStyle(
                "-fx-background-color: #bb86fc; -fx-text-fill: black;" +
                        "-fx-background-radius: 20; -fx-font-size: 14px;"
        );
        subscribeBtn.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("Arial", FontWeight.BOLD, Math.max(12, extensionRoot.getWidth() / 60)),
                extensionRoot.widthProperty()));

        HBox subscribeBox = new HBox(emailField, subscribeBtn);
        subscribeBox.setSpacing(12);
        subscribeBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(emailField, Priority.ALWAYS);

        // ======================================================
        // SECTION 3 — SOCIAL MEDIA ICONS (NO IMAGES)
        // ======================================================
        Label fb = createIcon("\uD83D\uDC99");  // 💙 (Facebook-themed)
        Label ig = createIcon("\uD83D\uDCF7");  // 📷 (Instagram camera)
        Label tw = createIcon("\uD83D\uDD4A");  // 🕊 (Twitter bird)
        Label yt = createIcon("\u25B6");        // ▶ (YouTube play)

        HBox socialBar = new HBox(fb, ig, tw, yt);
        socialBar.setSpacing(20);
        socialBar.setAlignment(Pos.CENTER);

        // ======================================================
        // SECTION 4 — FOOTER COLUMNS
        // ======================================================
        VBox col1 = buildColumn("MUNCHOAK",
                "Premium Restaurant Experience.",
                "Fresh | Fast | Delicious"
        );

        VBox col2 = buildColumn("Company",
                "About Us", "Menu", "Contact Us", "Career"
        );

        VBox col3 = buildColumn("Customer Service",
                "My Orders", "Track Order", "Return Policy", "FAQ"
        );

        VBox col4 = buildColumn("Contact Info",
                "+0123-456-789",
                "info@munchoak.com",
                "8502 Oak Rd.\nMunchwood, Dhaka 98380"
        );

        HBox footerColumns = new HBox(col1, col2, col3, col4);
        footerColumns.setSpacing(50);
        footerColumns.setAlignment(Pos.TOP_CENTER);
        footerColumns.setFillHeight(false);

        // Make columns responsive
        for (VBox col : new VBox[]{col1, col2, col3, col4}) {
            HBox.setHgrow(col, Priority.ALWAYS);
            col.minWidthProperty().bind(Bindings.createDoubleBinding(() -> Math.max(120, footerColumns.getWidth() / 4 - 50), footerColumns.widthProperty()));
        }

        // ======================================================
        // ADD EVERYTHING TO MAIN CONTAINER
        // ======================================================
        container.getChildren().addAll(
                headingBox,
                subscribeBox,
                socialBar,
                footerColumns
        );

        extensionRoot.getChildren().add(container);
    }

    // ===== Social Media Icon Builder (Unicode) =====
    private Label createIcon(String symbol) {
        Label icon = new Label(symbol);
        icon.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("Arial", Math.max(16, extensionRoot.getWidth() / 45)),
                extensionRoot.widthProperty()));
        icon.setStyle(
                "-fx-text-fill: #bb86fc;"
        );

        icon.setOnMouseEntered(e ->
                icon.setStyle("-fx-text-fill: #ffffff;")
        );
        icon.setOnMouseExited(e ->
                icon.setStyle("-fx-text-fill: #bb86fc;")
        );

        return icon;
    }

    // ===== Footer Column Builder =====
    private VBox buildColumn(String title, String... items) {

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font("Arial", FontWeight.BOLD, Math.max(14, extensionRoot.getWidth() / 50)),
                extensionRoot.widthProperty()));

        VBox box = new VBox();
        box.setSpacing(8);
        box.getChildren().add(titleLabel);

        for (String text : items) {
            Label l = new Label(text);
            l.setTextFill(Color.web("#bbbbbb"));
            l.fontProperty().bind(Bindings.createObjectBinding(() ->
                            Font.font("Arial", Math.max(11, extensionRoot.getWidth() / 70)),
                    extensionRoot.widthProperty()));
            l.setWrapText(true);
            box.getChildren().add(l);
        }

        box.setAlignment(Pos.TOP_LEFT);
        box.setFillWidth(true);
        return box;
    }

    @Override
    public void initialize() {
    }

    @Override
    public double getPrefHeight() {
        return 350;
    }

    @Override
    public double getPrefWidth() {
        return 900;
    }

    @Override
    public AnchorPane getRoot() {
        return extensionRoot;
    }
}