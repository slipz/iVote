package ivotewebserver.action;

import java.rmi.RemoteException;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;

public class mostraMesasOnline extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	
	public String execute() { //para receber o form
		//faz cenas no RMI 
		AdminBean bean = this.getAdminBean();
		if(bean != null){
			return SUCCESS;
		}else{
			return LOGIN;
		}
		//return ERROR;
	}
	
	

	public AdminBean getAdminBean() {
		if(!session.containsKey("AdminBean"))
			return null;
		return (AdminBean) session.get("AdminBean");
	}

	public void setAdminBean(AdminBean AdminBean) {
		this.session.put("AdminBean", AdminBean);
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
