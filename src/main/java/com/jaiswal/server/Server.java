package com.jaiswal.server;

import java.io.File;
import java.io.PrintWriter;

public class Server {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8001;

        // Parse command line arguments if provided
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default: 8001");
            }
        }

        // Create security policy file if it doesn't exist
        createSecurityPolicyIfNeeded();

        // Set important system properties
        System.setProperty("java.security.policy", new File("security.policy").getAbsolutePath());
        System.setProperty("java.rmi.server.hostname", host);
        System.setProperty("java.rmi.server.useLocalHostname", "true");
        System.setProperty("java.net.preferIPv4Stack", "true");

        // Set security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // Create and start the server
        WhiteboardServer server = new WhiteboardServer(host, port);
        server.start();
    }

    private static void createSecurityPolicyIfNeeded() {
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
        } catch (Exception e) {
            System.err.println("Error creating security policy: " + e.getMessage());
        }
    }
}