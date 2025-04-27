package com.jaiswal.shared;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IRemoteClient extends Remote {
    // Update methods to be called by server
    void updateCanvas(Map<Integer, IDrawable> state) throws RemoteException;
    void updateUserList(List<String> users) throws RemoteException;
    void receiveNotification(String message) throws RemoteException;
    void joinRequestResult(boolean approved) throws RemoteException;
    void kickedFromServer() throws RemoteException;
    void managerClosedWhiteboard() throws RemoteException;
    String getUsername() throws RemoteException;
}