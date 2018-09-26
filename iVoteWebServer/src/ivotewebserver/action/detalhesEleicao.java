package ivotewebserver.action;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import rmiserver.DetalhesVoto;


import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;
import rmiserver.Eleicao;
import rmiserver.Lista;

public class detalhesEleicao extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	private Map<String, String> result;
	private String idEleicao, maiorNome;
	private CopyOnWriteArrayList<Map<String, String>> resultList, resultContagem, resultDetalhes;
	private int sum = 0, contaVotos=0, maior=0;

	@Override
	public String execute() throws Exception {
		AdminBean bean = this.getAdminBean();
		resultList = new CopyOnWriteArrayList<>();
		resultContagem = new CopyOnWriteArrayList<>();
        resultDetalhes = new CopyOnWriteArrayList<>();

		if(bean != null){
			
			// é admin
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
			            if(verData(e.dataFim)) {
			            	result.put("finished", "true");
			            }
			            else {
			            	result.put("finished", "false");
			            }
			            this.getAdminBean().setResultMap(result);
			            
			            if(e.listaCandidaturas != null) {
			    	    	for(Lista l: e.listaCandidaturas){
			    	    		sum++;
			    	    		String[] listaFunc = {"Estudante","Funcionario", "Docente"};
			    	    		HashMap<String, String> resultAux = new HashMap<>();
			    	    		HashMap<String, String> resultAuxConta = new HashMap<>();
			    	    		resultAux.put("nome",l.nome);
			    	    		
			    	    		resultAuxConta.put("nome",l.nome);
			    	    		resultAuxConta.put("contagem",Integer.toString(l.contagem));
			    	    		contaVotos+=l.contagem;
			    	    		
			    	    		if((l.nome.equals("Nulo")) || (l.nome.equals("Branco"))) {
			    	    			resultAux.put("tipo","Comum");
			    	    		}
			    	    		else {
			    	    			resultAux.put("tipo",listaFunc[Integer.parseInt(l.tipoLista)-1]);
			    	    			if(l.contagem>maior) {
				    	    			maiorNome=l.nome;
				    	    			maior=l.contagem;
				    	    		}
			    	    		}
			    	            resultList.add(resultAux);
			    	            resultContagem.add(resultAuxConta);
			    	    	}
			    	    	
			    	    	this.getAdminBean().setResult(resultList);
			    	    	this.getAdminBean().setResultContagem(resultContagem);
			    	    	this.getAdminBean().setContagem(contaVotos);
			    	    	this.getAdminBean().setSum(sum);
			    	    	this.getAdminBean().setMaiorNome(maiorNome);
			    	    	
			            }
                        ArrayList<DetalhesVoto> detalhes = bean.getDetalhes(idEleicao);
                        int sumDet = 0;
                        for(DetalhesVoto d: detalhes){
                            sumDet++;
                            HashMap<String, String> resultAuxDet = new HashMap<>();
                            resultAuxDet.put("local",d.local);
                            resultAuxDet.put("total",Integer.toString(d.totalVotos));
                            resultAuxDet.put("tAlunos",Integer.toString(d.totalAlunos));
                            resultAuxDet.put("tFunc",Integer.toString(d.totalFunc));
                            resultAuxDet.put("tDoc",Integer.toString(d.totalDocentes));
                            resultDetalhes.add(resultAuxDet);
                        }
                        this.getAdminBean().setResultDetalhes(resultDetalhes);
                        this.getAdminBean().setSumDet(sumDet);
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

	public String getMaiorNome() {
		return maiorNome;
	}

	public void setMaiorNome(String maiorNome) {
		this.maiorNome = maiorNome;
	}

	public CopyOnWriteArrayList<Map<String, String>> getResultContagem() {
		return resultContagem;
	}

	public void setResultContagem(CopyOnWriteArrayList<Map<String, String>> resultContagem) {
		this.resultContagem = resultContagem;
	}

	public CopyOnWriteArrayList<Map<String, String>> getResultDetalhes() {
		return resultDetalhes;
	}

	public void setResultDetalhes(CopyOnWriteArrayList<Map<String, String>> resultDetalhes) {
		this.resultDetalhes = resultDetalhes;
	}

	public int getContaVotos() {
		return contaVotos;
	}

	public void setContaVotos(int contaVotos) {
		this.contaVotos = contaVotos;
	}

	public int getMaior() {
		return maior;
	}

	public void setMaior(int maior) {
		this.maior = maior;
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
	
	public boolean verData(Date dia) {
        Date d = new Date();
        if(dia.before(d) || dia.equals(d)) {
        	return true;
        }
        return false;
	}

}
