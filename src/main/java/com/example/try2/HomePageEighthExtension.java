package com.example.try2;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class HomePageEighthExtension implements HomePageComponent {
    private final AnchorPane extensionRoot;

    public HomePageEighthExtension() {
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(getPrefWidth(), getPrefHeight());
        extensionRoot.setMinSize(getPrefWidth(), getPrefHeight());

        // --- SOLID BLACK BACKGROUND ---
        extensionRoot.setStyle("-fx-background-color: #000000;");

        // --- Footer Message ---
        Label footerLabel = new Label("THANK YOU FOR VISITING MUNCHOAK");
        footerLabel.setTextFill(Color.WHITE);
        footerLabel.setStyle("""
                -fx-font-size: 24px;
                -fx-font-weight: bold;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 6, 0, 0, 2);
                """);

        // Center the label
        AnchorPane.setTopAnchor(footerLabel, 0.0);
        AnchorPane.setBottomAnchor(footerLabel, 0.0);
        AnchorPane.setLeftAnchor(footerLabel, 0.0);
        AnchorPane.setRightAnchor(footerLabel, 0.0);
        footerLabel.setAlignment(Pos.CENTER);

        extensionRoot.getChildren().add(footerLabel);

        initialize();
    }

    @Override
    public void initialize() {
        // No animation needed
    }

    @Override
    public double getPrefHeight() {
        return 384; // Footer height
    }

    @Override
    public double getPrefWidth() {
        return 1000;
    }

    @Override
    public AnchorPane getRoot() {
        return extensionRoot;
    }
}