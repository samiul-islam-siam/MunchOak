package com.example.munchoak;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantDashboard extends Application {

    private BorderPane root;
    private VBox sidebar;
    private StackPane contentPane;
    private boolean compact = false; // false = full, true = icons only
    private final List<Button> menuButtons = new ArrayList<>();
    private Button hamburgerBtn;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        // Sidebar setup
        sidebar = new VBox(8);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        // Hamburger button
        hamburgerBtn = new Button("☰");
        hamburgerBtn.setFont(Font.font(18));
        hamburgerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        hamburgerBtn.setOnAction(e -> toggleCompact());

        HBox topBox = new HBox(hamburgerBtn);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(6, 0, 10, 6));

        // Sidebar menu buttons
        Button homeBtn = createMenuButton("🏠", "Home");
        Button ordersBtn = createMenuButton("🧾", "Orders");
        Button menuBtn = createMenuButton("🍔", "Menu");
        Button reservationsBtn = createMenuButton("📅", "Reservations");
        Button reportsBtn = createMenuButton("📊", "Reports");
        Button aboutBtn = createMenuButton("ℹ️", "About Us");

        menuButtons.addAll(Arrays.asList(homeBtn, ordersBtn, menuBtn, reservationsBtn, reportsBtn, aboutBtn));
        sidebar.getChildren().add(topBox);
        sidebar.getChildren().addAll(menuButtons);

        // Content pane setup
        contentPane = new StackPane();
        contentPane.setPadding(new Insets(20));
        showHomePage(); // default page

        // Button actions
        homeBtn.setOnAction(e -> showHomePage());
        ordersBtn.setOnAction(e -> updateContentSimple("🧾 Orders", "Manage current orders here."));
        menuBtn.setOnAction(e -> contentPane.getChildren().setAll(new MenuPage().getView()));
        reservationsBtn.setOnAction(e -> updateContentSimple("📅 Reservations", "View and manage reservations."));
        reportsBtn.setOnAction(e -> updateContentSimple("📊 Reports", "View sales and analytics."));
        aboutBtn.setOnAction(e -> updateContentSimple("ℹ️ About Us", "MunchOak – we cook with passion. Open daily 10:00 - 23:00."));

        // Layout
        root.setLeft(sidebar);
        root.setCenter(contentPane);

        // Start in full mode
        setCompact(false);

        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setTitle("Restaurant Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createMenuButton(String icon, String labelText) {
        Button btn = new Button(labelText);
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(18));
        btn.setGraphic(iconLabel);
        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setPrefWidth(200);
        btn.setMaxWidth(Double.MAX_VALUE);

        // Hover effect
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;"));

        btn.setUserData(labelText); // store full label
        return btn;
    }

    private void toggleCompact() {
        compact = !compact;
        setCompact(compact);
    }

    private void setCompact(boolean compactMode) {
        if (compactMode) {
            sidebar.setPrefWidth(64);
            for (Button btn : menuButtons) {
                btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                btn.setPrefWidth(48);
                Tooltip.install(btn, new Tooltip((String) btn.getUserData()));
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            }
            hamburgerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px;");
        } else {
            sidebar.setPrefWidth(220);
            for (Button btn : menuButtons) {
                btn.setContentDisplay(ContentDisplay.LEFT);
                btn.setPrefWidth(Double.MAX_VALUE);
                btn.setTooltip(null);
                btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;");
            }
            hamburgerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px;");
        }
    }

    private void updateContentSimple(String title, String paragraph) {
        VBox v = new VBox(12);
        v.setPadding(new Insets(8));
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label p = new Label(paragraph);
        p.setWrapText(true);
        p.setStyle("-fx-font-size: 14px;");
        v.getChildren().addAll(t, p);
        contentPane.getChildren().setAll(v);
    }

    private void showHomePage() {
        contentPane.getChildren().setAll(new HomePage().getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
