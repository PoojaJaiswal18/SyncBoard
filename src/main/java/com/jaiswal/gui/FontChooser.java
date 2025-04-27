package com.jaiswal.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A dialog that allows users to select font properties (family, style, and size).
 * This component can be used to provide font selection capabilities in applications.
 */
public class FontChooser extends JDialog {
    private Font selectedFont;
    private boolean okPressed = false;

    private JComboBox<String> fontFamilyCombo;
    private JComboBox<String> fontStyleCombo;
    private JComboBox<Integer> fontSizeCombo;
    private JTextArea previewArea;

    /**
     * Shows a modal font chooser dialog and returns the selected font.
     *
     * @param parent The parent component for the dialog
     * @param initialFont The font to initialize the dialog with
     * @return The selected font, or null if the dialog was canceled
     */
    public static Font showDialog(Component parent, Font initialFont) {
        FontChooser dialog = new FontChooser(parent, initialFont);
        dialog.setVisible(true);

        return dialog.okPressed ? dialog.selectedFont : null;
    }

    /**
     * Creates a new font chooser dialog.
     *
     * @param parent The parent component for the dialog
     * @param initialFont The font to initialize the dialog with
     */
    private FontChooser(Component parent, Font initialFont) {
        // Fix the constructor call by using the JDialog constructor properly
        super(SwingUtilities.getWindowAncestor(parent));
        setTitle("Choose Font");
        setModal(true);

        this.selectedFont = initialFont != null ? initialFont : new Font("Dialog", Font.PLAIN, 12);

        initComponents();
        updatePreview();

        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Initializes the components of the dialog.
     */
    private void initComponents() {
        // Font family selector
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilies = ge.getAvailableFontFamilyNames();
        fontFamilyCombo = new JComboBox<>(fontFamilies);
        fontFamilyCombo.setSelectedItem(selectedFont.getFamily());

        // Font style selector
        String[] styles = {"Plain", "Bold", "Italic", "Bold Italic"};
        fontStyleCombo = new JComboBox<>(styles);
        int styleIndex;
        switch (selectedFont.getStyle()) {
            case Font.BOLD:
                styleIndex = 1;
                break;
            case Font.ITALIC:
                styleIndex = 2;
                break;
            case Font.BOLD | Font.ITALIC:
                styleIndex = 3;
                break;
            default:
                styleIndex = 0;
        }
        fontStyleCombo.setSelectedIndex(styleIndex);

        // Font size selector
        Integer[] sizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};
        fontSizeCombo = new JComboBox<>(sizes);
        fontSizeCombo.setSelectedItem(Integer.valueOf(selectedFont.getSize()));
        // Enable editable for custom sizes
        fontSizeCombo.setEditable(true);

        // Preview area
        previewArea = new JTextArea("AaBbYyZz 123");
        previewArea.setEditable(false);
        previewArea.setPreferredSize(new Dimension(300, 100));
        previewArea.setLineWrap(true);
        previewArea.setBorder(BorderFactory.createEtchedBorder());

        // Add action listeners
        ActionListener updateListener = e -> updateSelectedFont();
        fontFamilyCombo.addActionListener(updateListener);
        fontStyleCombo.addActionListener(updateListener);
        fontSizeCombo.addActionListener(updateListener);

        // Create buttons
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            okPressed = true;
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        // Layout components
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 1;

        // Font family
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Font:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(fontFamilyCombo, gbc);

        // Font style
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Style:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        mainPanel.add(fontStyleCombo, gbc);

        // Font size
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Size:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        mainPanel.add(fontSizeCombo, gbc);

        // Preview
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        previewPanel.add(new JScrollPane(previewArea), BorderLayout.CENTER);
        mainPanel.add(previewPanel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Main layout
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set default button
        getRootPane().setDefaultButton(okButton);

        // Handle ESCAPE key to close dialog
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    /**
     * Updates the selected font based on the current selections in the UI components.
     */
    private void updateSelectedFont() {
        String family = (String) fontFamilyCombo.getSelectedItem();

        // Get style constant from selected index
        int style;
        switch (fontStyleCombo.getSelectedIndex()) {
            case 1:
                style = Font.BOLD;
                break;
            case 2:
                style = Font.ITALIC;
                break;
            case 3:
                style = Font.BOLD | Font.ITALIC;
                break;
            default:
                style = Font.PLAIN;
        }

        // Get size, handling potential custom input
        int size;
        try {
            Object sizeValue = fontSizeCombo.getSelectedItem();
            if (sizeValue instanceof Integer) {
                size = (Integer) sizeValue;
            } else if (sizeValue instanceof String) {
                size = Integer.parseInt((String) sizeValue);
            } else {
                size = 12; // Default size if parsing fails
            }
            // Ensure size is within reasonable bounds
            size = Math.max(1, Math.min(size, 999));
        } catch (NumberFormatException e) {
            size = 12; // Default size if parsing fails
        }

        selectedFont = new Font(family, style, size);
        updatePreview();
    }

    /**
     * Updates the preview area with the currently selected font.
     */
    private void updatePreview() {
        previewArea.setFont(selectedFont);
    }
}