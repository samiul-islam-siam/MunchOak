package com.example.view;

import com.example.menu.MenuPage;
import com.example.munchoak.Cart;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;

public class MessagePage {
    private final Stage primaryStage;
    private final Cart cart;

    public MessagePage(Stage primaryStage, Cart cart) {
        this.primaryStage = primaryStage;
        this.cart = cart;
    }

    public Scene getMessageScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #FFDAB9;");

        // NAV + DASHBOARD
        root.setTop(createNavBar());
        root.setLeft(createDashboard());

        // MAIN CONTENT
        VBox contentBox = new VBox();
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setSpacing(20);
        contentBox.setPadding(new Insets(50));
        contentBox.setStyle("-fx-background-color: transparent;");

        // TITLE
        Label titleLabel = new Label("Messages");
        titleLabel.setStyle("""
                -fx-font-size: 48px;
                -fx-text-fill: #001F3F;
                -fx-font-weight: bold;
                -fx-font-family: 'Arial Rounded MT Bold';
                """);

        // CHAT AREA FOR INCOMING MESSAGES ONLY
        VBox chatArea = new VBox(10);
        chatArea.setPrefWidth(600);
        chatArea.setPrefHeight(400);
        chatArea.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #ccc;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-padding: 10;
                """);

        // Initial placeholder
        Label noMessageLabel = new Label("No messages yet. Send a message to start a conversation.");
        noMessageLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666; -fx-alignment: center;");
        noMessageLabel.setWrapText(true);
        noMessageLabel.setPadding(new Insets(20));
        chatArea.getChildren().add(noMessageLabel);

        // ScrollPane for chat area
        ScrollPane chatScroll = new ScrollPane(chatArea);
        chatScroll.setFitToWidth(true);
        chatScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScroll.setStyle("-fx-background-color: transparent;");

        // INPUT SECTION (always shown)
        HBox inputBox = new HBox(10);
        TextArea messageInput = new TextArea();
        messageInput.setPromptText("Type your message here...");
        messageInput.setPrefRowCount(3);
        messageInput.setPrefColumnCount(50);
        messageInput.setWrapText(true);
        messageInput.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #ccc;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-padding: 10;
                -fx-font-size: 14px;
                """);

        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("dashboard-button");
        sendButton.setPrefWidth(100);
        sendButton.setOnAction(e -> {
            String messageText = messageInput.getText().trim();
            if (!messageText.isEmpty()) {
                // Clear input
                messageInput.clear();

                // Remove placeholder if present
                if (!chatArea.getChildren().isEmpty() && chatArea.getChildren().get(0) instanceof Label && "No messages yet. Send a message to start a conversation.".equals(((Label) chatArea.getChildren().get(0)).getText())) {
                    chatArea.getChildren().remove(0);
                }

                // TODO: Save user message to FileStorage

                // Simulate support response after delay (incoming message only)
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(ev -> {
                    String response = "Support: Thank you for your message. We'll get back to you shortly.";
                    addIncomingMessage(chatArea, response);
                    chatScroll.setVvalue(1.0); // Scroll to bottom
                });
                delay.play();
            }
        });

        inputBox.getChildren().addAll(messageInput, sendButton);
        inputBox.setAlignment(Pos.BOTTOM_CENTER);

        // Overall content layout
        VBox chatContainer = new VBox(20, chatScroll, inputBox);
        chatContainer.setAlignment(Pos.CENTER);

        contentBox.getChildren().addAll(titleLabel, chatContainer);

        root.setCenter(contentBox);

        // Scene setup
        Scene scene = new Scene(root, 1200, 800);
        URL cssURL = getClass().getResource("/com/example/view/styles/reservation-style.css");
        if (cssURL != null) {
            scene.getStylesheets().add(cssURL.toExternalForm());
        }

        return scene;
    }

    private void addIncomingMessage(VBox chatArea, String text) {
        Label msgLabel = new Label("Support: " + text);
        msgLabel.setWrapText(true);
        msgLabel.setPadding(new Insets(10, 15, 10, 15));
        msgLabel.setStyle("""
                -fx-background-color: #E3F2FD;
                -fx-background-radius: 18px;
                -fx-border-radius: 18px;
                -fx-border-color: #BBDEFB;
                -fx-border-width: 1px;
                -fx-text-fill: #001F3F;
                """);

        HBox messageContainer = new HBox(msgLabel);
        messageContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setMargin(messageContainer, new Insets(0, 0, 0, 20)); // Indent from left

        chatArea.getChildren().add(messageContainer);
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
        Button reservationBtn = createDashboardButton("RESERVATIONS");
        Button profileBtn = createDashboardButton("PROFILE");
        Button messageBtn = createDashboardButton("MESSAGES");
        Button aboutBtn = createDashboardButton("ABOUT US");

        homeBtn.setOnAction(e -> {
            double currentWidth = primaryStage.getWidth();
            double currentHeight = primaryStage.getHeight();
            boolean wasFullScreen = primaryStage.isFullScreen();
            boolean wasMaximized = primaryStage.isMaximized();

            HomePage homePage = new HomePage(primaryStage, cart);
            Scene homeScene = homePage.getHomeScene();
            primaryStage.setScene(homeScene);

            Platform.runLater(() -> {
                if (wasFullScreen) primaryStage.setFullScreen(true);
                else if (wasMaximized) primaryStage.setMaximized(true);
                else {
                    primaryStage.setWidth(currentWidth);
                    primaryStage.setHeight(currentHeight);
                }
            });
        });

        menuBtn.setOnAction(e -> {
            double currentWidth = primaryStage.getWidth();
            double currentHeight = primaryStage.getHeight();
            boolean wasFullScreen = primaryStage.isFullScreen();
            boolean wasMaximized = primaryStage.isMaximized();

            MenuPage menuPage = new MenuPage(primaryStage, cart);
            Scene menuScene = menuPage.getMenuScene();
            primaryStage.setScene(menuScene);

            Platform.runLater(() -> {
                if (wasFullScreen) primaryStage.setFullScreen(true);
                else if (wasMaximized) primaryStage.setMaximized(true);
                else {
                    primaryStage.setWidth(currentWidth);
                    primaryStage.setHeight(currentHeight);
                }
            });
        });

        reservationBtn.setOnAction(e -> {
            double currentWidth = primaryStage.getWidth();
            double currentHeight = primaryStage.getHeight();
            boolean wasFullScreen = primaryStage.isFullScreen();
            boolean wasMaximized = primaryStage.isMaximized();

            ReservationPage reservationPage = new ReservationPage(primaryStage, cart);
            Scene reservationScene = reservationPage.getReservationScene();
            primaryStage.setScene(reservationScene);

            Platform.runLater(() -> {
                if (wasFullScreen) primaryStage.setFullScreen(true);
                else if (wasMaximized) primaryStage.setMaximized(true);
                else {
                    primaryStage.setWidth(currentWidth);
                    primaryStage.setHeight(currentHeight);
                }
            });
        });

        profileBtn.setOnAction(e -> {
            Scene currentScene = primaryStage.getScene();
            ProfilePage profilePage = new ProfilePage(primaryStage, currentScene);
            primaryStage.setScene(profilePage.getProfileScene());
        });

        messageBtn.setOnAction(e -> {
            // Already on messages page, optional refresh or no-op
            double currentWidth = primaryStage.getWidth();
            double currentHeight = primaryStage.getHeight();
            boolean wasFullScreen = primaryStage.isFullScreen();
            boolean wasMaximized = primaryStage.isMaximized();

            MessagePage messagePage = new MessagePage(primaryStage, cart);
            Scene messageScene = messagePage.getMessageScene();
            primaryStage.setScene(messageScene);

            Platform.runLater(() -> {
                if (wasFullScreen) primaryStage.setFullScreen(true);
                else if (wasMaximized) primaryStage.setMaximized(true);
                else {
                    primaryStage.setWidth(currentWidth);
                    primaryStage.setHeight(currentHeight);
                }
            });
        });

        aboutBtn.setOnAction(e -> {
            double currentWidth = primaryStage.getWidth();
            double currentHeight = primaryStage.getHeight();
            boolean wasFullScreen = primaryStage.isFullScreen();
            boolean wasMaximized = primaryStage.isMaximized();

            AboutUsPage aboutPage = new AboutUsPage(primaryStage, cart);
            aboutPage.showAboutUs();

            Platform.runLater(() -> {
                if (wasFullScreen) primaryStage.setFullScreen(true);
                else if (wasMaximized) primaryStage.setMaximized(true);
                else {
                    primaryStage.setWidth(currentWidth);
                    primaryStage.setHeight(currentHeight);
                }
            });
        });

        dashboard.getChildren().addAll(homeBtn, menuBtn, reservationBtn, profileBtn, messageBtn, aboutBtn);
        return dashboard;
    }

    private Button createDashboardButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("dashboard-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }
}