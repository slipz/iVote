package ivotewebserver.action;

import java.rmi.RemoteException;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;

public class criarEleicaoGeral extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	
	private String idEleicao;
	private String titulo;
	private String descricao;
	private String dataI;
	private String dataF;
	
	
	@Override
	public String execute() throws Exception {
		AdminBean bean = this.getAdminBean();
		if(bean != null){
			// é admin
			if(idEleicao.trim().equals("") || titulo.trim().equals("") || descricao.trim().equals("") || dataI.trim().equals("") || dataI.trim().equals("")){
				addActionError("Campos invalidos. Nao podem ser deixados em branco/apenas espacos");
				return INPUT;
			}else{
			
				try {
					String msg = bean.criaEleicao(2, this.idEleicao, this.titulo, this.descricao, this.dataI, this.dataF, "");
					if(msg.equals("\nEleicao criada com sucesso")){
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

	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDataI() {
		return dataI;
	}
	public void setDataI(String dataI) {
		this.dataI = dataI;
	}

	public String getDataF() {
		return dataF;
	}
	public void setDataF(String dataF) {
		this.dataF = dataF;
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
