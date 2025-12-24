package com.munchoak.homepage;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Minimal/clean HomePageExtension.
 * All UI nodes removed — this class preserves the API and connections
 * so it can be swapped into your app without breaking references.
 * <p>
 * Use this as a blank slate to add only the controls you actually need.
 */
public class HomePageExtension implements HomePageComponent {
    private final StackPane root;
    private static final double PREF_WIDTH = 375;
    private static final double PREF_HEIGHT = 812;

    public HomePageExtension() {
        root = new StackPane();
        root.setPrefSize(PREF_WIDTH, PREF_HEIGHT);
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
    public void initialize() {
    }

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
}
