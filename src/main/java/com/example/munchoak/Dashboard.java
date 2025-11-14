package com.example.munchoak;//package com.example.munchoak;
//
//import com.example.manager.Session;
//import com.example.network.ChatClient;
//import javafx.application.Application;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Node;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.control.ContentDisplay;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.*;
//import javafx.scene.text.Font;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class Dashboard extends Application {
//
//    private BorderPane root;
//    private VBox sidebar;
//    private StackPane contentPane;
//    private boolean compact = false;               // false = full (text + icon), true = compact (icons only)
//    private final List<Button> menuButtons = new ArrayList<>();
//    private Button hamburgerBtn;
//
//    @Override
//    public void start(Stage primaryStage) {
//        root = new BorderPane();
//
//        // Sidebar (left)
//        sidebar = new VBox(8);
//        sidebar.setPadding(new Insets(10));
//        sidebar.setStyle("-fx-background-color: #2c3e50;");
//
//        // Hamburger (3-bar) button at top
//        hamburgerBtn = new Button("☰");
//        hamburgerBtn.setFont(Font.font(18));
//        hamburgerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
//        hamburgerBtn.setOnAction(e -> toggleCompact());
//
//        HBox topBox = new HBox(hamburgerBtn);
//        topBox.setAlignment(Pos.CENTER_LEFT);
//        topBox.setPadding(new Insets(6, 0, 10, 6));
//
//        // Create menu buttons
//        Button homeBtn = createMenuButton("🏠", "Home");
//        Button ordersBtn = createMenuButton("com/example/munchoak/order-icon.jpg", "Orders");
//        Button menuBtn = createMenuButton("🍔", "Menu");
//        Button reservationBtn = createMenuButton("📅", "Reservation");
//        Button reportsBtn = createMenuButton("📊", "Reports");
//        Button historyBtn = createMenuButton("📜", "History");
//        Button loginBtn = createMenuButton("🔐", "Login");
//        Button profileBtn = createMenuButton("👤", "Profile");
//        Button aboutBtn = createMenuButton("/com/example/munchoak/about-us-icon.png", "About Us");
//        Button chatBtn = createMenuButton("/com/example/munchoak/chat-icon.png", "Chat");
//
//        menuButtons.addAll(Arrays.asList(
//                homeBtn, ordersBtn, menuBtn, reservationBtn, reportsBtn, historyBtn, loginBtn, profileBtn, aboutBtn, chatBtn
//        ));
//
//        // Put hamburger + menu buttons in sidebar
//        sidebar.getChildren().add(topBox);
//        sidebar.getChildren().addAll(menuButtons);
//
//        // Content area in center
//        contentPane = new StackPane();
//        contentPane.setPadding(new Insets(20));
//        // showHomePage(); // default
//
//        // Wire up actions
//        // homeBtn.setOnAction(e -> showHomePage());
//        ordersBtn.setOnAction(e -> updateContentSimple("Orders", "List and manage current orders."));
//        menuBtn.setOnAction(e -> {
//            contentPane.getChildren().clear();
//
//            String username = Session.getCurrentUsername();
//            if ("admin".equalsIgnoreCase(username)) {
//                //System.out.println("Admin Logged in");
//                contentPane.getChildren().add(new AdminMenu().getView());
//            } else {
//                //System.out.println("User/Guest Logged in");
//                contentPane.getChildren().add(new UserMenu().getView());
//            }
//
//        });
//
//        reservationBtn.setOnAction(e -> {
//            Reservation reservation = new Reservation();
//            reservation.showReservationWindow((Stage) ((Node) e.getSource()).getScene().getWindow());
//        });
//
//
//        reportsBtn.setOnAction(e -> updateContentSimple("📊 Reports", "Sales and analytics."));
////        aboutBtn.setOnAction(e -> {
////            contentPane.getChildren().setAll(AboutUs.getContent());
////        });
//
//        historyBtn.setOnAction(e -> {
//            contentPane.getChildren().clear();
//            contentPane.getChildren().add(new History().getView());
//        });
//
//        loginBtn.setOnAction(e -> {
//            try {
//                // Get current stage from the clicked button
//                Stage currentStage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
//
//                // Create and show the LoginPage again
//                com.example.view.LoginPage loginPage = new com.example.view.LoginPage(currentStage);
//                Scene loginScene = loginPage.getLoginScene();
//
//                currentStage.setScene(loginScene);
//                currentStage.setTitle("Login");
//                currentStage.show();
//
//            } catch (Exception err) {
//                err.printStackTrace();
//            }
//        });
//
//        profileBtn.setOnAction(e -> {
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/FXMLs/profile.fxml"));
//                Node profileView = loader.load();
//
//                contentPane.getChildren().setAll(profileView);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//
//        chatBtn.setOnAction(e -> {
//            openChatWindow();
//        });
//
//        // Layout
//        root.setLeft(sidebar);
//        root.setCenter(contentPane);
//
//        // Start in full view
//        setCompact(false);
//
//        Scene scene = new Scene(root, 800, 600);
//        primaryStage.setTitle("Restaurant Dashboard");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private Button createMenuButton(String icon, String labelText) {
//        Button btn = new Button(labelText);
//
//        // Check if the icon is an image path or emoji/text
//        if (icon.endsWith(".png") || icon.endsWith(".jpg") || icon.endsWith(".jpeg")) {
//            try {
//                Image image = new Image(getClass().getResourceAsStream(icon.startsWith("/") ? icon : "/" + icon));
//                ImageView imageView = new ImageView(image);
//                imageView.setFitWidth(20);
//                imageView.setFitHeight(20);
//                btn.setGraphic(imageView);
//            } catch (Exception e) {
//                // fallback if image not found
//                Label fallbackLabel = new Label("❓");
//                fallbackLabel.setFont(Font.font(18));
//                btn.setGraphic(fallbackLabel);
//            }
//        } else {
//            Label iconLabel = new Label(icon);
//            iconLabel.setFont(Font.font(18));
//            btn.setGraphic(iconLabel);
//        }
//
//        btn.setContentDisplay(ContentDisplay.LEFT);
//        btn.setPrefWidth(200);
//        btn.setMaxWidth(Double.MAX_VALUE);
//
//        // Styling
//        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;");
//        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14px;"));
//        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;"));
//
//        btn.setUserData(labelText);
//        return btn;
//    }
//
//
//    /**
//     * Toggle between compact and full sidebar
//     */
//    private void toggleCompact() {
//        compact = !compact;
//        setCompact(compact);
//    }
//
//    /**
//     * Apply compact/full mode to sidebar and menu buttons
//     */
//    private void setCompact(boolean compactMode) {
//        if (compactMode) {
//            // icons only
//            sidebar.setPrefWidth(64);
//            for (Button btn : menuButtons) {
//                btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//                btn.setPrefWidth(48);
//                Tooltip t = new Tooltip((String) btn.getUserData());
//                Tooltip.install(btn, t);
//                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
//            }
//            hamburgerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px;");
//        } else {
//            sidebar.setPrefWidth(220);
//            for (Button btn : menuButtons) {
//                btn.setContentDisplay(ContentDisplay.LEFT);
//                btn.setPrefWidth(Double.MAX_VALUE);
//                btn.setTooltip(null);
//                btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;");
//            }
//            hamburgerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px;");
//        }
//    }
//
//    private void updateContentSimple(String title, String paragraph) {
//        contentPane.getChildren().clear();
//        VBox v = new VBox(12);
//        v.setPadding(new Insets(8));
//        Label t = new Label(title);
//        t.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
//        Label p = new Label(paragraph);
//        p.setWrapText(true);
//        p.setStyle("-fx-font-size: 14px;");
//        v.getChildren().addAll(t, p);
//        contentPane.getChildren().add(v);
//    }
//
//    @FXML
//    private void openChatWindow() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/network/ChatWindow.fxml"));
//            Stage chatStage = new Stage();
//            chatStage.setScene(new Scene(loader.load()));
//
//            // Get the controller and set username explicitly
//            ChatClient controller = loader.getController();
//            String username = Session.getCurrentUsername();
//            if (username == null || username.isEmpty()) {
//                username = "Guest";
//            }
//            controller.setUsername(username);  // <-- pass username here
//
//            // Set stage title with username
//            chatStage.setTitle("Chatting as [" + username + "]");
//            chatStage.show();
//
//            chatStage.setOnCloseRequest(e -> {
//                try {
//                    controller.closeConnection();
//                } catch (Exception ignored) {}
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}