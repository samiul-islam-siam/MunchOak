package com.example.try2;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
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

        // --- Load extension images ---
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
        nextImageView.setOpacity(1); // keep visible during slide

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
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), e -> slideToNextImage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void slideToNextImage() {
        int nextIndex = (currentIndex + 1) % images.size();
        nextImageView.setImage(images.get(nextIndex));

        // Position next image to the right of the current image
        nextImageView.setTranslateX(1366);

        Timeline slideTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(currentImageView.translateXProperty(), 0),
                        new KeyValue(nextImageView.translateXProperty(), 1366)
                ),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(currentImageView.translateXProperty(), -1366), // slide out left
                        new KeyValue(nextImageView.translateXProperty(), 0)         // slide in to center
                )
        );

        slideTimeline.setOnFinished(e -> {
            currentImageView.setImage(images.get(nextIndex));
            currentImageView.setTranslateX(0);
            nextImageView.setTranslateX(0);
            currentIndex = nextIndex;
        });

        slideTimeline.play();
    }

    private void handleImageClick(int index) {
        System.out.println("Clicked on image " + index);
    }
}
