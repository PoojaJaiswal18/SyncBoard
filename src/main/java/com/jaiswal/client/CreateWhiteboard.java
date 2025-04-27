package com.jaiswal.client;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class CreateWhiteboard {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java CreateWhiteBoard <serverIPAddress> <serverPort> <username>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];

        SwingUtilities.invokeLater(() -> {
            WhiteboardClient client = new WhiteboardClient(host, port, username, true);
            boolean connected = client.connect();

            if (!connected) {
                JOptionPane.showMessageDialog(null, "Failed to connect to the server or create whiteboard.",
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}