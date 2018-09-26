package ivotewebserver.action;

import java.rmi.RemoteException;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;

public class removeMesaVotoAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	
	private String nomedep;
	private String idEleicao;

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		AdminBean bean = this.getAdminBean();
		if(bean != null){
			// é admin
			if(nomedep.equals("") || idEleicao.equals("")){
				addActionError("Campos invalidos. Nao podem ser deixados em branco/apenas espacos");
				return INPUT;
			}else{

				try {
					String msg = bean.removeMesaVoto(idEleicao, nomedep);
					if(msg.equals("\nMesa de voto removida com sucesso da eleicao")){
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

	public String getNomedep() {
		return nomedep;
	}
	public void setNomedep(String nomedep) {
		this.nomedep = nomedep;
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


}
