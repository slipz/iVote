package ivotewebserver.model;

import java.util.ArrayList;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import rmiserver.RMI_S_I;

public class LoginBean {
	private RMI_S_I server;
	private String username; // username and password supplied by the user
	private String password;
	
	
	
	public LoginBean() {
		try {
			server = (RMI_S_I) Naming.lookup("rmi://localhost:6501/rmi");
		}
		catch(NotBoundException|MalformedURLException|RemoteException e) {
			e.printStackTrace(); // what happens *after* we reach this line?
		}
	}

	/*public ArrayList<String> getAllUsers() throws RemoteException {
		return server.getAllUsers(); // are you going to throw all exceptions?
	}*/

	public String getUserMatchesPassword() throws RemoteException {
		return server.LoginWeb(this.username, this.password);
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
