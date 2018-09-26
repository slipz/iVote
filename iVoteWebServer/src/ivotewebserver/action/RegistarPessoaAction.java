package ivotewebserver.action;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ivotewebserver.model.AdminBean;

public class RegistarPessoaAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -7648950222992149925L;
	private Map<String, Object> session;
	
	private List<String> tipos;

	private String tipo; //onde vai guardar o resultado 
	private String nome;
	private String username; //aka cc
	private String password; 
	private String departamento;
	private String ntelef;
	private String morada;
	private String validade;

	private static final String ESTUDANTE = "Estudante";	//O que aparece nos butoes
	private static final String FUNCIONARIO = "Funcionario";
	private static final String DOCENTE = "Docente";
	
	public RegistarPessoaAction(){ //para criar o form, neste caso
		
		//Radio Button para o tipo
		tipos = new ArrayList<String>();
		tipos.add(ESTUDANTE);
		tipos.add(FUNCIONARIO);
		tipos.add(DOCENTE);
		
	}
	
	public String execute() { //para receber o form
		//faz cenas no RMI 
		AdminBean bean = this.getAdminBean();
		if(bean != null){
			if(tipo.trim().equals("") || nome.trim().equals("") || username.trim().equals("") || password.trim().equals("") || departamento.trim().equals("") || ntelef.trim().equals("") || morada.trim().equals("")|| validade.trim().equals("")){
				addActionError("Campos ivalidos. Nao podem ser deixados em branco/apenas espacos");
				return INPUT;
			}else{
				int t1 = 0;
				switch(tipo){
					case "Estudante":
						t1 = 1;
						break;
					case "Funcionario":
						t1 = 2;
						break;
					case "Docente":
						t1 = 3;
						break;
				}
				
				try {
					String msg = bean.setRegistarPessoa(t1, this.nome, this.password, this.departamento, this.ntelef, this.morada, this.username, this.validade);
					if(msg.equals("\nEleitor criado com sucesso")){
						addActionMessage(msg);
						return SUCCESS;
					}
					else
						addActionError(msg);
						return ERROR;
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			return LOGIN;
		}
		return ERROR;
	}
	
	public String display() { //para mostrar o form
		return SUCCESS; //no structs.xml, quando recebe none mostra a pagina do form
	}
	

	public List<String> getTipos() {
		return tipos;
	}

	public void setTipos(List<String> tipos) {
		this.tipos = tipos;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	
	public String getNtelef() {
		return ntelef;
	}

	public void setNtelef(String ntelef) {
		this.ntelef = ntelef;
	}

	public String getMorada() {
		return morada;
	}

	public void setMorada(String morada) {
		this.morada = morada;
	}

	public String getValidade() {
		return validade;
	}

	public void setValidade(String validade) {
		this.validade = validade;
	}
	
	//return default gender value
	public String getDefaultTipoValue(){
		return ESTUDANTE;
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
