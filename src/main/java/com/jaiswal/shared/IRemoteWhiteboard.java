package com.jaiswal.shared;
import com.jaiswal.shared.shapes.Shape;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IRemoteWhiteboard extends Remote {
    // User management
    boolean requestJoin(String username, IRemoteClient client) throws RemoteException;
    void disconnect(String username) throws RemoteException;
    void kickUser(String username) throws RemoteException;
    List<String> getConnectedUsers() throws RemoteException;

    // Drawing operations
    int drawShape(Shape shape) throws RemoteException;
    int drawText(TextElement text) throws RemoteException;
    void clearCanvas() throws RemoteException;

    // File operations
    boolean saveWhiteboard(String filename) throws RemoteException;
    boolean loadWhiteboard(String filename) throws RemoteException;

    // Get current state
    Map<Integer, IDrawable> getCurrentState() throws RemoteException;
}