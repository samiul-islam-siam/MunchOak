package com.example.try2;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class HomePageFifthExtension {

    private final AnchorPane extensionRoot;
    private final ImageView bgView;
    private final Button enterMenuBtn;

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

        // --- Enter Menu button ---
        enterMenuBtn = new Button("ENTIRE MENU");
        enterMenuBtn.getStyleClass().add("enter-menu-button");

        // Center-bottom placement
        StackPane buttonContainer = new StackPane(enterMenuBtn);
        buttonContainer.setAlignment(Pos.BOTTOM_CENTER);
        AnchorPane.setBottomAnchor(buttonContainer, 60.0);
        AnchorPane.setLeftAnchor(buttonContainer, 0.0);
        AnchorPane.setRightAnchor(buttonContainer, 0.0);

        // --- Fade-in animation ---
        FadeTransition fade = new FadeTransition(Duration.seconds(1.2), enterMenuBtn);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        extensionRoot.getChildren().addAll(bgView, buttonContainer);
    }

    public AnchorPane getExtensionRoot() {
        return extensionRoot;
    }
}