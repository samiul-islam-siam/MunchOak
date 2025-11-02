package com.example.try2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class HomePageSeventhExtension {

    private final AnchorPane extensionRoot;

    public HomePageSeventhExtension() {
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(1366, 768);

        // --- Background image ---
        Image bgImage = new Image(getClass().getResource("/com/example/try2/images/seventh_ext_bg.png").toExternalForm());
        ImageView bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(true);
        bgView.setFitWidth(1366);
        bgView.setFitHeight(768);
        AnchorPane.setTopAnchor(bgView, 0.0);
        AnchorPane.setBottomAnchor(bgView, 0.0);
        AnchorPane.setLeftAnchor(bgView, 0.0);
        AnchorPane.setRightAnchor(bgView, 0.0);

        // --- Fonts ---
        Font titleFont = Font.font("The Seasons", 36);
        Font subtitleFont = Font.font("The Seasons", 18);

        // --- Headline ---
        Label headline = new Label("UPCOMING EVENTS");
        headline.setFont(titleFont);
        headline.setStyle("""
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 2);
                """);
        headline.setAlignment(Pos.CENTER);

        // --- Container for 3 sections ---
        HBox threeSections = new HBox();
        threeSections.setPrefSize(1366, 520);
        threeSections.setSpacing(30); // spacing between sections
        threeSections.setPadding(new Insets(40, 60, 40, 60));
        threeSections.setAlignment(Pos.CENTER);

        // --- Create image sections ---
        VBox section1 = createImageSection(
                "/com/example/try2/images/event1.png",
                "ILLISH UTTSOB",
                "02 NOV, Sunday",
                "Reservations 12.30 - 2 PM",
                subtitleFont,
                420, 280 // larger corner image
        );
        section1.setBorder(new Border(new BorderStroke(
                Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0)
        )));

        VBox section2 = createImageSection(
                "/com/example/try2/images/event2.png",
                "FOOD FORWARDERS",
                "20 NOV, Thursday",
                "Join Us : 4 - 8 PM",
                subtitleFont,
                360, 220 // center image default
        );
        section2.setBorder(new Border(new BorderStroke(
                Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0)
        )));

        VBox section3 = createImageSection(
                "/com/example/try2/images/event3.png",
                "CHRISTMAS EVE",
                "25 DEC, Thursday",
                "Reservations 11 AM - 1 PM",
                subtitleFont,
                420, 280 // larger corner image
        );

        threeSections.getChildren().addAll(section1, section2, section3);
        HBox.setHgrow(section1, Priority.ALWAYS);
        HBox.setHgrow(section2, Priority.ALWAYS);
        HBox.setHgrow(section3, Priority.ALWAYS);

        // --- Combine headline + sections ---
        VBox contentBox = new VBox(50, headline, threeSections); // slightly larger spacing for balance
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(60, 0, 0, 0));

        // --- Add to root ---
        extensionRoot.getChildren().addAll(bgView, contentBox);
        AnchorPane.setTopAnchor(contentBox, 0.0);
        AnchorPane.setBottomAnchor(contentBox, 0.0);
        AnchorPane.setLeftAnchor(contentBox, 0.0);
        AnchorPane.setRightAnchor(contentBox, 0.0);
    }

    private VBox createImageSection(String imagePath, String eventTitle, String eventDate, String eventDetails, Font font, double width, double height) {
        VBox section = new VBox(15);
        section.setAlignment(Pos.TOP_CENTER);
        section.setPadding(new Insets(20));

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
        titleLabel.setFont(Font.font("The Seasons", 22));
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label dateLabel = new Label(eventDate);
        dateLabel.setFont(font);
        dateLabel.setStyle("-fx-text-fill: #ffd700;"); // golden highlight

        Label detailsLabel = new Label(eventDetails);
        detailsLabel.setFont(font);
        detailsLabel.setStyle("-fx-text-fill: white;");

        VBox textBox = new VBox(4, titleLabel, dateLabel, detailsLabel);
        textBox.setAlignment(Pos.CENTER);

        section.getChildren().addAll(imgView, textBox);
        return section;
    }

    public AnchorPane getExtensionRoot() {
        return extensionRoot;
    }
}
