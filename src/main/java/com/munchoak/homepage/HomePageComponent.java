package com.munchoak.homepage;

import javafx.scene.layout.Region;

/**
 * Abstract model interface for home page components and extensions.
 * Defines common structure and properties for all sections.
 * Implementing classes must provide a root node and can override default properties.
 */
public interface HomePageComponent {
    /**
     * Returns the root node of this component.
     *
     * @return the Region root for layout integration (all roots extend Region).
     */
    Region getRoot();

    /**
     * Preferred width for this component (default: 1000).
     * Override in implementations if needed.
     *
     * @return the preferred width.
     */
    default double getPrefWidth() {
        return 1000;
    }

    /**
     * Preferred height for this component (default: 700).
     * Override in implementations if needed (e.g., for footers).
     *
     * @return the preferred height.
     */
    default double getPrefHeight() {
        return 700;
    }

    /**
     * Initializes the component (e.g., load resources, setup animations).
     * Default implementation is empty; override as needed.
     */
    default void initialize() {
        // Default: no-op. Subclasses can implement specific init logic.
    }
}