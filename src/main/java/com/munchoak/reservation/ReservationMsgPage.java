package com.munchoak.reservation;

import com.munchoak.authentication.ProfilePage;
import com.munchoak.cart.Cart;
import com.munchoak.homepage.HomePage;
import com.munchoak.manager.Session;
import com.munchoak.menu.MenuPage;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

public class ReservationMsgPage {
    private final Stage primaryStage;
    private final Cart cart;

    public ReservationMsgPage(Stage primaryStage, Cart cart) {
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
        Label titleLabel = new Label("Notifications");
        titleLabel.setStyle("""
                -fx-font-size: 48px;
                -fx-text-fill: #001F3F;
                -fx-font-weight: bold;
                -fx-font-family: 'Arial Rounded MT Bold';
                """);

        // CHAT AREA FOR INCOMING MESSAGES ONLY
        VBox chatArea = new VBox(10);
        // chatArea.setPrefWidth(800); // Increased horizontally
        // chatArea.setPrefHeight(500); // Increased vertically
        chatArea.setFillWidth(true);
        VBox.setVgrow(chatArea, Priority.ALWAYS);

        chatArea.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #ccc;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-padding: 10;
                """);



        // Initial placeholder
        Label noMessageLabel = new Label("There is no notification yet.");
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
        // allow it to grow
        VBox.setVgrow(chatScroll, Priority.ALWAYS);

        refreshMessages(chatArea, noMessageLabel);
        Session.addMessageListener(() ->
                Platform.runLater(() -> refreshMessages(chatArea, noMessageLabel))
        );

        // Overall content layout
        VBox chatContainer = new VBox(chatScroll);
        chatContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(chatContainer, Priority.ALWAYS);

        contentBox.getChildren().addAll(titleLabel, chatContainer);

        root.setCenter(contentBox);

        // Scene setup
        Scene scene = new Scene(root, 1200, 800);
        URL cssURL = getClass().getResource("/com/munchoak/view/styles/reservation-style.css");
        if (cssURL != null) {
            scene.getStylesheets().add(cssURL.toExternalForm());
        }

        return scene;
    }

    private void addIncomingMessage(VBox chatArea, String text) {
        Label msgLabel = new Label("Support: " + text);
        msgLabel.setWrapText(true);

        // ✅ dynamic width based on chatArea width
        msgLabel.maxWidthProperty().bind(chatArea.widthProperty().subtract(80));

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
        HBox.setMargin(messageContainer, new Insets(0, 0, 0, 20));

        chatArea.getChildren().add(messageContainer);
    }

    private HBox createNavBar() {
        HBox navBar = new HBox();
        navBar.setPadding(new Insets(15, 30, 15, 30));
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setBackground(new Background(new BackgroundFill(Color.web("#E9967A"), CornerRadii.EMPTY, Insets.EMPTY)));
        navBar.setPrefHeight(70);

        Label title = new Label("MunchOak");
        title.getStyleClass().add("nav-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        navBar.getChildren().addAll(title, spacer);
        return navBar;
    }

    private VBox createDashboard() {
        VBox dashboard = new VBox(15);
        dashboard.setPadding(new Insets(30));
        dashboard.setPrefWidth(260);
        dashboard.setStyle("-fx-background-color: #F4A460;");

        Button homeBtn = createDashboardButton("HOME");
        Button menuBtn = createDashboardButton("MENU");
        Button reservationBtn = createDashboardButton("RESERVATIONS");
        Button profileBtn = createDashboardButton("PROFILE");
        Button messageBtn = createDashboardButton("NOTIFICATIONS");
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

            ReservationMsgPage reservationMsgPage = new ReservationMsgPage(primaryStage, cart);
            Scene messageScene = reservationMsgPage.getMessageScene();
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

    private void refreshMessages(VBox chatArea, Label noMessageLabel) {
        int userId = Session.getCurrentUserId();

        chatArea.getChildren().clear();

        List<ReservationMsgStorage.MessageRecord> messages =
                ReservationMsgStorage.loadMessagesForUser(userId);

        if (messages.isEmpty()) {
            chatArea.getChildren().add(noMessageLabel);
        } else {
            for (ReservationMsgStorage.MessageRecord m : messages) {
                addIncomingMessage(chatArea, m.message);
            }
        }
    }

    private Button createDashboardButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("dashboard-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }
}