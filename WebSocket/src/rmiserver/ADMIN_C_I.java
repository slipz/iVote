package rmiserver;

import java.rmi.*;
import java.util.Date;

public interface ADMIN_C_I extends Remote{
	// TODO JavaDoc
	//Func que imprime o estados das mesas
	public void print(String msg) throws java.rmi.RemoteException;
	
	public String getId() throws java.rmi.RemoteException;
	
	public void notifica(String msg) throws java.rmi.RemoteException;

}
