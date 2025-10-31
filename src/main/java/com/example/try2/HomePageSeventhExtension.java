package com.example.try2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class HomePageSeventhExtension {

    private final AnchorPane extensionRoot;

    public HomePageSeventhExtension() {
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(1366, 768);

        // --- Optional Background ---
        Image bgImage = new Image(getClass().getResource("/com/example/try2/images/seventh_ext_bg.png").toExternalForm());
        ImageView bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(true);
        bgView.setFitWidth(1366);
        bgView.setFitHeight(768);
        AnchorPane.setTopAnchor(bgView, 0.0);
        AnchorPane.setBottomAnchor(bgView, 0.0);
        AnchorPane.setLeftAnchor(bgView, 0.0);
        AnchorPane.setRightAnchor(bgView, 0.0);

        // --- Horizontal container for 3 sections ---
        HBox threeSections = new HBox();
        threeSections.setPrefSize(1366, 768);
        threeSections.setSpacing(0); // spacing handled by borders
        threeSections.setPadding(new Insets(40, 60, 40, 60));

        // --- Section 1 ---
        VBox section1 = createSection("Section 1", "This is the first section.");
        section1.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 1, 0, 0))));

        // --- Section 2 ---
        VBox section2 = createSection("Section 2", "This is the second section.");
        section2.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 1, 0, 0))));

        // --- Section 3 ---
        VBox section3 = createSection("Section 3", "This is the third section.");
        // No right border for the last section

        threeSections.getChildren().addAll(section1, section2, section3);
        HBox.setHgrow(section1, Priority.ALWAYS);
        HBox.setHgrow(section2, Priority.ALWAYS);
        HBox.setHgrow(section3, Priority.ALWAYS);

        // --- Add to root ---
        extensionRoot.getChildren().addAll(bgView, threeSections);
        AnchorPane.setTopAnchor(threeSections, 0.0);
        AnchorPane.setBottomAnchor(threeSections, 0.0);
        AnchorPane.setLeftAnchor(threeSections, 0.0);
        AnchorPane.setRightAnchor(threeSections, 0.0);
    }

    private VBox createSection(String title, String content) {
        VBox section = new VBox(20);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(20));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        contentLabel.setWrapText(true);

        section.getChildren().addAll(titleLabel, contentLabel);
        return section;
    }

    public AnchorPane getExtensionRoot() {
        return extensionRoot;
    }
}
