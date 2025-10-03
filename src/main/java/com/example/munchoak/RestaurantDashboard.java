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
    private boolean compact = false; // false = full (text + icon), true = compact (icons only)
    private final List<Button> menuButtons = new ArrayList<>();
    private Button hamburgerBtn;

    // Current logged-in user (demo single user)
    private final User currentUser = new User(1, "user"); // can fetch from Users table later

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        // Sidebar (left)
        sidebar = new VBox(8);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        // Hamburger (3-bar) button at top
        hamburgerBtn = new Button("☰");
        hamburgerBtn.setFont(Font.font(18));
        hamburgerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        hamburgerBtn.setOnAction(e -> toggleCompact());

        HBox topBox = new HBox(hamburgerBtn);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(6, 0, 10, 6));

        // Create menu buttons (icon + label)
        Button homeBtn = createMenuButton("🏠", "Home");
        Button ordersBtn = createMenuButton("🧾", "Orders");
        Button menuBtn   = createMenuButton("🍔", "Menu");
        Button reservationsBtn = createMenuButton("📅", "Reservations");
        Button reportsBtn = createMenuButton("📊", "Reports");
        Button aboutBtn = createMenuButton("ℹ️", "About Us");

        menuButtons.addAll(Arrays.asList(homeBtn, ordersBtn, menuBtn, reservationsBtn, reportsBtn, aboutBtn));

        // Put hamburger + menu buttons in sidebar
        sidebar.getChildren().add(topBox);
        sidebar.getChildren().addAll(menuButtons);

        // Content area in center
        contentPane = new StackPane();
        contentPane.setPadding(new Insets(20));
        showHomePage(); // default view

        // Wire up actions
        homeBtn.setOnAction(e -> showHomePage());
        ordersBtn.setOnAction(e -> updateContentSimple("🧾 Orders", "List and manage current orders."));
        menuBtn.setOnAction(e -> {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(new MenuPage(currentUser).getView());
        });
        reservationsBtn.setOnAction(e -> updateContentSimple("📅 Reservations", "View and manage reservations."));
        reportsBtn.setOnAction(e -> updateContentSimple("📊 Reports", "Sales and analytics."));
        aboutBtn.setOnAction(e -> updateContentSimple("About Us",
                "MunchOak — we cook with passion. Open daily 10:00 - 23:00."));

        // Layout
        root.setLeft(sidebar);
        root.setCenter(contentPane);

        // Start in full sidebar mode
        setCompact(false);

        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setTitle("Restaurant Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ---------------------- Sidebar / Buttons -----------------------
    private Button createMenuButton(String icon, String labelText) {
        Button btn = new Button(labelText);
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(18));
        btn.setGraphic(iconLabel);
        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setPrefWidth(200);
        btn.setMaxWidth(Double.MAX_VALUE);

        // Styling
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;"));

        btn.setUserData(labelText); // save the full label for toggling
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
                Tooltip t = new Tooltip((String) btn.getUserData());
                Tooltip.install(btn, t);
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

    // ---------------------- Content -----------------------
    private void updateContentSimple(String title, String paragraph) {
        contentPane.getChildren().clear();
        VBox v = new VBox(12);
        v.setPadding(new Insets(8));
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label p = new Label(paragraph);
        p.setWrapText(true);
        p.setStyle("-fx-font-size: 14px;");
        v.getChildren().addAll(t, p);
        contentPane.getChildren().add(v);
    }

    private void showHomePage() {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(new HomePage().getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
