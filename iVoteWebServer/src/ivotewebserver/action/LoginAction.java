package ivotewebserver.action;

import java.rmi.RemoteException;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.*;

public class LoginAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String username = null, password = null;
	private String tipo;

	@Override
	public String execute() {
		// any username is accepted without confirmation (should check using RMI)
		
		LoginBean bean = this.getHeyBean();
		bean.setUsername(username);
		bean.setPassword(password);
		
		try {
			String msg = bean.getUserMatchesPassword();
			if(msg.equals("normalUser")){
				this.tipo = "user";
				//Criar novo userBean
				UserBean defBean = new UserBean();
				defBean.setUsername(username);
				defBean.setPassword(password);
				//Remover possiveis beans antigos
				this.removeHeyBean();
				this.removeAdminBean();
				//Colocar novo
				this.setUserBean(defBean);
				//loggar o user
				session.put("username", username);
				session.put("password", password);
				session.put("loggedin", true); // this marks the user as logged in
				return "user";
				
			}else if(msg.equals("admin")){
				this.tipo = "admin";
				//Criar novo AdminBean
				AdminBean defBean = new AdminBean();
				defBean.setUsername(username);
				defBean.setPassword(password);
				//Remover possiveis beans antigos
				this.removeHeyBean();
				this.removeUserBean();
				//Colocar novo
				this.setAdminBean(defBean);
				//loggar o user
				session.put("username", username);
				session.put("password", password);
				session.put("loggedin", true); // this marks the user as logged in
				return "admin";
			}else{
				addActionError(msg);
				return LOGIN;
			}
			
		} catch (RemoteException e) {
			System.out.println("aqui3");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return LOGIN;
	}
	
	public void setUsername(String username) {
		this.username = username; // will you sanitize this input? maybe use a prepared statement?
	}

	public void setPassword(String password) {
		this.password = password; // what about this input? 
	}
	
	public LoginBean getHeyBean() {
		if(!session.containsKey("Bean"))
			this.setHeyBean(new LoginBean());
		return (LoginBean) session.get("Bean");
	}
	public void setHeyBean(LoginBean heyBean) {
		this.session.put("Bean", heyBean);
	}
	public void removeHeyBean(){
		if(session.containsKey("Bean"))
			session.remove("Bean");
	}
	
	public void setUserBean(UserBean bean){
		this.session.put("UserBean", bean);
	}
	public UserBean getUserBean() {
		if(!session.containsKey("UserBean"))
			this.setUserBean(new UserBean());
		return (UserBean) session.get("UserBean");
	}
	public void removeUserBean(){
		if(session.containsKey("UserBean"))
			session.remove("UserBean");
	}
	
	public void setAdminBean(AdminBean bean){
		this.session.put("AdminBean", bean);
	}
	public AdminBean getAdminBean() {
		if(!session.containsKey("AdminBean"))
			this.setAdminBean(new AdminBean());
		return (AdminBean) session.get("AdminBean");
	}
	public void removeAdminBean(){
		if(session.containsKey("AdminBean"))
			session.remove("AdminBean");
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
