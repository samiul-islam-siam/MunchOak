package com.example.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class HomePageSecondExtension {
    private final StackPane extensionRoot;
    private final ImageView bgView;

    public HomePageSecondExtension() {
        extensionRoot = new StackPane();
        extensionRoot.setPrefSize(1366, 768);

        // Load background image
        Image bgImage = new Image(getClass().getResource("/com/example/view/images/second_ext_bg.png").toExternalForm());
        bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(true);
        bgView.setFitWidth(1366);
        bgView.setFitHeight(768);

        extensionRoot.getChildren().add(bgView);
    }

    public StackPane getExtensionRoot() {
        return extensionRoot;
    }
}
