package com.jaiswal.gui.theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the light theme for the whiteboard application.
 * Provides light color scheme inspired by modern UI design principles.
 */
public class LightTheme implements Theme {
    private static final Color BACKGROUND = new Color(245, 245, 250);
    private static final Color CANVAS_BACKGROUND = Color.WHITE;
    private static final Color FOREGROUND = new Color(33, 33, 33);
    private static final Color ACCENT = new Color(66, 133, 244);  // Google blue
    private static final Color SECONDARY_ACCENT = new Color(52, 168, 83);  // Google green
    private static final Color BORDER = new Color(218, 220, 224);
    private static final Color SELECTION = new Color(210, 227, 252);
    private static final Color TOOLBAR = new Color(255, 255, 255);
    private static final Color STATUS_BAR = new Color(240, 240, 240);

    private final Map<String, ImageIcon> iconCache = new HashMap<>();

    @Override
    public String getName() {
        return "Light";
    }

    @Override
    public Color getBackgroundColor() {
        return BACKGROUND;
    }

    @Override
    public Color getCanvasBackgroundColor() {
        return CANVAS_BACKGROUND;
    }

    @Override
    public Color getForegroundColor() {
        return FOREGROUND;
    }

    @Override
    public Color getAccentColor() {
        return ACCENT;
    }

    @Override
    public Color getSecondaryAccentColor() {
        return SECONDARY_ACCENT;
    }

    @Override
    public Color getBorderColor() {
        return BORDER;
    }

    @Override
    public Color getSelectionColor() {
        return SELECTION;
    }

    @Override
    public Color getToolbarColor() {
        return TOOLBAR;
    }

    @Override
    public Color getStatusBarColor() {
        return STATUS_BAR;
    }

    @Override
    public void apply(JComponent component) {
        // Set default UI colors
        UIManager.put("Panel.background", new ColorUIResource(BACKGROUND));
        UIManager.put("OptionPane.background", new ColorUIResource(BACKGROUND));
        UIManager.put("TextField.background", new ColorUIResource(CANVAS_BACKGROUND));
        UIManager.put("TextArea.background", new ColorUIResource(CANVAS_BACKGROUND));
        UIManager.put("List.background", new ColorUIResource(CANVAS_BACKGROUND));
        UIManager.put("ComboBox.background", new ColorUIResource(CANVAS_BACKGROUND));

        UIManager.put("Panel.foreground", new ColorUIResource(FOREGROUND));
        UIManager.put("Label.foreground", new ColorUIResource(FOREGROUND));
        UIManager.put("Button.foreground", new ColorUIResource(FOREGROUND));
        UIManager.put("TextField.foreground", new ColorUIResource(FOREGROUND));
        UIManager.put("TextArea.foreground", new ColorUIResource(FOREGROUND));
        UIManager.put("List.foreground", new ColorUIResource(FOREGROUND));
        UIManager.put("ComboBox.foreground", new ColorUIResource(FOREGROUND));

        UIManager.put("Button.select", new ColorUIResource(SELECTION));
        UIManager.put("Button.focus", new ColorUIResource(SELECTION));
        UIManager.put("TabbedPane.selected", new ColorUIResource(SELECTION));

        UIManager.put("Button.background", new ColorUIResource(TOOLBAR));
        UIManager.put("ToggleButton.background", new ColorUIResource(TOOLBAR));
        UIManager.put("ToolBar.background", new ColorUIResource(TOOLBAR));
        UIManager.put("TabbedPane.background", new ColorUIResource(TOOLBAR));

        // Set component-specific properties
        component.setBackground(BACKGROUND);
        component.setForeground(FOREGROUND);

        Border border = BorderFactory.createLineBorder(BORDER);

        // Apply theme to all child components
        applyToChildren(component, border);

        // Force repaint
        SwingUtilities.updateComponentTreeUI(component);
    }

    private void applyToChildren(Container container, Border border) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(BACKGROUND);
                panel.setForeground(FOREGROUND);
            } else if (comp instanceof JButton || comp instanceof JToggleButton) {
                JComponent button = (JComponent) comp;
                button.setBackground(TOOLBAR);
                button.setForeground(FOREGROUND);
                button.setBorder(border);
            } else if (comp instanceof JTextField || comp instanceof JTextArea) {
                JTextComponent textComp = (JTextComponent) comp;
                textComp.setBackground(CANVAS_BACKGROUND);
                textComp.setForeground(FOREGROUND);
                textComp.setBorder(border);
            } else if (comp instanceof JList) {
                JList<?> list = (JList<?>) comp;
                list.setBackground(CANVAS_BACKGROUND);
                list.setForeground(FOREGROUND);
                list.setBorder(border);
            } else if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                scrollPane.setBorder(border);
                scrollPane.getViewport().setBackground(CANVAS_BACKGROUND);
            }

            // Recursively apply to child containers
            if (comp instanceof Container) {
                applyToChildren((Container) comp, border);
            }
        }
    }

    @Override
    public ImageIcon getThemedIcon(String path, int width, int height) {
        String key = path + "_" + width + "_" + height;
        if (iconCache.containsKey(key)) {
            return iconCache.get(key);
        }

        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(img);

        iconCache.put(key, scaledIcon);
        return scaledIcon;
    }
}