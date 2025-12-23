package com.example.view;

import com.example.manager.CouponStorage;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import java.util.List;

public class EditCouponPopup {

    public static void show(Stage owner, Runnable onSuccess) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Edit Coupon");

        // Load existing coupons

        List<CouponStorage.Coupon> coupons = CouponStorage.loadCoupons();

        ComboBox<String> couponDropdown = new ComboBox<>();
        for (CouponStorage.Coupon c : coupons) {
            couponDropdown.getItems().add(c.code);
        }
        couponDropdown.setPromptText("Select Coupon");

        TextField discountField = new TextField();
        discountField.setPromptText("New Discount (e.g. 0.15 for 15%)");
        TextField expiryField = new TextField();
        expiryField.setPromptText("New Expiry Date (e.g. 2025-12-31)");
        TextField usageLimitField = new TextField();
        usageLimitField.setPromptText("New Usage Limit (positive integer)");

        Label status = new Label();
        status.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        Button saveBtn = new Button("Update");
        saveBtn.setStyle("-fx-background-color: #1b4fa8; -fx-text-fill: white; -fx-font-weight: bold;");
        saveBtn.setDefaultButton(true); // ✅ Enter key will trigger this button

        saveBtn.setOnAction(e -> {
            String selectedCode = couponDropdown.getValue();
            String discountText = discountField.getText().trim();
            String expiryText = expiryField.getText().trim();
            String usageText = usageLimitField.getText().trim();
            if (selectedCode == null || selectedCode.isEmpty()) {
                status.setText("Please select a coupon!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
                return;
            }

            // double newDiscount;
            Double newDiscount = null;
            String newExpiry = null;
            Integer newUsageLimit = null;
            if (!discountText.isEmpty()) {
            try {
                // newDiscount = Double.parseDouble(discountText);
                double d = Double.parseDouble(discountText);
                if (d <= 0 || d >= 1) {
                    status.setText("Discount must be between 0.01 and 0.99");
                    status.setTextFill(javafx.scene.paint.Color.RED);
                    clearMessageAfterDelay(status);
                    return;
                }
                newDiscount = d;
            } catch (NumberFormatException ex) {
                status.setText("Invalid discount format!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
                return;
            }
        }

            if (!expiryText.isEmpty()) {
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
            }
           // int newUsageLimit;
            if (!usageText.isEmpty()) {

                try {
                    //newUsageLimit = Integer.parseInt(usageLimitField.getText().trim());
                    int u = Integer.parseInt(usageText);
                    if (u <= 0) {
                        status.setText("Usage limit must be a positive integer!");
                        status.setTextFill(javafx.scene.paint.Color.RED);
                        clearMessageAfterDelay(status);
                        return;
                    }
                    newUsageLimit = u;
                } catch (NumberFormatException ex) {
                    status.setText("Usage limit must be a valid integer!");
                    status.setTextFill(javafx.scene.paint.Color.RED);
                    clearMessageAfterDelay(status);
                    return;
                }
            }
            try {
                CouponStorage.editCoupon(selectedCode, newDiscount,newExpiry,newUsageLimit);
                status.setText("Coupon updated successfully!");
                status.setTextFill(javafx.scene.paint.Color.GREEN);
                clearMessageAfterDelay(status);
            } catch (Exception ex) {
                status.setText("Error updating coupon!");
                status.setTextFill(javafx.scene.paint.Color.RED);
                clearMessageAfterDelay(status);
            }
        });

        VBox box = new VBox(12, couponDropdown, discountField, expiryField,usageLimitField, saveBtn, status);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setMaxWidth(300);

        BackgroundFill bgFill = new BackgroundFill(
                javafx.scene.paint.Color.web("#E0F7FA"), CornerRadii.EMPTY, Insets.EMPTY
        );
        box.setBackground(new Background(bgFill));

        popup.setScene(new Scene(box, 340, 240));
        popup.showAndWait();
        onSuccess.run();

    }
        private static void clearMessageAfterDelay(Label status) {
            PauseTransition delay = new PauseTransition(Duration.seconds(2)); // 2 seconds
            delay.setOnFinished(evt -> status.setText(""));
            delay.play();
        }
    }

