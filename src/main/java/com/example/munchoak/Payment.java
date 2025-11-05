package com.example.munchoak;
import com.example.manager.FileStorage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.Instant;
import java.util.Map;

public class Payment {
    private int id;
    private double amount;
    private boolean success;
    private String timestamp;

    public Payment(int id, double amount) {
        this.id = id;
        this.amount = amount;
        this.timestamp = Instant.now().toString();
        this.success = false;
    }

    public void processPayment(Cart cart, Map<Integer, FoodItems> foodMap) {
        Stage paymentStage = new Stage();
        paymentStage.setTitle("Payment");

        Label cardLabel = new Label("Card Number:");
        TextField cardField = new TextField();
        cardField.setPromptText("Enter card number");

        Label pinLabel = new Label("PIN:");
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("Enter PIN");

        Button payButton = new Button("Pay");
        payButton.setDefaultButton(true);

        VBox vbox = new VBox(10, cardLabel, cardField, pinLabel, pinField, payButton);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);
        paymentStage.setScene(scene);
        paymentStage.show();

        payButton.setOnAction(e -> {
            if (cardField.getText().trim().isEmpty() || pinField.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please fill all fields!").show();
                return;
            }

            this.success = true;
            paymentStage.close();

            Bill bill = new Bill(cart, this);
            String receipt = bill.generateReceipt(foodMap);

            Stage billStage = new Stage();
            billStage.setTitle("Bill Receipt");

            TextArea receiptArea = new TextArea(receipt);
            receiptArea.setEditable(false);
            receiptArea.setStyle("-fx-font-size: 14px; -fx-font-family: monospace;");
            receiptArea.setPrefSize(500, 400);

            VBox billBox = new VBox(15, receiptArea);
            billBox.setPadding(new Insets(20));
            billBox.setAlignment(Pos.CENTER);

            billStage.setScene(new Scene(billBox));
            billStage.show();

            cart.getBuyHistory().clear(); // empty cart after payment
        });
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public boolean isSuccess() { return success; }
    public String getTimestamp() { return timestamp; }
}
