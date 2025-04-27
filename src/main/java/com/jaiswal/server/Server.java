package com.jaiswal.server;

public class Server {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Server <serverIPAddress> <serverPort>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        WhiteboardServer server = new WhiteboardServer(host, port);
        server.start();
    }
}