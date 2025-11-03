package com.example.try2;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class HomePageEighthExtension {

    private final AnchorPane extensionRoot;

    public HomePageEighthExtension() {
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(1366, 384); // 🔹 Half of 768 height

        // --- Background Image ---
        Image bgImage = new Image(getClass().getResource("/com/example/try2/images/eighth_ext_bg.png").toExternalForm());
        ImageView bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(false);
        bgView.setFitWidth(1366);
        bgView.setFitHeight(384);

        AnchorPane.setTopAnchor(bgView, 0.0);
        AnchorPane.setBottomAnchor(bgView, 0.0);
        AnchorPane.setLeftAnchor(bgView, 0.0);
        AnchorPane.setRightAnchor(bgView, 0.0);

        // --- Optional Text / Footer Message ---
        Label footerLabel = new Label("THANK YOU FOR VISITING MUNCHOAK");
        footerLabel.setTextFill(Color.WHITE);
        footerLabel.setStyle("""
                -fx-font-size: 24px;
                -fx-font-weight: bold;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 6, 0, 0, 2);
                """);
        AnchorPane.setTopAnchor(footerLabel, 150.0);
        AnchorPane.setLeftAnchor(footerLabel, 0.0);
        AnchorPane.setRightAnchor(footerLabel, 0.0);
        footerLabel.setAlignment(Pos.CENTER);

        extensionRoot.getChildren().addAll(bgView, footerLabel);
    }

    public AnchorPane getExtensionRoot() {
        return extensionRoot;
    }
}
