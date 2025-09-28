package com.example.munchoak;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class HomePage {

    public Node getView() {
        VBox box = new VBox(18);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(20));

        // Advertisement banner (replace with your own image or local file if desired)
        ImageView adImage;
        try {
            Image img = new Image("https://via.placeholder.com/900x220.png?text=Special+Offer+-+50%25+Off!", true);
            adImage = new ImageView(img);
            adImage.setFitWidth(900);
            adImage.setPreserveRatio(true);
        } catch (Exception e) {
            // fallback to a simple label if image fails
            adImage = new ImageView();
        }

        Label title = new Label("🍽️ Welcome to MunchOak");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label description = new Label(
                "MunchOak brings you the freshest seasonal dishes, crafted with love.\n\n" +
                        "Enjoy handcrafted meals, fast friendly service, and a cozy atmosphere. " +
                        "We focus on locally sourced ingredients and unique flavor combinations—perfect for family dinners, dates, and celebrations."
        );
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");

        box.getChildren().addAll(adImage, title, description);
        return box;
    }
}
