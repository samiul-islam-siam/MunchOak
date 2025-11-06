package com.example.view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class HomePageThirdExtension implements HomePageComponent {
    private final StackPane extensionRoot;
    private final ImageView imageView;
    private final VBox textContainer;
    private final double WIDTH = getPrefWidth();
    private final double HEIGHT = getPrefHeight();

    public HomePageThirdExtension() {
        extensionRoot = new StackPane();
        extensionRoot.setPrefSize(WIDTH, HEIGHT);
        extensionRoot.setMinSize(WIDTH, HEIGHT);
        // Set exact gradient background color: linear 90 degrees (#5de0e6 to #004aad)
        extensionRoot.setStyle("-fx-background-color: linear-gradient(to right, #5de0e6, #004aad);");
        // --- Text Container (Left Side) ---
        textContainer = new VBox(10);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        textContainer.setPrefWidth(WIDTH * 0.5);
        textContainer.setMinHeight(HEIGHT * 0.8); // Ensure container takes sufficient height
        // Title parts (staggered lines) with "The Seasons" font, increased sizes, explicit black color
        Label title1 = new Label("ONLY THE");
        title1.setFont(Font.font("The Seasons", FontWeight.BOLD, 32));
        title1.setTextFill(Color.BLACK);
        title1.setStyle("-fx-text-fill: black;");
        Label title2 = new Label("FINEST");
        title2.setFont(Font.font("The Seasons", FontWeight.BOLD, 36));
        title2.setTextFill(Color.BLACK);
        title2.setStyle("-fx-text-fill: black;");
        Label title3 = new Label("FOOD &");
        title3.setFont(Font.font("The Seasons", FontWeight.BOLD, 32));
        title3.setTextFill(Color.BLACK);
        title3.setStyle("-fx-text-fill: black;");
        Label title4 = new Label("GREAT");
        title4.setFont(Font.font("The Seasons", FontWeight.BOLD, 36));
        title4.setTextFill(Color.BLACK);
        title4.setStyle("-fx-text-fill: black;");
        Label title5 = new Label("SERVICE");
        title5.setFont(Font.font("The Seasons", FontWeight.BOLD, 32));
        title5.setTextFill(Color.BLACK);
        title5.setStyle("-fx-text-fill: black;");
        // Paragraph with "The Seasons" font, increased size, explicit black color, full wrap
        Label paragraph = new Label("""
                Experience the harmony of taste and hospitality.
                From fresh ingredients to flawless presentation,
                every detail matters.
                Our team is devoted to offering you comfort,
                elegance, and perfection in every visit.
                Here, every meal is more than food — it’s a moment
                to remember.""");
        paragraph.setFont(Font.font("The Seasons", 18));
        paragraph.setTextFill(Color.BLACK);
        paragraph.setStyle("-fx-text-fill: black;");
        paragraph.setWrapText(true);
        paragraph.setMaxWidth(WIDTH * 0.45); // Slightly increased for better fit
        paragraph.setPrefHeight(Region.USE_COMPUTED_SIZE); // Allow natural height
        VBox.setVgrow(paragraph, Priority.ALWAYS); // Allow expansion in VBox
        // Add to container
        textContainer.getChildren().addAll(title1, title2, title3, title4, title5, paragraph);
        // Position text container on left with padding
        StackPane.setAlignment(textContainer, Pos.CENTER_LEFT);
        StackPane.setMargin(textContainer, new Insets(50, 0, 50, 50)); // top=50, right=0, bottom=50, left=50
        // Initially off-screen to the left
        textContainer.setTranslateX(-WIDTH * 0.5);
        extensionRoot.getChildren().add(textContainer);
        // --- Image (Right Side) ---
        Image image = new Image(getClass().getResource("/com/example/view/images/third_ext_bg.png").toExternalForm());
        imageView = new ImageView(image);
        imageView.setPreserveRatio(false);
        imageView.setFitWidth(WIDTH * 0.5); // 50% width
        imageView.setFitHeight(HEIGHT * 0.8); // 80% height
        StackPane.setAlignment(imageView, Pos.CENTER_RIGHT);
        // Add padding for image: space from top, bottom, right
        StackPane.setMargin(imageView, new Insets(50, 20, 50, 0)); // top=50, right=20, bottom=50, left=0
        // Initially off-screen to the right
        imageView.setTranslateX(WIDTH * 0.5);
        extensionRoot.getChildren().add(imageView);
        // Call initialize for this component
        initialize();
    }

    @Override
    public void initialize() {
        // --- Text Slide-in from Left ---
        TranslateTransition textSlideIn = new TranslateTransition(Duration.seconds(1.5), textContainer);
        textSlideIn.setFromX(-WIDTH * 0.5);
        textSlideIn.setToX(0);
        textSlideIn.setCycleCount(1);
        textSlideIn.setAutoReverse(false);
        // --- Image Slide-in from Right ---
        TranslateTransition imageSlideIn = new TranslateTransition(Duration.seconds(1.5), imageView);
        imageSlideIn.setFromX(WIDTH * 0.5);
        imageSlideIn.setToX(0);
        imageSlideIn.setCycleCount(1);
        imageSlideIn.setAutoReverse(false);
        // --- Floating Effect for Image (after slide-in) ---
        Timeline imageFloating = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(imageView.translateYProperty(), 0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(imageView.translateYProperty(), -10)),
                new KeyFrame(Duration.seconds(4), new KeyValue(imageView.translateYProperty(), 0))
        );
        imageFloating.setCycleCount(Timeline.INDEFINITE);
        imageFloating.setAutoReverse(true);
        // --- Sequential for Image: slide then float ---
        SequentialTransition imageSeq = new SequentialTransition(imageSlideIn, imageFloating);
        // --- Parallel: text and image slide in together ---
        ParallelTransition parallelSlide = new ParallelTransition(textSlideIn, imageSeq);
        parallelSlide.play();
        // Note: This triggers on page load. For scroll-triggered, add listener in main ScrollPane.
    }

    @Override
    public StackPane getRoot() {
        return extensionRoot;
    }
}