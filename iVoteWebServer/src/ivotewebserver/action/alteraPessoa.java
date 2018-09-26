package ivotewebserver.action;

import java.rmi.RemoteException;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;

public class alteraPessoa extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	
	private String campo;
	private String info;
	private String ncc;
	
	@Override
	public String execute() throws Exception {
		String tipo = "0";
		AdminBean bean = this.getAdminBean();
		if(bean != null){
			// é admin
			if(campo.trim().equals("") || info.trim().equals("")){
				addActionError("Campos invalidos. Nao podem ser deixados em branco/apenas espacos");
				return INPUT;
			}else{
				switch(campo){
					case "Morada":
						tipo = "1";
						break;
					case "Contacto telefonico":
						tipo = "2";
						break;
					case "Departamento em que se insere":
						tipo = "3";
						break;
					case "Password":
						tipo = "4";
						break;
				}
				try {
					String msg = bean.alterarPessoa(ncc, tipo, info);
					if(msg.equals("\nInformacao alterada com sucesso")){
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

	
	public String getCampo() {
		return campo;
	}
	public void setCampo(String campo) {
		this.campo = campo;
	}

	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}

	public String getNcc() {
		return ncc;
	}


	public void setNcc(String ncc) {
		this.ncc = ncc;
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
