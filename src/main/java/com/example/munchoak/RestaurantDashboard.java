package com.example.munchoak;
import java.util.Map;
import java.util.HashMap;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        menuButtons.addAll(Arrays.asList(
                homeBtn, ordersBtn, menuBtn, reservationBtn, reportsBtn, aboutBtn, historyBtn
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
            HistoryPage historyPage = new HistoryPage();
            historyPage.show();
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
    public class HistoryPage {

        public void show() {
            Stage stage = new Stage();
            stage.setTitle("Payment History");

            TableView<PaymentRecord> table = new TableView<>();

            TableColumn<PaymentRecord, Integer> paymentCol = new TableColumn<>("Payment ID");
            paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentId"));

            TableColumn<PaymentRecord, Integer> userCol = new TableColumn<>("User ID");
            userCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

            TableColumn<PaymentRecord, Double> amountCol = new TableColumn<>("Amount");
            amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

            TableColumn<PaymentRecord, String> methodCol = new TableColumn<>("Method");
            methodCol.setCellValueFactory(new PropertyValueFactory<>("method"));

            TableColumn<PaymentRecord, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

            TableColumn<PaymentRecord, Void> billCol = new TableColumn<>("Bill");
            billCol.setCellFactory(col -> new TableCell<>() {
                private final Button btn = new Button("View Bill");

                {
                    btn.setOnAction(e -> {
                        PaymentRecord record = getTableView().getItems().get(getIndex());
                        showBill(record);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            });

            table.getColumns().addAll(paymentCol, userCol, amountCol, methodCol, dateCol, billCol);

            ObservableList<PaymentRecord> data = FXCollections.observableArrayList();

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT Payment_ID, User_ID, TotalAmount, PaymentMethod, PaymentDate FROM PaymentHistory";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    data.add(new PaymentRecord(
                            rs.getInt("Payment_ID"),
                            rs.getInt("User_ID"),
                            rs.getDouble("TotalAmount"),
                            rs.getString("PaymentMethod"),
                            rs.getString("PaymentDate")
                    ));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            table.setItems(data);

            VBox root = new VBox(table);
            Scene scene = new Scene(root, 800, 400);
            stage.setScene(scene);
            stage.show();
        }

        private void showBill(PaymentRecord record) {
            try (Connection conn = DatabaseConnection.getConnection()) {

                // 1. Build Cart object from PaymentItems
                Cart cart = new Cart("dummy"); // temporary userId
                String sqlCart = "SELECT Food_ID, Quantity FROM PaymentItems WHERE Payment_ID = ?";
                PreparedStatement stmtCart = conn.prepareStatement(sqlCart);
                stmtCart.setInt(1, record.getPaymentId());
                ResultSet rsCart = stmtCart.executeQuery();
                while (rsCart.next()) {
                    int foodId = rsCart.getInt("Food_ID");
                    int qty = rsCart.getInt("Quantity");
                    cart.addToCart(foodId, qty);
                }

                // 2. Build foodMap for Bill
                Map<Integer, FoodItems> foodMap = new HashMap<>();
                String sqlFood = "SELECT Food_ID, Food_Name, Price FROM Details";
                PreparedStatement stmtFood = conn.prepareStatement(sqlFood);
                ResultSet rsFood = stmtFood.executeQuery();
                while (rsFood.next()) {
                    int id = rsFood.getInt("Food_ID");
                    String name = rsFood.getString("Food_Name");
                    double price = rsFood.getDouble("Price");
                    foodMap.put(id, new FoodItems(id, name, "", price, 0, "", ""));
                }

                // 3. Build Payment object
                Payment payment = new Payment(record.getAmount()); // true = paid

                // 4. Generate receipt using your Bill class
                Bill bill = new Bill(cart, payment);
                String receiptText = bill.generateReceipt(foodMap);

                // 5. Show in TextArea
                Stage billStage = new Stage();
                billStage.setTitle("Bill Receipt");

                TextArea receiptArea = new TextArea(receiptText);
                receiptArea.setEditable(false);
                receiptArea.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");
                receiptArea.setPrefSize(500, 400);

                VBox vbox = new VBox(receiptArea);
                vbox.setPadding(new Insets(20));

                billStage.setScene(new Scene(vbox));
                billStage.show();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }



        // PaymentRecord class
        public static class PaymentRecord {
            private int paymentId;
            private int userId;
            private double amount;
            private String method;
            private String date;

            public PaymentRecord(int paymentId, int userId, double amount, String method, String date) {
                this.paymentId = paymentId;
                this.userId = userId;
                this.amount = amount;
                this.method = method;
                this.date = date;
            }

            public int getPaymentId() { return paymentId; }
            public int getUserId() { return userId; }
            public double getAmount() { return amount; }
            public String getMethod() { return method; }
            public String getDate() { return date; }
        }
    }


    // Update PaymentRecord class to include userId
    public class PaymentRecord {
        private int paymentId;
        private int userId;
        private double amount;
        private String method;
        private String date;

        public PaymentRecord(int paymentId, int userId, double amount, String method, String date) {
            this.paymentId = paymentId;
            this.userId = userId;
            this.amount = amount;
            this.method = method;
            this.date = date;
        }

        public int getPaymentId() { return paymentId; }
        public int getUserId() { return userId; }
        public double getAmount() { return amount; }
        public String getMethod() { return method; }
        public String getDate() { return date; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

