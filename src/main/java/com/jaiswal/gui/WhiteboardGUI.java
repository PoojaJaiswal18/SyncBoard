package com.jaiswal.gui;

import com.jaiswal.client.WhiteboardClient;
import com.jaiswal.gui.components.CanvasPanel;
import com.jaiswal.gui.components.StatusBarPanel;
import com.jaiswal.gui.components.ToolbarPanel;
import com.jaiswal.gui.components.UserListPanel;
import com.jaiswal.gui.utils.IconLoader;
import com.jaiswal.gui.utils.UIConstants;
import com.jaiswal.shared.IDrawable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;

/**
 * Main entry point for the Whiteboard GUI application.
 * Manages the overall UI structure and coordinates between components.
 */
public class WhiteboardGUI extends JFrame {
    private final WhiteboardClient client;
    private final boolean isManager;

    // UI Components
    private CanvasPanel canvasPanel;
    private UserListPanel userListPanel;
    private ToolbarPanel toolbarPanel;
    private StatusBarPanel statusBarPanel;

    // Menu components
    private JMenuBar menuBar;

    /**
     * Constructor for WhiteboardGUI
     * @param client The client instance to connect with
     * @param isManager Whether this client is the whiteboard manager
     */
    public WhiteboardGUI(WhiteboardClient client, boolean isManager) {
        this.client = client;
        this.isManager = isManager;

        setTitle("Collaborative Whiteboard - " + (isManager ? "Manager" : "Participant"));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Set application icon
        setIconImage(IconLoader.loadIcon("app_icon.png").getImage());

        // Apply look and feel
        applyLookAndFeel();

        // Initialize and setup all UI components
        initComponents();
        setupLayout();
        setupListeners();

        // Handle window close event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    /**
     * Attempts to set a modern look and feel
     */
    private void applyLookAndFeel() {
        try {
            // Try to use FlatLaf (comment this out if not using the library)
            // FlatLightLaf.install();

            // Fallback to system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Customize UI defaults for a more modern look
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);

            // Set global font
            Font defaultFont = new Font(UIConstants.FONT_FAMILY, Font.PLAIN, UIConstants.FONT_SIZE_NORMAL);
            UIManager.put("Button.font", defaultFont);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("Menu.font", defaultFont);
            UIManager.put("MenuItem.font", defaultFont);
            UIManager.put("Panel.font", defaultFont);
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
    }

    /**
     * Initialize all UI components
     */
    private void initComponents() {
        // Create all main panels
        canvasPanel = new CanvasPanel(client);
        userListPanel = new UserListPanel(client, isManager);
        toolbarPanel = new ToolbarPanel(canvasPanel);
        statusBarPanel = new StatusBarPanel();

        // Create menu bar
        menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Set initial status
        updateStatus("Connected as " + (isManager ? "Manager" : "Participant"));
    }

    /**
     * Set up the layout of the GUI components
     */
    private void setupLayout() {
        // Main container with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Top area: Toolbar
        mainPanel.add(toolbarPanel, BorderLayout.NORTH);

        // Center area: Canvas inside a scroll pane
        JScrollPane canvasScrollPane = new JScrollPane(canvasPanel);
        canvasScrollPane.setBorder(BorderFactory.createEmptyBorder());
        canvasScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        canvasScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        // Right area: User list panel
        // Add a split pane for resizable layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                canvasScrollPane, userListPanel);
        splitPane.setResizeWeight(0.85); // Give more space to canvas by default
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Bottom area: Status bar
        mainPanel.add(statusBarPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Create the menu bar with File and other menus
     * @return The configured menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(UIConstants.TOOLBAR_BACKGROUND);

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(UIConstants.TEXT_COLOR);

        // Always add these items
        JMenuItem newItem = new JMenuItem("New Whiteboard");
        newItem.setIcon(IconLoader.loadIcon("new.png"));
        newItem.addActionListener(e -> client.clearCanvas());

        JMenuItem closeItem = new JMenuItem("Exit");
        closeItem.setIcon(IconLoader.loadIcon("exit.png"));
        closeItem.addActionListener(e -> exitApplication());

        fileMenu.add(newItem);

        // Only add these menu items for manager
        if (isManager) {
            JMenuItem openItem = new JMenuItem("Open...");
            openItem.setIcon(IconLoader.loadIcon("open.png"));
            openItem.addActionListener(e -> openWhiteboard());

            JMenuItem saveItem = new JMenuItem("Save");
            saveItem.setIcon(IconLoader.loadIcon("save.png"));
            saveItem.addActionListener(e -> saveWhiteboard(false));

            JMenuItem saveAsItem = new JMenuItem("Save As...");
            saveAsItem.setIcon(IconLoader.loadIcon("save_as.png"));
            saveAsItem.addActionListener(e -> saveWhiteboard(true));

            fileMenu.add(openItem);
            fileMenu.add(new JSeparator());
            fileMenu.add(saveItem);
            fileMenu.add(saveAsItem);
        }

        fileMenu.add(new JSeparator());
        fileMenu.add(closeItem);

        // View menu for all users
        JMenu viewMenu = new JMenu("View");
        viewMenu.setForeground(UIConstants.TEXT_COLOR);

        JMenuItem zoomInItem = new JMenuItem("Zoom In");
        zoomInItem.setIcon(IconLoader.loadIcon("zoom_in.png"));
        zoomInItem.addActionListener(e -> canvasPanel.zoomIn());

        JMenuItem zoomOutItem = new JMenuItem("Zoom Out");
        zoomOutItem.setIcon(IconLoader.loadIcon("zoom_out.png"));
        zoomOutItem.addActionListener(e -> canvasPanel.zoomOut());

        JMenuItem resetZoomItem = new JMenuItem("Reset Zoom");
        resetZoomItem.setIcon(IconLoader.loadIcon("zoom_reset.png"));
        resetZoomItem.addActionListener(e -> canvasPanel.resetZoom());

        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(resetZoomItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(UIConstants.TEXT_COLOR);

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setIcon(IconLoader.loadIcon("about.png"));
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * Set up event listeners for global application events
     */
    private void setupListeners() {
        // Global keyboard shortcuts
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            // Handle global keyboard shortcuts here
            return false;
        });
    }

    /**
     * Shows the about dialog
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Collaborative Whiteboard Application\n" +
                        "Version 1.0\n\n" +
                        "A distributed whiteboard application for real-time collaboration.",
                "About Whiteboard",
                JOptionPane.INFORMATION_MESSAGE,
                IconLoader.loadIcon("app_icon.png"));
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
        SwingUtilities.invokeLater(() -> userListPanel.updateUsers(users));
    }

    /**
     * Update status bar message
     * @param message The status message to display
     */
    public void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> statusBarPanel.setStatus(message));
    }

    /**
     * Show a notification message
     * @param message The message to display
     */
    public void showNotification(String message) {
        SwingUtilities.invokeLater(() -> {
            updateStatus(message);
            JOptionPane.showMessageDialog(this,
                    message,
                    "Notification",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * Show a confirmation dialog
     * @param message The message to display
     * @return true if confirmed, false otherwise
     */
    public boolean showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this,
                message,
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Open a whiteboard file
     */
    private void openWhiteboard() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Whiteboard");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Whiteboard files (*.wb)", "wb"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getPath();
            client.loadWhiteboard(path);
            updateStatus("Opened whiteboard: " + path);
        }
    }

    /**
     * Save a whiteboard to a file
     * @param saveAs Whether to prompt for a new filename
     */
    private void saveWhiteboard(boolean saveAs) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Whiteboard");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Whiteboard files (*.wb)", "wb"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getPath();
            if (!path.endsWith(".wb")) {
                path += ".wb";
            }
            client.saveWhiteboard(path);
            updateStatus("Saved whiteboard to: " + path);
        }
    }

    /**
     * Safely exit the application
     */
    private void exitApplication() {
        // Ask for confirmation if manager
        if (isManager) {
            boolean confirm = showConfirmDialog(
                    "Closing this window will disconnect all users.\nDo you want to continue?");
            if (!confirm) {
                return;
            }
        }

        client.disconnect();
        dispose();
        System.exit(0);
    }
}