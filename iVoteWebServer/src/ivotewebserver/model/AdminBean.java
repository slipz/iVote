package ivotewebserver.model;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import rmiserver.Eleicao;
import rmiserver.Pessoa;
import rmiserver.RMI_S_I;
import rmiserver.Departamento;
import rmiserver.Faculdade;
import rmiserver.RegistoVoto;
import rmiserver.DetalhesVoto;


public class AdminBean {
	private RMI_S_I server;
	private String username; // username and password supplied by the user
	private String password;
	private String maiorNome;
	private CopyOnWriteArrayList<Map<String, String>> result, resultContagem, resultDetalhes;
	private Map<String, String> resultMap;
	private int sum, contagem, sumDet;
	

	public AdminBean() {
		try {
			server = (RMI_S_I) Naming.lookup("rmi://localhost:6501/rmi");
			result = new CopyOnWriteArrayList<>();
		}
		catch(NotBoundException|MalformedURLException|RemoteException e) {
			e.printStackTrace(); // what happens *after* we reach this line?
		}
	}
	
	public String setRegistarPessoa(int tipo, String nome, String password, String nomeDep, String ntelef, String morada, String nCC, String data) throws RemoteException{
		return server.registarPessoa(tipo, nome, password, nomeDep, ntelef, morada, nCC, data);
	}
	
	public String inserirFac(String nome, String morada, String ntlf, String email) throws RemoteException{
		return server.inserirFac(nome, morada, ntlf, email);
	}
	
	public String inserirDep(String nome, String morada, String ntlf, String email, String nomeFac) throws RemoteException{
		return server.inserirDep(nome, morada, ntlf, email, nomeFac);
	}
	
	public ArrayList<DetalhesVoto> getDetalhes(String idEleicao) throws RemoteException {
        return server.detalhesVoto(idEleicao);
    }
	
	public String criaEleicao(int tipo, String id, String titulo, String descricao, String dataI, String dataF, String dept) throws RemoteException{
		return server.criaEleicao(tipo, id, titulo, descricao, dataI, dataF, dept);
	}
	
	public String adicionaMesaVoto(String id, String nomeDep) throws RemoteException{
		return server.adicionaMesaVoto(id, nomeDep);
	}
	
	public String alterarPessoa(String ncc, String campo, String info) throws RemoteException{
		return server.alterarPessoa(ncc, campo, info);
	}
	
	public String alterarDep(String nome, String campo, String info) throws RemoteException{
		return server.alterarDep(nome, campo, info);
	}
	
	public String alterarFac(String nome, String campo, String info) throws RemoteException{
		return server.alterarFac(nome, campo, info);
	}
	
	public synchronized String alteraEleicao(String idEleicao, String campo, String info) throws RemoteException {
		return server.alteraEleicao(idEleicao, campo, info);
	}
	
	public String criaLista(String id, String tipoLista, String nome, ArrayList<Pessoa> lista) throws RemoteException {
		return server.criaLista(id, tipoLista, nome, lista);
	}
	public String getUsername() {
		return username;
	}
	
	public String removeMesaVoto(String id, String nomeDep) throws RemoteException{
		return server.removeMesaVoto(id, nomeDep);
	}

	public ArrayList<Eleicao> getEleicoes() throws RemoteException {
		return server.getEleicoes();
	}
	
	public Eleicao getEleicao(String idEleicao) throws RemoteException{
		return server.procuraEleicao(idEleicao);
	}
	
	public ArrayList<Departamento> getDepartamentos() throws RemoteException {
		return server.getDepartamentos();
	}
	
	public ArrayList<Faculdade> getFaculdades() throws RemoteException {
		return server.getFaculdades();
	}
	
	public RegistoVoto getRegisto(String idEleicao, String ncc) throws RemoteException{
        return server.pesquisaLocalVoto(ncc,idEleicao);
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

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public Map<String, String> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, String> resultMap) {
		this.resultMap = resultMap;
	}

	public CopyOnWriteArrayList<Map<String, String>> getResultContagem() {
		return resultContagem;
	}

	public void setResultContagem(CopyOnWriteArrayList<Map<String, String>> resultContagem) {
		this.resultContagem = resultContagem;
	}

	public int getContagem() {
		return contagem;
	}

	public void setContagem(int contagem) {
		this.contagem = contagem;
	}

	public String getMaiorNome() {
		return maiorNome;
	}

	public void setMaiorNome(String maiorNome) {
		this.maiorNome = maiorNome;
	}
	
	public CopyOnWriteArrayList<Map<String, String>> getResultDetalhes() {
	    return resultDetalhes;
	}
	
	public void setResultDetalhes(CopyOnWriteArrayList<Map<String, String>> resultDetalhes) {
	    this.resultDetalhes = resultDetalhes;
	}
	
	public int getSumDet() {
	    return sumDet;
	}
	
	public void setSumDet(int sumDet) {
	    this.sumDet = sumDet;
	}

}
