package ivotewebserver.action;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.UserBean;
import rmiserver.ADMIN_C_I;
import rmiserver.Eleicao;
import rmiserver.Lista;
import rmiserver.RMI_S_I;
import rmiserver.Voto;

public class votarEleitor extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	
	private String idEleicao;
	private String idLista;

	@Override
	public String execute() throws Exception {
		UserBean bean = this.getUserBean();
		int check1 = 0;
		if(bean != null){
			// é user
			if(idEleicao.trim().equals("") || idLista.trim().equals("")){
				addActionError("Campos invalidos. Nao podem ser deixados em branco/apenas espacos");
				return INPUT;
			}else{
				ArrayList<Eleicao> teste = new ArrayList<>();
				// alterar, verificar idEleicao
				
				check1 = 0;
				int tries = 0;
				do {
					bean = this.getUserBean();
					try {
						teste = bean.getEleicoesEleitor(bean.getUsername());
						check1 = 1;
					} catch (RemoteException e1) {
						tries++;
						// Tratamento da Excepcao da chamada RMI devolveEleicoes
						if(tries == 0) {

							Thread.sleep(6000);
							//faz novo lookup
							this.session.remove("UserBean");
							UserBean novo = new UserBean();
							novo.setUsername((String)this.session.get("username"));
							novo.setPassword((String)this.session.get("password"));
							this.setUserBean(novo);
							tries = 0;

						}else if(tries > 0 && tries < 24) {
							Thread.sleep(1000);
							//faz novo lookup
							this.session.remove("UserBean");
							UserBean novo = new UserBean();
							novo.setUsername((String)this.session.get("username"));
							novo.setPassword((String)this.session.get("password"));
							this.setUserBean(novo);
							tries = 0;
							
						}else if(tries >= 24) {
							System.out.println("Impossivel realizar a operacao devido a falha tecnica (Servidores RMI desligados).");
							return ERROR;
						}
					}
				}while(check1==0);
				
				int check = 0;
				for(Eleicao t1 : teste){
					if(t1.id.equals(idEleicao)){
						check = 1;
					}
				}
				if(check == 1){
					System.out.println(idEleicao);
					System.out.println(idLista);
					Voto v = new Voto(idEleicao, idLista, "Browser", bean.getUsername());
					
					String msg = "";
					
					
					check1 = 0;
					tries = 0;
					do {
						bean = this.getUserBean();
						try {
							msg = bean.votar(v);
							if(msg.equals("sucesso")){
								//voto efetuado com sucesso
								addActionMessage(msg);
								this.clearErrors();
								return SUCCESS;
								
							}else{
								addActionError(msg);
								this.clearMessages();
								return ERROR;
							}
						} catch (RemoteException e1) {
							tries++;
							// Tratamento da Excepcao da chamada RMI devolveEleicoes
							if(tries == 0) {
								Thread.sleep(6000);
								//faz novo lookup
								this.session.remove("UserBean");
								UserBean novo = new UserBean();
								novo.setUsername((String)this.session.get("username"));
								novo.setPassword((String)this.session.get("password"));
								this.setUserBean(novo);
								tries = 0;

							}else if(tries > 0 && tries < 24) {
								Thread.sleep(1000);
								//faz novo lookup
								this.session.remove("UserBean");
								UserBean novo = new UserBean();
								novo.setUsername((String)this.session.get("username"));
								novo.setPassword((String)this.session.get("password"));
								this.setUserBean(novo);
								tries = 0;
								
							}else if(tries >= 24) {
								System.out.println("Impossivel realizar a operacao devido a falha tecnica (Servidores RMI desligados).");
								return ERROR;
							}
						}
					}while(check1==0);
					return SUCCESS;
				}else{
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

	public String getIdLista() {
		return idLista;
	}

	public void setIdLista(String idLista) {
		this.idLista = idLista;
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
