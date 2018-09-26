package ivotewebserver.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.UserBean;
import rmiserver.Eleicao;

public class listaEleicoesEleitor extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 1L;
    private Map<String, Object> session;
    private CopyOnWriteArrayList<Map<String, String>> result;
    
    @Override
	public String execute() throws Exception {
    	result = new CopyOnWriteArrayList<>();
    	UserBean bean = this.getUserBean();
    	if(bean != null){
	    	try{
	    		bean.setResult(result);
	    		bean.setSum(0);
		    	ArrayList<Eleicao> eleicoes = bean.getEleicoesEleitor(bean.getUsername());
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
		    	bean.setResult(result);
		    	bean.setSum(sum);
		    	return SUCCESS;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		return ERROR;
	    	}
    	}else{
    		return LOGIN;
    	}

    }
    
    
    
    public CopyOnWriteArrayList<Map<String, String>> getResult() {
		return result;
	}

	public void setResult(CopyOnWriteArrayList<Map<String, String>> result) {
		this.result = result;
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

}
