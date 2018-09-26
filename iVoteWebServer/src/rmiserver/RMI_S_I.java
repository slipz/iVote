package rmiserver;

import java.rmi.*;
import java.util.ArrayList;
import ws.WS_I;;

public interface RMI_S_I extends Remote {
	
	public String registarAdmin(String username, String password) throws java.rmi.RemoteException;
		
	public String registarPessoa(int tipo, String nome, String password, String nomeDep, String ntelef, String morada, String nCC, String validadeCC ) throws java.rmi.RemoteException;
	// 1 - Funcao que regista uma pessoa na base de dados

	public String inserirDep(String nome, String morada, String ntlf, String email, String nomeFac) throws java.rmi.RemoteException;
	// 2 - Funcao que insere um novo departamento
	
	public String inserirFac(String nome, String morada, String ntlf, String email) throws java.rmi.RemoteException;
	// 2 - Funcao que insere uma nova faculdade 
	
	public String alterarPessoa(String nome, String campo, String info) throws java.rmi.RemoteException;
	
	public String alterarDep(String nome, String campo, String info) throws java.rmi.RemoteException;
	// 2 - Funcao que altera uma informacao de um departamento
	
	public String alterarFac(String nome, String campo, String info) throws java.rmi.RemoteException;
	// 2 - Funcao que altera uma faculdade

	public String criaEleicao(int tipo, String id, String titulo, String descricao, String dataI, String dataF, String dept) throws java.rmi.RemoteException;
	// 3 - Funcao que cria uma nova eleicao na base de dados.

	public String criaLista(String id, String tipoLista, String nome, ArrayList<Pessoa> lista) throws java.rmi.RemoteException;
    // 4 - Funcao que cria uma nova lista de candidatos
        
    public String adicionaCandidatos(String idEleicao, String titulo, ArrayList<Pessoa> listaCandidatos) throws java.rmi.RemoteException;
    // 4 - Funcao que adiciona um candidato a uma lista
        
    public String removeCandidatos(String idEleicao, String titulo, ArrayList<Pessoa> listaCandidatos) throws java.rmi.RemoteException;
    // 4 - Funcao que remove um candidato de uma lista
    
    public ArrayList<DetalhesVoto> detalhesVoto(String idEleicao) throws RemoteException;
    
	public String alteraEleicao(String idEleicao, String campo, String info) throws java.rmi.RemoteException;
	// 9 - Funcao que permite alterar os campos de uma eleicao
        
    //public String alteraOrdemCandidatos() throws java.rmi.RemoteException;
    // 4 - Funcao que altera a ordem dos candidatos numa lista

	public String adicionaMesaVoto(String idEleicao, String nomeDep) throws java.rmi.RemoteException;
	// 5 - Funcao que permite adicionar as mesas de voto a uma eleicao com o id idEleicao

	public String removeMesaVoto(String idEleicao, String nomeDep) throws java.rmi.RemoteException;
	// 5 - Funcao que permite remover uma mesa de voto de uma eleicao
	
	public String subscribeConsola(ADMIN_C_I consola) throws java.rmi.RemoteException;
	// Funcao para passar consola para o rmiserver
	
	public String subscribeTCP(TCP_S_I tcp) throws java.rmi.RemoteException;
	
	public void printMesasVoto(String idAdmin) throws java.rmi.RemoteException;
	// Funcao para mostrar as mesas de voto

	//public void registaMesaVoto(TCPServer server) throws java.rmi.RemoteException;
	
	public ArrayList<Faculdade> getFaculdades() throws java.rmi.RemoteException;
	// Funcao para retornar para a consola de admin a lista de faculdades inseridas no rmiserver
	
	public ArrayList<Departamento> getDepartamentos() throws java.rmi.RemoteException;
	// Funcao para retornar para a consola de admin a lista de departamentos inseridas no rmiserver

	public ArrayList<Eleicao> getEleicoes() throws java.rmi.RemoteException;
	// Funcao para retornar para a consola de admin a lista de eleicoes inseridas no rmiserver
	
	public ArrayList<Eleicao> getEleicoesPassadas() throws java.rmi.RemoteException;
	
	public int moveEleicoesPassadas() throws java.rmi.RemoteException;
	
	public RegistoVoto pesquisaLocalVoto(String cc, String idEleicao) throws java.rmi.RemoteException;
	
	
        /* ------------------ TCP SERVER METHODS -----------------*/
        
    public String identificarEleitor(String cc,String idEleicao) throws java.rmi.RemoteException;
	// 6 - Fun��o que identifica o eleitor e diz se est� ou n�o autorizado a votar naquela mesa, devolvendo o seu Nome
	// Message leva o campo identificador do eleitor, pode ser qq um dos seus dados
	// Fun��o respons�vel por verificar se pode ou n�o votar na respetiva elei��o
	// Verifica se eleitor j� votou nessa elei��o

	public ArrayList<Lista> autenticaEleitor(String username, String password, String idEleicao) throws java.rmi.RemoteException;
	// 7 - Fun��o para autenticar o utilizador no terminal de voto

	public String votar(Voto voto) throws java.rmi.RemoteException;
	// 8 - Fun��o que efetua o voto e envia-o para a base de dados

	public String retornaDep(String nome) throws java.rmi.RemoteException;
	
	public ArrayList<Message> devolveEleicoes(String dep) throws java.rmi.RemoteException;
	
	
	/* ------------------------ Web server -------------------------*/
	public String LoginWeb(String username, String password) throws java.rmi.RemoteException;
	
	public Eleicao procuraEleicao(String idEleicao) throws java.rmi.RemoteException;
	
	public ArrayList<Eleicao> getEleicoesEleitor(String username) throws java.rmi.RemoteException;
	
	public String getTipoUser(String username) throws java.rmi.RemoteException;
	
	public String subscribeWebSocket(WS_I websocket) throws java.rmi.RemoteException;
	
	public String unsubscribeWebSocket(WS_I websocket) throws java.rmi.RemoteException;
	
	public void printMesasVotoWeb() throws RemoteException;
	

}