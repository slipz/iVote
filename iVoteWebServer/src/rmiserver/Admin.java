package rmiserver;

import java.io.Serializable;
import java.util.Date;

public class Admin implements Serializable {
	
    public String username;
    public String password;

	public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
}
