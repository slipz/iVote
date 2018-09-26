package ivotewebserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;
import rmiserver.Pessoa;

public class criaListaCandidatos extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	
	private String idEleicao;
	private String nome;
	private String tipoLista;
	private String nomePres;
	private String nomeVice;
	
	
	@Override
	public String execute() throws Exception {
		AdminBean bean = this.getAdminBean();
		if(bean != null){
			// é admin
			if(idEleicao.trim().equals("") || nome.trim().equals("") || tipoLista.trim().equals("") || nomePres.trim().equals("") || nomeVice.trim().equals("")){
				addActionError("Campos invalidos. Nao podem ser deixados em branco/apenas espacos");
				return INPUT;
			}else{
				try {
					String t1 = "";
					switch(tipoLista){
						case "Estudantes":
							t1 = "1";
							break;
						case "Funcionarios":
							t1 = "2";
							break;
						case "Docentes":
							t1 = "3";
							break;
					}
					ArrayList<Pessoa> lista = new ArrayList<>();
					lista.add(new Pessoa(nomePres));
					lista.add(new Pessoa(nomeVice));
					String msg = bean.criaLista(idEleicao, t1, nome, lista);
					if(msg.equals("\nLista criada com sucesso")){
						addActionMessage(msg);
						this.clearErrors();
						return SUCCESS;
					}
					else
						addActionError(msg);
						this.clearMessages();
						return ERROR;
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return ERROR;
				}
			}
		}else{
			return LOGIN;
		}
		
	}

	public String getIdEleicao() {
		return idEleicao;
	}
	public void setIdEleicao(String idEleicao) {
		this.idEleicao = idEleicao;
	}

	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipoLista() {
		return tipoLista;
	}

	public void setTipoLista(String tipoLista) {
		this.tipoLista = tipoLista;
	}

	public String getNomePres() {
		return nomePres;
	}

	public void setNomePres(String nomePres) {
		this.nomePres = nomePres;
	}

	public String getNomeVice() {
		return nomeVice;
	}

	public void setNomeVice(String nomeVice) {
		this.nomeVice = nomeVice;
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
