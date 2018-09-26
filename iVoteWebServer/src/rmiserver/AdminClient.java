package rmiserver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

/**
* Classe AdminClient
* </p>
* Esta classe representa uma consola de administracao, onde e possivel:
* criar eleitores,departamentos, faculdades, eleicoes, listas e mesas de voto;
* alterar propriedades de um departamento, faculdade, eleicao e listas;
* fazer consultas e listagens; entre outros.
* Contem varios metodos que permitem a implementacao destas funcionalidades.
* </p>
* Ao correr a main, sao carregadas as configuracoes do RMIServer e 
* a consola faz lookup ao servidor no Registry.
* Em seguida, faz-se o display do menu de entrada e a partir dai fazemos o que o 
* administrador escolher.
* </p>
* Podemos ter varias consolas de administracao, desde que todas se liguem com ids diferentes.
* </p>
* @see RMIServer
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/

public class AdminClient extends UnicastRemoteObject implements ADMIN_C_I{
	
	private static final long serialVersionUID = 1L;
	public static int registryPort;
	public static String registryIP;
	public String idConsola;
	public static boolean mostraNotificacoes = false;
	public static RMI_S_I rmiserver;
	public static AdminClient consola;
	
	protected AdminClient() throws RemoteException {
		super();
	}

	
	public static void carregaConfig(){
		String file = "AdminConsoleConfig.txt";
		String line;
		String[] l1;
		System.out.println("Uploding AdminConsole configurations...");
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
			
			buffer.close();
		}catch(FileNotFoundException e){
			System.out.println("File "+file+" not found");
			System.exit(0);
		}catch(IOException e){
			System.out.println("Erro na leitura das configuracoes");
			System.exit(0);
		}
		System.out.println("AdminConsoleConfig.txt successfully uploaded.");
	}

	/**
	* Metodo que permite reunir informacoes de uma pessoa para se criar um eleitor
	* e colocar na base de dados do RMIServer.
	* @see Pessoa
	*/
	public static void criaPessoa() {
		Scanner sc = new Scanner(System.in);		
		String nome, pwd, dep, ntlf, morada, nCC, data, tipo;
		int check = 0;
		
		do {
			System.out.println("\nInsira o nome:");
			nome = sc.nextLine();
			if(verificarLetras(nome) && nome.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome apenas pode conter letras");
			}
		}while(check==0);
		
		check = 0;
		
		do {
			System.out.println("\nInsira a profissao:\n(1)Estudante\n(2)Funcionario\n(3)Docente");
			tipo = sc.nextLine();
			if(tipo.equals("1") || tipo.equals("2") || tipo.equals("3")) {
				check = 1;
			}
			else {
				System.out.println("\nA opcao so pode conter digitos de 1 a 3");
			}
		}while(check==0);
		
		check = 0;
		
		do {
			System.out.println("\nInsira o nome do departamento:");
			dep = sc.nextLine();
			if(verificarLetras(dep) && dep.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome apenas pode conter letras");
			}
		}while(check==0);
		
		check = 0;
		
		do {
			System.out.println("Insira o numero de contacto:\n");
			ntlf = sc.nextLine();
			if(verificarNumeros(ntlf) && ntlf.length()<= 9) {
				if (Integer.parseInt(ntlf) > 0) {
					check = 1;
				}
			}
			else {
				System.out.println("\nO numero de contacto apenas pode conter 9 digitos");
			}
		}while(check==0);
			
		check = 0;
		
		System.out.println("\nInsira a morada:");
		morada = sc.nextLine();
		
		do {
			System.out.println("Insira o numero do cartao de cidadao:\n");
			nCC = sc.nextLine();
			if(verificarNumeros(nCC) && nCC.length()<= 9) {
				if (Integer.parseInt(nCC) > 0) {
					check = 1;
				}
			}
			else {
				System.out.println("\nO numero do CC apenas pode conter 9 digitos");
			}
		}while(check==0);
		
		System.out.println("Insira a validade do cartao de cidadao (DD/MM/AAAA):\n");
		data = sc.nextLine();

		System.out.println(">>>\nELEITOR:\nPor favor insira aqui a sua password:\n");
		pwd = sc.nextLine();
		
		String msgServer = "";
		
		check = 0;
		int tries = 0;
		do {
			try {
				msgServer = rmiserver.registarPessoa(Integer.parseInt(tipo), nome, pwd, dep, ntlf, morada, nCC, data);
				System.out.println(msgServer);
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI devolveEleicoes
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
	}
	
	public static void criaAdmin(){
		int check = 0;
		String username = "", password = "";
		Scanner sc = new Scanner(System.in);
		
		System.out.println("\nInsira o username");
		username = sc.nextLine();
		
		System.out.println("\nInsira a password");
		password = sc.nextLine();
		
		String msgServer = "";
		
		check = 0;
		int tries = 0;
		do {
			try {
				msgServer = rmiserver.registarAdmin(username,password);
				System.out.println(msgServer);
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI devolveEleicoes
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
	}
	
	/**
	* Metodo que permite reunir informacoes de um departamento para se criar o mesmo
	* e colocar na base de dados do RMIServer.
	* @see Departamento
	*/
	public static void criaDep() {
		String nome, morada, ntlf, email, nomeFac;
		Scanner sc = new Scanner(System.in);
		int check = 0;
		
		do {
			System.out.println("\nInsira o nome do departamento:");
			nome = sc.nextLine();
			if(verificarLetras(nome) && nome.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome apenas pode conter letras");
			}
		}while(check==0);
		
		check = 0;
		

		System.out.println("\nInsira a morada do departamento:");
		morada = sc.nextLine();
		
		do {
			System.out.println("Insira o numero de contacto:\n");
			ntlf = sc.nextLine();
			if(verificarNumeros(ntlf) && ntlf.length()<= 9) {
				if (Integer.parseInt(ntlf) > 0) {
					check = 1;
				}
			}
			else {
				System.out.println("\nO numero de contacto so pode conter 9 digitos");
			}
		}while(check==0);
		
		check = 0;
		
		System.out.println("Insira o email:\n");
		email = sc.nextLine();
		
		do {
			System.out.println("\nInsira o nome da faculdade:");
			nomeFac = sc.nextLine();
			if(verificarLetras(nomeFac) && nomeFac.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome apenas pode conter letras");
			}
		}while(check==0);
		
		check = 0;
		
		String msgServer = "";
		
		check = 0;
		int tries = 0;
		do {
			try {
				msgServer = rmiserver.inserirDep(nome, morada, ntlf, email, nomeFac);
				System.out.println(msgServer);
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI inserirDep
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
		
	}
	
	/**
	* Metodo que permite reunir informacoes de uma faculdade para se criar a mesma
	* e colocar na base de dados do RMIServer.
	* @see Faculdade
	*/
	public static void criaFac() {
		String nome, morada, ntlf, email;
		Scanner sc = new Scanner(System.in);
		int check = 0;
		
		do {
			System.out.println("\nInsira o nome da faculdade:");
			nome = sc.nextLine();
			if(verificarLetras(nome) && nome.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome apenas pode conter letras");
			}
		}while(check==0);
		
		check = 0;

		System.out.println("\nInsira a morada da faculdade:");
		morada = sc.nextLine();
		//TODO protecoes para len de moradas em todas as funcoes

		do {
			System.out.println("Insira o numero de contacto:\n");
			ntlf = sc.nextLine();
			if(verificarNumeros(ntlf) && ntlf.length()<= 9) {
				if (Integer.parseInt(ntlf) > 0) {
					check = 1;
				}
			}
			else {
				System.out.println("\nO numero de contacto so pode conter 9 digitos");
			}
		}while(check==0);
		
		check =  0;
		
		System.out.println("Insira o email:\n");
		email = sc.nextLine();
		
		String msgServer = "";
		
		check = 0;
		int tries = 0;
		do {
			try {
				msgServer = rmiserver.inserirFac(nome, morada, ntlf, email);
				System.out.println(msgServer);
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI inserirFac
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
		
	}
	
	public static void alteraPessoa() {
		String ncc, novaInfo = "";
		Scanner sc = new Scanner(System.in);
		int check = 0;

		do {
			System.out.println("\nInsira o numero do cartao de cidadao do eleitor:");
			ncc = sc.nextLine();
			if(verificarNumeros(ncc) && ncc.length()==9 && ncc!="000000000") {
				check = 1;
			}
			else {
				System.out.println("\nO numero apenas pode conter digitos");
			}
		}while(check==0);
		
		check = 0;
		
		System.out.println("\nQual dos campos pretende alterar?\n1- Morada\n2- Numero de contacto\n3-Departamento em que se insere\\n4- Password");
		String op = sc.nextLine();
		
		switch(op) {
			case "1":
				System.out.println("\nInsira a nova morada:");
				novaInfo = sc.nextLine();
				break;
			case "2":
				do {
					System.out.println("Insira o numero de contacto:\n");
					novaInfo = sc.nextLine();
					if(verificarNumeros(novaInfo) && novaInfo.length()<= 9) {
						if (Integer.parseInt(novaInfo) > 0) {
							check = 1;
						}
					}
					else {
						System.out.println("\nO numero de contacto so pode conter 9 digitos");
					}
				}while(check==0);
				check = 0;
				break;
			case "3":
				do {
					System.out.println("\nInsira o nome do departamento:");
					novaInfo = sc.nextLine();
					if(verificarLetras(novaInfo) && novaInfo.length()>0) {
						check = 1;
					}
					else {
						System.out.println("\nO nome apenas pode conter letras");
					}
				}while(check==0);
				check = 0;
				break;
			case "4":
				System.out.println("Insira a nova password:\n");
				novaInfo = sc.nextLine();
				break;
		}
		
		String msgServer = "";
		
		check = 0;
		int tries = 0;
		do {
			try {
				msgServer = rmiserver.alterarPessoa(ncc, op, novaInfo);
				System.out.println(msgServer);
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI alterarPessoa
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
	}

	
	/**
	* Metodo que permite selecionar uma informacao que se queira alterar de um 
	* departamento, identificado pelo seu nome. E pedida a nova informacao que ira 
	* substituir a anterior e envia-se para o RMIServer para atualizar as informacoes no servidor.
	* @see Departamento
	*/
	public static void alteraDep() {
		String nome, novaInfo = "";
		Scanner sc = new Scanner(System.in);
		int check = 0;

		do {
			System.out.println("\nInsira o nome do departamento:");
			nome = sc.nextLine();
			if(verificarLetras(nome) && nome.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome apenas pode conter letras");
			}
		}while(check==0);
		
		check = 0;
		
		System.out.println("\nQual dos campos pretende alterar?\n1- Morada\n2- Numero de contacto\n3- Email de contacto\n4- Faculdade em que se insere");
		String op = sc.nextLine();
		
		switch(op) {
			case "1":
				System.out.println("\nInsira a nova morada:");
				novaInfo = sc.nextLine();
				break;
			case "2":
				do {
					System.out.println("Insira o numero de contacto:\n");
					novaInfo = sc.nextLine();
					if(verificarNumeros(novaInfo) && novaInfo.length()<= 9) {
						if (Integer.parseInt(novaInfo) > 0) {
							check = 1;
						}
					}
					else {
						System.out.println("\nO numero de contacto so pode conter 9 digitos");
					}
				}while(check==0);
				check = 0;
				break;
			case "3":
				System.out.println("Insira o email:\n");
				novaInfo = sc.nextLine();
				break;
			case "4":
				do {
					System.out.println("\nInsira o nome da faculdade:");
					novaInfo = sc.nextLine();
					if(verificarLetras(novaInfo) && novaInfo.length()>0) {
						check = 1;
					}
					else {
						System.out.println("\nO nome apenas pode conter letras");
					}
				}while(check==0);
				check = 0;
				break;
		}
		
		String msgServer = "";
		
		check = 0;
		int tries = 0;
		do {
			try {
				msgServer = rmiserver.alterarDep(nome, op, novaInfo);
				System.out.println(msgServer);
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI inserirFac
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
	}
	
	/**
	* Metodo que permite selecionar uma informacao que se queira alterar de uma 
	* faculdade, identificada pelo seu nome. E pedida a nova informacao que ira 
	* substituir a anterior e envia-se para o RMIServer para atualizar as informacoes no servidor.
	* @see Faculdade
	*/
	public static void alteraFac() {
		String nome, novaInfo = "";
		Scanner sc = new Scanner(System.in);
		int check = 0;

		do {
			System.out.println("\nInsira o nome da faculdade:");
			nome = sc.nextLine();
			if(verificarLetras(nome) && nome.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome apenas pode conter letras");
			}
		}while(check==0);
		
		check = 0;
		
		System.out.println("\nQual dos campos pretende alterar?\n1- Morada\n2- Numero de contacto\n3- Email de contacto");
		String op = sc.nextLine();
		
		switch(op) {
			case "1":
				System.out.println("\nInsira a nova morada:");
				novaInfo = sc.nextLine();
				break;
			case "2":
				do {
					System.out.println("Insira o numero de contacto:\n");
					novaInfo = sc.nextLine();
					if(verificarNumeros(novaInfo) && novaInfo.length()<= 9) {
						if (Integer.parseInt(novaInfo) > 0) {
							check = 1;
						}
					}
					else {
						System.out.println("\nO numero so pode conter 9 digitos");
					}
				}while(check==0);
				check = 0;
				break;
			case "3":
				System.out.println("Insira o email:\n");
				novaInfo = sc.nextLine();
				break;
		}
		
		String msgServer = "";
		
		check = 0;
		int tries = 0;
		do {
			try {
				msgServer = rmiserver.alterarFac(nome, op, novaInfo);
				System.out.println(msgServer);
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI alteraFac
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
	}
	
	/**
	* Metodo que permite reunir informacoes de uma eleicao para se criar a mesma
	* e colocar na base de dados do RMIServer.
	* No final da operacao, e sugerido ao utilizador que crie uma lista de candidatos para associar a eleicao.
	* @see Eleicao
	*/
	public static void criaEleicao() {
		Scanner sc = new Scanner(System.in);		
		String tipo, id, titulo, descricao, yesno;
		String dataI, dataF;
		String dept = "";
		int check = 0;
		
		do {
			System.out.println("\nInsira o tipo de eleicao:\n(1)Nucleo de Estudantes\n(2)Conselho Geral");
			tipo = sc.nextLine();
			if(tipo.equals("1") || tipo.equals("2")) {
				check = 1;
			}
			else {
				System.out.println("\nA opcao so pode conter digitos de 1 a 2");
			}
		}while(check==0);
		
		check = 0;
		
		if (tipo.equals("1")) {
			do {
				System.out.println("\nInsira o nome do departamento:");
				dept = sc.nextLine();
				if(verificarLetras(dept) && dept.length()>0) {
					check = 1;
				}
				else {
					System.out.println("\nO departamento apenas pode conter letras");
				}
			}while(check==0);
				
			check = 0;
		}

		System.out.println("\nInsira o id:");
		id = sc.nextLine();

		do {
			System.out.println("Insira o titulo da eleicao:\n");
			titulo = sc.nextLine();
			if(verificarLetras(titulo) && titulo.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO titulo apenas pode conter letras");
			}
		}while(check==0);
			
		check = 0;
		
		do {
			System.out.println("\nInsira a descricao da eleicao:");
			descricao = sc.nextLine();
			if(verificarLetras(descricao) && descricao.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nA descricao apenas pode conter letras");
			}
		}while(check==0);
			
		check = 0;
		
		System.out.println("Insira a data de inicio da eleicao (DD/MM/AAAA HH:MM):\n");
		dataI = sc.nextLine();
		
		System.out.println("Insira a data do fim da eleicao (DD/MM/AAAA HH:MM):\n");
		dataF = sc.nextLine();
		
		String msgServer = "";
		
		check = 0;
		int tries = 0;
		do {
			try {
				msgServer = rmiserver.criaEleicao(Integer.parseInt(tipo), id, titulo, descricao, dataI, dataF, dept);
				System.out.println(msgServer);
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI alteraFac
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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

		if (msgServer.equals("\nEleicao criada com sucesso")){
			do {
				System.out.println("\nDeseja criar uma lista de candidatos para associar a eleicao?\n(1) Sim      (2) Nao");
				yesno = sc.nextLine();
				if(verificarNumeros(yesno)) {
					if (yesno.equals("1")) {
						criaListaCandidatos();
					}
					check = 1;
				}
				else {
					System.out.println("\nA opcao so pode conter digitos");
				}
			}while(check==0);
			
			check = 0;
		}
	}
	
	/**
	* Metodo que permite reunir informacoes de uma lista de candidatos para se criar a mesma
	* e colocar na base de dados do RMIServer, associando-a a uma eleicao.
	* @see Lista
	*/
	public static void criaListaCandidatos() {
		Scanner sc = new Scanner(System.in);	
		String id, numCandidatos, nomePessoa, nomeLista, tipoLista;
		ArrayList<Pessoa> listaCandidatos = new ArrayList<>();
		int check = 0, num, i;
		
		System.out.println("\nInsira o id da eleicao a qual pretende adicionar uma lista de candidatos:");
		id = sc.nextLine();

		do {
			System.out.println("\nInsira numero de elementos (Min. 2):");
			numCandidatos = sc.nextLine();
			if(verificarNumeros(numCandidatos)) {
				if (Integer.parseInt(numCandidatos) >= 2) {
					check = 1;
				}
			}
			else {
				System.out.println("\nO numero so pode conter digitos");
			}
		}while(check==0);
		num = Integer.parseInt(numCandidatos);
		check = 0;
		
		do {
			System.out.println("\nInsira o nome da lista:");
			nomeLista = sc.nextLine();
			if(verificarLetras(nomeLista) && nomeLista.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome apenas pode conter letras");
			}
		}while(check==0);
		
		check = 0;
		
		do {
			System.out.println("\nEm que tipo se insere a lista?\n(1) Estudantes      (2) Funcionarios       (3) Docentes");
			tipoLista = sc.nextLine();
			if(verificarNumeros(tipoLista)) {
				if ( (tipoLista.equals("1")) || (tipoLista.equals("2")) || (tipoLista.equals("3")) ) {
					check = 1;
				}
				else {
					System.out.println("\nA opcao so pode conter digitos de 1 a 3");
				}
			}
			else {
				System.out.println("\nA opcao so pode conter digitos");
			}
		}while(check==0);
		
		check = 0;
		
		do {
			System.out.println("\nInsira o nome do presidente:");
			nomePessoa = sc.nextLine();
			if(verificarLetras(nomePessoa) && nomePessoa.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome apenas pode conter letras");
			}
		}while(check==0);
		Pessoa p = new Pessoa(nomePessoa);
		listaCandidatos.add(p);	
		num--;
		
		check = 0;
		
		do {
			System.out.println("\nInsira o nome do vice-presidente:");
			nomePessoa = sc.nextLine();
			if(verificarLetras(nomePessoa) && nomePessoa.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome apenas pode conter letras");
			}
		}while(check==0);
		Pessoa novap = new Pessoa(nomePessoa);
		listaCandidatos.add(novap);	
		num--;
		
		check = 0;
		
		for (i=0; i<num;i++) {
			do {
				System.out.println("\nInsira o nome do elemento seguinte:");
				nomePessoa = sc.nextLine();
				if(verificarLetras(nomePessoa) && nomePessoa.length()>0) {
					check = 1;
				}
				else {
					System.out.println("\nO nome apenas pode conter letras");
				}
			}while(check==0);
			Pessoa novaPessoa = new Pessoa(nomePessoa);
			listaCandidatos.add(novaPessoa);
			check = 0;
		}
		
		String msgServer = "";
		
		check = 0;
		int tries = 0;
		do {
			try {
				msgServer = rmiserver.criaLista(id, tipoLista, nomeLista, listaCandidatos);
				System.out.println(msgServer);
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI alteraFac
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
	}
	
	/**
	* Metodo que permite adicionar ou remover candidatos a uma eleicao que nao tenha comecado. 
	* E pedida a nova informacao que ira ser adicionada a lista e envia-se 
	* para o RMIServer para atualizar as informacoes no servidor.
	* @see Lista
	*/
	public static void gereCandidatos() {
		String idE, titulo, op, numCandidatos, nomeP;
		Scanner sc = new Scanner(System.in);
		int check = 0, num, i;
		ArrayList<Pessoa> listaCandidatos = new ArrayList<>();
		String msgServer = "";
		
		System.out.println("\nInsira o id da eleicao a qual pretende adicionar uma lista de candidatos:");
		idE = sc.nextLine();
		
		do {
			System.out.println("\nInsira o titulo da lista:");
			titulo = sc.nextLine();
			if(verificarLetras(titulo) && titulo.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO titulo apenas pode conter letras");
			}
		}while(check==0);
		
		check = 0;
		
		do {
			System.out.println("\nO que pretende fazer?\n(1) Adicoes       (2) Remocoes");
			op = sc.nextLine();
			if(op.equals("1") || op.equals("2")) {
				check = 1;
			}
			else {
				System.out.println("\nA opcao so pode conter digitos de 1 a 2");
			}
		}while(check==0);
		check = 0;
		
		switch(op) {
			case "1":
				do {
					System.out.println("\nInsira numero de elementos:");
					numCandidatos = sc.nextLine();
					if(verificarNumeros(numCandidatos)) {
						if (Integer.parseInt(numCandidatos) > 0) {
							check = 1;
						}
					}
					else {
						System.out.println("\nO numero so pode conter digitos");
					}
				}while(check==0);
				num = Integer.parseInt(numCandidatos);
				check = 0;
				
				for (i=0; i<num;i++) {
					do {
						System.out.println("\nInsira o nome do elemento a adicionar:");
						nomeP = sc.nextLine();
						if(verificarLetras(nomeP) && nomeP.length()>0) {
							check = 1;
						}
						else {
							System.out.println("\nO nome apenas pode conter letras");
						}
					}while(check==0);
					Pessoa novaP = new Pessoa(nomeP);
					listaCandidatos.add(novaP);
					check = 0;
				}
				
				check = 0;
				int tries = 0;
				do {
					try {
						msgServer = rmiserver.adicionaCandidatos(idE, titulo, listaCandidatos);
						System.out.println(msgServer);
						System.out.println("\n");
						check = 1;
					} catch (RemoteException e1) {
						// Tratamento da Excepcao da chamada RMI addCandidatos
						if(tries == 0) {
							try {
								Thread.sleep(6000);
								//faz novo lookup
								rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
				break;
			case "2":
				do {
					System.out.println("\nInsira numero de elementos:");
					numCandidatos = sc.nextLine();
					if(verificarNumeros(numCandidatos)) {
						if (Integer.parseInt(numCandidatos) > 0) {
							check = 1;
						}
					}
					else {
						System.out.println("\nO numero so pode conter digitos");
					}
				}while(check==0);
				num = Integer.parseInt(numCandidatos);
				check = 0;
				
				for (i=0; i<num;i++) {
					do {
						System.out.println("\nInsira o nome do elemento a remover:");
						nomeP = sc.nextLine();
						if(verificarLetras(nomeP) && nomeP.length()>0) {
							check = 1;
						}
						else {
							System.out.println("\nO nome apenas pode conter letras");
						}
					}while(check==0);
					Pessoa novaP = new Pessoa(nomeP);
					listaCandidatos.add(novaP);
					check = 0;
				}
			
				check = 0;
				tries = 0;
				do {
					try {
						msgServer = rmiserver.removeCandidatos(idE, titulo, listaCandidatos);
						System.out.println(msgServer);
						System.out.println("\n");
						check = 1;
					} catch (RemoteException e1) {
						// Tratamento da Excepcao da chamada RMI addCandidatos
						if(tries == 0) {
							try {
								Thread.sleep(6000);
								//faz novo lookup
								rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
				
				break;
		}
	}
	
	/**
	*Metodo que permite selecionar uma informacao que se queira alterar de uma eleicao
	*identificada pelo seu id. E pedida a nova informacao que ira substituir a anterior
	* e envia-se para o RMIServer para atualizar as informacoes no servidor, 
	*mas apenas se ainda nao se tiver iniciado a eleicao.
	*@see Eleicao
	*/
	public static void alteraEleicao() {
		String op, idE, strInfo;
		Scanner sc = new Scanner(System.in);
		int check = 0;
		String msgServer = "";
		
		System.out.println("\nInsira o id da eleicao que pretende alterar:");
		idE = sc.nextLine();
		
		do {
			System.out.println("\nO que pretende alterar?\n(1) Titulo da eleicao\n(2) Descricao da eleicao\n(3) Data de inicio \n(4)Data de fim");
			op = sc.nextLine();
			if(op.equals("1") || op.equals("2") || op.equals("3") || op.equals("4")) {
				check = 1;
			}
			else {
				System.out.println("\nA opcao so pode conter digitos de 1 a 4");
			}
		}while(check==0);
		check = 0;
		
		switch(op) {
			case "1":
				do {
					System.out.println("Insira o novo titulo da eleicao:\n");
					strInfo = sc.nextLine();
					if(verificarLetras(strInfo) && strInfo.length()>0) {
						check = 1;
					}
					else {
						System.out.println("\nO titulo apenas pode conter letras");
					}
				}while(check==0);
					
				
				check = 0;
				int tries = 0;
				do {
					try {
						msgServer = rmiserver.alteraEleicao(idE, op, strInfo);
						System.out.println(msgServer);
						System.out.println("\n");
						check = 1;
					} catch (RemoteException e1) {
						// Tratamento da Excepcao da chamada RMI addCandidatos
						if(tries == 0) {
							try {
								Thread.sleep(6000);
								//faz novo lookup
								rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
				break;
				
			case "2":
				do {
					System.out.println("Insira a nova descricao da eleicao:\n");
					strInfo = sc.nextLine();
					if(verificarLetras(strInfo) && strInfo.length()>0) {
						check = 1;
					}
					else {
						System.out.println("\nA descricao apenas pode conter letras");
					}
				}while(check==0);
					
				
				check = 0;
				tries = 0;
				do {
					try {
						msgServer = rmiserver.alteraEleicao(idE, op, strInfo);
						System.out.println(msgServer);
						System.out.println("\n");
						check = 1;
					} catch (RemoteException e1) {
						// Tratamento da Excepcao da chamada RMI addCandidatos
						if(tries == 0) {
							try {
								Thread.sleep(6000);
								//faz novo lookup
								rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
				break;
				
			case "3":
				System.out.println("Insira a nova data de inicio da eleicao (DD/MM/AAAA HH:MM):\n");
				strInfo = sc.nextLine();
				
				check = 0;
				tries = 0;
				do {
					try {
						msgServer = rmiserver.alteraEleicao(idE, op, strInfo);
						System.out.println(msgServer);
						System.out.println("\n");
						check = 1;
					} catch (RemoteException e1) {
						// Tratamento da Excepcao da chamada RMI addCandidatos
						if(tries == 0) {
							try {
								Thread.sleep(6000);
								//faz novo lookup
								rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
				break;
				
			case "4":
				System.out.println("Insira a nova data para o final da eleicao (DD/MM/AAAA HH:MM):\n");
				strInfo = sc.nextLine();

				check = 0;
				tries = 0;
				do {
					try {
						msgServer = rmiserver.alteraEleicao(idE, op, strInfo);
						System.out.println(msgServer);
						System.out.println("\n");
						check = 1;
					} catch (RemoteException e1) {
						// Tratamento da Excepcao da chamada RMI addCandidatos
						if(tries == 0) {
							try {
								Thread.sleep(6000);
								//faz novo lookup
								rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
								rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
				break;
		}
	}
	
	/**
	* Metodo que permite reunir informacoes de uma mesa de voto para se criar a mesma
	* e colocar na base de dados do RMIServer, associando-a a uma eleicao.
	* @see MesaVoto
	*/
	public static void adicionaMesas() {
		Scanner sc = new Scanner(System.in);	
		String idE, local;
		int check = 0;
		
		System.out.println("\nInsira o id da eleicao a qual pretende adicionar uma mesa de voto:");
		idE = sc.nextLine();

		do {
			System.out.println("\nInsira o departamento em que a mesa de voto irá ficar:");
			local = sc.nextLine();
			if(verificarLetras(local) && local.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome do departamento so pode conter letras");
			}
		}while(check==0);
		check = 0;
		
		String msgServer = "";
		
		check = 0;
		int tries = 0;
		do {
			try {
				msgServer = rmiserver.adicionaMesaVoto(idE, local);
				System.out.println(msgServer);
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI addCandidatos
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
	}
	
	/**
	* Metodo que permite reunir informacoes de uma mesa de voto para se apagar a mesma
	* do servidor RMI e remover a sua associacao a uma eleicao
	* @see MesaVoto
	*/
	public static void removeMesas() {
		Scanner sc = new Scanner(System.in);	
		String idE, local;
		int check = 0;
		
		System.out.println("\nInsira o id da eleicao da qual pretende remover uma mesa de voto:");
		idE = sc.nextLine();

		do {
			System.out.println("\nInsira o departamento em que se encontra a mesa de voto:");
			local = sc.nextLine();
			if(verificarLetras(local) && local.length()>0) {
				check = 1;
			}
			else {
				System.out.println("\nO nome do departamento so pode conter letras");
			}
		}while(check==0);
		check = 0;
		
		String msgServer = "";
		
		check = 0;
		int tries = 0;
		do {
			try {
				msgServer = rmiserver.removeMesaVoto(idE, local);
				System.out.println(msgServer);
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI addCandidatos
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
	}
	
	/**
	* Metodo que utiliza callback para receber a informacao das mesas de voto que estao ligadas
	* @see MesaVoto
	*/
	public void mostraMesasVoto() {
		int check = 0;
		int tries = 0;
		do {
			try {
				rmiserver.printMesasVoto(this.idConsola);
				
				System.out.println("\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI addCandidatos
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
	}
	
	/**
	* Metodo que recebe um ArrayList do servidor com todas as faculdades registadas 
	* e imprime as suas informaçoes
	* @see Faculdade
	*/
	public static void imprimeFaculdades() {
		ArrayList<Faculdade> facs = null;
		
		int check = 0;
		int tries = 0;
		do {
			try {
				facs = rmiserver.getFaculdades();
				if(facs.isEmpty()) {
					System.out.println("Nao existem Faculdades inseridas no sistema.");
				}else {
					System.out.println("Faculdades: ");
					for(Faculdade x: facs) {
						System.out.println("\nNome: "+x.nome);
						System.out.println("\tMorada: "+x.morada);
						System.out.println("\tTelefone: "+x.ntlf);
						System.out.println("\tEmail: "+x.email);
					}
				}
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI addCandidatos
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
		
	}
	
	/**
	* Metodo que recebe um ArrayList do servidor com todos os departamentos registados 
	* e imprime as suas informaçoes
	* @see Departamento
	*/
	public static void imprimeDepartamentos() {
		ArrayList<Departamento> deps = null;
		
		int check = 0;
		int tries = 0;
		do {
			try {
				deps = rmiserver.getDepartamentos();
				if(deps.isEmpty()) {
					System.out.println("Nao existem Departamentos inseridas no sistema.");
				}else {
					System.out.println("Departamentos: ");
					for(Departamento x: deps) {
						System.out.println("\nNome: "+x.nome);
						System.out.println("\tMorada: "+x.morada);
						System.out.println("\tTelefone: "+x.ntlf);
						System.out.println("\tFaculdade a que pertence: "+x.fac.nome);
					}
				}
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI addCandidatos
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
	}
	
	/**
	* Metodo que recebe um ArrayList do servidor com todas as eleicoes registadas 
	* e imprime as suas informaçoes
	* @see Eleicao
	*/
	public static void imprimeEleicoes() {
		ArrayList<Eleicao> eleicoes = null;
		
		int check = 0;
		int tries = 0;
		do {
			try {
				eleicoes = rmiserver.getEleicoes();
				if(eleicoes.isEmpty()) {
					System.out.println("Nao existem Eleicoes inseridas no sistema.");
				}else {
					System.out.println("Eleicoes: ");
					for(Eleicao x: eleicoes) {
						System.out.println("\nTitulo: "+x.titulo);
						System.out.println("\tID: "+x.id);
						System.out.println("\tDescricao: "+x.descricao);
						System.out.println("\tData Inicio: "+x.dataInicio.toString());
						System.out.println("\tData Fim: "+x.dataFim.toString());
					}
				}
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI addCandidatos
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
		
	}
	
	/**
	* Metodo que recebe uma lista de eleicoes do servidor e para cada uma vai imprimir
	* as suas listas de candidaturas e qual o numero de votos obtido
	* @see Eleicao
	* @see Lista
	*/
	public static void consultaEleicoesPassadas() {
		ArrayList<Eleicao> lista;
		
		int check = 0;
		int tries = 0;
		do {
			try {
				lista = rmiserver.getEleicoesPassadas();
				if(lista.isEmpty()) {
					System.out.println("Nao existem eleicoes para mostrar.");
				}else {
					for(Eleicao x: lista) {
						System.out.println("\nEleicao "+x.titulo);
						System.out.println("\tID: "+x.id);
						System.out.println("\tDescricao: "+x.descricao);
						System.out.println("\tData Inicio: "+x.dataInicio.toString());
						System.out.println("\tData Fim: "+x.dataInicio.toString());
						System.out.println("Resultados: ");
						if(x.tipo == 1) {
							if(x.registoVotos.size()>0) {
								for(Lista y: x.listaCandidaturas) {
									double perc = y.contagem/(double)x.registoVotos.size() * 100;
									System.out.println("\t"+y.nome+": "+perc+"% (Num. votos: "+y.contagem+")");
								}
							}
						}else {
							if(x.registoVotos.size()>0) {
								int alunos = 0, docentes = 0, funcs = 0;
								for(Lista y:x.listaCandidaturas) {
									if(y.tipoLista.equals("1")) {
										alunos += y.contagem;
									}else if(y.tipoLista.equals("2")) {
										funcs += y.contagem;
									}else if(y.tipoLista.equals("3")) {
										docentes += y.contagem;
									}
								}
								
								for(Lista y: x.listaCandidaturas) {
									if(y.tipoLista.equals("1")) {
										if(alunos>0) {
											double perc = (double)y.contagem/(double)alunos * 100;
											System.out.println("\t"+y.nome+": "+perc+"% (Num. votos: "+y.contagem+")");
										}else {
											System.out.println("\t"+y.nome+": 0% (Num. votos: "+y.contagem+")");
										}
									}else if(y.tipoLista.equals("2")) {
										if(funcs>0) {
											double perc = (double)y.contagem/(double)funcs * 100;
											System.out.println("\t"+y.nome+": "+perc+"% (Num. votos: "+y.contagem+")");
										}else {
											System.out.println("\t"+y.nome+": 0% (Num. votos: "+y.contagem+")");
										}
									}else if(y.tipoLista.equals("3")) {
										if(docentes>0) {
											double perc = (double)y.contagem/(double)docentes * 100;
											System.out.println("\t"+y.nome+": "+perc+"% (Num. votos: "+y.contagem+")");
										}else {
											System.out.println("\t"+y.nome+": 0% (Num. votos: "+y.contagem+")");
										}
									}else if(y.tipoLista.equals("4")) {
										double perc = y.contagem/(double)x.registoVotos.size() * 100;
										System.out.println("\t"+y.nome+": "+perc+"% sobre Total (Num. votos: "+y.contagem+")");
									}
								}
							}
						}
					}
				}
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI addCandidatos
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
		
		
	}
	
	/**
	* Metodo que faz o servidor remover as eleicoes passadas da base de dados comum e
	* colocar na base de dados de eleicoes antigas.
	*/
	public static void verificaEleicoesPassadas() {
		int count;
		
		int check = 0;
		int tries = 0;
		do {
			try {
				count = rmiserver.moveEleicoesPassadas();
				System.out.println("Removidas "+count+" eleicoes!\n");
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI addCandidatos
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
		
	}
	
	public void run() {
		while(true) {
			switch(menuEntrada()) {
		        case "0":
		            System.exit(0);
		            break;
		        case "1":
		            criaPessoa();
		            break;
		        case "2":
		        	criaAdmin();
		        	break;
		        case "3":
		        	menuGestaoEntidades();
		            break;
		        case "4":
		        	menuGestaoEleicoes();
		            break;
		        case "5":
		        	menuConsulta();
		            break;
		        case "6":
		        	mostraMesasVoto();
		        	break;
			}
		}
	}
	

	public static void main(String[] args) {
		int check = 0;
		Scanner sc = new Scanner(System.in);
		String idConsola;
		carregaConfig();
		try {
			rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
			
			consola = new AdminClient();
			
			do {
				System.out.println("\nInsira o ID do user da Consola de Administracao: ");
				idConsola = sc.nextLine();
				consola.idConsola = idConsola;
				
				String ans = rmiserver.subscribeConsola((ADMIN_C_I)consola);
				if(ans.equals("true")) {
					check = 1;
				}else {
					System.out.println("ID de consola ja em uso. Troque por favor.");
				}
				
			}while(check==0);
			
			consola.run(); //Ultimo comando OBRIGATORIAMENTE
			
		} catch (RemoteException | NotBoundException | MalformedURLException e) {
			System.out.println("Erro na ligacao ao servidor RMI. Tente novamente ou altere o ficheiro de configuracoes.");
			System.exit(0);
		}
	}
	
	
	public static String menuEntrada(){
		System.out.println("\n\n\n\n===== Bem vindo a consola de administracao =====\n");
		System.out.println("\n1- Registar um novo eleitor");
		System.out.println("\n2- Registar um novo admin");
		System.out.println("\n3- Gestao de departamentos e faculdades");
		System.out.println("\n4- Gestao de eleicoes");
		System.out.println("\n5- Consultas");
		System.out.println("\n6- Mostrar Mesas de voto ligadas ao servidor.");
		System.out.println("\n0- Sair");
		System.out.println("Opcao: ");
		Scanner sc = new Scanner(System.in);
		String opcao;
		opcao = sc.nextLine();
		return opcao;
	}

	public static void menuGestaoEntidades(){
		while(true){
			System.out.println("\n\n\n\n\n\n===== Gestao de departamentos e faculdades =====\n");
			System.out.println("\n1- Registar um novo departamento");
			System.out.println("\n2- Registar uma nova faculdade");
			System.out.println("\n3- Alterar informacoes de um departamento");
			System.out.println("\n4- Alterar informacoes de uma faculdade");
			System.out.println("\n5- Imprimir Faculdades");
			System.out.println("\n6- Imprimir Departamentos");
			System.out.println("\n0- Sair");
			System.out.print("Opcao: ");
			Scanner sc = new Scanner(System.in);
			String opcao = sc.nextLine();
			switch (opcao) {
			case "0":
				return;
			case "1":
				criaDep();
				break;
			case "2":
				criaFac();
				break;
			case "3":
				alteraDep();
				break;
			case "4":
				alteraFac();
				break;
			case "5":
				imprimeFaculdades();
				break;
			case "6":
				imprimeDepartamentos();
				break;
			}
		}
	}

	public static void menuGestaoEleicoes(){
        while(true){
            System.out.println("\n\n\n\n\n===== Gestao de eleicoes =====\n");
            System.out.println("\n1- Criar uma nova eleicao");
            System.out.println("\n2- Adicionar listas de candidatos a uma eleicao");
            System.out.println("\n3- Gerir listas de candidatos a uma eleicao");
            System.out.println("\n4- Alterar propriedades de uma eleicao");
            System.out.println("\n5- Adicionar mesas de voto associadas a uma eleicao");
            System.out.println("\n6- Remover mesas de voto associadas a uma eleicao");
            System.out.println("\n7- Imprimir eleicoes no sistema");
            System.out.println("\n8- Verifica Eleicoes terminadas");
            System.out.println("\n0- Sair");
            System.out.print("Opcao: ");
            Scanner sc = new Scanner(System.in);
            int opcao = sc.nextInt();
            switch (opcao) {
            case 0:
                return;
            case 1:
                criaEleicao();
                break;
            case 2:
                criaListaCandidatos();
                break;
            case 3:
                gereCandidatos();
                break;
            case 4:
                alteraEleicao();
                break;
            case 5:
            	adicionaMesas();
                break;
            case 6:
            	removeMesas();
                break;
            case 7:
            	imprimeEleicoes();
            	break;
            case 8:
            	verificaEleicoesPassadas();
            	break;
            }
        }
    }
		
	public static void menuConsulta(){
		while(true){
			System.out.println("\n\n\n\n\n\n===== Consultas =====\n");
			System.out.println("\n1- Ver em que local votou um eleitor");
			System.out.println("\n2- Ver notificacoes em tempo real");
			System.out.println("\n3- Ver resultados de eleicoes anteriores");
			System.out.println("\n0- Sair");
			System.out.print("Opcao: ");
			Scanner sc = new Scanner(System.in);
			String opcao = sc.nextLine();
			switch (opcao) {
			case "0":
				return;
			case "1":
				localVotouEleitor();
				break;
			case "2":
				mostrarNotificacoesTempoReal();
				break;
			case "3":
				consultaEleicoesPassadas();
				break;
			}
		}
	}

	/**
	* Metodo que reune as informacoes do eleitor e da eleicao 
	* e recebe do servidor RMI a informacao sobre o registo de voto
	*/
	public static void localVotouEleitor() {
		Scanner sc = new Scanner(System.in);
		RegistoVoto ans = null;
		String cc, idEleicao;
		System.out.println("Insira o CC do eleitor que pretende pesquisar: ");
		cc = sc.nextLine();
		System.out.println("Insira o ID da eleicao que pretende pesquisar: ");
		idEleicao = sc.nextLine();
		
		int check = 0;
		int tries = 0;
		do {
			try {
				ans = rmiserver.pesquisaLocalVoto(cc, idEleicao);
				if(ans != null) {
					System.out.println("\nCC: "+ans.cc+"\nID Eleicao: "+idEleicao+"\nLocal: "+ans.nomeDep+"Data/Hora: "+ans.dataVoto.toString());
				}else{
					System.out.println("Voto nao encontrado.");
				}
				check = 1;
			} catch (RemoteException e1) {
				// Tratamento da Excepcao da chamada RMI addCandidatos
				if(tries == 0) {
					try {
						Thread.sleep(6000);
						//faz novo lookup
						rmiserver = (RMI_S_I) Naming.lookup("rmi://"+registryIP+":"+registryPort+"/rmi");
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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
						rmiserver.subscribeConsola((ADMIN_C_I)consola);
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

	}
	
	/**
	* Metodo que mostra as notificacoes em tempo real, sendo que todos 
	* os votos sao notificados. So e ativo quando a variavel
	* mostraNotificacoes esta a true
	*/
	public static void mostrarNotificacoesTempoReal() {
		Scanner sc = new Scanner(System.in);
		mostraNotificacoes = true;
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nNotificacoes em Tempo Real sobre as Eleicoes a decorrer: \nPrima qualquer tecla para sair...\n\n");
		sc.nextLine();
		mostraNotificacoes = false;
	}

	/**
	* Metodo boolean que verifica se uma dada cadeia tem apenas letras.
	* @param cadeia uma String que e a cadeia que queremos verificar
	*@return true se a cadeia nao contiver digitos
	*/
	public static boolean verificarLetras(String cadeia) {
		for (int i = 0; i < cadeia.length(); i++) {
			if (Character.isDigit(cadeia.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	* Metodo boolean que verifica se uma dada cadeia tem apenas digitos.
	* @param numero uma String que e a cadeia que queremos verificar
	* @return true se a cadeia nao contiver letras
	*/
	public static boolean verificarNumeros(String numero) {
		try{
			Integer.parseInt(numero);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	
	@Override
	public void print(String msg) throws RemoteException {
		System.out.println(msg);	
	}

	@Override
	public String getId() throws RemoteException {
		return this.idConsola;
	}


	@Override
	public void notifica(String msg) throws RemoteException {
		if(mostraNotificacoes) {
			//Imprime a mensagem
			System.out.println(msg);
		}
	}
}
