package com.jaiswal.gui.utils;

import java.awt.*;

/**
 * Central location for UI constants throughout the application.
 * This class provides a unified approach to styling and theming for the application.
 */
public class UIConstants {
    // Theme mode tracking
    private static boolean darkMode = false;

    // Font constants
    public static final String FONT_FAMILY = "Roboto, Segoe UI, Arial, sans-serif";
    public static final int FONT_SIZE_SMALL = 11;
    public static final int FONT_SIZE_NORMAL = 13;
    public static final int FONT_SIZE_LARGE = 16;
    public static final int FONT_SIZE_HEADING = 20;

    // Font objects for common usage
    public static final Font FONT_NORMAL = new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_NORMAL);
    public static final Font FONT_BOLD = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_NORMAL);
    public static final Font FONT_HEADING = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_HEADING);
    public static final Font SMALL_FONT = new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_SMALL);

    // Padding and spacing constants
    public static final int PADDING_TINY = 2;
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 15;
    public static final int PADDING_XLARGE = 20;

    // Corner radius for components
    public static final int CORNER_RADIUS = 8;

    // Border constants
    public static final int BORDER_WIDTH = 1;

    // Shadow constants
    public static final int SHADOW_SIZE = 5;
    public static final float SHADOW_OPACITY = 0.2f;

    // Canvas constants
    public static final int DEFAULT_CANVAS_WIDTH = 2000;
    public static final int DEFAULT_CANVAS_HEIGHT = 1500;
    public static final float DEFAULT_ZOOM_LEVEL = 1.0f;
    public static final float ZOOM_INCREMENT = 0.1f;
    public static final float MAX_ZOOM = 5.0f;
    public static final float MIN_ZOOM = 0.2f;

    // Animation constants
    public static final int ANIMATION_DURATION = 200; // milliseconds

    // Default light theme colors
    private static final Color LIGHT_BACKGROUND = new Color(248, 249, 250);
    private static final Color LIGHT_FOREGROUND = new Color(33, 37, 41);
    private static final Color LIGHT_PRIMARY = new Color(66, 133, 244);
    private static final Color LIGHT_SECONDARY = new Color(108, 117, 125);
    private static final Color LIGHT_SUCCESS = new Color(76, 175, 80);
    private static final Color LIGHT_DANGER = new Color(244, 67, 54);
    private static final Color LIGHT_WARNING = new Color(255, 152, 0);
    private static final Color LIGHT_INFO = new Color(33, 150, 243);
    private static final Color LIGHT_BORDER = new Color(222, 226, 230);
    private static final Color LIGHT_CANVAS = new Color(255, 255, 255);
    private static final Color LIGHT_TOOLBAR_BACKGROUND = new Color(241, 243, 244);
    private static final Color LIGHT_PANEL_BACKGROUND = new Color(255, 255, 255);
    private static final Color LIGHT_SELECTION = new Color(66, 133, 244, 40);

    // Default dark theme colors
    private static final Color DARK_BACKGROUND = new Color(33, 37, 43);
    private static final Color DARK_FOREGROUND = new Color(237, 240, 245);
    private static final Color DARK_PRIMARY = new Color(86, 157, 255);
    private static final Color DARK_SECONDARY = new Color(130, 140, 150);
    private static final Color DARK_SUCCESS = new Color(105, 200, 110);
    private static final Color DARK_DANGER = new Color(255, 90, 90);
    private static final Color DARK_WARNING = new Color(255, 170, 35);
    private static final Color DARK_INFO = new Color(64, 170, 255);
    private static final Color DARK_BORDER = new Color(70, 75, 85);
    private static final Color DARK_CANVAS = new Color(45, 50, 60);
    private static final Color DARK_TOOLBAR_BACKGROUND = new Color(50, 55, 65);
    private static final Color DARK_PANEL_BACKGROUND = new Color(38, 42, 50);
    private static final Color DARK_SELECTION = new Color(86, 157, 255, 40);

    // Public color getters that respect current theme
    public static Color BACKGROUND_COLOR = LIGHT_BACKGROUND;
    public static Color TEXT_COLOR = LIGHT_FOREGROUND;
    public static Color PRIMARY_COLOR = LIGHT_PRIMARY;
    public static Color SECONDARY_COLOR = LIGHT_SECONDARY;
    public static Color SUCCESS_COLOR = LIGHT_SUCCESS;
    public static Color DANGER_COLOR = LIGHT_DANGER;
    public static Color WARNING_COLOR = LIGHT_WARNING;
    public static Color INFO_COLOR = LIGHT_INFO;
    public static Color BORDER_COLOR = LIGHT_BORDER;
    public static Color CANVAS_COLOR = LIGHT_CANVAS;
    public static Color TOOLBAR_BACKGROUND = LIGHT_TOOLBAR_BACKGROUND;
    public static Color PANEL_BACKGROUND = LIGHT_PANEL_BACKGROUND;
    public static Color SELECTION_COLOR = LIGHT_SELECTION;

    // Drawing colors palette inspired by Google Jamboard
    public static final Color[] DRAWING_COLORS = {
            new Color(0, 0, 0),           // Black
            new Color(128, 128, 128),     // Gray
            new Color(255, 255, 255),     // White
            new Color(244, 67, 54),       // Red
            new Color(233, 30, 99),       // Pink
            new Color(156, 39, 176),      // Purple
            new Color(103, 58, 183),      // Deep Purple
            new Color(63, 81, 181),       // Indigo
            new Color(33, 150, 243),      // Blue
            new Color(3, 169, 244),       // Light Blue
            new Color(0, 188, 212),       // Cyan
            new Color(0, 150, 136),       // Teal
            new Color(76, 175, 80),       // Green
            new Color(139, 195, 74),      // Light Green
            new Color(205, 220, 57),      // Lime
            new Color(255, 235, 59),      // Yellow
            new Color(255, 193, 7),       // Amber
            new Color(255, 152, 0),       // Orange
            new Color(255, 87, 34),       // Deep Orange
            new Color(121, 85, 72)        // Brown
    };

    // Brush stroke widths
    public static final float[] STROKE_WIDTHS = {1.0f, 2.0f, 3.0f, 5.0f, 8.0f, 12.0f};

    /**
     * Toggles between light and dark theme.
     * @return true if the new mode is dark, false if light
     */
    public static boolean toggleDarkMode() {
        darkMode = !darkMode;
        updateTheme();
        return darkMode;
    }

    /**
     * Sets the theme mode explicitly
     * @param isDark true for dark mode, false for light mode
     */
    public static void setDarkMode(boolean isDark) {
        if (darkMode != isDark) {
            darkMode = isDark;
            updateTheme();
        }
    }

    /**
     * Returns current dark mode status
     * @return true if currently in dark mode
     */
    public static boolean isDarkMode() {
        return darkMode;
    }

    /**
     * Updates all color values based on current theme mode
     */
    private static void updateTheme() {
        if (darkMode) {
            BACKGROUND_COLOR = DARK_BACKGROUND;
            TEXT_COLOR = DARK_FOREGROUND;
            PRIMARY_COLOR = DARK_PRIMARY;
            SECONDARY_COLOR = DARK_SECONDARY;
            SUCCESS_COLOR = DARK_SUCCESS;
            DANGER_COLOR = DARK_DANGER;
            WARNING_COLOR = DARK_WARNING;
            INFO_COLOR = DARK_INFO;
            BORDER_COLOR = DARK_BORDER;
            CANVAS_COLOR = DARK_CANVAS;
            TOOLBAR_BACKGROUND = DARK_TOOLBAR_BACKGROUND;
            PANEL_BACKGROUND = DARK_PANEL_BACKGROUND;
            SELECTION_COLOR = DARK_SELECTION;
        } else {
            BACKGROUND_COLOR = LIGHT_BACKGROUND;
            TEXT_COLOR = LIGHT_FOREGROUND;
            PRIMARY_COLOR = LIGHT_PRIMARY;
            SECONDARY_COLOR = LIGHT_SECONDARY;
            SUCCESS_COLOR = LIGHT_SUCCESS;
            DANGER_COLOR = LIGHT_DANGER;
            WARNING_COLOR = LIGHT_WARNING;
            INFO_COLOR = LIGHT_INFO;
            BORDER_COLOR = LIGHT_BORDER;
            CANVAS_COLOR = LIGHT_CANVAS;
            TOOLBAR_BACKGROUND = LIGHT_TOOLBAR_BACKGROUND;
            PANEL_BACKGROUND = LIGHT_PANEL_BACKGROUND;
            SELECTION_COLOR = LIGHT_SELECTION;
        }
    }

    /**
     * Creates a slightly darker version of the given color
     * @param color The base color
     * @param factor How much darker (0.0-1.0)
     * @return A darker shade of the color
     */
    public static Color darken(Color color, float factor) {
        return new Color(
                Math.max((int)(color.getRed() * (1 - factor)), 0),
                Math.max((int)(color.getGreen() * (1 - factor)), 0),
                Math.max((int)(color.getBlue() * (1 - factor)), 0),
                color.getAlpha()
        );
    }

    /**
     * Creates a slightly lighter version of the given color
     * @param color The base color
     * @param factor How much lighter (0.0-1.0)
     * @return A lighter shade of the color
     */
    public static Color lighten(Color color, float factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();

        r = (int) Math.min(r + (255 - r) * factor, 255);
        g = (int) Math.min(g + (255 - g) * factor, 255);
        b = (int) Math.min(b + (255 - b) * factor, 255);

        return new Color(r, g, b, alpha);
    }

    /**
     * Returns a color with modified alpha
     * @param color The base color
     * @param alpha The new alpha value (0-255)
     * @return The color with new alpha
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}