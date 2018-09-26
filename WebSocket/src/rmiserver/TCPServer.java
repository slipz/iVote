package rmiserver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;


/**
* Classe TCPServer
* </p>
* Esta classe implementa uma interface remota. E a classe que representa uma mesa de voto
* e serve de servidor e meio de comunicacao entre os terminais e o servidor RMI.
* E composto por outros dois tipos de threads: Receiver e Connection
* </p> 
* @param registryPort inteiro que representa o porto do RMI Registry
* @param registryIP String que representa o IP do RMI Registry
* @param tcpServerPort Inteiro que representa o porto do servdor TCP
* @param tcpServerIP String que representa o IP do servidor TCP
* @param dep String que representa o nome do departamento onde se situa este servidor TCP
* @param rmiserver Objeto que representa o servidor RMI
* @param server Objeto que reprensenta o próprio servidor TCP
* </p>
* @see Receiver
* @see Connection
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/
public class TCPServer extends UnicastRemoteObject implements TCP_S_I{
	
	protected TCPServer() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = 1L;
	public static int registryPort;
	public static String registryIP;
	public static int tcpServerPort;
	public static String tcpServerIP;
	public static String dep = "";
	public static RMI_S_I rmiserver;
	public static TCPServer server;
	
  
	public static void carregaConfig(){
		String file = "TCPServerConfig.txt";
		String line;
		String[] l1;
		System.out.println("Uploding TCPServer configurations...");
		try{
			FileReader inputFile = new FileReader(file);
			BufferedReader buffer = new BufferedReader(inputFile);
			
			line = buffer.readLine();
			l1 = line.split("=");
			if(l1[0].equals("registryPort")) {
				registryPort = Integer.parseInt(l1[1]);
			}
			
			line = buffer.readLine();
			l1 = line.split("=");
			if(l1[0].equals("registryIP")) {
				registryIP = l1[1];
			}		
			
			line = buffer.readLine();
			l1 = line.split("=");
			if(l1[0].equals("tcpServerPort")) {
				tcpServerPort = Integer.parseInt(l1[1]);
			}
			
			line = buffer.readLine();
			l1 = line.split("=");
			if(l1[0].equals("tcpServerIP")) {
				tcpServerIP = l1[1];
			}	
			
		}catch(FileNotFoundException e){
			System.out.println("File "+file+" not found");
			System.exit(0);
		}catch(IOException e){
			System.out.println("Erro na leitura das configuracoes");
			System.exit(0);
		}
		System.out.println("TCPServerConfig.txt successfully uploaded.");
	}
	
	private static String menu(){
	    System.out.println("\nMENU");
	    System.out.println("1- Identificar Eleitor.");
	    System.out.println("0- Sair");
	    System.out.print("Opcao: ");
	    Scanner sc = new Scanner(System.in);
	    String opcao = sc.nextLine();
	    return opcao;
	}
	

	/**
    * Metodo utilizado para identificar um eleitor. Usa chamadas RMI pelo que 
    * lanca RemoteException. Todas as Exceptions sao tratadas atraves do failover
    * </p>
    * @param terminais  
    * @param rmiserver
    *
    */   
	public static void identificaEleitor(ArrayList<Connection> terminais, RMI_S_I rmiserver) {
		int check = 0;							//Verifica se encontrou algum terminal livre
		Scanner sc = new Scanner(System.in);
		String cc, idEleicao, ans = "", nome = "";
		ArrayList<Message> msgs = null;
		
		int tries = 0;
		do {
			try {
				msgs = rmiserver.devolveEleicoes(dep);
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI devolveEleicoes
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
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
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeTCP((TCP_S_I)server);
						tries = 0;
					} catch (RemoteException | InterruptedException | MalformedURLException | NotBoundException e) {
						// FailOver falhou
						tries++;
					}
				}else if(tries >= 24) {
					System.out.println("Impossivel realizar a operacao devido a falha tecnica (Servidores RMI desligados).");
					return;
				}
			}
		}while(check==0);
		
		check = 0;
		
		if(msgs == null || msgs.isEmpty()) {
			System.out.println("Nao existem eleicoes a decorrer de momento.");
			return;
		}
		
		do {
			System.out.println("Eleicoes a decorrer:");
			for(Message x: msgs) {
				System.out.println("\n\tNome: "+x.m1+"\n\tID: "+x.m3);
			}
			
			System.out.println("Insira o ID da eleicao em que pretende votar: ");
			idEleicao = sc.nextLine();
			
			for(Message x: msgs) {
				if(x.m3.equals(idEleicao)) {
					check = 1;
				}
			}
			if(check == 0) {
				System.out.println("ID de eleicao invalido. Escolha uma eleicao valida.");
			}
		}while(check==0);
		check = 0;
		
		do {
			// Identificar eleitor por cc
			System.out.println("Insira o CC do eleitor");
			cc = sc.nextLine();
			if(verificarNumeros(cc)) {
				check = 1;
			}
			else {
				System.out.println("\nO numero de CC apenas pode conter letras");
			}
		}while(check==0);
		check = 0;
				
		tries = 0;
		do {
			try {
				ans = rmiserver.identificarEleitor(cc, idEleicao);
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da excepcao da chamada RMI identificarEleitor
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
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
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeTCP((TCP_S_I)server);
						tries = 0;
					} catch (RemoteException | InterruptedException | MalformedURLException | NotBoundException e) {
						// FailOver falhou
						tries++;
					}
				}else if(tries >= 24) {
					System.out.println("Impossivel realizar a operacao devido a falha tecnica (Servidores RMI desligados).");
					return;
				}
			}
		}while(check==0);
		
		check = 0;
		
		if(ans.equals("Pessoa nao existe")) {
			System.out.println("O numero de cc introduzido nao esta associado a um eleitor registado.");
			return;
		}else if(ans.equals("Pessoa nao pode votar nessa eleicao")) {
			System.out.println("O eleitor nao tem autorizacao para votar nesta eleicao.");
			return;
		}else if(ans.equals("null")) {
			return;
		}else {
			nome = ans;
		}
		
		
		//Procurar terminal livre, ou seja, blocked;
		synchronized(terminais) {
			for(Connection x: terminais) {
				if(x.terminal.value()) {
					PrintWriter out = null;
					try {
						out = new PrintWriter(x.clientSocket.getOutputStream(), true);
					} catch (IOException e) {
						System.out.println("Erro na comunicacao com o terminal, a saltar terminal");
						continue;
					}
					out.println("type|unlock ; username|"+nome+" ; idEleicao|"+idEleicao);
					x.terminal.unblock();
					synchronized(x.lock){
						x.lock.notify();
					}
					System.out.println("Mesa para o eleitor "+nome+": "+x.id);
					check = 1;					//Encontrou terminal livre
					break;
				}
			}
		}
		
		if(check == 0) {
			System.out.println("Nao ha terminais livres");
		}

	}
	
	public void run(RMI_S_I rmiserver, ArrayList<Connection> terminais) {
		Receiver rec = new Receiver(tcpServerPort, terminais, rmiserver, dep, server);
		
		while(true) {
			switch(menu()) {
                case "0":
                    System.exit(0);
                    break;
                case "1":
                	identificaEleitor(terminais,rmiserver);
                    break;
                default:
                	System.out.println("Opcao invalida.");
                	break;
			}
		}
		
	}
	
	
	public static int getRegistryPort() {
		return registryPort;
	}

	public static void setRegistryPort(int registryPort1) {
		registryPort = registryPort1;
	}

	public static String getRegistryIP() {
		return registryIP;
	}

	public static void setRegistryIP(String registryIP1) {
		registryIP = registryIP1;
	}

	/**
    * Metodo responsavel pelo lookup do servidor tcp ao RMI Registry. Tratamento também
    * de excepcoes 
    *
    */   

	public static void main(String[] args) {
		System.getProperties().put("java.security.policy", "policy.all");
		System.setSecurityManager(new RMISecurityManager());
		int check = 0, check1 = 0;
		Scanner sc = new Scanner(System.in);
		final ArrayList<Connection> terminais = new ArrayList<>();
		String nomeDep;
		String ans;
		carregaConfig();

		try {
			server = new TCPServer();
			rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
			do {
				do {
					System.out.println("\nDepartamento em que est� a mesa de voto: ");
					nomeDep = sc.nextLine();
					ans = rmiserver.retornaDep(nomeDep);
					if(ans.equals("null")) {
						//Dep nao existe na BD
						System.out.println("Departamento n�o existente, insira novamente.");
					}else {
						//Dep existe
						server.dep = ans;
						check = 1;
					}
					
				}while(check==0);
				
				ans = rmiserver.subscribeTCP((TCP_S_I)server);
				if(ans.equals("false")) {
					System.out.print("Já existe uma Mesa de voto nesse departamento.");
				}else {
					check1 = 1;
				}
			}while(check1==0);
			server.run(rmiserver, terminais);

		} catch ( RemoteException | NotBoundException | MalformedURLException e) {
			// Servidores RMI nao se encontram ligados
			System.out.println("De momento nao e possivel ligar-se aos servidores principais (RMI).");
			return;
		}
		
	}

	@Override
	public String getDepName() throws RemoteException {
		return dep;
	}

	public static boolean verificarLetras(String cadeia) {
		for (int i = 0; i < cadeia.length(); i++) {
			if (Character.isDigit(cadeia.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean verificarNumeros(String numero) {
		try{
			Integer.parseInt(numero);
			return true;
		}catch(Exception e){
			return false;
		}
	}


}