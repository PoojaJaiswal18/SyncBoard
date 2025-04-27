package com.jaiswal.client;

import com.jaiswal.gui.WhiteboardGUI;
import com.jaiswal.shared.*;
import com.jaiswal.shared.shapes.Shape;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Client implementation for the distributed whiteboard application.
 * Handles communication with the server and updates the GUI.
 */
public class WhiteboardClient implements IRemoteClient {
    private final String host;
    private final int port;
    private final String username;
    private final boolean isManager;

    private Registry registry;
    private IRemoteWhiteboard server;
    private WhiteboardGUI gui;
    private boolean connected = false;

    /**
     * Constructor for WhiteboardClient
     * @param host The server host address
     * @param port The server port
     * @param username The user's username
     * @param isManager Whether this client is the whiteboard manager
     */
    public WhiteboardClient(String host, int port, String username, boolean isManager) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.isManager = isManager;

        initGUI();
    }

    /**
     * Initialize the graphical user interface
     */
    private void initGUI() {
        SwingUtilities.invokeLater(() -> {
            gui = new WhiteboardGUI(this, isManager);
            gui.setVisible(true);
        });
    }

    /**
     * Connect to the whiteboard server
     * @return true if connection successful, false otherwise
     */
    public boolean connect() {
        try {
            // Set security policy
            System.setProperty("java.security.policy", "security.policy");

            // Modern approach - no SecurityManager needed in newer Java versions

            // Get registry and lookup the server
            registry = LocateRegistry.getRegistry(host, port);
            server = (IRemoteWhiteboard) registry.lookup("WhiteboardServer");

            // Export this client
            IRemoteClient stub = (IRemoteClient) UnicastRemoteObject.exportObject(this, 0);

            // Request to join
            boolean approved = server.requestJoin(username, stub);

            if (!approved) {
                if (isManager) {
                    showError("Failed to create whiteboard. Server might already have a manager.");
                } else {
                    showError("Join request was denied by the manager.");
                }
                return false;
            }

            connected = true;
            return true;

        } catch (RemoteException | NotBoundException e) {
            showError("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Disconnect from the whiteboard server
     */
    public void disconnect() {
        if (connected) {
            try {
                server.disconnect(username);
                connected = false;
            } catch (RemoteException e) {
                System.err.println("Error disconnecting: " + e.getMessage());
            } finally {
                try {
                    // Clean up RMI resources
                    UnicastRemoteObject.unexportObject(this, true);
                } catch (Exception ignored) {
                    // Ignore exceptions during cleanup
                }
            }
        }
    }

    /**
     * Send a drawable shape to the server
     * @param drawable The drawable element to send
     */
    public void drawShape(IDrawable drawable) {
        try {
            if (drawable instanceof Shape) {
                server.drawShape((Shape) drawable);
            } else {
                showError("Error: Invalid shape type");
            }
        } catch (RemoteException e) {
            showError("Error drawing shape: " + e.getMessage());
        } catch (ClassCastException e) {
            showError("Error: Shape type mismatch - " + e.getMessage());
        }
    }

    /**
     * Send text element to the server
     * @param text The text element to send
     */
    public void drawText(TextElement text) {
        try {
            server.drawText(text);
        } catch (RemoteException e) {
            showError("Error adding text: " + e.getMessage());
        }
    }

    /**
     * Clear the whiteboard canvas
     */
    public void clearCanvas() {
        try {
            server.clearCanvas();
        } catch (RemoteException e) {
            showError("Error clearing canvas: " + e.getMessage());
        }
    }

    /**
     * Save the whiteboard state to a file
     * @param filename The filename to save to
     */
    public void saveWhiteboard(String filename) {
        try {
            CompletableFuture.supplyAsync(() -> {
                try {
                    return server.saveWhiteboard(filename);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }).thenAccept(success -> {
                if (success) {
                    showMessage("Whiteboard saved successfully.");
                } else {
                    showError("Failed to save whiteboard.");
                }
            }).exceptionally(e -> {
                showError("Error saving whiteboard: " + e.getMessage());
                return null;
            });
        } catch (Exception e) {
            showError("Error initiating save: " + e.getMessage());
        }
    }

    /**
     * Load a whiteboard state from a file
     * @param filename The filename to load from
     */
    public void loadWhiteboard(String filename) {
        try {
            CompletableFuture.supplyAsync(() -> {
                try {
                    return server.loadWhiteboard(filename);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }).thenAccept(success -> {
                if (success) {
                    showMessage("Whiteboard loaded successfully.");
                } else {
                    showError("Failed to load whiteboard.");
                }
            }).exceptionally(e -> {
                showError("Error loading whiteboard: " + e.getMessage());
                return null;
            });
        } catch (Exception e) {
            showError("Error initiating load: " + e.getMessage());
        }
    }

    /**
     * Kick a user from the whiteboard (manager only)
     * @param username The username of the user to kick
     */
    public void kickUser(String username) {
        try {
            server.kickUser(username);
        } catch (RemoteException e) {
            showError("Error kicking user: " + e.getMessage());
        }
    }

    /**
     * Get the username of this client
     * @return The username
     */
    public String getUsernameLocal() {
        return username;
    }

    // IRemoteClient implementation
    @Override
    public void updateCanvas(Map<Integer, IDrawable> state) throws RemoteException {
        gui.updateCanvas(state);
    }

    @Override
    public void updateUserList(List<String> users) throws RemoteException {
        gui.updateUserList(users);
    }

    @Override
    public void receiveNotification(String message) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            boolean approved = gui.showConfirmDialog(message);
            try {
                // In a real application, you would send this approval back to the server
                // For simplicity, we're auto-approving in the server
            } catch (Exception e) {
                showError("Error responding to notification: " + e.getMessage());
            }
        });
    }

    @Override
    public void joinRequestResult(boolean approved) throws RemoteException {
        if (approved) {
            showMessage("You have joined the whiteboard.");
        } else {
            showError("Your join request was denied.");
            System.exit(0);
        }
    }

    @Override
    public void kickedFromServer() throws RemoteException {
        showError("You have been kicked from the whiteboard.");
        System.exit(0);
    }

    @Override
    public void managerClosedWhiteboard() throws RemoteException {
        showError("The manager has closed the whiteboard.");
        System.exit(0);
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    // Helper methods
    private void showMessage(String message) {
        SwingUtilities.invokeLater(() ->
                gui.showNotification(message)
        );
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(gui, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}
