package com.jaiswal.gui.components;

import com.jaiswal.gui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A status bar panel that displays application status messages, user count,
 * and other relevant information at the bottom of the main window.
 */
public class StatusBarPanel extends JPanel {
    private JLabel statusLabel;
    private JLabel userCountLabel;
    private JLabel timeLabel;
    private Timer timer;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Creates a new StatusBarPanel.
     */
    public StatusBarPanel() {
        initComponents();
        setupLayout();
        startTimeUpdates();
    }

    /**
     * Initialize the UI components.
     */
    private void initComponents() {
        setPreferredSize(new Dimension(0, 28));

        // Status message label (left-aligned)
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(UIConstants.FONT_NORMAL);

        // User count label (right-aligned)
        userCountLabel = new JLabel("Users: 1");
        userCountLabel.setFont(UIConstants.FONT_NORMAL);

        // Time label (right-aligned)
        timeLabel = new JLabel();
        timeLabel.setFont(UIConstants.FONT_NORMAL);
        updateTime(); // Set initial time
    }

    /**
     * Setup the layout of components.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UIConstants.BORDER_COLOR),
                new EmptyBorder(4, 10, 4, 10)
        ));
        setBackground(UIConstants.TOOLBAR_BACKGROUND);

        // Add main status to the left
        add(statusLabel, BorderLayout.WEST);

        // Create a panel for right-aligned items
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(userCountLabel);
        rightPanel.add(new JSeparator(JSeparator.VERTICAL) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(1, 16);
            }
        });
        rightPanel.add(timeLabel);

        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Start the timer to update the time display.
     */
    private void startTimeUpdates() {
        timer = new Timer(1000, e -> updateTime());
        timer.start();
    }

    /**
     * Update the time display.
     */
    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        timeLabel.setText(TIME_FORMATTER.format(now));
    }

    /**
     * Set the status message to display.
     *
     * @param status The status message
     */
    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    /**
     * Update the user count display.
     *
     * @param count The number of users
     */
    public void setUserCount(int count) {
        userCountLabel.setText("Users: " + count);
    }

    /**
     * Display a temporary status message that reverts to the previous message after a delay.
     *
     * @param tempStatus The temporary status message
     * @param durationMs The duration in milliseconds to show the temporary message
     */
    public void setTemporaryStatus(String tempStatus, int durationMs) {
        final String oldStatus = statusLabel.getText();
        statusLabel.setText(tempStatus);

        // Highlight the status briefly with a different color
        Color oldColor = statusLabel.getForeground();
        statusLabel.setForeground(UIConstants.INFO_COLOR);

        // Revert after the specified duration
        Timer tempTimer = new Timer(durationMs, e -> {
            statusLabel.setText(oldStatus);
            statusLabel.setForeground(oldColor);
        });
        tempTimer.setRepeats(false);
        tempTimer.start();
    }

    /**
     * Clean up resources when the panel is no longer needed.
     */
    public void cleanup() {
        if (timer != null) {
            timer.stop();
        }
    }
}