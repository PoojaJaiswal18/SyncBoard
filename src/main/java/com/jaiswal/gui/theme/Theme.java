package com.jaiswal.gui.theme;

import javax.swing.*;
import java.awt.*;

/**
 * Interface defining theme characteristics for the whiteboard application.
 * Provides color schemes and UI properties for consistent theming.
 */
public interface Theme {
    /**
     * Get the name of the theme
     * @return Theme name
     */
    String getName();

    /**
     * Get background color for the main panels
     * @return Background color
     */
    Color getBackgroundColor();

    /**
     * Get background color for the canvas
     * @return Canvas background color
     */
    Color getCanvasBackgroundColor();

    /**
     * Get foreground color for text
     * @return Foreground color
     */
    Color getForegroundColor();

    /**
     * Get accent color for highlighting elements
     * @return Accent color
     */
    Color getAccentColor();

    /**
     * Get secondary accent color
     * @return Secondary accent color
     */
    Color getSecondaryAccentColor();

    /**
     * Get color for borders
     * @return Border color
     */
    Color getBorderColor();

    /**
     * Get color for selection
     * @return Selection color
     */
    Color getSelectionColor();

    /**
     * Get color for toolbar background
     * @return Toolbar background color
     */
    Color getToolbarColor();

    /**
     * Get color for status bar background
     * @return Status bar background color
     */
    Color getStatusBarColor();

    /**
     * Apply this theme to the specified component and its children
     * @param component The component to apply the theme to
     */
    void apply(JComponent component);

    /**
     * Get an icon with colors adapted to this theme
     * @param path Path to the icon resource
     * @param width Width of the icon
     * @param height Height of the icon
     * @return Themed icon
     */
    ImageIcon getThemedIcon(String path, int width, int height);
}