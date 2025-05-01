package com.jaiswal.gui.components;

import com.jaiswal.gui.dialogs.FontChooser;
import com.jaiswal.gui.utils.IconLoader;
import com.jaiswal.gui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

/**
 * Toolbar panel containing drawing tools, color selection, and other controls
 * for the whiteboard application.
 */
public class ToolbarPanel extends JPanel implements ActionListener {
    private final CanvasPanel canvasPanel;

    // Tool buttons
    private final List<JToggleButton> toolButtons;
    private final JToggleButton selectButton;
    private final JToggleButton penButton;
    private final JToggleButton lineButton;
    private final JToggleButton rectangleButton;
    private final JToggleButton ovalButton;
    private final JToggleButton textButton;
    private final JToggleButton eraserButton;

    // Color controls
    private final JButton colorButton;
    private final JPanel currentColorPanel;

    // Stroke width control
    private final JComboBox<String> strokeWidthCombo;

    // Fill mode control
    private final JToggleButton fillModeButton;

    // Font controls
    private final JButton fontButton;
    private Font currentFont = new Font(UIConstants.FONT_FAMILY, Font.PLAIN, 14);

    // Grid controls
    private final JToggleButton showGridButton;
    private final JComboBox<String> gridSizeCombo;

    /**
     * Constructor for the toolbar panel
     * @param canvasPanel The canvas panel to control
     */
    public ToolbarPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;

        // Configure panel
        setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR),
                new EmptyBorder(4, 8, 4, 8)));
        setBackground(UIConstants.TOOLBAR_BACKGROUND);

        // Create drawing tools group
        ButtonGroup toolGroup = new ButtonGroup();

        selectButton = createToolButton("select", "Select", toolGroup, "SELECT");
        penButton = createToolButton("pen", "Freehand", toolGroup, "FREEHAND");
        lineButton = createToolButton("line", "Line", toolGroup, "LINE");
        rectangleButton = createToolButton("rectangle", "Rectangle", toolGroup, "RECTANGLE");
        ovalButton = createToolButton("oval", "Oval", toolGroup, "CIRCLE");
        textButton = createToolButton("text", "Text", toolGroup, "TEXT");
        eraserButton = createToolButton("eraser", "Eraser", toolGroup, "ERASER");

        // Add tools to a list for easy access
        toolButtons = Arrays.asList(
                selectButton, penButton, lineButton, rectangleButton,
                ovalButton, textButton, eraserButton
        );

        // Add separator
        add(createSeparator());

        // Color picker
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        colorPanel.setBackground(UIConstants.TOOLBAR_BACKGROUND);

        JLabel colorLabel = new JLabel("Color:");
        colorLabel.setFont(UIConstants.FONT_NORMAL);
        colorPanel.add(colorLabel);

        currentColorPanel = new JPanel();
        currentColorPanel.setPreferredSize(new Dimension(24, 24));
        currentColorPanel.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        currentColorPanel.setBackground(Color.BLACK);
        colorPanel.add(currentColorPanel);

        colorButton = new JButton(IconLoader.loadIcon("color_picker.png"));
        colorButton.setToolTipText("Choose Color");
        colorButton.addActionListener(this);
        colorPanel.add(colorButton);

        add(colorPanel);

        // Add separator
        add(createSeparator());

        // Stroke width control
        JPanel strokePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        strokePanel.setBackground(UIConstants.TOOLBAR_BACKGROUND);

        JLabel strokeLabel = new JLabel("Width:");
        strokeLabel.setFont(UIConstants.FONT_NORMAL);
        strokePanel.add(strokeLabel);

        strokeWidthCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "8", "12"});
        strokeWidthCombo.setSelectedIndex(1); // Default to 2
        strokeWidthCombo.addActionListener(this);
        strokeWidthCombo.setFont(UIConstants.FONT_NORMAL);
        strokeWidthCombo.setPreferredSize(new Dimension(50, 28));
        strokePanel.add(strokeWidthCombo);

        add(strokePanel);

        // Fill mode toggle
        fillModeButton = new JToggleButton(IconLoader.loadIcon("fill.png"));
        fillModeButton.setToolTipText("Fill Shape");
        fillModeButton.addActionListener(this);
        add(fillModeButton);

        // Add separator
        add(createSeparator());

        // Font button (only enabled for text tool)
        fontButton = new JButton(IconLoader.loadIcon("font.png"));
        fontButton.setToolTipText("Choose Font");
        fontButton.addActionListener(this);
        fontButton.setEnabled(false); // Initially disabled
        add(fontButton);

        // Add separator
        add(createSeparator());

        // Grid controls
        JPanel gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        gridPanel.setBackground(UIConstants.TOOLBAR_BACKGROUND);

        showGridButton = new JToggleButton(IconLoader.loadIcon("grid.png"));
        showGridButton.setToolTipText("Show Grid");
        showGridButton.setSelected(true); // Show grid by default
        showGridButton.addActionListener(this);
        gridPanel.add(showGridButton);

        gridSizeCombo = new JComboBox<>(new String[]{"10", "20", "40", "80"});
        gridSizeCombo.setSelectedIndex(1); // Default to 20
        gridSizeCombo.addActionListener(this);
        gridSizeCombo.setFont(UIConstants.FONT_NORMAL);
        gridSizeCombo.setPreferredSize(new Dimension(50, 28));
        gridPanel.add(gridSizeCombo);

        add(gridPanel);

        // Select the default tool (SELECT)
        selectButton.setSelected(true);
        updateToolSettings();
    }

    /**
     * Creates a tool toggle button with icon and tooltip
     * @param iconName Base name of the icon file
     * @param tooltip Tooltip text
     * @param group Button group to add to
     * @param tool The drawing tool this button represents
     * @return Configured toggle button
     */
    private JToggleButton createToolButton(String iconName, String tooltip,
                                           ButtonGroup group, String tool) {
        JToggleButton button = new JToggleButton(IconLoader.loadIcon(iconName + ".png"));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(36, 36));
        button.setFocusPainted(false);
        button.putClientProperty("tool", tool);
        button.addActionListener(this);

        group.add(button);
        add(button);

        return button;
    }

    /**
     * Creates a vertical separator for the toolbar
     * @return JSeparator component
     */
    private Component createSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 32));
        separator.setForeground(UIConstants.BORDER_COLOR);

        JPanel separatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        separatorPanel.setBackground(UIConstants.TOOLBAR_BACKGROUND);
        separatorPanel.add(separator);

        return separatorPanel;
    }

    /**
     * Update the canvas with the currently selected tool settings
     */
    private void updateToolSettings() {
        // Get the selected tool button
        JToggleButton selectedButton = null;
        for (JToggleButton button : toolButtons) {
            if (button.isSelected()) {
                selectedButton = button;
                break;
            }
        }

        if (selectedButton != null) {
            String tool = (String) selectedButton.getClientProperty("tool");
            canvasPanel.setSelectedTool(tool);

            // Enable/disable font button based on whether text tool is selected
            fontButton.setEnabled("TEXT".equals(tool));
        }

        // Update other settings
        try {
            float strokeWidth = Float.parseFloat((String) strokeWidthCombo.getSelectedItem());
            canvasPanel.setCurrentStrokeWidth(strokeWidth);
        } catch (NumberFormatException | NullPointerException e) {
            canvasPanel.setCurrentStrokeWidth(2.0f);
        }

        canvasPanel.setFillShape(fillModeButton.isSelected());

        int gridSize = 20;
        try {
            gridSize = Integer.parseInt((String) gridSizeCombo.getSelectedItem());
        } catch (NumberFormatException | NullPointerException e) {
            // Use default
        }

        canvasPanel.setShowGrid(showGridButton.isSelected());
        canvasPanel.setGridSize(gridSize);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == colorButton) {
            // Show color picker
            Color newColor = JColorChooser.showDialog(this,
                    "Choose Color", currentColorPanel.getBackground());

            if (newColor != null) {
                currentColorPanel.setBackground(newColor);
                canvasPanel.setCurrentColor(newColor);
            }
        } else if (source == fillModeButton) {
            // Update fill mode
            canvasPanel.setFillShape(fillModeButton.isSelected());
        } else if (source == fontButton) {
            // Show font chooser
            Font newFont = FontChooser.showDialog(this, "Choose Font", currentFont);
            if (newFont != null) {
                currentFont = newFont;
                canvasPanel.setCurrentFont(newFont);
            }
        } else if (source == showGridButton || source == gridSizeCombo) {
            // Update grid settings
            int gridSize = 20;
            try {
                gridSize = Integer.parseInt((String) gridSizeCombo.getSelectedItem());
            } catch (NumberFormatException | NullPointerException ex) {
                // Use default
            }

            canvasPanel.setShowGrid(showGridButton.isSelected());
            canvasPanel.setGridSize(gridSize);
        } else if (source == strokeWidthCombo) {
            // Update stroke width
            try {
                float strokeWidth = Float.parseFloat((String) strokeWidthCombo.getSelectedItem());
                canvasPanel.setCurrentStrokeWidth(strokeWidth);
            } catch (NumberFormatException | NullPointerException ex) {
                canvasPanel.setCurrentStrokeWidth(2.0f);
            }
        } else if (source instanceof JToggleButton) {
            // A tool button was selected
            updateToolSettings();
        }
    }
}