package com.example.view;

import com.example.manager.FileStorage;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

public class ReservationPage {
    private final Stage primaryStage;

    public ReservationPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene getReservationScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #FFDAB9;");

        // NAV + DASHBOARD
        root.setTop(createNavBar());
        root.setLeft(createDashboard());

        // MAIN CONTENT
        VBox contentBox = new VBox();
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setSpacing(0);
        contentBox.setStyle("-fx-background-color: transparent;");

        // IMAGE SECTION
        StackPane imagePane = new StackPane();
        ImageView bgView = createBackgroundImage("/com/example/view/images/reservation-bg.png");
        if (bgView.getImage() != null) {
            bgView.setPreserveRatio(false);
            bgView.fitWidthProperty().bind(imagePane.widthProperty());
            bgView.fitHeightProperty().bind(imagePane.heightProperty());
            imagePane.getChildren().add(bgView);
        }
        imagePane.setPrefHeight(600);
        imagePane.setMaxWidth(Double.MAX_VALUE);

        // EXTENSION SECTION
        VBox extensionSection = new VBox(20);
        extensionSection.setAlignment(Pos.CENTER);
        extensionSection.setPadding(new Insets(60, 20, 100, 20));
        extensionSection.setStyle("""
                    -fx-background-color: linear-gradient(to bottom, #FFDAB9, #FFF5E1);
                    -fx-background-radius: 60 60 0 0;
                    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, -2);
                """);

        Label headline = new Label("Reserve Your Table");
        headline.setStyle("""
                    -fx-font-size: 52px;
                    -fx-text-fill: #001F3F;
                    -fx-font-weight: bold;
                    -fx-font-family: 'Arial Rounded MT Bold';
                """);

        // FULL NAME & PHONE
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setStyle("""
                    -fx-background-color: white;
                    -fx-border-color: #ccc;
                    -fx-border-radius: 10;
                    -fx-background-radius: 10;
                    -fx-font-size: 16px;
                    -fx-padding: 10 15 10 15;
                """);
        nameField.setMaxWidth(350);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setStyle("""
                    -fx-background-color: white;
                    -fx-border-color: #ccc;
                    -fx-border-radius: 10;
                    -fx-background-radius: 10;
                    -fx-font-size: 16px;
                    -fx-padding: 10 15 10 15;
                """);
        phoneField.setMaxWidth(350);

        VBox personalBox = new VBox(15, nameField, phoneField);
        personalBox.setAlignment(Pos.CENTER);

        // GUESTS SPINNER
        Label guestsLabel = new Label("Guests:");
        guestsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #001F3F;");

        Spinner<Integer> guestSpinner = new Spinner<>(1, 20, 1); // min=1, max=20, initial=1
        guestSpinner.setEditable(true);
        guestSpinner.setPrefWidth(100);
        // Match time/date style
        guestSpinner.setStyle("""
                    -fx-background-color: #FFB266;
                    -fx-text-fill: #001F3F;
                    -fx-font-weight: bold;
                    -fx-font-size: 16px;
                    -fx-background-radius: 10;
                    -fx-border-radius: 10;
                    -fx-border-color: transparent;
                    -fx-cursor: hand;
                    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);
                """);

// Style inner text field (the spinner editor)
        guestSpinner.getEditor().setStyle("""
                    -fx-background-color: transparent;
                    -fx-text-fill: #001F3F;
                    -fx-font-weight: bold;
                    -fx-font-size: 16px;
                    -fx-alignment: center;
                """);

        HBox guestsBox = new HBox(10, guestsLabel, guestSpinner);
        guestsBox.setAlignment(Pos.CENTER);


        // DATE PICKER
        Label dateLabel = new Label("Date:");
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #001F3F;");

        DatePicker datePicker = new DatePicker();
        datePicker.setEditable(true); // allow manual typing
        datePicker.setStyle("""
                    -fx-background-color: #FFB266;
                    -fx-text-fill: #001F3F;
                    -fx-font-weight: bold;
                    -fx-font-size: 16px;
                    -fx-background-radius: 10;
                    -fx-border-radius: 10;
                    -fx-padding: 6 15 6 15;
                    -fx-cursor: hand;
                    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);
                """);
        datePicker.setPrefWidth(150);
// TIME SELECTOR
        Label timeLabel = new Label("Time:");
        timeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #001F3F;");

        ComboBox<String> timeBox = new ComboBox<>();
        for (int hour = 10; hour <= 22; hour++) {
            timeBox.getItems().add(String.format("%02d:00", hour));
        }
        timeBox.setPromptText("Select Time");
        timeBox.setStyle("""
                    -fx-background-color: #FFB266;
                    -fx-text-fill: #001F3F;
                    -fx-font-weight: bold;
                    -fx-font-size: 16px;
                    -fx-background-radius: 10;
                    -fx-border-radius: 10;
                    -fx-padding: 6 15 6 15;
                    -fx-cursor: hand;
                    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);
                """);
        timeBox.setPrefWidth(150);

// ROW CONTAINING GUESTS, DATE & TIME
        HBox topRow = new HBox(50,
                guestsBox,
                new HBox(5, dateLabel, datePicker),
                new HBox(5, timeLabel, timeBox)
        );
        topRow.setAlignment(Pos.CENTER);

        //topRow = new HBox(50, guestsBox, new HBox(5, dateLabel, datePicker));
        //topRow.setAlignment(Pos.CENTER);

        // ADDITIONAL REQUESTS
        Label requestLabel = new Label("Additional Requests:");
        requestLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #001F3F;");

        TextArea requestArea = new TextArea();
        requestArea.setPromptText("Any special requests?");
        requestArea.setPrefRowCount(4);
        requestArea.setPrefWidth(400);
        requestArea.setWrapText(true);
        requestArea.setStyle("""
                    -fx-background-color: white;
                    -fx-border-color: #ccc;
                    -fx-border-radius: 0;
                    -fx-background-radius: 0;
                    -fx-font-size: 16px;
                """);


        // BOOK BUTTON
        Button bookButton = new Button("Book Now");
        bookButton.getStyleClass().add("dashboard-button");
        bookButton.setOnAction(e -> {
//            String fullName = nameField.getText();
//            String phone = phoneField.getText();
//            String guests = guestsButton.getText();
//            String date = datePicker.getValue() != null ? datePicker.getValue().toString() : "N/A";
//            String request = requestArea.getText();
//            showAlert("Reservation confirmed!\nFull Name: " + fullName + "\nPhone: " + phone +
//                    "\nGuests: " + guests + "\nDate: " + date + "\nRequests: " + request);
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            int guests = guestSpinner.getValue();
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
                    "Requests: " + requestArea.getText();

            Optional<ButtonType> result = showConfirm("Confirm Reservation", summary);
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean saved = FileStorage.saveReservation(name, phone, guests, date.toString(), time, requestArea.getText());
                if (saved) {
                    showAlert(Alert.AlertType.INFORMATION, "Reservation Confirmed 🎉",
                            "Your table has been successfully reserved!\nWe look forward to serving you.");
                    clearFields(nameField, phoneField, requestArea, datePicker, timeBox, guestSpinner);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to save reservation. Please try again.");
                }
            }

        });

        // FINAL EXTENSION LAYOUT
        VBox inputBox = new VBox(20, personalBox, topRow, requestLabel, requestArea, bookButton);
        inputBox.setAlignment(Pos.CENTER);
        extensionSection.getChildren().addAll(headline, inputBox);
        extensionSection.setOpacity(0);
        extensionSection.setTranslateY(50);

        contentBox.getChildren().addAll(imagePane, extensionSection);

        // Scroll container
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        contentBox.minWidthProperty().bind(scrollPane.widthProperty());
        root.setCenter(scrollPane);

        // Fade animation on scroll
        ChangeListener<Number> scrollListener = (obs, oldVal, newVal) -> {
            double scrollY = newVal.doubleValue();
            if (scrollY > 0.25 && extensionSection.getOpacity() == 0) {
                FadeTransition fade = new FadeTransition(Duration.millis(800), extensionSection);
                fade.setFromValue(0);
                fade.setToValue(1);
                TranslateTransition slide = new TranslateTransition(Duration.millis(800), extensionSection);
                slide.setFromY(50);
                slide.setToY(0);
                fade.play();
                slide.play();
            }
        };
        scrollPane.vvalueProperty().addListener(scrollListener);

        // Scene setup
        Scene scene = new Scene(root, 1200, 800);
        URL cssURL = getClass().getResource("/com/example/view/styles/reservation-style.css");
        if (cssURL != null) scene.getStylesheets().add(cssURL.toExternalForm());

        return scene;
    }

    private Optional<ButtonType> showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    private void clearFields(TextField nameField, TextField phoneField, TextArea requestArea,
                             DatePicker datePicker, ComboBox<String> timeBox, Spinner<Integer> guestSpinner) {
        nameField.clear();
        phoneField.clear();
        requestArea.clear();
        guestSpinner.getValueFactory().setValue(1);
        datePicker.setValue(null);
        timeBox.setValue(null);
    }

    private HBox createNavBar() {
        HBox navBar = new HBox();
        navBar.setPadding(new Insets(15, 30, 15, 30));
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setBackground(new Background(new BackgroundFill(Color.web("#E9967A"), CornerRadii.EMPTY, Insets.EMPTY)));
        navBar.setPrefHeight(70);

        Label title = new Label("MUNCH-OAK");
        title.getStyleClass().add("nav-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        navBar.getChildren().addAll(title, spacer);
        return navBar;
    }

    private VBox createDashboard() {
        VBox dashboard = new VBox(15);
        dashboard.setPadding(new Insets(30));
        dashboard.setPrefWidth(220);
        dashboard.setStyle("-fx-background-color: #F4A460;");

        Button homeBtn = createDashboardButton("HOME");
        Button menuBtn = createDashboardButton("MENU");
        Button profileBtn = createDashboardButton("PROFILE");
        Button aboutBtn = createDashboardButton("ABOUT US");
        Button reviewBtn = createDashboardButton("REVIEW");

        homeBtn.setOnAction(e -> {
            HomePage homePage = new HomePage(primaryStage);
            primaryStage.setScene(homePage.getHomeScene());
        });

        dashboard.getChildren().addAll(homeBtn, menuBtn, profileBtn, aboutBtn, reviewBtn);
        return dashboard;
    }

    private Button createDashboardButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("dashboard-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private ImageView createBackgroundImage(String path) {
        try {
            URL resource = getClass().getResource(path);
            if (resource == null) return new ImageView();
            Image img = new Image(resource.toExternalForm());
            if (img.isError()) return new ImageView();
            return new ImageView(img);
        } catch (Exception e) {
            System.err.println("Error loading background: " + e.getMessage());
            return new ImageView();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
