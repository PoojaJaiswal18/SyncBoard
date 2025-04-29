package com.jaiswal.server;
import com.jaiswal.shared.IDrawable;
import com.jaiswal.shared.IRemoteClient;
import com.jaiswal.shared.IRemoteWhiteboard;
import com.jaiswal.shared.TextElement;
import com.jaiswal.shared.shapes.Shape;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;

public class WhiteboardServer implements IRemoteWhiteboard {
    private final String host;
    private final int port;
    private Registry registry;
    private boolean isRunning = false;

    // Store connected clients
    private IRemoteClient managerClient;
    private final Map<String, IRemoteClient> clients = new ConcurrentHashMap<>();

    // Store whiteboard content
    private final Map<Integer, IDrawable> canvasState = new ConcurrentHashMap<>();
    private int nextElementId = 1;

    /**
     * Constructor for WhiteboardServer
     * @param host The host address to bind to
     * @param port The port to run on
     */
    public WhiteboardServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Starts the RMI server
     */
    public void start() {
        if (isRunning) {
            System.out.println("Server is already running");
            return;
        }

        try {
            // Create and set security policy if it doesn't exist
            createSecurityPolicyIfNeeded();

            // Set security manager if not already set
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            // Set hostname property
            System.setProperty("java.rmi.server.hostname", host);

            // Create registry
            try {
                System.out.println("Creating RMI registry on port " + port);
                registry = LocateRegistry.createRegistry(port);
                System.out.println("RMI registry created on port " + port);
            } catch (RemoteException e) {
                System.out.println("RMI registry already exists, getting existing registry");
                registry = LocateRegistry.getRegistry(host, port);
                System.out.println("RMI registry found on port " + port);
            }

            // Export this object
            IRemoteWhiteboard stub = (IRemoteWhiteboard) UnicastRemoteObject.exportObject(this, 0);

            // Bind to registry
            registry.rebind("WhiteboardServer", stub);
            isRunning = true;

            System.out.println("WhiteboardServer is running on " + host + ":" + port);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Creates a security policy file if it doesn't exist
     */
    private void createSecurityPolicyIfNeeded() {
        try {
            File securityPolicy = new File("security.policy");
            if (!securityPolicy.exists()) {
                try (PrintWriter writer = new PrintWriter(securityPolicy)) {
                    writer.println("grant {");
                    writer.println("    permission java.net.SocketPermission \"*:1024-65535\", \"connect,accept,resolve\";");
                    writer.println("    permission java.net.SocketPermission \"*:80\", \"connect\";");
                    writer.println("    permission java.net.SocketPermission \"*:8001\", \"connect,accept,resolve\";");
                    writer.println("    permission java.net.SocketPermission \"*:1099\", \"connect,accept,resolve\";");
                    writer.println("    permission java.io.FilePermission \"<<ALL FILES>>\", \"read,write,execute,delete\";");
                    writer.println("    permission java.util.PropertyPermission \"*\", \"read,write\";");
                    writer.println("    permission java.security.AllPermission;");
                    writer.println("};");
                }
                System.out.println("Created security policy file");
            }
            System.setProperty("java.security.policy", securityPolicy.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error creating security policy: " + e.getMessage());
        }
    }

    @Override
    public synchronized boolean requestJoin(String username, IRemoteClient client) throws RemoteException {
        System.out.println("Join request from: " + username);

        // Check if username already exists
        if (clients.containsKey(username)) {
            System.out.println("Username already exists: " + username);
            return false;
        }

        // If this is the first client, make them the manager
        if (clients.isEmpty() && managerClient == null) {
            managerClient = client;
            clients.put(username, client);
            System.out.println(username + " joined as manager");

            // Update the new manager with current state
            client.updateCanvas(new HashMap<>(canvasState));
            client.updateUserList(new ArrayList<>(clients.keySet()));
            client.joinRequestResult(true);
            return true;
        }

        // Ask manager for approval
        try {
            managerClient.receiveNotification("User " + username + " wants to join. Do you approve?");

            // For simplicity in this example, we auto-approve
            // In a real implementation, you would need to handle the manager's response
            clients.put(username, client);

            // Update all clients with the new user list
            updateAllClientsUserList();

            // Update the new client with current canvas state
            client.updateCanvas(new HashMap<>(canvasState));
            client.joinRequestResult(true);

            System.out.println(username + " joined as client");
            return true;

        } catch (RemoteException e) {
            System.err.println("Error contacting manager: " + e.getMessage());
            return false;
        }
    }

    @Override
    public synchronized void disconnect(String username) throws RemoteException {
        if (clients.containsKey(username)) {
            IRemoteClient client = clients.get(username);
            clients.remove(username);
            System.out.println(username + " disconnected");

            // If manager left, close the whiteboard
            if (client != null && client.equals(managerClient)) {
                System.out.println("Manager left, closing whiteboard");
                notifyManagerClosed();
                // In a real application, you might want to choose a new manager instead
            } else {
                // Update all clients with new user list
                updateAllClientsUserList();
            }
        }
    }

    private String getManagerUsername() {
        try {
            for (Map.Entry<String, IRemoteClient> entry : clients.entrySet()) {
                if (entry.getValue().equals(managerClient)) {
                    return entry.getKey();
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting manager username: " + e.getMessage());
        }
        return null;
    }

    @Override
    public synchronized void kickUser(String username) throws RemoteException {
        if (clients.containsKey(username)) {
            IRemoteClient client = clients.get(username);
            clients.remove(username);

            try {
                client.kickedFromServer();
            } catch (RemoteException e) {
                System.err.println("Error notifying kicked user: " + e.getMessage());
            }

            System.out.println(username + " was kicked");
            updateAllClientsUserList();
        }
    }

    @Override
    public synchronized List<String> getConnectedUsers() throws RemoteException {
        return new ArrayList<>(clients.keySet());
    }

    @Override
    public synchronized int drawShape(Shape shape) throws RemoteException {
        // Assign ID and add to canvas state
        int id = nextElementId++;
        shape.setId(id);
        canvasState.put(id, shape);

        // Update all clients
        updateAllClientsCanvas();

        return id;
    }

    @Override
    public synchronized int drawText(TextElement text) throws RemoteException {
        // Assign ID and add to canvas state
        int id = nextElementId++;
        text.setId(id);
        canvasState.put(id, text);

        // Update all clients
        updateAllClientsCanvas();

        return id;
    }

    @Override
    public synchronized void clearCanvas() throws RemoteException {
        canvasState.clear();
        nextElementId = 1;

        // Update all clients
        updateAllClientsCanvas();
    }

    @Override
    public synchronized boolean saveWhiteboard(String filename) throws RemoteException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(new HashMap<>(canvasState));
            System.out.println("Whiteboard saved to " + filename);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving whiteboard: " + e.getMessage());
            return false;
        }
    }

    @Override
    public synchronized boolean loadWhiteboard(String filename) throws RemoteException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            @SuppressWarnings("unchecked")
            Map<Integer, IDrawable> loaded = (Map<Integer, IDrawable>) ois.readObject();

            // Update canvas state
            canvasState.clear();
            canvasState.putAll(loaded);

            // Set nextElementId to max ID + 1
            nextElementId = 1;
            for (Integer id : canvasState.keySet()) {
                if (id >= nextElementId) {
                    nextElementId = id + 1;
                }
            }

            // Update all clients
            updateAllClientsCanvas();

            System.out.println("Whiteboard loaded from " + filename);
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading whiteboard: " + e.getMessage());
            return false;
        }
    }

    @Override
    public synchronized Map<Integer, IDrawable> getCurrentState() throws RemoteException {
        return new HashMap<>(canvasState);
    }

    // Helper methods
    private void updateAllClientsCanvas() {
        Map<Integer, IDrawable> stateCopy = new HashMap<>(canvasState);
        for (Map.Entry<String, IRemoteClient> entry : new HashMap<>(clients).entrySet()) {
            try {
                entry.getValue().updateCanvas(stateCopy);
            } catch (RemoteException e) {
                System.err.println("Error updating client " + entry.getKey() + ": " + e.getMessage());
                // Remove disconnected client
                handleDisconnectedClient(entry.getKey());
            }
        }
    }

    private void updateAllClientsUserList() {
        List<String> userList = new ArrayList<>(clients.keySet());
        for (Map.Entry<String, IRemoteClient> entry : new HashMap<>(clients).entrySet()) {
            try {
                entry.getValue().updateUserList(userList);
            } catch (RemoteException e) {
                System.err.println("Error updating client " + entry.getKey() + " user list: " + e.getMessage());
                // Remove disconnected client
                handleDisconnectedClient(entry.getKey());
            }
        }
    }

    private void handleDisconnectedClient(String username) {
        try {
            clients.remove(username);
            System.out.println("Removed disconnected client: " + username);
        } catch (Exception e) {
            System.err.println("Error removing disconnected client: " + e.getMessage());
        }
    }

    private void notifyManagerClosed() {
        for (Map.Entry<String, IRemoteClient> entry : new HashMap<>(clients).entrySet()) {
            if (!entry.getValue().equals(managerClient)) {
                try {
                    entry.getValue().managerClosedWhiteboard();
                } catch (RemoteException e) {
                    System.err.println("Error notifying client of manager close: " + e.getMessage());
                }
            }
        }

        // Clear all clients
        clients.clear();
        managerClient = null;
    }

    // Main method to run the server directly
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1099;

        // Parse command line arguments if provided
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default: 1099");
                port = 1099;
            }
        }

        WhiteboardServer server = new WhiteboardServer(host, port);
        server.start();
    }
}