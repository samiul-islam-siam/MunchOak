package com.example.view;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomePageThirdExtension implements HomePageComponent {
    private final StackPane extensionRoot;
    private final ImageView imageView;
    private final VBox textContainer;
    private final double HEIGHT_RATIO = 0.7; // Reduced height ratio for image

    public HomePageThirdExtension() {
        extensionRoot = new StackPane();
        extensionRoot.setPrefSize(getPrefWidth(), getPrefHeight());
        extensionRoot.setMinSize(getPrefWidth(), getPrefHeight());
        // Set exact gradient background color: linear 90 degrees (#5de0e6 to #004aad)
        extensionRoot.setStyle("-fx-background-color: lightyellow;");

        // --- Text Container (Left Side) ---
        textContainer = new VBox(10);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        textContainer.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> extensionRoot.getWidth() * 0.55, extensionRoot.widthProperty()));
        textContainer.setMinHeight(HEIGHT_RATIO * getPrefHeight() * 0.9); // Ensure container takes sufficient height
        // Title parts (staggered lines) with CSS styling
        Label title1 = new Label("ONLY THE");
        title1.setStyle("-fx-font-family: 'The Seasons'; -fx-font-weight: bold; -fx-font-size: 32px; -fx-text-fill: black;");
        Label title2 = new Label("FINEST");
        title2.setStyle("-fx-font-family: 'The Seasons'; -fx-font-weight: bold; -fx-font-size: 36px; -fx-text-fill: black;");
        Label title3 = new Label("FOOD &");
        title3.setStyle("-fx-font-family: 'The Seasons'; -fx-font-weight: bold; -fx-font-size: 32px; -fx-text-fill: black;");
        Label title4 = new Label("GREAT");
        title4.setStyle("-fx-font-family: 'The Seasons'; -fx-font-weight: bold; -fx-font-size: 36px; -fx-text-fill: black;");
        Label title5 = new Label("SERVICE");
        title5.setStyle("-fx-font-family: 'The Seasons'; -fx-font-weight: bold; -fx-font-size: 32px; -fx-text-fill: black;");
        // Paragraph with CSS styling, full wrap
        Label paragraph = new Label("""
                Experience the harmony of taste and hospitality.
                From fresh ingredients to flawless presentation,
                every detail matters.
                Our team is devoted to offering you comfort,
                elegance, and perfection in every visit.
                Here, every meal is more than food — it’s a moment
                to remember.""");
        paragraph.setStyle("-fx-font-family: 'The Seasons'; -fx-font-size: 18px; -fx-text-fill: black;");
        paragraph.setWrapText(true);
        paragraph.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> textContainer.getWidth() * 0.9, textContainer.widthProperty()));
        paragraph.setPrefHeight(Region.USE_COMPUTED_SIZE); // Allow natural height
        VBox.setVgrow(paragraph, Priority.ALWAYS); // Allow expansion in VBox
        // Add to container
        textContainer.getChildren().addAll(title1, title2, title3, title4, title5, paragraph);
        // Position text container on left with reduced padding
        StackPane.setAlignment(textContainer, Pos.CENTER_LEFT);
        StackPane.setMargin(textContainer, new Insets(30, 0, 30, 70)); // Reduced margins: top=30, right=0, bottom=30, left=30
        // No initial off-screen; appear immediately
        textContainer.setTranslateX(0);
        extensionRoot.getChildren().add(textContainer);

        // --- Image (Right Side) ---
        Image image = new Image(getClass().getResource("/com/example/view/images/third_ext_bg.png").toExternalForm());
        imageView = new ImageView(image);
        imageView.setPreserveRatio(false);
        imageView.fitWidthProperty().bind(Bindings.createDoubleBinding(() -> extensionRoot.getWidth() * 0.35, extensionRoot.widthProperty())); // Reduced to 40% width
        imageView.fitHeightProperty().bind(Bindings.createDoubleBinding(() -> extensionRoot.getHeight() * HEIGHT_RATIO, extensionRoot.heightProperty())); // Responsive height
        StackPane.setAlignment(imageView, Pos.CENTER_RIGHT);
        // Add reduced padding for image: space from top, bottom, right
        StackPane.setMargin(imageView, new Insets(30, 30, 30, 70)); // Reduced: top=30, right=10, bottom=30, left=0
        // No initial off-screen; appear immediately
        imageView.setTranslateX(0);
        extensionRoot.getChildren().add(imageView);

        // Dynamic layout listener for responsiveness
        ChangeListener<Number> layoutListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateLayout();
            }
        };
        extensionRoot.widthProperty().addListener(layoutListener);
        extensionRoot.heightProperty().addListener(layoutListener);

        // Call initialize for this component
        initialize();
    }

    private void updateLayout() {
        double width = extensionRoot.getWidth();
        double height = extensionRoot.getHeight();
        if (width <= 0 || height <= 0) return;

        // No translates needed since no transition; elements appear immediately

        // Ensure margins and alignments are updated
        StackPane.setMargin(textContainer, new Insets(30, 30, 30, 70));
        StackPane.setMargin(imageView, new Insets(30, 50, 30, 20));
    }

    @Override
    public void initialize() {
        // No transitions; elements appear immediately
        updateLayout();
    }

    @Override
    public StackPane getRoot() {
        return extensionRoot;
    }
}