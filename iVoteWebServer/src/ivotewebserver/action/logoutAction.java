/**
 * 
 */
package ivotewebserver.action;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;
import ivotewebserver.model.UserBean;

/**
 * @author localhost
 *
 */
public class logoutAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	
	@Override
	public String execute() {
		this.session.clear();
		return SUCCESS;
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
