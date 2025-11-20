package com.example.view;

import com.example.manager.Session;
import com.example.menu.MenuPage;
import com.example.munchoak.History;
import com.example.network.ChatClient;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class HomePage implements HomePageComponent {
    private final StackPane root;
    private final List<HBox> heroGroups = new ArrayList<>();
    private final List<HomePageComponent> sections;
    private final Stage primaryStage;
    private final double WIDTH = 1000;
    private final double HEIGHT = 700;

    // Side Panel
    private VBox sidePanel;
    private Pane overlay;
    private boolean panelOpen = false;
    private boolean loggedIn = false;
    private boolean navigatingAway = false;

    private BorderPane content;
    private ScrollPane scrollPane;
    private HBox heroSection;
    private int currentIndex = 0;

    public HomePage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // === HAMBURGER ICON ===
        Image hamburgerImg = new Image(getClass().getResource("/com/example/view/images/hamburger.png").toExternalForm());
        ImageView hamburgerIcon = new ImageView(hamburgerImg);
        hamburgerIcon.setFitWidth(24);
        hamburgerIcon.setFitHeight(24);
        hamburgerIcon.setPreserveRatio(true);

        Button menuIconBtn = new Button();
        menuIconBtn.setGraphic(hamburgerIcon);
        menuIconBtn.setPrefSize(40, 40);
        menuIconBtn.getStyleClass().addAll("top-button", "menu-icon-button");
        menuIconBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        loggedIn = (Session.getCurrentUsername() != null &&
                !Session.getCurrentUsername().equals("guest")) &&
                (Session.getCurrentEmail() != null &&
                        !Session.getCurrentEmail().isEmpty());

        // === Top Buttons ===
        Button authBtn = new Button(loggedIn ? "Log Out" : "Log In");
        authBtn.getStyleClass().addAll("top-button", "login-button");

        Button menuBtn = new Button("MENU");
        menuBtn.getStyleClass().add("top-button");
        menuBtn.setOnAction(e -> openMenu());

        Button reservationBtn = new Button("Reservation");
        reservationBtn.getStyleClass().add("top-button");
        reservationBtn.setOnAction(e -> openReservationPageDirectly());

        // === Logo & Title ===
        Image logoImg = new Image(getClass().getResource("/com/example/view/images/logo.png").toExternalForm());
        ImageView logoView = new ImageView(logoImg);
        logoView.setFitWidth(40);
        logoView.setFitHeight(40);
        logoView.setPreserveRatio(true);
        logoView.setClip(new Circle(20, 20, 20));

        Label title = new Label("MUNCHOAK");
        title.getStyleClass().add("nav-title");
        title.setStyle("-fx-font-family: 'Georgia', serif; -fx-font-weight: bold; -fx-font-size: 24px;");

        HBox leftPart = new HBox(10, logoView, title);
        leftPart.setAlignment(Pos.CENTER_LEFT);

        HBox rightButtons = new HBox(10, menuBtn, reservationBtn, authBtn, menuIconBtn);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);

        BorderPane navBar = new BorderPane();
        navBar.setLeft(leftPart);
        navBar.setRight(rightButtons);
        navBar.getStyleClass().add("home-nav");
        navBar.setPadding(new Insets(5, 20, 5, 20));
        navBar.setStyle("-fx-background-color: transparent;");

        content = new BorderPane();
        content.setTop(navBar);
        content.setBackground(Background.EMPTY);

        root = new StackPane();
        root.prefWidthProperty().bind(primaryStage.widthProperty());
        root.prefHeightProperty().bind(primaryStage.heightProperty());

        authBtn.setOnAction(e ->
        {
            if (loggedIn) {
                Session.logout();
                primaryStage.setScene(new LoginPage(primaryStage).getLoginScene());
            } else {
                openLoginPageDirectly();
            }
        });
        menuIconBtn.setOnAction(e -> toggleSidePanel());

        createOverlay();
        createSidePanel();
        root.getChildren().setAll(content, overlay, sidePanel);

        this.sections = Arrays.asList(
                this,
                new HomePageThirdExtension(),
                new HomePageFourthExtension(),
                new HomePageFifthExtension(),
                new HomePageSixthExtension(primaryStage),
                new HomePageSeventhExtension(),
                new HomePageEighthExtension()
        );
        initialize();
    }

    @Override
    public Region getRoot() {
        return root;
    }

    @Override
    public double getPrefWidth() {
        return WIDTH;
    }

    @Override
    public double getPrefHeight() {
        return HEIGHT;
    }

    @Override
    public void initialize() {
        createHeroGroups();
        for (HBox group : heroGroups) {
            group.prefWidthProperty().bind(content.widthProperty());
        }

        ChangeListener<Number> widthListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
                if (newVal.doubleValue() > 0) {
                    content.widthProperty().removeListener(this);
                    currentIndex = 0;
                    heroSection = heroGroups.get(0);
                    content.setCenter(heroSection);
                    heroSection.setOpacity(0.0);
                    performHeroTransition(heroSection);
                    startSlideshow();
                }
            }
        };
        content.widthProperty().addListener(widthListener);
    }

    private void startSlideshow() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), e -> nextSlide()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void nextSlide() {
        int next = (currentIndex + 1) % heroGroups.size();
        HBox nextGroup = heroGroups.get(next);
        HBox current = heroSection;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), current);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            content.setCenter(nextGroup);
            ImageView nextImg = (ImageView) nextGroup.getChildren().get(0);
            VBox nextTxt = (VBox) nextGroup.getChildren().get(1);
            nextImg.setTranslateX(0);
            nextTxt.setTranslateX(0);
            performHeroTransition(nextGroup);
            currentIndex = next;
            heroSection = nextGroup;
        });
        fadeOut.play();
    }

    private void performHeroTransition(HBox group) {
        ImageView img = (ImageView) group.getChildren().get(0);
        VBox txt = (VBox) group.getChildren().get(1);
        double halfWidth = group.getWidth() / 2;
        img.setTranslateX(-halfWidth);
        txt.setTranslateX(halfWidth);
        group.setOpacity(0.0);

        TranslateTransition leftTrans = new TranslateTransition(Duration.millis(400), img);
        leftTrans.setToX(0);

        TranslateTransition rightTrans = new TranslateTransition(Duration.millis(400), txt);
        rightTrans.setToX(0);

        FadeTransition ft = new FadeTransition(Duration.millis(400), group);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        ParallelTransition parallel = new ParallelTransition(leftTrans, rightTrans, ft);
        parallel.play();
    }

    private HBox createHeroGroup(String imagePath, String title, String para) {
        HBox group = new HBox(30);
        group.setMinHeight(600);
        group.setAlignment(Pos.CENTER);
        group.setOpacity(0.0);

        Image img = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.fitWidthProperty().bind(Bindings.divide(group.widthProperty(), 2));
        iv.fitHeightProperty().bind(group.heightProperty());

        VBox textBox = new VBox(20);
        textBox.setAlignment(Pos.CENTER_LEFT);
        textBox.setPadding(new Insets(50, 50, 50, 100));
        textBox.prefWidthProperty().bind(Bindings.divide(group.widthProperty(), 2));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-font-family: 'Brush Script MT', cursive; -fx-text-fill: white; -fx-padding: 0 0 10 0;");

        Label paraLabel = new Label(para);
        paraLabel.setWrapText(true);
        paraLabel.setStyle("-fx-font-size: 18px; -fx-font-family: 'Georgia', serif; -fx-text-fill: white; -fx-padding: 0 0 20 0;");

        Button bookBtn = new Button("Book your table now!");
        bookBtn.getStyleClass().addAll("top-button", "login-button");
        bookBtn.setOnAction(e -> openReservationPageDirectly());

        textBox.getChildren().addAll(titleLabel, paraLabel, bookBtn);
        group.getChildren().addAll(iv, textBox);
        return group;
    }

    private void createHeroGroups() {
        heroGroups.add(createHeroGroup("/com/example/view/images/bg2.png",
                "Savor the MAGIC!!",
                "Join us for a culinary journey with our master chefs, crafting every dish with passion and flair. Fresh ingredients, bold flavors, and an unforgettable dining experience await at Munch-Oak."));

        heroGroups.add(createHeroGroup("/com/example/view/images/bg3.png",
                "Indulge in Bold Flavors",
                "Our menu features a delightful array of dishes, each prepared with the finest ingredients sourced locally. From appetizers to desserts, every bite is a testament to our commitment to excellence."));

        heroGroups.add(createHeroGroup("/com/example/view/images/bg4.png",
                "Join Our Community",
                "Our team of passionate chefs brings years of experience to create memorable meals. Let us take you on a gastronomic adventure you'll never forget."));
    }

    public VBox getFullPage() {
        VBox fullPage = new VBox();
        Stop[] stops = new Stop[]{new Stop(0, Color.web("#49bad8")), new Stop(1, Color.web("#1c71bd"))};
        LinearGradient lg = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        fullPage.setBackground(new Background(new BackgroundFill(lg, CornerRadii.EMPTY, Insets.EMPTY)));
        fullPage.setSpacing(0);

        for (HomePageComponent section : sections) {
            Region sectionRoot = section.getRoot();
            sectionRoot.prefWidthProperty().bind(primaryStage.widthProperty());
            sectionRoot.prefHeightProperty().bind(primaryStage.heightProperty());
            fullPage.getChildren().add(sectionRoot);
            if (section != this) section.initialize();
        }
        return fullPage;
    }

    private void createOverlay() {
        overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.setVisible(false);
        overlay.setOnMouseClicked(e -> {
            if (panelOpen) {
                navigatingAway = false;
                toggleSidePanel();
                e.consume();
            }
        });
        StackPane.setAlignment(overlay, Pos.TOP_LEFT);
    }

    private void createSidePanel() {
        // FIXED CLOSE BUTTON - Now consumes event properly
        Button closeBtn = new Button("X");
        closeBtn.getStyleClass().addAll("top-button", "login-button");
        closeBtn.setPrefSize(30, 30);
        closeBtn.setOnAction(e -> {
            e.consume();                    // ← THIS FIXES THE JUMP TO 5TH SCREEN
            navigatingAway = false;
            toggleSidePanel();
        });

        Button profileBtn = createSideButton("Profile");
        Button historyBtn = createSideButton("History");
        Button cartBtn = createSideButton("Cart");
        Button reserveBtn = createSideButton("Reservation");
        Button chatBtn = createSideButton("Chat");
        Button aboutBtn = createSideButton("About Us");

        Consumer<Runnable> navigateAndClose = action -> {
            navigatingAway = true;
            toggleSidePanel();
            PauseTransition pause = new PauseTransition(Duration.millis(50));
            pause.setOnFinished(evt -> action.run());
            pause.play();
        };

        profileBtn.setOnAction(e -> navigateAndClose.accept(this::openProfilePageDirectly));
        historyBtn.setOnAction(e -> navigateAndClose.accept(this::openHistoryPageDirectly));
        reserveBtn.setOnAction(e -> navigateAndClose.accept(this::openReservationPageDirectly));
        aboutBtn.setOnAction(e -> navigateAndClose.accept(this::openAboutUsPageDirectly));
        chatBtn.setOnAction(e -> openChatWindow());

        Button authBtn = new Button(loggedIn ? "Log Out" : "Log In");
        authBtn.setPrefWidth(Double.MAX_VALUE);
        authBtn.getStyleClass().add("top-button");
        if (!loggedIn) authBtn.getStyleClass().add("login-button");

        authBtn.setOnAction(e -> {
            if (loggedIn) {
                loggedIn = false;
                authBtn.setText("Log In");
                authBtn.getStyleClass().remove("login-button");
                navigatingAway = true;
                toggleSidePanel();
                System.out.println("Logged out");
            } else {
                navigateAndClose.accept(this::openLoginPageDirectly);
            }
        });

        VBox items = new VBox(18, profileBtn, cartBtn, reserveBtn, historyBtn, chatBtn, aboutBtn, authBtn);
        items.setAlignment(Pos.CENTER_LEFT);
        items.setFillWidth(true);

        HBox closeContainer = new HBox(closeBtn);
        closeContainer.setAlignment(Pos.TOP_RIGHT);
        closeContainer.setPrefHeight(30);

        VBox panelContent = new VBox(25, closeContainer, items);
        panelContent.setPadding(new Insets(30, 25, 30, 25));
        panelContent.setAlignment(Pos.TOP_LEFT);
        panelContent.setPrefWidth(280);

        sidePanel = new VBox(panelContent);
        sidePanel.setPrefWidth(280);
        sidePanel.setMaxWidth(280);
        sidePanel.getStyleClass().add("side-panel");
        sidePanel.setTranslateX(280);
        sidePanel.setVisible(false);
        sidePanel.setFillWidth(true);
        StackPane.setAlignment(sidePanel, Pos.CENTER_RIGHT);
    }

    private Button createSideButton(String text) {
        Button b = new Button(text);
        b.setPrefWidth(Double.MAX_VALUE);
        b.getStyleClass().add("top-button");
        return b;
    }

    private void toggleSidePanel() {
        if (panelOpen) slideOut();
        else slideIn();
        panelOpen = !panelOpen;
    }

    private void slideIn() {
        sidePanel.setVisible(true);
        overlay.setVisible(true);
        sidePanel.setTranslateX(280);
        content.setMouseTransparent(true);
        overlay.toFront();
        sidePanel.toFront();

        TranslateTransition tt = new TranslateTransition(Duration.millis(300), sidePanel);
        tt.setToX(0);
        tt.play();
    }

    private void slideOut() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), sidePanel);
        tt.setToX(280);
        tt.setOnFinished(e -> {
            sidePanel.setVisible(false);
            overlay.setVisible(false);
            content.setMouseTransparent(false);
            if (!navigatingAway && scrollPane != null) {
                scrollPane.setVvalue(0.0);
            }
            navigatingAway = false;
        });
        tt.play();
    }

    public Scene getHomeScene() {
        VBox fullPage = getFullPage();
        scrollPane = new ScrollPane(fullPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0));
        scrollPane.prefHeightProperty().bind(primaryStage.heightProperty());

        double stageWidth = primaryStage.getWidth() > 0 ? primaryStage.getWidth() : WIDTH;
        double stageHeight = primaryStage.getHeight() > 0 ? primaryStage.getHeight() : HEIGHT;
        Scene scene = new Scene(scrollPane, stageWidth, stageHeight);

        scene.getStylesheets().clear();
        var css = getClass().getResource("/com/example/view/styles/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println("CSS not found!");
            scene.getStylesheets().add(Application.STYLESHEET_MODENA);
        }
        return scene;
    }

    private void openMenu() {
        MenuPage menuPage = new MenuPage(primaryStage);
        primaryStage.setScene(menuPage.getMenuScene());
    }

    @FXML
    public void openChatWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/network/ChatWindow.fxml"));
            Stage chatStage = new Stage();
            chatStage.setScene(new Scene(loader.load()));

            ChatClient controller = loader.getController();
            String username = Session.getCurrentUsername();
            if (username == null || username.isEmpty()) username = "Guest";
            String role = Session.getCurrentRole();
            boolean isAdmin = "ADMIN".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(username);

            controller.setUsername(username);
            controller.setAdmin(isAdmin);
            chatStage.setTitle("Chatting as [" + username + "]");
            chatStage.show();
            chatStage.setOnCloseRequest(e -> controller.closeConnection());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void preserveStageState(Scene newScene) {
        double w = primaryStage.getWidth();
        double h = primaryStage.getHeight();
        boolean fs = primaryStage.isFullScreen();
        boolean max = primaryStage.isMaximized();

        primaryStage.setScene(newScene);
        primaryStage.setWidth(w);
        primaryStage.setHeight(h);
        if (fs) primaryStage.setFullScreen(true);
        else if (max) primaryStage.setMaximized(true);
        primaryStage.centerOnScreen();
    }

    private void openLoginPageDirectly() {
        Platform.runLater(() -> preserveStageState(new LoginPage(primaryStage).getLoginScene()));
    }

    private void openReservationPageDirectly() {
        Platform.runLater(() -> preserveStageState(new ReservationPage(primaryStage).getReservationScene()));
    }

    private void openAboutUsPageDirectly() {
        Platform.runLater(() -> preserveStageState(new AboutUsPage(primaryStage).getAboutUsScene()));
    }

    private void openHistoryPageDirectly() {
        Platform.runLater(() -> preserveStageState(new History(primaryStage).getScene()));
    }

    private void openProfilePageDirectly() {
        Platform.runLater(() -> preserveStageState(new ProfilePage(primaryStage).getProfileScene()));
    }
}