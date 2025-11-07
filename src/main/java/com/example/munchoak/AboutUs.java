package com.example.munchoak;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AboutUs {

    public static VBox getContent() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("about-root");

        // Restaurant name
        Label nameLabel = new Label("MunchOak");
        nameLabel.getStyleClass().add("restaurant-name");

        // Owner name
        Label ownerLabel = new Label("Owner: Oak");
        ownerLabel.getStyleClass().add("owner-name");

        // Intro
        Label intro = new Label("""
            Welcome to MunchOak — where culinary passion meets cozy dining. 
            We specialize in fresh, handcrafted dishes made from locally sourced ingredients.
            Whether it’s a quick lunch or a relaxed dinner, our kitchen serves happiness every day.
        """);
        intro.getStyleClass().add("intro-text");
        intro.setWrapText(true);

        // Optional image (logo or restaurant photo)
        ImageView imageView = new ImageView(new Image("file:src/main/resources/com/example/view/images/bg1.png"));
        imageView.setFitWidth(350);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("about-image");

        // Contact info
        Label contact = new Label("📞 Contact: +880 1234 567 890\n📍 Address: 12 Oak Street, Dhaka, Bangladesh");
        contact.getStyleClass().add("contact-info");

        root.getChildren().addAll(imageView, nameLabel, ownerLabel, intro, contact);

        root.getStylesheets().add(AboutUs.class.getResource("/com/example/munchoak/aboutus.css").toExternalForm());
        return root;
    }
}
