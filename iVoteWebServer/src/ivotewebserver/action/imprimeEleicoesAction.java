package ivotewebserver.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;
import rmiserver.Eleicao;

public class imprimeEleicoesAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 1L;
    private Map<String, Object> session;
    private CopyOnWriteArrayList<Map<String, String>> result;
    
    @Override
	public String execute() throws Exception {
    	result = new CopyOnWriteArrayList<>();
    	try{
	    	ArrayList<Eleicao> eleicoes = this.getAdminBean().getEleicoes();
	    	int sum = 0;
	    	String[] list = {"Núcleo de Estudantes","Conselho Geral"};
	    	for(Eleicao x: eleicoes){
	    		sum++;
	    		HashMap<String, String> resultAux = new HashMap<>();
	    		resultAux.put("titulo",x.titulo);
	    		resultAux.put("idEleicao",x.id);
	            resultAux.put("tipo",list[x.tipo-1]);
	            result.add(resultAux);
	    	}
	    	this.getAdminBean().setResult(result);
	    	this.getAdminBean().setSum(sum);
    	}catch(Exception e){
    		e.printStackTrace();
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
