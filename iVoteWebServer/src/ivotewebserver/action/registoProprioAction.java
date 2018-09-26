package ivotewebserver.action;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.UserBean;
import rmiserver.RegistoVoto;

public class registoProprioAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	private String ide, cc;
	private Map<String, String> result;
	
	@Override
	public String execute() throws Exception {
		UserBean bean = this.getUserBean();
		result = new HashMap<>();
		System.out.println("-1");
		if(bean != null){
			String cc = bean.getUsername();
			// é user
			System.out.println("0");
			if(ide.trim().equals("")){
				addActionError("Campo invalido. Nao pode ser deixado em branco/apenas espacos");
				return INPUT;
			}else{
				try{
			    	RegistoVoto r = bean.getRegisto(ide, cc);
			    	System.out.println("1");
			    	int sum = 0;
			    	if (r != null) {
			    		System.out.println("2");
			    		sum++;
			    		result.put("cc",r.cc);
			    		result.put("idE",this.ide);
			    		result.put("local",r.nomeDep);
			            result.put("data",r.dataVoto.toString());
			            System.out.println("3");
			    	}
			    	bean.setResultMap(result);
			    	System.out.println("4");
			    	bean.setSum(sum);
			    	System.out.println("5");
		    	}catch(RemoteException e){
		    		System.out.println("6");
		    		e.printStackTrace();
		    		return ERROR;
		    	}
			}
		}else{
			return LOGIN;
		}
		return SUCCESS;
	}
	
	
	public UserBean getUserBean() {
		if(!session.containsKey("UserBean"))
			return null;
		return (UserBean) session.get("UserBean");
	}

	public void setUserBean(UserBean UserBean) {
		this.session.put("UserBean", UserBean);
	}
	

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getIde() {
		return ide;
	}

	public void setIde(String ide) {
		this.ide = ide;
	}

	public Map<String, String> getResult() {
		return result;
	}

	public void setResult(Map<String, String> result) {
		this.result = result;
	}
	
}
