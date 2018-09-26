package ivotewebserver.action;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.HashMap;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import ivotewebserver.model.AdminBean;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import rmiserver.Eleicao;
import rmiserver.RegistoVoto;


public class verRegistoEleitor extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	private String cc, ide;
	private Map<String, String> result;
	
	
	@Override
	public String execute() throws Exception {
		AdminBean bean = this.getAdminBean();
		result = new HashMap<>();
		if(bean != null){
			// é admin
			if(cc.trim().equals("") || ide.trim().equals("")){
				addActionError("Campos invalidos. Nao podem ser deixados em branco/apenas espacos");
				return INPUT;
			}else{
		    	try{
			    	RegistoVoto r = bean.getRegisto(ide, cc);
			    	int sum = 0;
			    	if (r != null) {
			    		sum++;
			    		result.put("cc",r.cc);
			    		result.put("idE",this.ide);
			    		result.put("local",r.nomeDep);
			            result.put("data",r.dataVoto.toString());
			    	}
			    	bean.setResultMap(result);
			    	bean.setSum(sum);
		    	}catch(Exception e){
		    		e.printStackTrace();
		    		return ERROR;
		    	}
			}
		}else{
			return LOGIN;
		}
		return SUCCESS;
		
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
