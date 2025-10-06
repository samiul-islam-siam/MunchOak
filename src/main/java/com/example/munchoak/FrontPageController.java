package com.example.munchoak;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class FrontPageController {

    @FXML
    private ImageView background;

    @FXML
    private StackPane stackPane;

    @FXML
    public void initialize() {
        // Bind background to stackPane size → full coverage
        background.fitWidthProperty().bind(stackPane.widthProperty());
        background.fitHeightProperty().bind(stackPane.heightProperty());
    }


        @FXML
        private void handleNextButton(ActionEvent event) {
            // 🔹 Add this debug line
            System.out.println("NEXT button clicked");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/munchoak/Welcome.fxml"));

                // 🔹 Optional: check if the FXML path is valid
                if (loader.getLocation() == null) {
                    System.out.println("FXML not found!");
                }

                Parent root = loader.load();

                // Get current stage
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // New scene for Welcome page
                Scene scene = new Scene(root, 800, 600);
                stage.setScene(scene);
                stage.setTitle("Welcome - Munchoak");
                stage.centerOnScreen();

                // Fade-in animation
                FadeTransition ft = new FadeTransition(Duration.millis(600), root);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();

                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
