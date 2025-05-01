package com.jaiswal.gui.components;

import com.jaiswal.client.WhiteboardClient;
import com.jaiswal.gui.utils.IconLoader;
import com.jaiswal.gui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel displaying the list of connected users in the whiteboard session.
 * Provides additional controls for the whiteboard manager.
 */
public class UserListPanel extends JPanel {

    // Data and state
    private final WhiteboardClient client;
    private final boolean isManager;
    private final List<String> users = new ArrayList<>();

    // UI Components
    private final JList<String> userList;
    private final DefaultListModel<String> userListModel;
    private final JLabel titleLabel;
    private JButton kickButton;        // Removed final to allow conditional initialization
    private JButton grantControlButton; // Removed final to allow conditional initialization

    // Constants for UI
    private static final int PANEL_WIDTH = 200;
    private static final int TITLE_HEIGHT = 35;
    private static final int BUTTON_HEIGHT = 30;

    /**
     * Constructs a new UserListPanel
     *
     * @param client The whiteboard client
     * @param isManager Whether this user is the manager
     */
    public UserListPanel(WhiteboardClient client, boolean isManager) {
        this.client = client;
        this.isManager = isManager;

        // Set up panel properties
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(PANEL_WIDTH, 0));
        setBackground(UIConstants.PANEL_BACKGROUND);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UIConstants.BORDER_COLOR));

        // Create title section
        titleLabel = createTitleLabel();
        add(titleLabel, BorderLayout.NORTH);

        // Create user list
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        configureUserList();

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBackground(getBackground());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Create control buttons for manager
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        refreshUserList();
    }

    /**
     * Creates and configures the title label
     *
     * @return The configured title label
     */
    private JLabel createTitleLabel() {
        JLabel label = new JLabel("Connected Users", SwingConstants.CENTER);
        label.setFont(UIConstants.FONT_BOLD);  // Changed from SUBTITLE_FONT to FONT_BOLD
        label.setForeground(UIConstants.TEXT_COLOR);
        label.setPreferredSize(new Dimension(PANEL_WIDTH, TITLE_HEIGHT));
        label.setBackground(UIConstants.TOOLBAR_BACKGROUND);  // Changed from HEADER_BACKGROUND to TOOLBAR_BACKGROUND
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR),
                new EmptyBorder(5, 10, 5, 10)));

        return label;
    }

    /**
     * Configures the user list with custom renderer and behavior
     */
    private void configureUserList() {
        // Set visual properties
        userList.setBackground(getBackground());
        userList.setSelectionBackground(UIConstants.SELECTION_COLOR);  // Changed from SELECTION_BACKGROUND to SELECTION_COLOR
        userList.setSelectionForeground(UIConstants.TEXT_COLOR);  // Changed from SELECTION_FOREGROUND to TEXT_COLOR
        userList.setFixedCellHeight(35);
        userList.setBorder(new EmptyBorder(5, 5, 5, 5));
        userList.setFont(UIConstants.FONT_NORMAL);  // Changed from NORMAL_FONT to FONT_NORMAL

        // Set custom cell renderer
        userList.setCellRenderer(new UserListCellRenderer());

        // Add context menu for manager
        if (isManager) {
            userList.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        int index = userList.locationToIndex(e.getPoint());
                        userList.setSelectedIndex(index);

                        if (index >= 0) {
                            showUserContextMenu(e.getX(), e.getY());
                        }
                    }
                }
            });
        }

        // Selection listener for enabling/disabling buttons
        userList.addListSelectionListener(e -> {
            boolean hasSelection = !userList.isSelectionEmpty();
            if (kickButton != null) {
                kickButton.setEnabled(hasSelection);
            }
            if (grantControlButton != null) {
                grantControlButton.setEnabled(hasSelection);
            }
        });
    }

    /**
     * Creates the button panel with control actions for manager
     *
     * @return The configured button panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(getBackground());

        // Only create control buttons for manager
        if (isManager) {
            // Kick button
            kickButton = new JButton("Kick User");
            kickButton.setIcon(IconLoader.loadIcon("kick.png"));
            kickButton.setEnabled(false);
            kickButton.setPreferredSize(new Dimension(PANEL_WIDTH - 30, BUTTON_HEIGHT));
            kickButton.addActionListener(e -> {
                String selectedUser = userList.getSelectedValue();
                if (selectedUser != null) {
                    if (confirmAction("Are you sure you want to kick " + selectedUser + "?")) {
                        client.kickUser(selectedUser);
                    }
                }
            });

            // Grant control button
            grantControlButton = new JButton("Transfer Control");
            grantControlButton.setIcon(IconLoader.loadIcon("transfer.png"));
            grantControlButton.setEnabled(false);
            grantControlButton.setPreferredSize(new Dimension(PANEL_WIDTH - 30, BUTTON_HEIGHT));
            grantControlButton.addActionListener(e -> {
                String selectedUser = userList.getSelectedValue();
                if (selectedUser != null) {
                    if (confirmAction("Transfer manager control to " + selectedUser + "?")) {
                        transferControl(selectedUser);  // Using local method instead of directly calling client
                    }
                }
            });

            buttonPanel.add(kickButton);
            buttonPanel.add(grantControlButton);
        } else {
            // For non-managers, add a status indicator
            JLabel statusLabel = new JLabel("Participant Mode", SwingConstants.CENTER);
            statusLabel.setIcon(IconLoader.loadIcon("user.png"));
            statusLabel.setForeground(new Color(60, 60, 60));
            statusLabel.setFont(UIConstants.FONT_NORMAL.deriveFont((float)UIConstants.FONT_SIZE_SMALL));  // Changed from SMALL_FONT to derived FONT_NORMAL
            buttonPanel.add(statusLabel);
        }

        return buttonPanel;
    }

    /**
     * Helper method to transfer control to another user
     * Added this method since WhiteboardClient doesn't have a transferControl method
     *
     * @param username User to transfer control to
     */
    private void transferControl(String username) {
        try {
            // This method should communicate with the server via the client
            // Since the client doesn't have this method directly, you might need to implement it
            // For now, we'll just show a message
            JOptionPane.showMessageDialog(this,
                    "Transfer control functionality is not implemented yet.",
                    "Not Implemented",
                    JOptionPane.INFORMATION_MESSAGE);

            // When implemented, it would look like:
            // client.transferControl(username);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error transferring control: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Displays a context menu for user management actions
     *
     * @param x X-coordinate for menu display
     * @param y Y-coordinate for menu display
     */
    private void showUserContextMenu(int x, int y) {
        if (!isManager) return;

        String selectedUser = userList.getSelectedValue();
        if (selectedUser == null) return;

        JPopupMenu menu = new JPopupMenu();

        JMenuItem kickItem = new JMenuItem("Kick User");
        kickItem.setIcon(IconLoader.loadIcon("kick.png"));
        kickItem.addActionListener(e -> {
            if (confirmAction("Are you sure you want to kick " + selectedUser + "?")) {
                client.kickUser(selectedUser);
            }
        });

        JMenuItem controlItem = new JMenuItem("Transfer Control");
        controlItem.setIcon(IconLoader.loadIcon("transfer.png"));
        controlItem.addActionListener(e -> {
            if (confirmAction("Transfer manager control to " + selectedUser + "?")) {
                transferControl(selectedUser);  // Using local method instead of directly calling client
            }
        });

        menu.add(kickItem);
        menu.add(controlItem);

        menu.show(userList, x, y);
    }

    /**
     * Shows a confirmation dialog before performing an action
     *
     * @param message The confirmation message
     * @return True if user confirmed, false otherwise
     */
    private boolean confirmAction(String message) {
        return JOptionPane.showConfirmDialog(this,
                message,
                "Confirm Action",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Updates the user list with the current connected users
     *
     * @param users List of usernames
     */
    public void updateUsers(List<String> users) {
        this.users.clear();
        this.users.addAll(users);
        refreshUserList();
    }

    /**
     * Refreshes the user list display
     */
    private void refreshUserList() {
        userListModel.clear();

        // Update count in title
        titleLabel.setText("Connected Users (" + users.size() + ")");

        // Add all users to list model
        for (String user : users) {
            userListModel.addElement(user);
        }

        // Disable buttons if no users are selected
        if (kickButton != null) {
            kickButton.setEnabled(!userList.isSelectionEmpty());
        }
        if (grantControlButton != null) {
            grantControlButton.setEnabled(!userList.isSelectionEmpty());
        }
    }

    /**
     * Custom cell renderer for the user list
     */
    private class UserListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            JPanel panel = new JPanel(new BorderLayout(10, 0));
            panel.setBorder(new EmptyBorder(5, 10, 5, 10));

            // Get the username
            String username = (String) value;

            // Create user icon with status indicator
            JLabel iconLabel = new JLabel(IconLoader.loadIcon("user.png"));
            iconLabel.setOpaque(false);

            // Create username label
            JLabel usernameLabel = new JLabel(username);
            usernameLabel.setFont(UIConstants.FONT_NORMAL);  // Changed from NORMAL_FONT to FONT_NORMAL

            // Create status indicator (optional)
            JLabel statusLabel = new JLabel("");
            statusLabel.setFont(UIConstants.FONT_NORMAL.deriveFont((float)UIConstants.FONT_SIZE_SMALL));  // Changed from SMALL_FONT to derived FONT_NORMAL
            statusLabel.setForeground(new Color(100, 100, 100));

            // Handle special cases (e.g., self, manager)
            String clientUsername = client.getUsernameLocal();  // Changed from getUsername() to getUsernameLocal()

            if (username.equals(clientUsername)) {
                usernameLabel.setText(username + " (You)");
                usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.BOLD));
            }

            // Check if this user is the manager (this is a simplified check - actual implementation would depend on client data)
            if (index == 0 && isManager && username.equals(clientUsername)) {
                iconLabel.setIcon(IconLoader.loadIcon("manager.png"));
                statusLabel.setText("Manager");
                statusLabel.setForeground(new Color(0, 100, 0));
            }

            // Add components to panel
            panel.add(iconLabel, BorderLayout.WEST);
            panel.add(usernameLabel, BorderLayout.CENTER);
            panel.add(statusLabel, BorderLayout.EAST);

            // Set background based on selection state
            if (isSelected) {
                panel.setBackground(UIConstants.SELECTION_COLOR);  // Changed from SELECTION_BACKGROUND to SELECTION_COLOR
                usernameLabel.setForeground(UIConstants.TEXT_COLOR);  // Changed from SELECTION_FOREGROUND to TEXT_COLOR
            } else {
                panel.setBackground(list.getBackground());
                usernameLabel.setForeground(UIConstants.TEXT_COLOR);
            }

            return panel;
        }
    }
}