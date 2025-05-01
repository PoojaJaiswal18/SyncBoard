package com.jaiswal.gui.dialogs;

import com.jaiswal.gui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for entering text to be placed on the whiteboard
 */
public class TextInputDialog extends JDialog implements ActionListener {

    private JTextArea textArea;
    private JButton okButton;
    private JButton cancelButton;
    private JComboBox<String> fontFamilyCombo;
    private JComboBox<Integer> fontSizeCombo;
    private JCheckBox boldCheckBox;
    private JCheckBox italicCheckBox;
    private JButton colorButton;
    private Color selectedColor;
    private boolean confirmed = false;

    private static final String[] FONT_FAMILIES = {
            "Arial", "Calibri", "Comic Sans MS", "Courier New", "Georgia",
            "Helvetica", "Times New Roman", "Trebuchet MS", "Verdana"
    };

    private static final Integer[] FONT_SIZES = {
            8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 28, 32, 36, 42, 48, 56, 64, 72
    };

    /**
     * Creates a new TextInputDialog
     *
     * @param parent The parent window
     * @param initialFont The initial font to use
     * @param initialColor The initial color to use
     */
    public TextInputDialog(Window parent, Font initialFont, Color initialColor) {
        super(parent, "Add Text", ModalityType.APPLICATION_MODAL);

        this.selectedColor = initialColor;

        initializeComponents(initialFont);
        layoutComponents();
        setupEventHandlers();

        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(parent);
    }

    /**
     * Initializes the dialog components
     *
     * @param initialFont The initial font
     */
    private void initializeComponents(Font initialFont) {
        textArea = new JTextArea(5, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(initialFont);
        textArea.setForeground(selectedColor);

        fontFamilyCombo = new JComboBox<>(FONT_FAMILIES);
        // Find the closest match for the font family
        String fontFamily = initialFont.getFamily();
        boolean found = false;
        for (String family : FONT_FAMILIES) {
            if (family.equalsIgnoreCase(fontFamily)) {
                fontFamilyCombo.setSelectedItem(family);
                found = true;
                break;
            }
        }
        if (!found && FONT_FAMILIES.length > 0) {
            fontFamilyCombo.setSelectedIndex(0);
        }

        fontSizeCombo = new JComboBox<>(FONT_SIZES);
        // Find the closest font size
        int fontSize = initialFont.getSize();
        int closestIndex = 0;
        int minDiff = Integer.MAX_VALUE;
        for (int i = 0; i < FONT_SIZES.length; i++) {
            int diff = Math.abs(FONT_SIZES[i] - fontSize);
            if (diff < minDiff) {
                minDiff = diff;
                closestIndex = i;
            }
        }
        fontSizeCombo.setSelectedIndex(closestIndex);

        boldCheckBox = new JCheckBox("Bold");
        boldCheckBox.setSelected((initialFont.getStyle() & Font.BOLD) != 0);

        italicCheckBox = new JCheckBox("Italic");
        italicCheckBox.setSelected((initialFont.getStyle() & Font.ITALIC) != 0);

        colorButton = new JButton("Text Color");
        colorButton.setForeground(selectedColor);

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
    }

    /**
     * Lays out components in the dialog
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Text area in a scroll pane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Text"));

        // Font controls panel
        JPanel fontPanel = new JPanel(new GridBagLayout());
        fontPanel.setBorder(BorderFactory.createTitledBorder("Text Formatting"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        fontPanel.add(new JLabel("Font:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        fontPanel.add(fontFamilyCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        fontPanel.add(new JLabel("Size:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        fontPanel.add(fontSizeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel stylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stylePanel.add(boldCheckBox);
        stylePanel.add(italicCheckBox);
        stylePanel.add(colorButton);
        fontPanel.add(stylePanel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(fontPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets up event handlers for components
     */
    private void setupEventHandlers() {
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        fontFamilyCombo.addActionListener(e -> updateTextAreaFont());
        fontSizeCombo.addActionListener(e -> updateTextAreaFont());
        boldCheckBox.addActionListener(e -> updateTextAreaFont());
        italicCheckBox.addActionListener(e -> updateTextAreaFont());

        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(
                    this, "Choose Text Color", selectedColor);
            if (newColor != null) {
                selectedColor = newColor;
                textArea.setForeground(selectedColor);
                colorButton.setForeground(selectedColor);
            }
        });
    }

    /**
     * Updates the text area font based on current selections
     */
    private void updateTextAreaFont() {
        String fontFamily = (String) fontFamilyCombo.getSelectedItem();
        int fontSize = (Integer) fontSizeCombo.getSelectedItem();
        int fontStyle = Font.PLAIN;

        if (boldCheckBox.isSelected()) {
            fontStyle |= Font.BOLD;
        }

        if (italicCheckBox.isSelected()) {
            fontStyle |= Font.ITALIC;
        }

        Font newFont = new Font(fontFamily, fontStyle, fontSize);
        textArea.setFont(newFont);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            confirmed = true;
            setVisible(false);
        } else if (e.getSource() == cancelButton) {
            confirmed = false;
            setVisible(false);
        }
    }

    /**
     * Returns whether the dialog was confirmed
     *
     * @return true if OK was clicked, false otherwise
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Returns the entered text
     *
     * @return The text entered by the user
     */
    public String getText() {
        return textArea.getText();
    }

    /**
     * Returns the selected font
     *
     * @return The font configured by the user
     */
    public Font getSelectedFont() {
        String fontFamily = (String) fontFamilyCombo.getSelectedItem();
        int fontSize = (Integer) fontSizeCombo.getSelectedItem();
        int fontStyle = Font.PLAIN;

        if (boldCheckBox.isSelected()) {
            fontStyle |= Font.BOLD;
        }

        if (italicCheckBox.isSelected()) {
            fontStyle |= Font.ITALIC;
        }

        return new Font(fontFamily, fontStyle, fontSize);
    }

    /**
     * Returns the selected color
     *
     * @return The color selected by the user
     */
    public Color getSelectedColor() {
        return selectedColor;
    }
}