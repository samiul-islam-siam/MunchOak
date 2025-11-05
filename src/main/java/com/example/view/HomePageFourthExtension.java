package com.example.view;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class HomePageFourthExtension {

    private final AnchorPane extensionRoot;
    private final ImageView bgView;
    private final Label textLabel;
    private final ScrollPane scrollPane;
    private final Button readMoreBtn;
    private final VBox textContainer;
    private final String previewText;
    private final String fullText;
    private boolean isExpanded = false;
    private double fixedScrollHeight = 0.0;  // To store the initial preview height

    public HomePageFourthExtension() {
        // --- Root setup ---
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(1366, 768);

        // --- Background image ---
        Image bgImage = new Image(getClass().getResource("/com/example/view/images/fourth_ext_bg.png").toExternalForm());
        bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(true);
        bgView.setFitWidth(1366);
        bgView.setFitHeight(768);

        // Anchor background to fill the entire pane
        AnchorPane.setTopAnchor(bgView, 0.0);
        AnchorPane.setBottomAnchor(bgView, 0.0);
        AnchorPane.setLeftAnchor(bgView, 0.0);
        AnchorPane.setRightAnchor(bgView, 0.0);

        // --- Text content ---
        previewText = """
                At Munch-Oak, we believe that true luxury lies not only in exquisite taste — but in generosity, sustainability, and heart.
                
                Our journey began beneath the shade of an old oak tree — a place where friends gathered, stories were shared, and meals brought people together. From that spirit of connection, Munch-Oak was born: an elegant fine dining experience that celebrates nature’s bounty while giving back to the community that sustains us. We believe that the beauty of fine dining reaches its fullest meaning when it gives back.
                """;

        fullText = """
                At Munch-Oak, we believe that true luxury lies not only in exquisite taste — but in generosity, sustainability, and heart.
                
                Our journey began beneath the shade of an old oak tree — a place where friends gathered, stories were shared, and meals brought people together. From that spirit of connection, Munch-Oak was born: an elegant fine dining experience that celebrates nature’s bounty while giving back to the community that sustains us. We believe that the beauty of fine dining reaches its fullest meaning when it gives back.
                
                Through our “Oak of Hope” initiative, each surplus meal finds new life — shared with those in need, never wasted, always cherished.
                
                At Munch-Oak, sustainability isn’t a choice; it’s our tradition.
                Compassion isn’t a gesture; it’s our flavor.
                
                When you dine with us, you become part of something timeless —
                a movement that nourishes both people and planet.
                
                Because true elegance is generous.
                """;

        textLabel = new Label(previewText);
        textLabel.setFont(Font.font("Segoe UI", 16));
        textLabel.setTextFill(Color.WHITE);
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(500);
        textLabel.setAlignment(Pos.TOP_LEFT); // Ensure text aligns left within the label for readability
        textLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 4, 0.2, 0, 1);");

        // --- Scrollable container ---
        scrollPane = new ScrollPane(textLabel);
        scrollPane.setPrefWidth(520);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);

        // --- Read More button (onAction set later) ---
        readMoreBtn = new Button("Read More ⬇");
        readMoreBtn.getStyleClass().add("read-more-button");  // Use the CSS class for styling

        // --- Container for scroll + button ---
        textContainer = new VBox(10, scrollPane, readMoreBtn);
        textContainer.setAlignment(Pos.TOP_LEFT);

        // Now set onAction after textContainer is initialized
        readMoreBtn.setOnAction(e -> {
            isExpanded = !isExpanded;
            if (isExpanded) {
                textLabel.setText(fullText);
                readMoreBtn.setText("Show Less ⬆");
                FadeTransition fade = new FadeTransition(Duration.seconds(0.6), scrollPane);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
            } else {
                textLabel.setText(previewText);
                readMoreBtn.setText("Read More ⬇");
            }
            // Trigger layout update
            Platform.runLater(() -> {
                textContainer.requestLayout();
                extensionRoot.requestLayout();
            });
        });

        // Anchor the textContainer to the bottom-right of the AnchorPane
        // Increased to 80px from bottom to move it upward
        // Keep 60px from right for padding
        AnchorPane.setBottomAnchor(textContainer, 80.0);
        AnchorPane.setRightAnchor(textContainer, 60.0);
        // No top or left anchors needed; it will position based on bottom/right

        // --- Add all to root ---
        extensionRoot.getChildren().addAll(bgView, textContainer);

        // Initial setup: Compute and fix the preview height to prevent any expansion
        Platform.runLater(() -> fixInitialHeight());
    }

    private void fixInitialHeight() {
        // Compute height after layout for preview text
        double contentHeight = textLabel.getHeight();
        if (!Double.isNaN(contentHeight) && contentHeight > 0) {
            fixedScrollHeight = contentHeight;
            scrollPane.setPrefHeight(fixedScrollHeight);
            scrollPane.setMaxHeight(fixedScrollHeight);  // Lock height to initial preview size
        }
    }

    public AnchorPane getExtensionRoot() {
        return extensionRoot;
    }
}