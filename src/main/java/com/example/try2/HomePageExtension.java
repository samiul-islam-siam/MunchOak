package com.example.try2;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class HomePageExtension {
    private final StackPane extensionRoot;
    private final ImageView currentImageView;
    private final ImageView nextImageView;
    private final List<Image> images = new ArrayList<>();
    private int currentIndex = 0;

    public HomePageExtension() {
        extensionRoot = new StackPane();
        extensionRoot.setPrefSize(1366, 768);
        extensionRoot.setBackground(new Background(
                new BackgroundFill(Color.color(0, 0, 0, 0.25), CornerRadii.EMPTY, null)
        ));

        // Load extension images
        images.add(new Image(getClass().getResource("/com/example/try2/images/ext1.png").toExternalForm()));
        images.add(new Image(getClass().getResource("/com/example/try2/images/ext2.png").toExternalForm()));
        images.add(new Image(getClass().getResource("/com/example/try2/images/ext3.png").toExternalForm()));

        currentImageView = new ImageView(images.get(0));
        currentImageView.setPreserveRatio(true);
        currentImageView.setFitWidth(1366);
        currentImageView.setFitHeight(768);

        nextImageView = new ImageView();
        nextImageView.setPreserveRatio(true);
        nextImageView.setFitWidth(1366);
        nextImageView.setFitHeight(768);
        nextImageView.setOpacity(0);

        // Make clickable
        currentImageView.setOnMouseClicked(e -> handleImageClick(currentIndex));
        nextImageView.setOnMouseClicked(e -> handleImageClick(currentIndex));

        currentImageView.setCursor(Cursor.HAND);
        nextImageView.setCursor(Cursor.HAND);

        extensionRoot.getChildren().addAll(currentImageView, nextImageView);
        StackPane.setAlignment(currentImageView, Pos.CENTER);
        StackPane.setAlignment(nextImageView, Pos.CENTER);

        startImageSlideshow();
    }

    public StackPane getExtensionRoot() {
        return extensionRoot;
    }

    private void startImageSlideshow() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), e -> fadeToNextImage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void fadeToNextImage() {
        int nextIndex = (currentIndex + 1) % images.size();
        nextImageView.setImage(images.get(nextIndex));

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), nextImageView);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), currentImageView);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeIn.play();
        fadeOut.play();

        fadeOut.setOnFinished(e -> {
            currentImageView.setImage(images.get(nextIndex));
            currentImageView.setOpacity(1);
            nextImageView.setOpacity(0);
            currentIndex = nextIndex;
        });
    }

    private void handleImageClick(int index) {
        System.out.println("Clicked on image " + index);
        // TODO: add redirection logic here
    }
}
