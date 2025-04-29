package com.jaiswal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jaiswal.client.CreateWhiteboard;
import com.jaiswal.client.JoinWhiteboard;
import com.jaiswal.server.WhiteboardServer;

/**
 * A unified launcher for the SyncBoard application.
 * Provides a professional interface to start different components
 * and handles error logging/recovery.
 */
public class WhiteboardLauncher {

    private static final String LOG_DIR = "logs";
    private static PrintStream logStream;

    public static void main(String[] args) {
        // Setup error logging
        setupErrorLogging();

        try {
            if (args.length < 1) {
                printUsage();
                System.exit(1);
            }

            String command = args[0].toLowerCase();
            String[] remainingArgs = new String[args.length - 1];
            System.arraycopy(args, 1, remainingArgs, 0, args.length - 1);

            // Set security policy for all commands
            createSecurityPolicyIfNeeded();

            // Set security property before creating security manager
            System.setProperty("java.security.policy", new File("security.policy").getAbsolutePath());

            // Grant permissions if security manager is used
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            switch (command) {
                case "server":
                    startServer(remainingArgs);
                    break;
                case "create":
                    createWhiteboard(remainingArgs);
                    break;
                case "join":
                    joinWhiteboard(remainingArgs);
                    break;
                case "help":
                    printUsage();
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    printUsage();
                    System.exit(1);
            }
        } catch (Exception e) {
            handleException("Fatal error in WhiteboardLauncher", e);
        }
    }

    private static void startServer(String[] args) {
        try {
            // Set default values
            String hostname = args.length > 0 ? args[0] : "localhost";
            int port = args.length > 1 ? Integer.parseInt(args[1]) : 8001; // Consistent default port

            System.out.println("Starting Whiteboard Server on " + hostname + ":" + port + "...");

            // Set crucial RMI properties
            System.setProperty("java.rmi.server.hostname", hostname);
            System.setProperty("java.rmi.server.useLocalHostname", "true");
            System.setProperty("java.net.preferIPv4Stack", "true");

            // Get local IP for display
            String localIP = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Local IP address: " + localIP);

            // Create the whiteboard server with the specified host and port
            WhiteboardServer server = new WhiteboardServer(hostname, port);
            server.start();  // Start the server (this will create registry and bind)

            System.out.println("WhiteboardServer is running on " + hostname + ":" + port);

        } catch (Exception e) {
            handleException("Error starting server", e);
        }
    }

    private static void createWhiteboard(String[] args) {
        try {
            System.out.println("Creating a new whiteboard as manager...");

            // Validate args
            if (args.length < 2) {
                System.out.println("Error: Missing required arguments for create command.");
                System.out.println("Usage: syncboard create <serverHostname> <port> <username>");
                System.exit(1);
            }

            String hostname = args[0];
            int port = Integer.parseInt(args[1]);
            String username = args.length > 2 ? args[2] : "manager";

            System.out.println("Connecting to server at " + hostname + ":" + port + " as " + username);

            // Execute create whiteboard
            String[] createArgs = {hostname, String.valueOf(port), username};
            CreateWhiteboard.main(createArgs);
        } catch (NumberFormatException e) {
            handleException("Invalid port number", e);
        } catch (Exception e) {
            handleException("Error creating whiteboard", e);
        }
    }

    private static void joinWhiteboard(String[] args) {
        try {
            System.out.println("Joining an existing whiteboard...");

            // Validate args
            if (args.length < 2) {
                System.out.println("Error: Missing required arguments for join command.");
                System.out.println("Usage: syncboard join <serverHostname> <port> <username>");
                System.exit(1);
            }

            String hostname = args[0];
            int port = Integer.parseInt(args[1]);
            String username = args.length > 2 ? args[2] : "user";

            System.out.println("Connecting to server at " + hostname + ":" + port + " as " + username);

            // Execute join whiteboard
            String[] joinArgs = {hostname, String.valueOf(port), username};
            JoinWhiteboard.main(joinArgs);
        } catch (NumberFormatException e) {
            handleException("Invalid port number", e);
        } catch (Exception e) {
            handleException("Error joining whiteboard", e);
        }
    }

    private static void printUsage() {
        System.out.println("SyncBoard - Distributed Whiteboard Application");
        System.out.println("============================================");
        System.out.println("Usage: syncboard <command> [options]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  server <hostname> <port>       - Start the whiteboard server");
        System.out.println("  create <server> <port> <user>  - Create a new whiteboard as manager");
        System.out.println("  join <server> <port> <user>    - Join an existing whiteboard");
        System.out.println("  help                           - Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  syncboard server localhost 8001");
        System.out.println("  syncboard create localhost 8001 manager");
        System.out.println("  syncboard join localhost 8001 user1");
    }

    private static void setupErrorLogging() {
        try {
            // Create logs directory if it doesn't exist
            File logDir = new File(LOG_DIR);
            if (!logDir.exists()) {
                logDir.mkdir();
            }

            // Create log file with timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            File logFile = new File(LOG_DIR + "/syncboard_" + timestamp + ".log");

            // Setup log stream
            logStream = new PrintStream(new FileOutputStream(logFile, true));

            // Redirect System.err to the log file
            System.setErr(new PrintStream(new FileOutputStream(logFile, true)));

            log("Log file initialized: " + logFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Warning: Could not initialize log file: " + e.getMessage());
        }
    }

    /**
     * Creates a security policy file if it doesn't exist
     */
    private static void createSecurityPolicyIfNeeded() {
        try {
            File securityPolicy = new File("security.policy");
            if (!securityPolicy.exists()) {
                try (PrintStream writer = new PrintStream(securityPolicy)) {
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
        } catch (Exception e) {
            System.err.println("Error creating security policy: " + e.getMessage());
        }
    }

    private static void handleException(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        log(message + ": " + e.getMessage());
        e.printStackTrace();
        if (logStream != null) {
            e.printStackTrace(logStream);
        }

        // Provide user-friendly recovery suggestions
        System.err.println("\nPossible solutions:");
        System.err.println("- Check that the server is running and accessible");
        System.err.println("- Verify that ports aren't blocked by firewall");
        System.err.println("- Ensure all application files are in the same directory");
        System.err.println("- Verify the security.policy file is correctly set up");
        System.err.println("- Check the log file in the logs directory for more details");

        System.exit(1);
    }

    private static void log(String message) {
        if (logStream != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            logStream.println("[" + timestamp + "] " + message);
            logStream.flush();
        }
    }
}