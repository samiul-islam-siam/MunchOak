package com.example.try2;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
        extensionRoot.setPrefSize(1366, 600);
        extensionRoot.setMinSize(1366, 600);

        // --- Background Image ---
        Image bgImage;
        try {
            bgImage = new Image(getClass().getResource("/com/example/try2/images/ext_bg.png").toExternalForm());
        } catch (Exception e) {
            bgImage = null;
            System.out.println("Background image not found: ext_bg.png");
        }
        ImageView bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(false);
        bgView.setFitWidth(1366);
        bgView.setFitHeight(600);

        // --- Fixed Logo ---
        Image logoImg;
        try {
            logoImg = new Image(getClass().getResource("/com/example/try2/images/logo.png").toExternalForm());
        } catch (Exception e) {
            logoImg = null;
            System.out.println("Logo image not found: logo.png");
        }
        ImageView logoView = new ImageView(logoImg);
        logoView.setPreserveRatio(false);
        logoView.setFitWidth(50);
        logoView.setFitHeight(50);
        Circle logoClip = new Circle(25, 25, 25);
        logoView.setClip(logoClip);
        StackPane.setAlignment(logoView, Pos.TOP_LEFT);
        StackPane.setMargin(logoView, new Insets(10, 0, 0, 20));

        // --- Headline ---
        Text headline = new Text("C O N N E C T   W I T H   U S");
        headline.setFont(Font.font("Arial", 24));
        headline.setFill(Color.WHITE);
        StackPane.setAlignment(headline, Pos.TOP_CENTER);
        StackPane.setMargin(headline, new Insets(15, 0, 0, 0));

        // --- Subtext ---
        Text subtext = new Text("Make beautiful Memories with MUNCHOAK");
        subtext.setFont(Font.font("Arial", 14));
        subtext.setFill(Color.LIGHTGRAY);
        StackPane.setAlignment(subtext, Pos.TOP_CENTER);
        StackPane.setMargin(subtext, new Insets(45, 0, 0, 0));

        // --- Container for 3 sections ---
        sectionsContainer = new HBox();
        sectionsContainer.setPrefSize(1366, 400);
        sectionsContainer.setSpacing(0);
        sectionsContainer.setPadding(new Insets(70, 60, 20, 60));
        sectionsContainer.setAlignment(Pos.CENTER);

        // --- Create Sections ---
        for (int i = 0; i < sectionCount; i++) {
            StackPane section = new StackPane();
            section.setAlignment(Pos.CENTER);
            section.setPadding(new Insets(5));
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
        // Section 1 (rounded rectangle)
        List<Image> baseImages = new ArrayList<>();
        try {
            baseImages.add(new Image(getClass().getResource("/com/example/try2/images/events1.png").toExternalForm()));
            baseImages.add(new Image(getClass().getResource("/com/example/try2/images/events2.png").toExternalForm()));
            baseImages.add(new Image(getClass().getResource("/com/example/try2/images/events3.png").toExternalForm()));
            baseImages.add(new Image(getClass().getResource("/com/example/try2/images/events4.png").toExternalForm()));
            baseImages.add(new Image(getClass().getResource("/com/example/try2/images/events5.png").toExternalForm()));
        } catch (Exception e) {
            System.out.println("Event images not found.");
        }
        sectionImages.get(0).addAll(baseImages);
        sectionImages.get(1).addAll(baseImages);
        sectionImages.get(2).addAll(baseImages);

        // --- Initialize images & apply frame shapes ---
        final double imageWidth = 320;
        final double imageHeight = 200;
        for (int i = 0; i < sectionCount; i++) {
            int startIndex = i % sectionImages.get(i).size();
            currentIndexes.set(i, startIndex);

            ImageView current = currentImageViews.get(i);
            if (!sectionImages.get(i).isEmpty()) {
                current.setImage(sectionImages.get(i).get(startIndex));
            }
            current.setPreserveRatio(false);
            current.setSmooth(true);
            current.setFitWidth(imageWidth);
            current.setFitHeight(imageHeight);
            applyFrameShape(i, current, imageWidth, imageHeight);

            ImageView next = nextImageViews.get(i);
            int nextStartIndex = (startIndex + 1) % sectionImages.get(i).size();
            if (!sectionImages.get(i).isEmpty()) {
                next.setImage(sectionImages.get(i).get(nextStartIndex));
            }
            next.setPreserveRatio(false);
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
        // All sections use rounded rectangle
        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(25);
        clip.setArcHeight(25);
        imageView.setClip(clip);
    }

    private void startSlideshow() {
        Timeline masterTimeline = new Timeline(new KeyFrame(Duration.seconds(4), e -> slideToNextImages()));
        masterTimeline.setCycleCount(Timeline.INDEFINITE);
        masterTimeline.play();
    }

    private void slideToNextImages() {
        final double imageWidth = 320;
        final double imageHeight = 200;
        for (int i = 0; i < sectionCount; i++) {
            int nextIndex = (currentIndexes.get(i) + 1) % sectionImages.get(i).size();
            ImageView current = currentImageViews.get(i);
            ImageView next = nextImageViews.get(i);
            if (!sectionImages.get(i).isEmpty()) {
                next.setImage(sectionImages.get(i).get(nextIndex));
            }
            applyFrameShape(i, next, imageWidth, imageHeight);
            next.setOpacity(0);
            next.setTranslateX(imageWidth);
            next.setVisible(true);

            // Create smooth transitions
            TranslateTransition ttCurrent = new TranslateTransition(Duration.seconds(0.8), current);
            ttCurrent.setFromX(0);
            ttCurrent.setToX(-imageWidth);
            ttCurrent.setInterpolator(Interpolator.EASE_BOTH);

            TranslateTransition ttNext = new TranslateTransition(Duration.seconds(0.8), next);
            ttNext.setFromX(imageWidth);
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
                if (!sectionImages.get(index).isEmpty()) {
                    nextImageViews.get(index).setImage(sectionImages.get(index).get(newNextIdx));
                }
                applyFrameShape(index, nextImageViews.get(index), imageWidth, imageHeight);
                nextImageViews.get(index).setTranslateX(imageWidth);
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