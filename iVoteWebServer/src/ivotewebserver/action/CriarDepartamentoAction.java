package ivotewebserver.action;

import java.rmi.RemoteException;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;

public class CriarDepartamentoAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	
	private String nome;
	private String morada;
	private String ntlf;
	private String email;
	private String nomeFac;
	
	
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		AdminBean bean = this.getAdminBean();
		if(bean != null){
			// é admin
			if(nome.trim().equals("") || morada.trim().equals("") || ntlf.trim().equals("") || email.trim().equals("") || nomeFac.trim().equals("")){
				addActionError("Campos ivalidos. Nao podem ser deixados em branco/apenas espacos");
				return INPUT;
			}else{
			
				try {
					String msg = bean.inserirDep(this.nome, this.morada, this.ntlf, this.email, this.nomeFac);
					if(msg.equals("\nDepartamento criado com sucesso")){
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



	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getMorada() {
		return morada;
	}
	public void setMorada(String morada) {
		this.morada = morada;
	}

	public String getNtlf() {
		return ntlf;
	}
	public void setNtlf(String ntlf) {
		this.ntlf = ntlf;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getNomeFac() {
		return nomeFac;
	}
	public void setNomeFac(String nomeFac) {
		this.nomeFac = nomeFac;
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
