package com.munchoak.homepage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class HomePageSecondExtension {
    private final StackPane extensionRoot;
    private final ImageView bgView;

    public HomePageSecondExtension() {
        extensionRoot = new StackPane();
        extensionRoot.setPrefSize(1000, 700);
        // Load background image
        Image bgImage = new Image(getClass().getResource("/com/munchoak/try2/view/second_ext_bg.png").toExternalForm());
        bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(true);
        bgView.setFitWidth(1000);
        bgView.setFitHeight(700);
        extensionRoot.getChildren().add(bgView);
    }

    public StackPane getExtensionRoot() {
        return extensionRoot;
    }
}