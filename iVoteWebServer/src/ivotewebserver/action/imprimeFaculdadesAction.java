package ivotewebserver.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;
import rmiserver.Faculdade;

public class imprimeFaculdadesAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 1L;
    private Map<String, Object> session;
    private CopyOnWriteArrayList<Map<String, String>> result;
    
    @Override
	public String execute() throws Exception {
    	result = new CopyOnWriteArrayList<>();
    	AdminBean bean = this.getAdminBean();
    	if(bean != null){
	    	try{
		    	ArrayList<Faculdade> facs = bean.getFaculdades();
		    	int sum = 0;
		    	for(Faculdade x: facs){
		    		sum++;
		    		HashMap<String, String> resultAux = new HashMap<>();
		    		resultAux.put("nomeFac",x.nome);
		    		resultAux.put("morada",x.morada);
		            resultAux.put("telf",x.ntlf);
		            resultAux.put("email",x.email);
		            result.add(resultAux);
		    	}
		    	bean.setResult(result);
		    	bean.setSum(sum);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
    	}else{
    		return LOGIN;
    	}
    	
    	return SUCCESS;
    }
    
    
    
    public CopyOnWriteArrayList<Map<String, String>> getResult() {
		return result;
	}

	public void setResult(CopyOnWriteArrayList<Map<String, String>> result) {
		this.result = result;
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
