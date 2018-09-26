package ivotewebserver.action;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

public class HomeAction extends ActionSupport implements SessionAware {

	private Map<String, Object> session;
	
	@Override
	public String execute() {
		if(!session.containsKey("loggedin")){
			return LOGIN;
		}else{
			if((boolean)session.get("loggedin")){
				if(session.containsKey("UserBean")){
					return "user";
				}else if(session.containsKey("AdminBean")){
					return "admin";
				}else{
					return LOGIN;
				}
			}else{
				return LOGIN;
			}
		}
	}
	
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
