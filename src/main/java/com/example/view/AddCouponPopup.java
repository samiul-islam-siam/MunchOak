package com.example.view;

import com.example.manager.CouponStorage;
import com.example.manager.Session;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddCouponPopup {

    public static void show(Stage owner, Runnable onSuccess) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add Coupon");

        // --- Input fields ---
        TextField codeField = new TextField();
        codeField.setPromptText("Coupon Code");

        TextField discountField = new TextField();
        discountField.setPromptText("Discount (e.g. 0.10 for 10%)");
        TextField expiryField = new TextField();
        expiryField.setPromptText("Expiry Date (e.g. 2025-12-31)");
        TextField usageLimitField = new TextField();
        usageLimitField.setPromptText("Usage Limit (positive integer)");

        Label status = new Label();
        status.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        // --- Save button ---
        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #1b4fa8; -fx-text-fill: white; -fx-font-weight: bold;");
        saveBtn.setDefaultButton(true); // ✅ Enter key will trigger this button

        saveBtn.setOnAction(e -> {
            String code = codeField.getText().trim().toUpperCase();
            String discountText = discountField.getText().trim();
            String expiryText = expiryField.getText().trim();
            String usageText = usageLimitField.getText().trim();
            if (code.isEmpty() || discountText.isEmpty() || expiryText.isEmpty() || usageText.isEmpty())
            {
                status.setText("All fields are required!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
                return;
            }
            // Validate coupon code
            if (code.isEmpty()) {
                status.setText("Coupon code cannot be empty!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
                return;
            }
            if (!code.matches("[A-Z0-9]+")) {
                status.setText("Coupon code must be alphanumeric (A–Z, 0–9)!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
                return;
            }

            // Validate discount
            double discount;
            try {
                discount = Double.parseDouble(discountText);
            } catch (NumberFormatException ex) {
                status.setText("Discount must be a valid number!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
                return;
            }

            if (discount <= 0 || discount >= 1) {
                status.setText("Discount must be between 0.01 and 0.99");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
                return;
            }

            if (expiryText.isEmpty())
            {
                status.setText("Expiry date cannot be empty!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
                return;
            }
            if (!expiryText.matches("\\d{4}-\\d{2}-\\d{2}")) {
                status.setText("Expiry date must be in YYYY-MM-DD format!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
                return;
            }
            try {
                LocalDate expiryDate = LocalDate.parse(expiryText);
                if (!expiryDate.isAfter(LocalDate.now())) {
                    status.setText("Expiry date must be in the future!");
                    status.setTextFill(javafx.scene.paint.Color.RED);
                    clearMessageAfterDelay(status);
                    return;
                }
            } catch (DateTimeParseException ex) {
                status.setText("Invalid expiry date format!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
                return;
            }

            int usageLimit;
            try {
                usageLimit = Integer.parseInt(usageLimitField.getText().trim());
                if (usageLimit <= 0) {
                    status.setText("Usage limit must be a positive integer!");
                    status.setTextFill(javafx.scene.paint.Color.RED);
                    clearMessageAfterDelay(status);
                    return;
                }
            } catch (NumberFormatException ex) {
                status.setText("Usage limit must be a valid integer!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
                return;
            }

            // Save coupon
            try {
                CouponStorage.addCoupon(code, discount,expiryText, usageLimit);
                Session.getMenuClient().sendCouponUpdate();
                status.setText("Coupon added successfully!");
                status.setTextFill(javafx.scene.paint.Color.GREEN);
                clearMessageAfterDelay(status);
            } catch (Exception ex) {
                status.setText("Error adding coupon!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
            }
        });

        VBox box = new VBox(12, codeField, discountField,expiryField,usageLimitField, saveBtn, status);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        // --- Bluish background ---
        BackgroundFill bgFill = new BackgroundFill(
                javafx.scene.paint.Color.web("#E3F2FD"), CornerRadii.EMPTY, Insets.EMPTY
        );
        box.setBackground(new Background(bgFill));

        popup.setScene(new Scene(box, 340, 220));
        popup.showAndWait();
        onSuccess.run();
    }
        // --- Helper method ---
        private static void clearMessageAfterDelay(Label status) {
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(evt -> status.setText(""));
            delay.play();
        }
    }



