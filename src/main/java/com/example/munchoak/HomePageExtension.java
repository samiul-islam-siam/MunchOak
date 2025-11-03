package com.example.munchoak;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class HomePageExtension {

    private final StackPane extensionRoot;
    private final HBox sectionsContainer;

    private final int sectionCount = 3;
    private final List<List<Image>> sectionImages = new ArrayList<>();
    private final List<ImageView> currentImageViews = new ArrayList<>();
    private final List<ImageView> nextImageViews = new ArrayList<>();
    private final List<Integer> currentIndexes = new ArrayList<>();

    public HomePageExtension() {
        extensionRoot = new StackPane();
        extensionRoot.setPrefSize(1366, 768);

        // --- Background Image ---
        Image bgImage = new Image(getClass().getResource("/com/example/munchoak/images/ext_bg.png").toExternalForm());
        ImageView bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(true);
        bgView.setFitWidth(1366);
        bgView.setFitHeight(768);

        // --- Fixed Logo ---
        ImageView logoView = new ImageView(new Image(getClass().getResource("/com/example/munchoak/images/logo.png").toExternalForm()));
        logoView.setFitWidth(50);
        logoView.setFitHeight(50);
        Circle logoClip = new Circle(25, 25, 25);
        logoView.setClip(logoClip);
        StackPane.setAlignment(logoView, Pos.TOP_LEFT);
        StackPane.setMargin(logoView, new Insets(20, 0, 0, 20));

        // --- Headline ---
        Text headline = new Text("C O N N E C T   W I T H   U S");
        headline.setFont(Font.font("Arial", 36));
        headline.setFill(Color.WHITE);
        StackPane.setAlignment(headline, Pos.TOP_CENTER);
        StackPane.setMargin(headline, new Insets(30, 0, 0, 0));

        // --- Subtext ---
        Text subtext = new Text("Make beautiful Memories with MUNCHOAK");
        subtext.setFont(Font.font("Arial", 20));
        subtext.setFill(Color.LIGHTGRAY);
        StackPane.setAlignment(subtext, Pos.TOP_CENTER);
        StackPane.setMargin(subtext, new Insets(80, 0, 0, 0));

        // --- Container for 3 sections ---
        sectionsContainer = new HBox();
        sectionsContainer.setPrefSize(1366, 500);
        sectionsContainer.setSpacing(0);
        sectionsContainer.setPadding(new Insets(140, 60, 40, 60));
        sectionsContainer.setAlignment(Pos.CENTER);

        // --- Create Sections ---
        for (int i = 0; i < sectionCount; i++) {
            StackPane section = new StackPane();
            section.setAlignment(Pos.CENTER);
            section.setPadding(new Insets(10));
            if (i < sectionCount - 1) {
                section.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));
            }
            sectionsContainer.getChildren().add(section);
            HBox.setHgrow(section, Priority.ALWAYS);

            sectionImages.add(new ArrayList<>());
            currentImageViews.add(new ImageView());
            nextImageViews.add(new ImageView());
            currentIndexes.add(0);

            section.getChildren().addAll(currentImageViews.get(i), nextImageViews.get(i));
        }

        // --- Load 5 images per section ---
        // Section 1 (circle)
        sectionImages.get(0).add(new Image(getClass().getResource("/com/example/munchoak/images/events1.png").toExternalForm()));
        sectionImages.get(0).add(new Image(getClass().getResource("/com/example/munchoak/images/events2.png").toExternalForm()));
        sectionImages.get(0).add(new Image(getClass().getResource("/com/example/munchoak/images/events3.png").toExternalForm()));
        sectionImages.get(0).add(new Image(getClass().getResource("/com/example/munchoak/images/events4.png").toExternalForm()));
        sectionImages.get(0).add(new Image(getClass().getResource("/com/example/munchoak/images/events5.png").toExternalForm()));

        // Section 2 (rounded rectangle)
        sectionImages.get(1).addAll(sectionImages.get(0)); // reuse for demo

        // Section 3 (hexagon)
        sectionImages.get(2).addAll(sectionImages.get(0)); // reuse for demo

        // --- Initialize images & apply frame shapes ---
        final double imageWidth = 400;
        final double imageHeight = 300;
        for (int i = 0; i < sectionCount; i++) {
            int startIndex = i % sectionImages.get(i).size();
            currentIndexes.set(i, startIndex);

            ImageView current = currentImageViews.get(i);
            current.setImage(sectionImages.get(i).get(startIndex));
            current.setPreserveRatio(true);
            current.setSmooth(true);
            current.setFitWidth(imageWidth);
            current.setFitHeight(imageHeight);
            applyFrameShape(i, current, imageWidth, imageHeight);

            ImageView next = nextImageViews.get(i);
            int nextStartIndex = (startIndex + 1) % sectionImages.get(i).size();
            next.setImage(sectionImages.get(i).get(nextStartIndex));
            next.setPreserveRatio(true);
            next.setSmooth(true);
            next.setFitWidth(imageWidth);
            next.setFitHeight(imageHeight);
            applyFrameShape(i, next, imageWidth, imageHeight);
            next.setOpacity(0);
            next.setTranslateX(imageWidth);
            next.setVisible(false);
        }

        extensionRoot.getChildren().addAll(bgView, sectionsContainer, logoView, headline, subtext);

        startSlideshow();
    }

    private void applyFrameShape(int sectionIndex, ImageView imageView, double width, double height) {
        switch (sectionIndex) {
            case 0 -> { // Circle
                Circle clip = new Circle(width / 2, height / 2, Math.min(width, height) / 2);
                imageView.setClip(clip);
            }
            case 1 -> { // Rounded rectangle
                Rectangle clip = new Rectangle(width, height);
                clip.setArcWidth(50);
                clip.setArcHeight(50);
                imageView.setClip(clip);
            }
            case 2 -> { // Hexagon
                double cx = width / 2;
                double cy = height / 2;
                double size = Math.min(width, height) / 2;
                Polygon clip = new Polygon(
                        cx, cy - size,  // top
                        cx + size * (Math.sqrt(3)/2), cy - size / 2,  // top-right
                        cx + size * (Math.sqrt(3)/2), cy + size / 2,  // bottom-right
                        cx, cy + size,  // bottom
                        cx - size * (Math.sqrt(3)/2), cy + size / 2,  // bottom-left
                        cx - size * (Math.sqrt(3)/2), cy - size / 2   // top-left
                );
                imageView.setClip(clip);
            }
        }
    }

    private void startSlideshow() {
        Timeline masterTimeline = new Timeline(new KeyFrame(Duration.seconds(4), e -> slideToNextImages()));
        masterTimeline.setCycleCount(Timeline.INDEFINITE);
        masterTimeline.play();
    }

    private void slideToNextImages() {
        final double imageWidth = 400;
        for (int i = 0; i < sectionCount; i++) {
            int nextIndex = (currentIndexes.get(i) + 1) % sectionImages.get(i).size();
            ImageView current = currentImageViews.get(i);
            ImageView next = nextImageViews.get(i);
            next.setImage(sectionImages.get(i).get(nextIndex));
            final double w = imageWidth;
            final double h = 300;
            applyFrameShape(i, next, w, h);
            next.setOpacity(0);
            next.setTranslateX(w);
            next.setVisible(true);

            // Create smooth transitions
            TranslateTransition ttCurrent = new TranslateTransition(Duration.seconds(0.8), current);
            ttCurrent.setFromX(0);
            ttCurrent.setToX(-w);
            ttCurrent.setInterpolator(Interpolator.EASE_BOTH);

            TranslateTransition ttNext = new TranslateTransition(Duration.seconds(0.8), next);
            ttNext.setFromX(w);
            ttNext.setToX(0);
            ttNext.setInterpolator(Interpolator.EASE_BOTH);

            FadeTransition ftCurrent = new FadeTransition(Duration.seconds(0.8), current);
            ftCurrent.setFromValue(1.0);
            ftCurrent.setToValue(0.0);

            FadeTransition ftNext = new FadeTransition(Duration.seconds(0.8), next);
            ftNext.setFromValue(0.0);
            ftNext.setToValue(1.0);

            ParallelTransition pt = new ParallelTransition(ttCurrent, ttNext, ftCurrent, ftNext);
            final int index = i;
            pt.setOnFinished(ev -> {
                // Update index
                currentIndexes.set(index, nextIndex);
                // Swap roles
                ImageView temp = currentImageViews.get(index);
                currentImageViews.set(index, nextImageViews.get(index));
                nextImageViews.set(index, temp);
                // Reset new current position
                currentImageViews.get(index).setTranslateX(0);
                currentImageViews.get(index).setOpacity(1);
                // Prepare new next
                int newNextIdx = (currentIndexes.get(index) + 1) % sectionImages.get(index).size();
                nextImageViews.get(index).setImage(sectionImages.get(index).get(newNextIdx));
                applyFrameShape(index, nextImageViews.get(index), w, h);
                nextImageViews.get(index).setTranslateX(w);
                nextImageViews.get(index).setOpacity(0);
                nextImageViews.get(index).setVisible(false);
            });
            pt.play();
        }
    }

    public StackPane getExtensionRoot() {
        return extensionRoot;
    }
}