package ivotewebserver.action;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;
import ivotewebserver.model.UserBean;
import rmiserver.Eleicao;
import rmiserver.Lista;

public class mostraListasEleitor extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	private Map<String, String> result;
	private String idEleicao;
	private CopyOnWriteArrayList<Map<String, String>> resultList;
	private int sum = 0;

	@Override
	public String execute() throws Exception {
		UserBean bean = this.getUserBean();
		String temp = "";
		resultList = new CopyOnWriteArrayList<>();
		if(bean != null){
			// é user
			try{
				temp = bean.getTipoUser(bean.getUsername());
			}catch(RemoteException e){
				e.printStackTrace();
			}
			
			if(idEleicao.trim().equals("")){
				addActionError("Campos invalidos. Nao podem ser deixados em branco/apenas espacos");
				return INPUT;
			}else{
				try{
					result = new HashMap<>();
					System.out.println(idEleicao);
					Eleicao e = bean.getEleicao(idEleicao);
					if(e != null){
						String[] list = {"Núcleo de Estudantes","Conselho Geral"};
			    		result.put("titulo",e.titulo);
			    		result.put("idEleicao",e.id);
			    		result.put("descricao",e.descricao);			            
			    		result.put("tipo",list[e.tipo-1]);
			            result.put("dataI", e.dataInicio.toString());
			            result.put("dataF", e.dataFim.toString());
			            bean.setResultMap(result);
			            if(e.listaCandidaturas != null) {
			    	    	for(Lista l: e.listaCandidaturas){
			    	    		if(l.tipoLista.equals(temp) || l.nome.equals("Nulo") || l.nome.equals("Branco")){
				    	    		sum++;
				    	    		String[] listaFunc = {"Estudante","Funcionario", "Docente"};
				    	    		HashMap<String, String> resultAux = new HashMap<>();
				    	    		resultAux.put("nome",l.nome);
				    	    		if((l.nome.equals("Nulo")) || (l.nome.equals("Branco"))) {
				    	    			resultAux.put("tipo","Comum");
				    	    		}
				    	    		else {
				    	    			resultAux.put("tipo",listaFunc[Integer.parseInt(l.tipoLista)-1]);
				    	    		}
				    	            resultList.add(resultAux);
			    	    		}
			    	    	}
			    	    	bean.setResult(resultList);
			    	    	bean.setSum(sum);
			            }
				    	return SUCCESS;
					}else{
						addActionError("Eleicao não existe");
						this.clearMessages();
						return ERROR;
					}
					
				}catch(RemoteException e){
					e.printStackTrace();
					return ERROR;
				}
			}
			
		}else{
			return LOGIN;
		}
		
	}
	


	public Map<String, String> getResult() {
		return result;
	}

	public void setResult(Map<String, String> result) {
		this.result = result;
	}

	public String getIdEleicao() {
		return idEleicao;
	}

	public void setIdEleicao(String idEleicao) {
		this.idEleicao = idEleicao;
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



	public CopyOnWriteArrayList<Map<String, String>> getResultList() {
		return resultList;
	}



	public void setResultList(CopyOnWriteArrayList<Map<String, String>> resultList) {
		this.resultList = resultList;
	}



	public int getSum() {
		return sum;
	}



	public void setSum(int sum) {
		this.sum = sum;
	}

}
