package com.example.try2;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class HomePageSixthExtension {

    private final AnchorPane extensionRoot;
    private final ImageView bgView;
    private final Button enterMenuBtn;

    public HomePageSixthExtension() {
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(1366, 768);

        // --- Background image ---
        Image bgImage = new Image(getClass().getResource("/com/example/try2/images/sixth_ext_bg.png").toExternalForm());
        bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(true);
        bgView.setFitWidth(1366);
        bgView.setFitHeight(768);

        AnchorPane.setTopAnchor(bgView, 0.0);
        AnchorPane.setBottomAnchor(bgView, 0.0);
        AnchorPane.setLeftAnchor(bgView, 0.0);
        AnchorPane.setRightAnchor(bgView, 0.0);

        // --- Three vertical divider lines ---
        Region line1 = new Region();
        line1.setPrefWidth(1);
        line1.setStyle("-fx-background-color: rgba(255, 255, 255, 0.4);");
        AnchorPane.setTopAnchor(line1, 0.0);
        AnchorPane.setBottomAnchor(line1, 0.0);
        AnchorPane.setLeftAnchor(line1, 1366.0 / 3);

        Region line2 = new Region();
        line2.setPrefWidth(1);
        line2.setStyle("-fx-background-color: rgba(255, 255, 255, 0.4);");
        AnchorPane.setTopAnchor(line2, 0.0);
        AnchorPane.setBottomAnchor(line2, 0.0);
        AnchorPane.setLeftAnchor(line2, (1366.0 / 3) * 2);

        Region line3 = new Region();
        line3.setPrefWidth(1);
        line3.setStyle("-fx-background-color: rgba(255, 255, 255, 0.4);");
        AnchorPane.setTopAnchor(line3, 0.0);
        AnchorPane.setBottomAnchor(line3, 0.0);
        AnchorPane.setLeftAnchor(line3, 1366.0 - 1);

        // --- ENTER MENU button (kept from original code) ---
        enterMenuBtn = new Button("ENTER MENU");
        enterMenuBtn.getStyleClass().add("enter-menu-button");

        // Center-bottom placement
        StackPane buttonContainer = new StackPane(enterMenuBtn);
        buttonContainer.setAlignment(Pos.BOTTOM_CENTER);
        AnchorPane.setBottomAnchor(buttonContainer, 60.0);
        AnchorPane.setLeftAnchor(buttonContainer, 0.0);
        AnchorPane.setRightAnchor(buttonContainer, 0.0);

        // --- Fade-in animation for button ---
        FadeTransition fade = new FadeTransition(Duration.seconds(1.2), enterMenuBtn);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        // --- Add all to root ---
        extensionRoot.getChildren().addAll(bgView, line1, line2, line3, buttonContainer);
    }

    public AnchorPane getExtensionRoot() {
        return extensionRoot;
    }
}
