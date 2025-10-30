package com.example.try2;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class HomePageSixthExtension {

    private final AnchorPane extensionRoot;
    private final ImageView bgView;

    public HomePageSixthExtension() {
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(1366, 768);

        // Load background image
        Image bgImage = new Image(getClass().getResource("/com/example/try2/images/sixth_ext_bg.png").toExternalForm());
        bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(true);
        bgView.setFitWidth(1366);
        bgView.setFitHeight(768);

        // Anchor background to fill entire pane
        AnchorPane.setTopAnchor(bgView, 0.0);
        AnchorPane.setBottomAnchor(bgView, 0.0);
        AnchorPane.setLeftAnchor(bgView, 0.0);
        AnchorPane.setRightAnchor(bgView, 0.0);

        extensionRoot.getChildren().add(bgView);
    }

    public AnchorPane getExtensionRoot() {
        return extensionRoot;
    }
}
