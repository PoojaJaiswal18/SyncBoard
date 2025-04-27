package com.jaiswal.gui;

import com.jaiswal.client.WhiteboardClient;
import com.jaiswal.shared.IDrawable;
import com.jaiswal.shared.TextElement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Graphical user interface for the  whiteboard application.
 * Handles the display of the canvas and user interactions.
 */
public class WhiteboardGUI extends JFrame {
    private final WhiteboardClient client;
    private final boolean isManager;

    private CanvasPanel canvasPanel;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JButton kickButton;

    /**
     * Constructor for WhiteboardGUI
     * @param client The client instance to connect with
     * @param isManager Whether this client is the whiteboard manager
     */
    public WhiteboardGUI(WhiteboardClient client, boolean isManager) {
        this.client = client;
        this.isManager = isManager;

        setTitle("Distributed Whiteboard - " + (isManager ? "Manager" : "Client"));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initComponents();
        setupLayout();
        setupListeners();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.disconnect();
                dispose();
                System.exit(0);
            }
        });
    }

    /**
     * Initialize GUI components
     */
    private void initComponents() {
        // Create canvas panel
        canvasPanel = new CanvasPanel();
        canvasPanel.setDrawListener(new CanvasPanel.DrawListener() {
            @Override
            public void onShapeDrawn(IDrawable shape) {
                client.drawShape(shape);
            }

            @Override
            public void onTextAdded(TextElement text) {
                client.drawText(text);
            }
        });

        canvasPanel.setTextInputListener(location -> {
            String text = JOptionPane.showInputDialog(WhiteboardGUI.this, "Enter text:");
            if (text != null && !text.isEmpty()) {
                canvasPanel.addText(text, location);
            }
        });

        // Create user list
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Create kick button (only for manager)
        kickButton = new JButton("Kick User");
        kickButton.setEnabled(isManager);
        kickButton.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                // Extract actual username if it has "(You)" suffix
                String username = selectedUser;
                if (username.endsWith(" (You)")) {
                    username = username.substring(0, username.length() - 6);
                }

                if (!username.equals(client.getUsernameLocal())) {
                    client.kickUser(username);
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot kick yourself!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to kick", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Set up the layout of the GUI components
     */
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create toolbar
        JToolBar toolBar = createToolBar();
        mainPanel.add(toolBar, BorderLayout.NORTH);

        // Add canvas to center
        JScrollPane canvasScrollPane = new JScrollPane(canvasPanel);
        canvasScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        canvasScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        mainPanel.add(canvasScrollPane, BorderLayout.CENTER);

        // Create sidebar for user list
        JPanel sidePanel = new JPanel(new BorderLayout(5, 5));
        sidePanel.setBorder(BorderFactory.createTitledBorder("Users"));
        sidePanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        if (isManager) {
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(kickButton);
            sidePanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        sidePanel.setPreferredSize(new Dimension(150, 600));
        mainPanel.add(sidePanel, BorderLayout.EAST);

        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        setContentPane(mainPanel);
    }

    /**
     * Create the toolbar with drawing tools and options
     * @return The configured toolbar
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // Drawing tools
        String[] tools = {"Line", "Rectangle", "Circle", "Text"};
        ButtonGroup toolGroup = new ButtonGroup();

        for (String tool : tools) {
            JToggleButton button = new JToggleButton(tool);
            button.addActionListener(e -> canvasPanel.setCurrentTool(tool));
            toolGroup.add(button);
            toolBar.add(button);

            if (tool.equals("Line")) {  // Select Line by default
                button.setSelected(true);
                canvasPanel.setCurrentTool("Line");
            }
        }

        toolBar.addSeparator();

        // Color chooser button
        AtomicReference<Color> currentColor = new AtomicReference<>(Color.BLACK);
        JButton colorButton = new JButton("Color");
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Color", currentColor.get());
            if (newColor != null) {
                currentColor.set(newColor);
                canvasPanel.setCurrentColor(newColor);
            }
        });
        toolBar.add(colorButton);

        // Stroke width selector
        String[] strokeWidths = {"1", "2", "3", "4", "5"};
        JComboBox<String> strokeCombo = new JComboBox<>(strokeWidths);
        strokeCombo.setSelectedIndex(1); // Default to 2px
        strokeCombo.addActionListener(e -> {
            String selectedItem = (String) strokeCombo.getSelectedItem();
            if (selectedItem != null) {
                canvasPanel.setCurrentStrokeWidth(Integer.parseInt(selectedItem));
            }
        });
        toolBar.add(new JLabel(" Width: "));
        toolBar.add(strokeCombo);

        // Font selector for text
        AtomicReference<Font> currentFont = new AtomicReference<>(new Font("Arial", Font.PLAIN, 12));
        JButton fontButton = new JButton("Font");
        fontButton.addActionListener(e -> {
            Font selectedFont = FontChooser.showDialog(this, currentFont.get());
            if (selectedFont != null) {
                currentFont.set(selectedFont);
                canvasPanel.setCurrentFont(selectedFont);
            }
        });
        toolBar.add(fontButton);

        // Clear button
        toolBar.addSeparator();
        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> client.clearCanvas());
        toolBar.add(clearButton);

        return toolBar;
    }

    /**
     * Create the menu bar
     * @return The configured menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Only add File menu for manager
        if (isManager) {
            JMenu fileMenu = new JMenu("File");

            JMenuItem newItem = new JMenuItem("New");
            newItem.addActionListener(e -> client.clearCanvas());
            fileMenu.add(newItem);

            JMenuItem openItem = new JMenuItem("Open");
            openItem.addActionListener(e -> openWhiteboard());
            fileMenu.add(openItem);

            JMenuItem saveItem = new JMenuItem("Save");
            saveItem.addActionListener(e -> saveWhiteboard(false));
            fileMenu.add(saveItem);

            JMenuItem saveAsItem = new JMenuItem("Save As");
            saveAsItem.addActionListener(e -> saveWhiteboard(true));
            fileMenu.add(saveAsItem);

            fileMenu.addSeparator();

            JMenuItem closeItem = new JMenuItem("Close");
            closeItem.addActionListener(e -> {
                client.disconnect();
                dispose();
                System.exit(0);
            });
            fileMenu.add(closeItem);

            menuBar.add(fileMenu);
        }

        return menuBar;
    }

    /**
     * Set up event listeners for components
     */
    private void setupListeners() {
        // Listen for key events
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            // Add keyboard shortcuts here if needed
            return false;
        });
    }

    /**
     * Update the canvas with new drawing elements
     * @param elements Map of drawing elements to display
     */
    public void updateCanvas(Map<Integer, IDrawable> elements) {
        SwingUtilities.invokeLater(() -> canvasPanel.setElements(elements));
    }

    /**
     * Update the user list display
     * @param users List of users to display
     */
    public void updateUserList(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users) {
                userListModel.addElement(user.equals(client.getUsernameLocal()) ?
                        user + " (You)" : user);
            }
        });
    }

    /**
     * Show a notification message
     * @param message The message to display
     */
    public void showNotification(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, "Notification", JOptionPane.INFORMATION_MESSAGE)
        );
    }

    /**
     * Show a confirmation dialog
     * @param message The message to display
     * @return true if confirmed, false otherwise
     */
    public boolean showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Open a whiteboard file
     */
    private void openWhiteboard() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Whiteboard files (*.wb)", "wb"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            client.loadWhiteboard(fileChooser.getSelectedFile().getPath());
        }
    }

    /**
     * Save a whiteboard to a file
     * @param saveAs Whether to prompt for a new filename
     */
    private void saveWhiteboard(boolean saveAs) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Whiteboard files (*.wb)", "wb"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getPath();
            if (!path.endsWith(".wb")) {
                path += ".wb";
            }
            client.saveWhiteboard(path);
        }
    }
}