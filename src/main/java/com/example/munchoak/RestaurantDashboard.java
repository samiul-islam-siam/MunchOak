package com.example.munchoak;

import com.example.login.WelcomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
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
    private boolean compact = false;               // false = full (text + icon), true = compact (icons only)
    private final List<Button> menuButtons = new ArrayList<>();
    private Button hamburgerBtn;

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

        // Create menu buttons
        Button homeBtn = createMenuButton("🏠", "Home");
        Button ordersBtn = createMenuButton("🧾", "Orders");
        Button menuBtn   = createMenuButton("🍔", "Menu");
        Button reservationBtn = createMenuButton("📅", "Reservation");
        Button reportsBtn = createMenuButton("📊", "Reports");
        Button aboutBtn = createMenuButton("ℹ️", "About Us");
        Button historyBtn = createMenuButton("📜", "History");
        Button loginBtn = createMenuButton("🔐", "Login");

        menuButtons.addAll(Arrays.asList(
                homeBtn, ordersBtn, menuBtn, reservationBtn, reportsBtn, aboutBtn, historyBtn, loginBtn
        ));

        // Put hamburger + menu buttons in sidebar
        sidebar.getChildren().add(topBox);
        sidebar.getChildren().addAll(menuButtons);

        // Content area in center
        contentPane = new StackPane();
        contentPane.setPadding(new Insets(20));
        showHomePage(); // default

        // Wire up actions
        homeBtn.setOnAction(e -> showHomePage());
        ordersBtn.setOnAction(e -> updateContentSimple("🧾 Orders", "List and manage current orders."));
        menuBtn.setOnAction(e -> {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(new MenuPage().getView());
        });

        reservationBtn.setOnAction(e -> {
            Reservation reservation = new Reservation();
            reservation.showReservationWindow((Stage) ((Node) e.getSource()).getScene().getWindow());
        });


        reportsBtn.setOnAction(e -> updateContentSimple("📊 Reports", "Sales and analytics."));
        aboutBtn.setOnAction(e -> updateContentSimple("About Us",
                "MunchOak — we cook with passion. Open daily 10:00 - 23:00."));

        historyBtn.setOnAction(e -> {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(new HistoryPage().getView());
        });

        loginBtn.setOnAction(e -> {
            try {
                javafx.fxml.FXMLLoader loader =
                        new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/login/welcome.fxml"));
                javafx.scene.Parent welcomeRoot = loader.load();

                // Get controller from FXML
                com.example.login.WelcomeController controller = loader.getController();

                // Call a custom method to modify it
                controller.openedFromDashboard();

                // Switch to Welcome page (same stage)
                javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(welcomeRoot));
                stage.setTitle("Welcome");
                stage.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Layout
        root.setLeft(sidebar);
        root.setCenter(contentPane);

        // Start in full view
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

        // Styling
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;"));

        btn.setUserData(labelText); // save the full label for toggling
        return btn;
    }

    /** Toggle between compact and full sidebar */
    private void toggleCompact() {
        compact = !compact;
        setCompact(compact);
    }

    /** Apply compact/full mode to sidebar and menu buttons */
    private void setCompact(boolean compactMode) {
        if (compactMode) {
            // icons only
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
