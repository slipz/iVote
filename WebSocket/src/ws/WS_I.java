package ws;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WS_I extends Remote{
    //void send_Notification_web(String usernameOnline, String message) throws RemoteException;
	void notifica_web(String message) throws RemoteException;
	void notifica_one(String message, String username) throws RemoteException;
	String getUsername() throws RemoteException;
}
