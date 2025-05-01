package com.jaiswal.gui.dialogs;

import com.jaiswal.gui.utils.IconLoader;
import com.jaiswal.gui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * A custom dialog for selecting fonts with preview functionality.
 * This component allows users to choose font family, style, and size
 * with a live preview of the selection.
 */
public class FontChooser extends JDialog implements ActionListener, ListSelectionListener {
    private JList<String> fontFamilyList;
    private JList<String> fontStyleList;
    private JList<String> fontSizeList;
    private JTextField searchField;
    private JTextField sampleTextField;
    private JPanel previewPanel;
    private JButton okButton;
    private JButton cancelButton;

    private Font selectedFont;
    private boolean approved = false;

    private static final String[] FONT_STYLES = {"Plain", "Bold", "Italic", "Bold Italic"};
    private static final String[] FONT_SIZES = {
            "8", "9", "10", "11", "12", "14", "16", "18", "20", "22",
            "24", "26", "28", "36", "48", "72"
    };

    /**
     * Creates a new FontChooser dialog
     *
     * @param owner The parent component
     * @param title The dialog title
     * @param initialFont The font to show initially
     */
    public FontChooser(Frame owner, String title, Font initialFont) {
        super(owner, title, true);
        this.selectedFont = initialFont != null ? initialFont : new Font(Font.SANS_SERIF, Font.PLAIN, 12);

        initComponents();
        layoutUI();
        updatePreview();

        setSize(600, 500);
        setLocationRelativeTo(owner);
        setResizable(true);
    }

    /**
     * Initialize all UI components
     */
    private void initComponents() {
        // Get available font families
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();

        // Setup the font family list
        fontFamilyList = new JList<>(fontNames);
        fontFamilyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontFamilyList.addListSelectionListener(this);
        fontFamilyList.setSelectedValue(selectedFont.getFamily(), true);

        // Setup the style list
        fontStyleList = new JList<>(FONT_STYLES);
        fontStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontStyleList.addListSelectionListener(this);
        fontStyleList.setSelectedIndex(selectedFont.getStyle());

        // Setup the size list
        fontSizeList = new JList<>(FONT_SIZES);
        fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontSizeList.addListSelectionListener(this);
        fontSizeList.setSelectedValue(String.valueOf(selectedFont.getSize()), true);

        // Setup search field
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterFontList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterFontList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterFontList();
            }
        });

        // Setup preview components
        sampleTextField = new JTextField("The quick brown fox jumps over the lazy dog");
        sampleTextField.setHorizontalAlignment(JTextField.CENTER);
        sampleTextField.setFont(selectedFont);

        previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        previewPanel.add(sampleTextField, BorderLayout.CENTER);

        // Setup buttons
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setIcon(IconLoader.loadIcon("check.png"));

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setIcon(IconLoader.loadIcon("cancel.png"));
    }

    /**
     * Layout the UI components
     */
    private void layoutUI() {
        // Set the main layout
        JPanel mainPanel = new JPanel(new BorderLayout(UIConstants.PADDING_MEDIUM, UIConstants.PADDING_MEDIUM));
        mainPanel.setBorder(new EmptyBorder(UIConstants.PADDING_MEDIUM, UIConstants.PADDING_MEDIUM,
                UIConstants.PADDING_MEDIUM, UIConstants.PADDING_MEDIUM));

        // Create a panel for the font selection components
        JPanel selectionPanel = new JPanel(new GridLayout(1, 3, UIConstants.PADDING_MEDIUM, 0));

        // Font family panel with search
        JPanel familyPanel = new JPanel(new BorderLayout(0, UIConstants.PADDING_SMALL));
        familyPanel.setBorder(BorderFactory.createTitledBorder("Font Family"));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        familyPanel.add(searchPanel, BorderLayout.NORTH);
        familyPanel.add(new JScrollPane(fontFamilyList), BorderLayout.CENTER);

        // Style panel
        JPanel stylePanel = new JPanel(new BorderLayout());
        stylePanel.setBorder(BorderFactory.createTitledBorder("Font Style"));
        stylePanel.add(new JScrollPane(fontStyleList), BorderLayout.CENTER);

        // Size panel
        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setBorder(BorderFactory.createTitledBorder("Font Size"));
        sizePanel.add(new JScrollPane(fontSizeList), BorderLayout.CENTER);

        // Add to selection panel
        selectionPanel.add(familyPanel);
        selectionPanel.add(stylePanel);
        selectionPanel.add(sizePanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Add all components to the main panel
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        mainPanel.add(previewPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        setContentPane(mainPanel);
    }

    /**
     * Filter the font list based on search text
     */
    private void filterFontList() {
        String searchText = searchField.getText().toLowerCase().trim();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] allFonts = ge.getAvailableFontFamilyNames();

        if (searchText.isEmpty()) {
            fontFamilyList.setListData(allFonts);
            return;
        }

        // Filter fonts that contain the search text
        String[] filteredFonts = Arrays.stream(allFonts)
                .filter(font -> font.toLowerCase().contains(searchText))
                .toArray(String[]::new);

        fontFamilyList.setListData(filteredFonts);
    }

    /**
     * Update the preview with the currently selected font options
     */
    private void updatePreview() {
        try {
            String fontFamily = fontFamilyList.getSelectedValue();
            int fontStyle = fontStyleList.getSelectedIndex();
            int fontSize = Integer.parseInt(fontSizeList.getSelectedValue());

            selectedFont = new Font(fontFamily, fontStyle, fontSize);
            sampleTextField.setFont(selectedFont);
        } catch (Exception e) {
            // In case of any error, keep the current font
            System.err.println("Error updating font preview: " + e.getMessage());
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            // Only update when the selection is stable
            updatePreview();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            approved = true;
            dispose();
        } else if (e.getSource() == cancelButton) {
            approved = false;
            dispose();
        }
    }

    /**
     * Show the dialog and wait for user input
     *
     * @return true if the user approved the selection, false otherwise
     */
    public boolean showDialog() {
        setVisible(true);
        return approved;
    }

    /**
     * Get the selected font
     *
     * @return the Font object selected by the user
     */
    public Font getSelectedFont() {
        return selectedFont;
    }

    /**
     * Static utility method to show a font chooser dialog
     *
     * @param parent the parent component
     * @param title the title for the dialog
     * @param initialFont the initial font to display
     * @return the selected font, or null if canceled
     */
    public static Font showDialog(Component parent, String title, Font initialFont) {
        Frame owner;
        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        FontChooser dialog = new FontChooser(owner, title, initialFont);
        if (dialog.showDialog()) {
            return dialog.getSelectedFont();
        } else {
            return null;
        }
    }
}