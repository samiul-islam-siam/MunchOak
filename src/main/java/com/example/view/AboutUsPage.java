package com.example.view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AboutUsPage {

    private final Stage primaryStage;

    public AboutUsPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showAboutUs() {
        Scene scene = getAboutUsScene();
        primaryStage.setScene(scene);
        primaryStage.setTitle("About Us");
        primaryStage.show();
    }

    public Scene getAboutUsScene() {
        BorderPane root = new BorderPane();

        // ---------------- Navigation Bar ----------------
        HBox navBar = new HBox(10);
        navBar.setPadding(new Insets(15, 20, 15, 20));
        navBar.setStyle("-fx-background-color: #b30000;");
        navBar.setAlignment(Pos.CENTER_LEFT);

        Label navTitle = new Label("MunchOak");
        navTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button homeBtn = createNavButton("Home");
        homeBtn.setOnAction(e -> {
            HomePage homePage = new HomePage(primaryStage);
            primaryStage.setScene(homePage.getHomeScene());
        });

        Button menuBtn = createNavButton("Menu");
        menuBtn.setOnAction(e -> showComingSoonAlert("Menu"));

        Button reservationBtn = createNavButton("Reservation");
        reservationBtn.setOnAction(e -> {
            ReservationPage reservationPage = new ReservationPage(primaryStage);
            primaryStage.setScene(reservationPage.getReservationScene());
        });

        Button profileBtn = createNavButton("Profile");
        profileBtn.setOnAction(e -> {
            ProfilePage profilePage = new ProfilePage(primaryStage);
            primaryStage.setScene(profilePage.getProfileScene());
        });

        Button cartBtn = createNavButton("Cart");
        cartBtn.setOnAction(e -> showComingSoonAlert("Cart"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        navBar.getChildren().addAll(navTitle, spacer, homeBtn, menuBtn, reservationBtn, profileBtn, cartBtn);
        root.setTop(navBar);

        // ---------------- Scrollable Content ----------------
        VBox mainContent = new VBox(50);
        mainContent.setPadding(new Insets(50));
        mainContent.setAlignment(Pos.TOP_CENTER);

        // ---------------- Top Section: Text + Right Image ----------------
        HBox topSection = new HBox(30);
        topSection.setAlignment(Pos.CENTER);

        VBox topTextBox = new VBox(20);
        topTextBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("About Us");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label description = new Label(
                "Welcome to MunchOak!\n\n" +
                        "We are dedicated to providing you with the best dining experience.\n" +
                        "Our mission is to deliver delicious meals made with fresh ingredients\n" +
                        "and exceptional service.\n\n" +
                        "Thank you for choosing us!"
        );
        description.setStyle("-fx-font-size: 18px; -fx-text-fill: #333333;");
        description.setWrapText(true);

        topTextBox.getChildren().addAll(title, description);

        ImageView topRightImage = loadImageView("/com/example/view/images/munchoak_interior.png", 400, 400);

        topSection.getChildren().addAll(topTextBox, topRightImage);

        // ---------------- Bottom Section: Image on Left, Text on Right ----------------
        HBox bottomSection = new HBox(30);
        bottomSection.setAlignment(Pos.CENTER);

        ImageView bottomLeftImage = loadImageView("/com/example/view/images/munchoak_interior2.png", 400, 400);

        VBox bottomTextBox = new VBox(10);
        bottomTextBox.setAlignment(Pos.TOP_LEFT);
        Label bottomTitle = new Label("Our Ambiance");
        bottomTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        Label bottomDescription = new Label(
                "Experience the warm and cozy ambiance of MunchOak.\n" +
                        "Our interior is designed to make you feel at home while enjoying great food."
        );
        bottomDescription.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
        bottomDescription.setWrapText(true);

        bottomTextBox.getChildren().addAll(bottomTitle, bottomDescription);
        bottomSection.getChildren().addAll(bottomLeftImage, bottomTextBox);

        // ---------------- Third Section: Text Left + Image Right ----------------
        HBox thirdSection = new HBox(30);
        thirdSection.setAlignment(Pos.CENTER);

        VBox thirdTextBox = new VBox(20);
        thirdTextBox.setAlignment(Pos.CENTER_LEFT);

        Label thirdTitle = new Label("Meet Our Staffs");
        thirdTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label thirdDescription = new Label(
                "Our dedicated team is passionate about providing the best dining experience.\n" +
                        "Feel free to interact with our staff and enjoy personalized service tailored to your liking."
        );
        thirdDescription.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
        thirdDescription.setWrapText(true);

        thirdTextBox.getChildren().addAll(thirdTitle, thirdDescription);

        ImageView thirdImage = loadImageView("/com/example/view/images/munchoak_interior3.png", 400, 250);
        thirdImage.setPreserveRatio(true);
        thirdImage.setSmooth(true);
        thirdImage.setOpacity(0); // fade-in

        thirdSection.getChildren().addAll(thirdTextBox, thirdImage);
        thirdSection.setPadding(new Insets(20, 0, 0, 0));

        // ---------------- Fourth Section: Contact Us ----------------
        HBox contactSection = new HBox(30);
        contactSection.setAlignment(Pos.CENTER);

        VBox contactTextBox = new VBox(15);
        contactTextBox.setAlignment(Pos.TOP_LEFT);

        Label contactTitle = new Label("Contact Us");
        contactTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label contactDetails = new Label(
                "Phone: +880 1234 567890\n" +
                        "Email: info@munchoak.com\n" +
                        "Location: Click here to open in map"
        );
        contactDetails.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
        contactDetails.setWrapText(true);
        contactDetails.setOnMouseClicked(e -> {
            getHostServices().showDocument("https://www.google.com/maps/place/Dhaka,+Bangladesh");
        });

        contactTextBox.getChildren().addAll(contactTitle, contactDetails);

        ImageView locationImage = loadImageView("/com/example/view/images/location.png", 400, 250);
        locationImage.setPreserveRatio(true);
        locationImage.setSmooth(true);

        contactSection.getChildren().addAll(contactTextBox, locationImage);
        contactSection.setPadding(new Insets(20, 0, 50, 0));

        // ---------------- Add all sections to main content ----------------
        mainContent.getChildren().addAll(topSection, bottomSection, thirdSection, contactSection);

        // ---------------- ScrollPane ----------------
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: lightyellow;");

        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            bottomLeftImage.setTranslateY(newVal.doubleValue() * -50);
            topRightImage.setTranslateY(newVal.doubleValue() * -20);
            thirdImage.setTranslateY(newVal.doubleValue() * -30);
            locationImage.setTranslateY(newVal.doubleValue() * -25);

            topRightImage.setScaleX(1 + newVal.doubleValue() * 0.05);
            topRightImage.setScaleY(1 + newVal.doubleValue() * 0.05);
            bottomLeftImage.setScaleX(1 + newVal.doubleValue() * 0.05);
            bottomLeftImage.setScaleY(1 + newVal.doubleValue() * 0.05);
            thirdImage.setScaleX(1 + newVal.doubleValue() * 0.03);
            thirdImage.setScaleY(1 + newVal.doubleValue() * 0.03);
            locationImage.setScaleX(1 + newVal.doubleValue() * 0.03);
            locationImage.setScaleY(1 + newVal.doubleValue() * 0.03);

            if (newVal.doubleValue() > 0.6 && thirdImage.getOpacity() == 0) {
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), thirdImage);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            }
        });

        root.setCenter(scrollPane);

        // ---------------- Entrance Animations ----------------
        TranslateTransition topTextSlide = new TranslateTransition(Duration.seconds(1), topTextBox);
        topTextSlide.setFromX(-500);
        topTextSlide.setToX(0);

        FadeTransition topImageFade = new FadeTransition(Duration.seconds(1), topRightImage);
        topImageFade.setFromValue(0);
        topImageFade.setToValue(1);

        FadeTransition bottomImageFade = new FadeTransition(Duration.seconds(1.2), bottomLeftImage);
        bottomImageFade.setFromValue(0);
        bottomImageFade.setToValue(1);

        TranslateTransition thirdTextSlide = new TranslateTransition(Duration.seconds(1), thirdTextBox);
        thirdTextSlide.setFromX(-500);
        thirdTextSlide.setToX(0);

        FadeTransition thirdImageFade = new FadeTransition(Duration.seconds(1), thirdImage);
        thirdImageFade.setFromValue(0);
        thirdImageFade.setToValue(1);

        TranslateTransition contactTextSlide = new TranslateTransition(Duration.seconds(1), contactTextBox);
        contactTextSlide.setFromX(-500);
        contactTextSlide.setToX(0);

        FadeTransition locationImageFade = new FadeTransition(Duration.seconds(1), locationImage);
        locationImageFade.setFromValue(0);
        locationImageFade.setToValue(1);

        ParallelTransition pageTransition = new ParallelTransition(
                topTextSlide, topImageFade, bottomImageFade, thirdTextSlide, thirdImageFade,
                contactTextSlide, locationImageFade
        );
        pageTransition.play();

        // ---------------- Responsive bindings ----------------
        topTextBox.maxWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 2.5));
        bottomLeftImage.fitWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 2.5));
        topRightImage.fitWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 2.5));
        bottomTextBox.maxWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 2.5));

        // Third Section Responsiveness
        thirdTextBox.maxWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 2.5));
        thirdImage.fitWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 2.5));
        thirdImage.fitHeightProperty().bind(Bindings.divide(primaryStage.heightProperty(), 3));

        // Contact Section Responsiveness
        contactTextBox.maxWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 2.5));
        locationImage.fitWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 2.5));
        locationImage.fitHeightProperty().bind(Bindings.divide(primaryStage.heightProperty(), 4));

        double minBtnWidth = 80;
        double maxBtnWidth = 150;
        Button[] buttons = {homeBtn, menuBtn, reservationBtn, profileBtn, cartBtn};
        for (Button btn : buttons) {
            btn.maxWidthProperty().bind(Bindings.min(Bindings.divide(root.widthProperty(), 6), maxBtnWidth));
            btn.minWidthProperty().bind(Bindings.min(Bindings.divide(root.widthProperty(), 8), minBtnWidth));
        }

        root.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));

        return new Scene(root, 1000, 1400);
    }

    private ImageView loadImageView(String path, double width, double height) {
        ImageView imageView;
        var imageUrl = getClass().getResource(path);
        if (imageUrl != null) {
            imageView = new ImageView(new Image(imageUrl.toExternalForm()));
        } else {
            imageView = new ImageView(new Image(getClass().getResource("/com/example/view/images/bg1.png").toExternalForm()));
            System.err.println("Fallback: " + path + " not found.");
        }
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-text-fill: #b30000;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: #b30000;" +
                        "-fx-border-width: 2;"
        );

        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #b30000;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: #b30000;" +
                        "-fx-border-width: 2;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-text-fill: #b30000;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: #b30000;" +
                        "-fx-border-width: 2;"
        ));
        return btn;
    }

    private void showComingSoonAlert(String pageName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(null);
        alert.setContentText(pageName + " page is coming soon!");
        alert.showAndWait();
    }

    // Helper to open map links
    private javafx.application.HostServices getHostServices() {
        return null; // override when called from actual Application
    }
}
