package rmiserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
* Classe Connection
* </p>
* Esta classe extends Thread, e responsavel pela ligacao entre o servidor e cada cliente
* tcp (terminal de voto).
* </p> 
* @param lock Object, serve para colocar a thread num estado de espera por um notify
* @param terminais uma ArrayList<Connection>, serve como lista partilhada por todas as thread e 
* contem todos os terminais ligados ao servidor
* @param terminal TerminalVoto, objeto que representa o terminal de voto
* @param out PrintWriter Stream para comunicacao com o client
* @param in BufferedReader Stream para a cominucacao com o cliente
* @param clientSocket Socket, representa a socket do client
* @param id String, guarda o id do terminal de voto
* @param rmisevrer RMI_S_I representa o servidor RMI
* @param nomeMEsa String guarda o nome do departamento em que a mesa esta inserida
* @param server TCPServer objeto que representa o servidor TCP / Mesa de Voto
* </p>
* @see TCPServer
* @see Receiver
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/
public class Connection extends Thread {
	// TODO Comentar + JavaDoc (?)
	Object lock;
	ArrayList<Connection> terminais;
	TerminalVoto terminal;
	PrintWriter out;
    BufferedReader in = null;
	Socket clientSocket;
	String id;
	RMI_S_I rmiserver;
	String nomeMesa;
	TCPServer server;

	public Connection (Socket aClientSocket, int numero, RMI_S_I rmiserver, ArrayList<Connection> terminais, String id, String nomeMesa, TCPServer server) {
		this.id = id;
		this.lock = new Object();
		this.terminais = terminais;
		this.terminal = new TerminalVoto();
		this.rmiserver = rmiserver;
		this.nomeMesa = nomeMesa;
		this.server = server;
		try{
			clientSocket = aClientSocket;
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    out = new PrintWriter(clientSocket.getOutputStream(), true);
			this.start();
		}catch(IOException e){
			System.out.println("Falha na ligacao a mesa de voto. Corriga o ficheiro de configuracoes ou ligue uma mesa de voto.");
			System.exit(0);
		}
	}


	/**
    * Metodo principal da thread Connection. Método que é executado quando a thread é lancada
    * e e responsavel por tratar do protocolo de comunicacao estabelecido assim como manter a 
    * comunicacao entre servidor e cliente.
    * A thread espera por um notify da thread main do TCPServer. Quando o recebe, comeca o 
    * protocolo. O protocolo baseia-se em esperar por uma mensagem, processa-la e analisa-la.
    * Para o processamento usa a funcao messageParser() e analisa-a atraves de uma sucessao de 
    * ifs. É um protocolo sequencial. Quando a mensagem que recebe nao segue o formato da 
    * esperada, envia para o cliente uma mensagem de erro e a mensagem esperada
	* </p>
 	*
    */   
	public void run(){
		// C�digo executado pelo TCPServer para tratar cada TCPClient
		String m = "";
		try {
			HashMap<String, String> hashmap = new HashMap<String, String>();
			m = in.readLine();	
			hashmap = messageParser(m);
			if(hashmap.get("type").equals("ready")) {
				//Terminal est� pronto a ser usado
				while(true) {
					try {
						synchronized(lock) {
							lock.wait();						// Espera que a thread principal d� permissao para iniciar o protocolo
						}
						String idEleicao = "";
						clientSocket.setSoTimeout(120000);		// Estabelece timeout de 120segundos para uma resposta/utiliza��o do terminal2
						
						int check = 0;
						do {	
							hashmap.clear();						// Inicio do protocolo de votar
							m = in.readLine();	
							hashmap = messageParser(m);
						
							//AQUI
							if(hashmap.get("type") != null && hashmap.get("type").equals("auth")) {	
							//if(hashmap.get("type").equals("auth")) {  // type|auth ; username|cc ; pw|pass ; idEleicao|id
								System.out.println(hashmap.toString());
								int verifica = 0;
								int tries = 0;
								do {
									try {
										ArrayList<Lista> listas = rmiserver.autenticaEleitor(hashmap.get("username"), hashmap.get("pw"), hashmap.get("idEleicao"));
										if(listas!=null) {
											check = 1;
											String nCC = hashmap.get("username");
											idEleicao = hashmap.get("idEleicao");
											out.println("type|auth ; result|true"); 
											//TODO enviar boletim de voto
											StringBuilder tmp = new StringBuilder();
											tmp.append("type|boletim ; item_count|");
											tmp.append(Integer.toString(listas.size()));
											tmp.append(" ");
											for(int i=0; i<listas.size(); i++) {
												tmp.append("; item");
												tmp.append(Integer.toString(i));
												tmp.append("|");
												tmp.append(listas.get(i).nome);
												tmp.append(" ");
											}
											
											out.println(tmp.toString());
											hashmap.clear();
											
											int check1 = 0;
											do {
												m = in.readLine();	
												hashmap = messageParser(m);		//type|vote ; list|name 
												int c_1 = 0;
												if(hashmap.get("type").equals("vote")) {
													for(Lista x:listas) {
														if(x.nome.equals(hashmap.get("list"))) {
															//nome valido
															c_1 = 1;
															break;
														}
													}
													if(c_1 == 1) {
														// Criar obj voto, enviar pelo rmi e esperar resposta
														int verifica1 = 0;
														tries = 0;
														do {
															try {
																Voto voto = new Voto(idEleicao, hashmap.get("list"), nomeMesa, nCC);
																System.out.println(hashmap.get("list"));
																String ans = rmiserver.votar(voto);
																if(ans.equals("sucesso")) {
																	//Voto registado com sucesso
																	check1 = 1;
																	out.println("type|vote ; result|true");
																	hashmap.clear();
																	idEleicao = "";
																	nCC = "";
																	terminal.block();
																}else if(ans.equals("lista_not_found")) {
																	//lista incorreta
																	System.out.println("entrei 3");
																	out.println("type|vote_error ; msg|list_not_found");
																	
																}else if(ans.equals("eleicao_not_found")) {
																	//eleicao nao encontrada
																	out.println("type|vote_error ; msg|eleicao_not_found");
																	
																}else if(ans.equals("write_file_error")) {
																	//eleicao nao encontrada
																	out.println("type|vote_error ; msg|write_file_error");
																}
																
																verifica1 = 1;
																verifica = 1;
															} catch (RemoteException e1) {
																// Tratamento da Excepcao da chamada RMI devolveEleicoes
																if(tries == 0) {
																	try {
																		Thread.sleep(6000);
																		//faz novo lookup
																		rmiserver = (RMI_S_I) Naming.lookup("rmi://"+server.registryIP+":"+server.registryPort+"/rmi");
																		rmiserver.subscribeTCP((TCP_S_I)server);
																		tries = 0;
																	} catch (RemoteException | InterruptedException | MalformedURLException | NotBoundException e) {
																		// FailOver falhou
																		tries++;
																	}
																}else if(tries > 0 && tries < 24) {
																	try {
																		Thread.sleep(1000);
																		//faz novo lookup
																		rmiserver = (RMI_S_I) Naming.lookup("rmi://"+server.registryIP+":"+server.registryPort+"/rmi");
																		rmiserver.subscribeTCP((TCP_S_I)server);
																		tries = 0;
																	} catch (RemoteException | InterruptedException | MalformedURLException | NotBoundException e) {
																		// FailOver falhou
																		tries++;
																	}
																}else if(tries >= 24) {
																	System.out.println("Impossivel realizar a operacao devido a falha tecnica (Servidores RMI desligados).\nVoto nao registado");
																	return;
																}
															}
														}while(verifica1==0);
														
														verifica1 = 0;
														
													}else {
														//enviar mensagem de erro ao cliente, nome da lista invalido
														out.println("type|vote_error ; msg|list_not_found");
													}
												}else {
													//caso mensagem errada -> wrong_cmd 
													out.println("type|wrong_cmd ; expected|\"type=vote . list=list_name\"");
												}
											}while(check1 == 0);
										}else {
											out.println("type|auth ; result|false"); 
											//TODO tratar caso em que dados do client nao estao corretos
										}
										verifica = 1;
									} catch (RemoteException e1) {
										// Tratamento da Excepcao da chamada RMI devolveEleicoes
										if(tries == 0) {
											try {
												Thread.sleep(6000);
												//faz novo lookup
												rmiserver = (RMI_S_I) Naming.lookup("rmi://"+server.registryIP+":"+server.registryPort+"/rmi");
												rmiserver.subscribeTCP((TCP_S_I)server);
												tries = 0;
											} catch (RemoteException | InterruptedException | MalformedURLException | NotBoundException e) {
												// FailOver falhou
												tries++;
											}
										}else if(tries > 0 && tries < 24) {
											try {
												Thread.sleep(1000);
												//faz novo lookup
												rmiserver = (RMI_S_I) Naming.lookup("rmi://"+server.registryIP+":"+server.registryPort+"/rmi");
												rmiserver.subscribeTCP((TCP_S_I)server);
												tries = 0;
											} catch (RemoteException | InterruptedException | MalformedURLException | NotBoundException e) {
												// FailOver falhou
												tries++;
											}
										}else if(tries >= 24) {
											System.out.println("Impossivel realizar a operacao devido a falha tecnica (Servidores RMI desligados).\nVoto nao registado");
											return;
										}
									}
									
								}while(verifica==0);
								verifica = 0;
								
							}else {
								//Caso mensagem recebida esteja errada (vinda de telnet ou netcat ou client)
								out.println("type|wrong_cmd ; expected|\"type=auth . username=cc . pw=pass . idEleicao=id\"");
							}
						}while(check==0);
					} catch (SocketTimeoutException e) {
						System.out.println("\nPassaram 120segundos sem utilizacao. A bloquear terminal.");
						terminal.block();
						out.println("type|lock ; term_no|1"); 
					}
				}
			}
		} catch (InterruptedException e) {
			terminais.remove(this);
			System.out.println("Terminal desligado do Servidor. Necessario reiniciar.");
		} catch (IOException e) {
			terminais.remove(this);
			System.out.println("Terminal desligado do Servidor. Necessario reiniciar.");
		}
		
	}

	/**
    * Metodo responsavel pelo processamento/parse da mensagem recebida para poterior
    * analise
	* </p>
	* @see Conection
	* </p>
    * @return hashMap
    */ 
	private HashMap<String, String> messageParser(String m) {
		// TODO Comentar + JavaDoc (?)
		HashMap<String, String> hashMap= new HashMap<String, String>();
		try {
			String[] m1 = m.split(";");
			for(String x: m1) {
				String[] m2 = x.split("\\|");
				hashMap.put(m2[0].trim(), m2[1].trim());
			}
			return hashMap;
		}catch(Exception e) {
			hashMap.put("type","error");
			hashMap.put("key","invalid_command");
			return hashMap;
		}
	}
}