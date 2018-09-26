package ivotewebserver.model;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import rmiserver.Eleicao;
import rmiserver.RMI_S_I;
import rmiserver.Voto;
import rmiserver.RegistoVoto;

public class UserBean {
	private RMI_S_I server;
	private String username; // username and password supplied by the user
	private String password;
	private String tipoUser;
	private CopyOnWriteArrayList<Map<String, String>> result;
	private Map<String, String> resultMap;
	private int sum;
	
	
	public UserBean() {
		try {
			server = (RMI_S_I) Naming.lookup("rmi://localhost:6501/rmi");
		}
		catch(NotBoundException|MalformedURLException|RemoteException e) {
			e.printStackTrace(); // what happens *after* we reach this line?
		}
	}
	
	public ArrayList<Eleicao> getEleicoesEleitor(String username) throws RemoteException {
		return server.getEleicoesEleitor(username);
	}
	
	public Eleicao getEleicao(String idEleicao) throws RemoteException{
		return server.procuraEleicao(idEleicao);
	}
	
	public String getTipoUser(String username) throws java.rmi.RemoteException{
		return server.getTipoUser(username);
	}
	
	public String votar(Voto voto) throws java.rmi.RemoteException {
		return server.votar(voto);
	}
	
	public RegistoVoto getRegisto(String idEleicao, String ncc) throws RemoteException{
        return server.pesquisaLocalVoto(ncc,idEleicao);
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


	public CopyOnWriteArrayList<Map<String, String>> getResult() {
		return result;
	}


	public void setResult(CopyOnWriteArrayList<Map<String, String>> result) {
		this.result = result;
	}


	public Map<String, String> getResultMap() {
		return resultMap;
	}


	public void setResultMap(Map<String, String> resultMap) {
		this.resultMap = resultMap;
	}


	public int getSum() {
		return sum;
	}


	public void setSum(int sum) {
		this.sum = sum;
	}

	public String getTipoUser() {
		return tipoUser;
	}

	public void setTipoUser(String tipoUser) {
		this.tipoUser = tipoUser;
	}
	
}
