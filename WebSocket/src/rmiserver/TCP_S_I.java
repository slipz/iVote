package rmiserver;

import java.rmi.Remote;

public interface TCP_S_I extends Remote {
	public String getDepName() throws java.rmi.RemoteException;
}
