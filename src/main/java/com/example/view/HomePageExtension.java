package com.example.view;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Minimal/clean HomePageExtension.
 * All UI nodes removed — this class preserves the API and connections
 * so it can be swapped into your app without breaking references.
 *
 * Use this as a blank slate to add only the controls you actually need.
 */
public class HomePageExtension implements HomePageComponent {
    private final StackPane root;
    private static final double PREF_WIDTH = 375;
    private static final double PREF_HEIGHT = 812;

    public HomePageExtension() {
        root = new StackPane();
        root.setPrefSize(PREF_WIDTH, PREF_HEIGHT);

        // Intentionally empty: all UI removed.
        // If you want a tiny placeholder for debugging, uncomment:
        // Text placeholder = new Text("HomePageExtension (clean)");
        // root.getChildren().add(placeholder);
    }

    /**
     * If you have a stylesheet, this method will still attach it.
     * If no stylesheet is required, calling this is harmless.
     */
    public void applyStylesheet(Scene scene) {
        if (scene == null) return;
        try {
            if (getClass().getResource("pizza-styles.css") != null) {
                scene.getStylesheets().add(getClass().getResource("pizza-styles.css").toExternalForm());
            }
        } catch (Exception e) {
            // fail gracefully if stylesheet missing
        }
    }

    // No-op initializer to preserve interface contract
    @Override
    public void initialize() {}

    @Override
    public Pane getRoot() {
        return root;
    }

    @Override
    public double getPrefWidth() {
        return PREF_WIDTH;
    }

    @Override
    public double getPrefHeight() {
        return PREF_HEIGHT;
    }

    // Quick local test helper (optional)
    public static void main(String[] args) {
        Stage stage = new Stage();
        HomePageExtension page = new HomePageExtension();
        Scene scene = new Scene(page.getRoot(), page.getPrefWidth(), page.getPrefHeight());
        page.applyStylesheet(scene);
        stage.setScene(scene);
        stage.setTitle("Home Page Extension (clean)");
        stage.show();
    }
}
