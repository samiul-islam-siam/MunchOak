package com.example.try2;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class HomePageFifthExtension {

    private final AnchorPane extensionRoot;
    private final ImageView bgView;

    public HomePageFifthExtension() {
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(1366, 768);

        // --- Background image ---
        Image bgImage = new Image(getClass().getResource("/com/example/try2/images/fifth_ext_bg.png").toExternalForm());
        bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(true);
        bgView.setFitWidth(1366);
        bgView.setFitHeight(768);

        AnchorPane.setTopAnchor(bgView, 0.0);
        AnchorPane.setBottomAnchor(bgView, 0.0);
        AnchorPane.setLeftAnchor(bgView, 0.0);
        AnchorPane.setRightAnchor(bgView, 0.0);

        // --- Add thin vertical lines dividing page into 3 parts ---
        // Line 1 (1/3 from left)
        Region line1 = new Region();
        line1.setPrefWidth(1);
        line1.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);"); // semi-transparent white
        AnchorPane.setTopAnchor(line1, 0.0);
        AnchorPane.setBottomAnchor(line1, 0.0);
        AnchorPane.setLeftAnchor(line1, 1366.0 / 3);

        // Line 2 (2/3 from left)
        Region line2 = new Region();
        line2.setPrefWidth(1);
        line2.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
        AnchorPane.setTopAnchor(line2, 0.0);
        AnchorPane.setBottomAnchor(line2, 0.0);
        AnchorPane.setLeftAnchor(line2, (1366.0 / 3) * 2);

        // --- Add everything to root ---
        extensionRoot.getChildren().addAll(bgView, line1, line2);
    }

    public AnchorPane getExtensionRoot() {
        return extensionRoot;
    }
}
