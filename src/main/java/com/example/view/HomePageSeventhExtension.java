package com.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class HomePageSeventhExtension implements HomePageComponent {
    private final AnchorPane extensionRoot;

    public HomePageSeventhExtension() {
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(getPrefWidth(), getPrefHeight());
        extensionRoot.setMinSize(getPrefWidth(), getPrefHeight());

        // --- 90° LINEAR GRADIENT BACKGROUND ---
        extensionRoot.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        // --- Headline ---
        Label headline = new Label("UPCOMING EVENTS");
        headline.setStyle("""
                -fx-font-family: 'The Seasons'; -fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 2);
                """);
        headline.setAlignment(Pos.CENTER);

        // --- Container for 3 sections ---
        HBox threeSections = new HBox();
        threeSections.setPrefSize(getPrefWidth(), 473);
        threeSections.setSpacing(22);
        threeSections.setPadding(new Insets(29, 44, 29, 44));
        threeSections.setAlignment(Pos.CENTER);

        // --- Create image sections ---
        VBox section1 = createImageSection(
                "/com/example/view/images/event1.png",
                "ILLISH UTTSOB",
                "02 NOV, Sunday",
                "Reservations 12.30 - 2 PM",
                307, 255
        );
        section1.setBorder(new Border(new BorderStroke(
                Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0)
        )));

        VBox section2 = createImageSection(
                "/com/example/view/images/event2.png",
                "FOOD FORWARDERS",
                "20 NOV, Thursday",
                "Join Us : 4 - 8 PM",
                263, 200
        );
        section2.setBorder(new Border(new BorderStroke(
                Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0)
        )));

        VBox section3 = createImageSection(
                "/com/example/view/images/event3.png",
                "CHRISTMAS EVE",
                "25 DEC, Thursday",
                "Reservations 11 AM - 1 PM",
                307, 255
        );

        threeSections.getChildren().addAll(section1, section2, section3);
        HBox.setHgrow(section1, Priority.ALWAYS);
        HBox.setHgrow(section2, Priority.ALWAYS);
        HBox.setHgrow(section3, Priority.ALWAYS);

        // --- Combine headline + sections ---
        VBox contentBox = new VBox(36, headline, threeSections);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(44, 0, 0, 0));

        // --- Add to root ---
        extensionRoot.getChildren().add(contentBox);
        AnchorPane.setTopAnchor(contentBox, 0.0);
        AnchorPane.setBottomAnchor(contentBox, 0.0);
        AnchorPane.setLeftAnchor(contentBox, 0.0);
        AnchorPane.setRightAnchor(contentBox, 0.0);

        initialize();
    }

    private VBox createImageSection(String imagePath, String eventTitle, String eventDate, String eventDetails, double width, double height) {
        VBox section = new VBox(11);
        section.setAlignment(Pos.TOP_CENTER);
        section.setPadding(new Insets(15));

        // --- Event image ---
        ImageView imgView = new ImageView();
        try {
            Image img = new Image(getClass().getResource(imagePath).toExternalForm());
            imgView.setImage(img);
            imgView.setPreserveRatio(true);
            imgView.setFitWidth(width);
            imgView.setFitHeight(height);
            imgView.setSmooth(true);
            imgView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 4);");
        } catch (Exception e) {
            System.out.println("Image not found: " + imagePath);
        }

        // --- Event text below image ---
        Label titleLabel = new Label(eventTitle);
        titleLabel.setStyle("-fx-font-family: 'The Seasons'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label dateLabel = new Label(eventDate);
        dateLabel.setStyle("-fx-font-family: 'The Seasons'; -fx-font-size: 18px; -fx-text-fill: #ffd700;");

        Label detailsLabel = new Label(eventDetails);
        detailsLabel.setStyle("-fx-font-family: 'The Seasons'; -fx-font-size: 18px; -fx-text-fill: white;");

        VBox textBox = new VBox(3, titleLabel, dateLabel, detailsLabel);
        textBox.setAlignment(Pos.CENTER);

        section.getChildren().addAll(imgView, textBox);
        return section;
    }

    @Override
    public void initialize() {
        // No animation needed
    }

    @Override
    public AnchorPane getRoot() {
        return extensionRoot;
    }

    @Override
    public double getPrefWidth() {
        return 1000;
    }

    @Override
    public double getPrefHeight() {
        return 700;
    }
}