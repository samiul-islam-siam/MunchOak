package com.munchoak.cart;

import com.munchoak.mainpage.FoodItems;
import com.munchoak.manager.Session;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.InputStream;

public class CartSearchCardFactory {

    public static VBox build(Stage primaryStage, Cart cart, FoodItems item, Runnable refreshCartScene) {
        VBox card = new VBox(10);
        card.setPrefWidth(180);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8,0,0,2);"
        );
        card.setAlignment(Pos.CENTER);

        ImageView iv = new ImageView();
        iv.setFitWidth(100);
        iv.setFitHeight(100);
        iv.setPreserveRatio(true);
        try (InputStream is = CartSearchCardFactory.class.getResourceAsStream("/images/" + item.getImagePath())) {
            if (is != null) iv.setImage(new Image(is));
            else iv.setImage(new Image("file:src/main/resources/com/munchoak/manager/images/" + item.getImagePath()));
        } catch (Exception ignored) {}

        Label name = new Label(item.getName());
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: black;");
        name.setWrapText(true);
        name.setMaxWidth(160);
        name.setAlignment(Pos.CENTER);

        Label price = new Label(String.format("৳ %.2f", item.getPrice()));
        price.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        price.setAlignment(Pos.CENTER);
        price.setMinWidth(70);

        Button addBtn = new Button("Add to Cart");
        addBtn.setStyle(
                "-fx-background-color: #FF6B00; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 20; -fx-padding: 8 16; -fx-cursor: hand;"
        );
        addBtn.setMaxWidth(Double.MAX_VALUE);

        addBtn.setOnAction(evt -> {
            if (Session.isGuest()) {
                Stage notifyPopup = new Stage();
                notifyPopup.initStyle(StageStyle.UNDECORATED);
                notifyPopup.setAlwaysOnTop(true);
                Label notifyLabel = new Label("Please Login !");
                notifyLabel.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20 10 20; -fx-background-radius: 10;");
                VBox notifyBox = new VBox(notifyLabel);
                notifyBox.setAlignment(Pos.CENTER);
                notifyBox.setStyle("-fx-background-color: transparent;");
                notifyPopup.setScene(new Scene(notifyBox, 200, 50));
                notifyPopup.show();
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(e2 -> notifyPopup.close());
                delay.play();
            } else {
                cart.addToCart(item.getId(), 1, 0.0);
                refreshCartScene.run();
            }
        });

        card.getChildren().addAll(iv, name, price, addBtn);
        return card;
    }
}