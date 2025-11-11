package com.example.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HomePageEighthExtension implements HomePageComponent {
    private final AnchorPane extensionRoot;

    public HomePageEighthExtension() {
        extensionRoot = new AnchorPane();
        extensionRoot.setPrefSize(getPrefWidth(), getPrefHeight());
        extensionRoot.setMinSize(getPrefWidth(), getPrefHeight());

        // --- SOLID BLACK BACKGROUND ---
        extensionRoot.setStyle("-fx-background-color: #000000;");

        // -----------------------------------------------------------------
        // 1. Three columns with content
        // -----------------------------------------------------------------
        VBox left   = createContactColumn();
        VBox center = createAddressColumn();
        VBox right  = createHoursColumn();

        HBox.setHgrow(left,   Priority.ALWAYS);
        HBox.setHgrow(center, Priority.ALWAYS);
        HBox.setHgrow(right,  Priority.ALWAYS);

        // -----------------------------------------------------------------
        // 2. Layout: HBox for columns
        // -----------------------------------------------------------------
        HBox columns = new HBox(left, center, right);
        columns.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(columns, 0.0);
        AnchorPane.setBottomAnchor(columns, 100.0); // leave space for footer
        AnchorPane.setLeftAnchor(columns, 0.0);
        AnchorPane.setRightAnchor(columns, 0.0);

        // -----------------------------------------------------------------
        // 3. Responsive vertical white lines (shortened to 85%)
        // -----------------------------------------------------------------
        Line line1 = createVerticalLine();
        Line line2 = createVerticalLine();

        // Bind X positions to 1/3 and 2/3 of root width
        line1.startXProperty().bind(extensionRoot.widthProperty().divide(3));
        line1.endXProperty().bind(extensionRoot.widthProperty().divide(3));
        line2.startXProperty().bind(extensionRoot.widthProperty().multiply(2.0 / 3.0));
        line2.endXProperty().bind(extensionRoot.widthProperty().multiply(2.0 / 3.0));

        // Bind Y positions (10% to 85%) to leave space for footer
        line1.startYProperty().bind(extensionRoot.heightProperty().multiply(0.1));
        line1.endYProperty().bind(extensionRoot.heightProperty().multiply(0.85));
        line2.startYProperty().bind(extensionRoot.heightProperty().multiply(0.1));
        line2.endYProperty().bind(extensionRoot.heightProperty().multiply(0.85));

        // -----------------------------------------------------------------
        // 4. Footer: Text with side lines
        // -----------------------------------------------------------------
        Label munchoakLabel = new Label("Thank You For Visiting MUNCHOAK");
        munchoakLabel.setTextFill(Color.WHITE);
        munchoakLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        munchoakLabel.setAlignment(Pos.CENTER);

        // Left line
        Line leftLine = new Line();
        leftLine.setStroke(Color.WHITE);
        leftLine.setStrokeWidth(1.5);

        // Right line
        Line rightLine = new Line();
        rightLine.setStroke(Color.WHITE);
        rightLine.setStrokeWidth(1.5);

        // Bind line widths dynamically
        leftLine.endXProperty().bind(Bindings.createDoubleBinding(() ->
                        (extensionRoot.getWidth() - computeTextWidth(munchoakLabel) - 60) / 2,
                extensionRoot.widthProperty(), munchoakLabel.widthProperty()));
        rightLine.endXProperty().bind(Bindings.createDoubleBinding(() ->
                        (extensionRoot.getWidth() - computeTextWidth(munchoakLabel) - 60) / 2,
                extensionRoot.widthProperty(), munchoakLabel.widthProperty()));

        // Layout: HBox to place left line, text, right line
        HBox footerBox = new HBox(10, leftLine, munchoakLabel, rightLine);
        footerBox.setAlignment(Pos.CENTER);

        // Anchor at bottom with spacing (moved farther down)
        AnchorPane.setBottomAnchor(footerBox, 10.0);
        AnchorPane.setLeftAnchor(footerBox, 0.0);
        AnchorPane.setRightAnchor(footerBox, 0.0);

        // -----------------------------------------------------------------
        // 5. Add all to root
        // -----------------------------------------------------------------
        extensionRoot.getChildren().addAll(columns, line1, line2, footerBox);

        initialize();
    }

    // Helper: Measure text width
    private double computeTextWidth(Label label) {
        javafx.scene.text.Text text = new javafx.scene.text.Text(label.getText());
        text.setFont(label.getFont());
        return text.getLayoutBounds().getWidth();
    }

    private VBox createContactColumn() {
        Label title = new Label("Contact Us");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label phone = new Label("T. +12 344 0567899");
        phone.setTextFill(Color.WHITE);
        phone.setStyle("-fx-font-size: 18px;");

        Label email = new Label("M. fidalgo@example.com");
        email.setTextFill(Color.WHITE);
        email.setStyle("-fx-font-size: 18px;");

        VBox box = new VBox(8, title, phone, email);
        box.setAlignment(Pos.CENTER); // center all vertically in column
        return box;
    }

    private VBox createAddressColumn() {
        Label title = new Label("Address");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label line1 = new Label("Piazza Della Signoria, 12");
        line1.setTextFill(Color.WHITE);
        line1.setStyle("-fx-font-size: 18px;");

        Label line2 = new Label("21562 . Firenze . Italy");
        line2.setTextFill(Color.WHITE);
        line2.setStyle("-fx-font-size: 18px;");

        VBox box = new VBox(8, title, line1, line2);
        box.setAlignment(Pos.CENTER); // center all vertically in column
        return box;
    }

    private VBox createHoursColumn() {
        Label title = new Label("Opening Hours");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label line1 = new Label("Everyday : From 12.30 To 23.00");
        line1.setTextFill(Color.WHITE);
        line1.setStyle("-fx-font-size: 18px;");

        Label line2 = new Label("Kitchen Closes At 22.00");
        line2.setTextFill(Color.WHITE);
        line2.setStyle("-fx-font-size: 18px;");

        VBox box = new VBox(8, title, line1, line2);
        box.setAlignment(Pos.CENTER); // center all vertically in column
        return box;
    }

    private Line createVerticalLine() {
        Line line = new Line();
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(2);
        return line;
    }

    @Override
    public void initialize() {}

    @Override
    public double getPrefHeight() {
        return 450;
    }

    @Override
    public double getPrefWidth() {
        return 1000;
    }

    @Override
    public AnchorPane getRoot() {
        return extensionRoot;
    }
}
