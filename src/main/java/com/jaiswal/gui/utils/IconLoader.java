package com.jaiswal.gui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for loading and caching icons throughout the application.
 * This ensures icons are only loaded once and reused as needed.
 */
public class IconLoader {
    private static final String ICON_PATH = "/icons/";
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();

    /**
     * Loads an icon from the resources folder and caches it for future use.
     *
     * @param iconName The name of the icon file (e.g., "brush.png")
     * @return The loaded ImageIcon, or a default icon if loading fails
     */
    public static ImageIcon loadIcon(String iconName) {
        // Check if icon is already in cache
        if (iconCache.containsKey(iconName)) {
            return iconCache.get(iconName);
        }

        try {
            // Attempt to load the icon from resources
            URL iconURL = IconLoader.class.getResource(ICON_PATH + iconName);
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                iconCache.put(iconName, icon);
                return icon;
            } else {
                System.err.println("Could not find icon: " + iconName);
                return createDefaultIcon();
            }
        } catch (Exception e) {
            System.err.println("Error loading icon " + iconName + ": " + e.getMessage());
            return createDefaultIcon();
        }
    }

    /**
     * Loads an icon and resizes it to the specified dimensions.
     *
     * @param iconName The name of the icon file
     * @param width Desired width
     * @param height Desired height
     * @return The resized ImageIcon
     */
    public static ImageIcon loadIcon(String iconName, int width, int height) {
        ImageIcon originalIcon = loadIcon(iconName);
        Image img = originalIcon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    /**
     * Creates a fallback icon when the requested icon cannot be found.
     *
     * @return A simple default icon
     */
    private static ImageIcon createDefaultIcon() {
        // Create a simple colored square as a fallback icon
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(150, 150, 150));
        g.fillRect(0, 0, size, size);
        g.setColor(new Color(200, 200, 200));
        g.drawRect(0, 0, size - 1, size - 1);
        g.dispose();

        ImageIcon icon = new ImageIcon(image);
        return icon;
    }

    /**
     * Clears the icon cache to free memory.
     */
    public static void clearCache() {
        iconCache.clear();
    }

    /**
     * Creates a custom colored icon based on a base icon.
     * Useful for creating tool indicators with different colors.
     *
     * @param iconName The base icon name
     * @param color The color to apply
     * @return A new ImageIcon with the applied color
     */
    public static ImageIcon createColoredIcon(String iconName, Color color) {
        ImageIcon original = loadIcon(iconName);
        int width = original.getIconWidth();
        int height = original.getIconHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(original.getImage(), 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.dispose();

        return new ImageIcon(image);
    }
}