package com.example.homepage;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class HomePageFourthExtension implements HomePageComponent {
    private final AnchorPane extensionRoot;
    private final ImageView mainImageView;
    private final Label textLabel;
    private final ScrollPane scrollPane;
    private final Button readMoreBtn;
    private final VBox textContainer;
    private final String previewText;
    private final String fullText;
    private boolean isExpanded = false;
    private double fixedScrollHeight = 0.0; // To store the initial preview height

    public HomePageFourthExtension() {
        // --- Root setup ---
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(getPrefWidth(), getPrefHeight());
        extensionRoot.setMinSize(getPrefWidth(), getPrefHeight());
        // Set linear gradient background (horizontal: left to right)
        extensionRoot.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));

        // --- Single Image: Rectangular frame at left center ---
        Image mainImage = new Image(getClass().getResource("/com/example/view/images/cocktails.png").toExternalForm());
        mainImageView = new ImageView(mainImage);
        mainImageView.setPreserveRatio(false);
        double initialImgWidth = 0.4 * getPrefWidth();
        double initialImgHeight = 0.7 * getPrefHeight();
        mainImageView.setFitWidth(initialImgWidth);
        mainImageView.setFitHeight(initialImgHeight);
        mainImageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 20, 0.3, 2, 8); -fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 10;");
        AnchorPane.setTopAnchor(mainImageView, (getPrefHeight() - initialImgHeight) / 2);
        AnchorPane.setLeftAnchor(mainImageView, 50.0);

        // --- Text content ---
        previewText = """
                At Munch-Oak, we believe that true luxury lies not only in exquisite taste — but in generosity, sustainability, and heart.
                Our journey began beneath the shade of an old oak tree — a place where friends gathered, stories were shared, and meals brought people together.
                From that spirit of connection, Munch-Oak was born: an elegant fine dining experience that celebrates nature's bounty
                """;
        fullText = """
                At Munch-Oak, we believe that true luxury lies not only in exquisite taste — but in generosity, sustainability, and heart.
                Our journey began beneath the shade of an old oak tree — a place where friends gathered, stories were shared, and meals brought people together. From that spirit of connection, Munch-Oak was born: an elegant fine dining experience that celebrates nature's bounty while giving back to the community that sustains us. We believe that the beauty of fine dining reaches its fullest meaning when it gives back.
                Through our "Oak of Hope" initiative, each surplus meal finds new life — shared with those in need, never wasted, always cherished.
                At Munch-Oak, sustainability isn't a choice; it's our tradition.
                Compassion isn't a gesture; it's our flavor.
                When you dine with us, you become part of something timeless —
                a movement that nourishes both people and planet.
                Because true elegance is generous.
                """;
        textLabel = new Label(previewText);
        textLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-text-fill: black; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 4, 0.2, 0, 1);");
        textLabel.setWrapText(true);
        textLabel.setAlignment(Pos.TOP_LEFT); // Ensure text aligns left within the label for readability

        // --- Scrollable container - FIXED: No scrollbars, transparent ---
        scrollPane = new ScrollPane(textLabel);
        // COMPLETELY REMOVE SCROLLBARS AND MAKE FULLY TRANSPARENT
        scrollPane.setStyle("""
                -fx-background-color: transparent;
                -fx-background: transparent;
                -fx-border-color: transparent;
                -fx-border-width: 0;
                -fx-scrollbar-width: 0;
                """);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Always hide vertical scrollbar
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Always hide horizontal scrollbar
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // --- Headers ---
        // C U I S I N E - spaced, bold, semi-transparent, larger font
        HBox cuisineBox = new HBox(8); // Spacing between letters
        cuisineBox.setAlignment(Pos.CENTER_LEFT);
        String[] letters = {"C", "U", "I", "S", "I", "N", "E"};
        for (String letter : letters) {
            Label letterLabel = new Label(letter);
            letterLabel.setStyle("-fx-font-family: 'The Seasons'; -fx-font-weight: bold; -fx-font-size: 56px; -fx-text-fill: black; -fx-opacity: 0.7;");
            cuisineBox.getChildren().add(letterLabel);
        }

        // Our History & Goal - larger font
        Label historyLabel = new Label("Our History & Goal");
        historyLabel.setStyle("-fx-font-family: 'The Seasons'; -fx-font-weight: bold; -fx-font-size: 32px; -fx-text-fill: black;");

        // --- Read More button (onAction set later) ---
        readMoreBtn = new Button("Read More ⬇");
        readMoreBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-border-color: black; -fx-border-width: 1; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 16;");

        // --- Container for headers + scroll + button ---
        textContainer = new VBox(25, cuisineBox, historyLabel, scrollPane, readMoreBtn); // Increased spacing for distance
        textContainer.setAlignment(Pos.TOP_LEFT);
        textContainer.setSpacing(10);

        // Now set onAction after textContainer is initialized
        readMoreBtn.setOnAction(e -> {
            isExpanded = !isExpanded;
            if (isExpanded) {
                textLabel.setText(fullText);
                readMoreBtn.setText("Show Less ⬆");
                // For expanded state, allow content to grow naturally without scrollbars
            } else {
                textLabel.setText(previewText);
                readMoreBtn.setText("Read More ⬇");
                scrollPane.setPrefHeight(fixedScrollHeight);
                scrollPane.setMaxHeight(fixedScrollHeight);
            }
            // Trigger layout update
            Platform.runLater(() -> {
                textContainer.requestLayout();
                extensionRoot.requestLayout();
                fixTextSizes();
            });
        });

        // Position textContainer closer to the image
        double initialGap = 50.0;
        AnchorPane.setLeftAnchor(textContainer, 50.0 + initialImgWidth + initialGap);
        AnchorPane.setRightAnchor(textContainer, 50.0);
        AnchorPane.setBottomAnchor(textContainer, 50.0);
        AnchorPane.setTopAnchor(textContainer, null); // Remove top anchor

        // --- Add all to root ---
        extensionRoot.getChildren().addAll(mainImageView, textContainer);

        // Dynamic layout listeners
        ChangeListener<Number> layoutListener = (observable, oldValue, newValue) -> updateLayout();
        extensionRoot.widthProperty().addListener(layoutListener);
        extensionRoot.heightProperty().addListener(layoutListener);

        // Text container width listener
        textContainer.widthProperty().addListener((obs, old, newW) -> fixTextSizes());

        // Call initialize for this component
        initialize();
    }

    private void updateLayout() {
        double width = extensionRoot.getWidth();
        double height = extensionRoot.getHeight();
        if (width <= 0 || height <= 0) return;

        double imgW = 0.4 * width;
        double imgH = 0.7 * height;
        mainImageView.setFitWidth(imgW);
        mainImageView.setFitHeight(imgH);
        AnchorPane.setTopAnchor(mainImageView, (height - imgH) / 2);
        AnchorPane.setLeftAnchor(mainImageView, 50.0);

        // Position textContainer closer to the image
        double gap = 50.0;
        double leftPos = 50.0 + imgW + gap;
        AnchorPane.setLeftAnchor(textContainer, leftPos);
        AnchorPane.setRightAnchor(textContainer, 50.0);
        AnchorPane.setBottomAnchor(textContainer, 50.0);
        AnchorPane.setTopAnchor(textContainer, null); // Remove top anchor

        // *** Text container stays anchored to bottom-right, no left positioning needed ***
        // Just ensure it takes available width from right edge
        Platform.runLater(() -> {
            textContainer.requestLayout();
            extensionRoot.requestLayout();
            fixTextSizes();
        });
    }

    private void fixTextSizes() {
        double contW = textContainer.getWidth();
        if (Double.isNaN(contW) || contW <= 0) return;

        double textMaxW = contW - 30.0; // Padding
        textLabel.setMaxWidth(textMaxW);
        scrollPane.setPrefWidth(textMaxW + 15.0);

        // Adjust headers and button
        if (textContainer.getChildren().size() >= 5) {
            HBox cuisineBox = (HBox) textContainer.getChildren().get(0);
            Label historyLabelLocal = (Label) textContainer.getChildren().get(1);
            Button btnLocal = (Button) textContainer.getChildren().get(4);
            cuisineBox.setPrefWidth(contW);
            historyLabelLocal.setPrefWidth(contW);
            btnLocal.setPrefWidth(contW);
        }
    }

    @Override
    public void initialize() {
        // Initial setup: Compute and fix the preview height to prevent any expansion
        Platform.runLater(() -> {
            fixInitialHeight();
            updateLayout();
            fixTextSizes();
        });
    }

    private void fixInitialHeight() {
        // Compute height after layout for preview text
        double contentHeight = textLabel.getHeight();
        if (!Double.isNaN(contentHeight) && contentHeight > 0) {
            fixedScrollHeight = contentHeight;
            scrollPane.setPrefHeight(fixedScrollHeight);
            scrollPane.setMaxHeight(fixedScrollHeight); // Lock height to initial preview size
        }
    }

    @Override
    public AnchorPane getRoot() {
        return extensionRoot;
    }
}