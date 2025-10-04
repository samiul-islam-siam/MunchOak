package com.example.munchoak;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class Reservation {

    public void showReservationWindow(Stage stage) {
        stage.setTitle("Table Reservation - MunchOak");

        // --- Code for Background Image ---
//        BackgroundImage bgImage = new BackgroundImage(
//                new Image(getClass().getResource("/images/reservation_bg.jpg").toExternalForm(), 800, 600, false, true),
//                BackgroundRepeat.NO_REPEAT,
//                BackgroundRepeat.NO_REPEAT,
//                BackgroundPosition.CENTER,
//                BackgroundSize.DEFAULT
//        );

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

//        root.setBackground(new Background(bgImage));

        Label titleLabel = new Label("Reserve Your Table 🍽️");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: blue;");

        // --- Form Fields ---
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        Spinner<Integer> guestsSpinner = new Spinner<>(1, 20, 2);
        guestsSpinner.setEditable(true);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPromptText("Select Date");

        ComboBox<String> timeBox = new ComboBox<>();
        for (int hour = 10; hour <= 22; hour++) {
            timeBox.getItems().add(String.format("%02d:00", hour));
        }
        timeBox.setPromptText("Select Time");

        TextArea specialRequest = new TextArea();
        specialRequest.setPromptText("Special requests (optional)");
        specialRequest.setPrefRowCount(3);

        Button reserveButton = new Button("Reserve Now");
        reserveButton.setStyle("-fx-background-color: #ff8800; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        reserveButton.setPrefWidth(200);

        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-underline: true;");

        // --- Layout ---
        VBox formBox = new VBox(12,
                nameField,
                phoneField,
                new HBox(10, new Label("Guests:"), guestsSpinner),
                datePicker,
                timeBox,
                specialRequest,
                reserveButton,
                backButton
        );
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(350);

        root.getChildren().addAll(titleLabel, formBox);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();

        // --- Button Actions ---
        reserveButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            Integer guests = guestsSpinner.getValue();
            LocalDate date = datePicker.getValue();
            String time = timeBox.getValue();

            if (name.isEmpty() || phone.isEmpty() || date == null || time == null) {
                showAlert(Alert.AlertType.WARNING, "Incomplete Information", "Please fill in all required fields.");
                return;
            }

            String summary = "Reservation Summary:\n\n" +
                    "Name: " + name + "\n" +
                    "Phone: " + phone + "\n" +
                    "Guests: " + guests + "\n" +
                    "Date: " + date + "\n" +
                    "Time: " + time + "\n" +
                    "Requests: " + specialRequest.getText();

            Optional<ButtonType> result = showConfirm("Confirm Reservation", summary);
            if (result.isPresent() && result.get() == ButtonType.OK) {
                showAlert(Alert.AlertType.INFORMATION, "Reservation Confirmed 🎉",
                        "Your table has been successfully reserved!\nWe look forward to serving you.");
                // Optionally save data to file or database here
                clearFields(nameField, phoneField, specialRequest, guestsSpinner, datePicker, timeBox);
            }
        });

        backButton.setOnAction(e -> {
            RestaurantDashboard mainMenu = new RestaurantDashboard();
            mainMenu.start(stage);
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    private void clearFields(TextField nameField, TextField phoneField, TextArea requestArea,
                             Spinner<Integer> guestSpinner, DatePicker datePicker, ComboBox<String> timeBox) {
        nameField.clear();
        phoneField.clear();
        requestArea.clear();
        guestSpinner.getValueFactory().setValue(2);
        datePicker.setValue(LocalDate.now());
        timeBox.setValue(null);
    }
}
