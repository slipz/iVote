package rmiserver;

import java.io.*;
import java.net.*;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import ws.WS_I;

/**
* Classe RMIServer
* </p>
* Classe que representa um servidor RMI e que se usa tanto para o primario
* como para o secundario.  
* Esta classe tem, fundamentalmente, a funcao de receber dados da AdminClient
* e do TCPServer, e depois coloca os dados na base de dados.
* Para alem disso, tambem recebe pacotes UDP "heartbeats" do servidor RMI secundario
* </p>
* @see AdminClient
* @see TCPServer
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/
public class RMIServer extends UnicastRemoteObject implements RMI_S_I{

    private static final long serialVersionUID = 1L;
    
    public static String sTipo;
    
    public static String registryIP;
    public static int registryPort;
    public static String udpOwnIP;
    public static int udpOwnPort;
    public static String udpOtherIP;
    public static int udpOtherPort;
    
    public static ArrayList<Admin> bdAdmins;
    public static ArrayList<Pessoa> bdPessoas;
    public static ArrayList<Eleicao> bdEleicoes;
    public static ArrayList<Departamento> bdDep;
    public static ArrayList<Faculdade> bdFac;
    public static ArrayList<Eleicao> registoEleicoes;
    public static ArrayList<ADMIN_C_I> listaConsolas;
    public static ArrayList<TCP_S_I> listaMesasVoto;
    public static ArrayList<WS_I> listaWebSockets;
    
    public RMIServer() throws RemoteException{
        super();
        loadBD();
    }
    
    //Fazer func que le e escreve listas para ficheiro
    
	public static void carregaConfig(){
		String file = "RMIServerConfig.txt";
		String line;
		String[] l1;
		System.out.println("Uploding RMIServer configurations...");
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
			if(l1[0].equals("udpOwnPort")) {
				udpOwnPort = Integer.parseInt(l1[1]);
			}
			
			line = buffer.readLine();
			l1 = line.split("=");
			if(l1[0].equals("udpOwnIP")) {
				udpOwnIP = l1[1];
			}	
			
			line = buffer.readLine();
			l1 = line.split("=");
			if(l1[0].equals("udpOtherPort")) {
				udpOtherPort = Integer.parseInt(l1[1]);
			}
			
			line = buffer.readLine();
			l1 = line.split("=");
			if(l1[0].equals("udpOtherIP")) {
				udpOtherIP = l1[1];
			}
			
		}catch(FileNotFoundException e){
			System.out.println("File "+file+" not found");
			System.exit(0);
		}catch(IOException e){
			System.out.println("Erro na leitura das configuracoes");
			System.exit(0);
		}
		System.out.println("RMIServerConfig.txt successfully uploaded.");
	}
	
	
	public void printAdmins(){
		for(Admin x: bdAdmins){
			System.out.println(x.username);
		}
	}
	
	
    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de uma pessoa(eleitor), cria o mesmo e coloca-o na base de dados.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a criacao do eleitor e o processo deve comecar de novo.
    * </p>
    * @param tipo um inteiro que indica qual a profissao do eleitor
    * @param nome uma String com o nome do eleitor
    * @param password uma String que contem a password escolhida pelo eleitor
    * para poder aceder ao terminal de voto
    * @param nomeDep uma String que permite identificar o departamento
    * em que o eleitor esta inserido pelo nome
    * @param ntelef uma String com o contacto telefonico do eleitor
    * @param morada uma String com a morada do eleitor
    * @param nCC uma String com o numero do cartao de cidadao do eleitor,
    * unica para cada Pessoa
    * @param data uma String com a date de validade do cartao de cidadao
    * </p>
    * @return "\nEleitor criado com sucesso" se o eleitor for criado e adicionado a base de dados
    */
	
	@Override
	public synchronized String registarAdmin(String username, String password) throws RemoteException {
		if(verificaUsernameAdmin(username)){
			Admin novo = new Admin(username,password);
			bdAdmins.add(novo);
			try {
				escreveAdmins(bdAdmins, "admins.txt");
				printAdmins();
				return "\nAdmin criado com sucesso";
			} catch (IOException e) {
				// Erro na escrita do ficheiro
				System.out.println("Erro na escrita do ficheiro \"admins.txt\". ");
				bdAdmins.remove(novo);
				return "\nErro no registo do admin. Falha na comunicacao com a BD";
			}
		}else{
			return "\nUsername j� existente na base de dados";
		}
	}
	
    @Override
    public synchronized String registarPessoa(int tipo, String nome, String password, String nomeDep, String ntelef, String morada, String nCC, String data) throws RemoteException {
    	Date validadecc = null;
    	DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		if(dataValidaDias(data)) {
			try {
				validadecc = format.parse(data);		
			} catch (ParseException e) {
				return "\nData invalida.";
			}
		}
		else {
			return "\nData invalida";
		}
    	Departamento dep = verificaDepExiste(nomeDep);
		if(verificaccPessoa(nCC)) {
			if(dep != null) {
				Pessoa novap = new Pessoa(tipo, nome, password, dep, ntelef, morada, nCC, validadecc);
		    	bdPessoas.add(novap);
		    	try {
					escrevePessoas(bdPessoas, "pessoas.txt");
					return "\nEleitor criado com sucesso";
				} catch (IOException e) {
					// Erro na escrita do ficheiro
					System.out.println("Erro na escrita do ficheiro \"pessoas.txt\". ");
					bdPessoas.remove(novap);
					return "\nErro no registo da pessoa. Falha na comunicacao com a BD";
				}
			}
			else {
				return "\nDepartamento inserido nao existente";
			}
		}
		else {
			return "\nCC ja existente na base de dados.";
		}	
    }
    
    public synchronized ArrayList<DetalhesVoto> detalhesVoto(String idEleicao) throws RemoteException{
        String nomeLocal, cc;
        ArrayList<DetalhesVoto> detalhes = new ArrayList<DetalhesVoto>();
        for(Eleicao x: bdEleicoes) {
            if(x.id.equals(idEleicao)) { //encontro eleicao
                for(MesaVoto m : x.listaMesas) { //percorro as mesas
                 //insiro todas as mesas no array de Detalhes
                    DetalhesVoto detalhe = new DetalhesVoto(m.dep.nome, 0, 0, 0, 0);
                    detalhes.add(detalhe);
                }
                DetalhesVoto online = new DetalhesVoto("Browser", 0, 0, 0, 0);
                detalhes.add(online);
                
                //agora conto os votos dos registos
                for(RegistoVoto r: x.registoVotos) {
                    cc=r.cc;
                    for(Pessoa p: bdPessoas) { //procuro a pessoa
                        if((p.nCC).equals(cc)) {                            
                            for(DetalhesVoto det:detalhes) { //procuro o detalheVoto certo
                                if((det.local).equals(r.nomeDep)) {
                                    det.totalVotos++;
                                    if(p.tipo == 1) {   //atualizo contador certo
                                        det.totalAlunos++;
                                    }
                                    else if(p.tipo == 2) {
                                        det.totalFunc++;
                                    }
                                    else{
                                        det.totalDocentes++;
                                    }
                                }
                            }
                        }
                    }
                }
                return detalhes;
            }
        }
        return null;
    }

    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de um departamento, cria o mesmo e coloca-o na base de dados.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a criacao do departamento e o processo deve comecar de novo.
    * </p>
    * @param nome uma String com o nome do departamento
    * @param ntlf uma String com o contacto telefonico do departamento
    * @param morada uma String com a morada do departamento
    * @param email uma String com o contacto eletronico do departamento
    * @param nomeFac uma String que permite identificar a faculdade em que um departamento se insere
    * </p>
    * @return "\nDepartamento criado com sucesso" se o departamento 
    * for criado e adicionado a base de dados
    */
    @Override
    public synchronized String inserirDep(String nome, String morada, String ntlf, String email, String nomeFac) throws RemoteException {
    	int check = 0;
    	Faculdade fac = verificaFacExiste(nomeFac);
    	Departamento dep = verificaDepExiste(nome);
    	if(dep == null) {
    		if(fac != null) {
				Departamento novoD = new Departamento(nome, morada, ntlf, email, fac);
				bdDep.add(novoD);
		    	try {
					escreveDep(bdDep, "departamentos.txt");
				} catch (IOException e) {
					// Erro na escrita do ficheiro
					System.out.println("Erro na escrita do ficheiro \"departamentos.txt\". ");
					bdDep.remove(novoD);
					return "\nErro no registo do Departamento. Falha na comunicacao com a BD";
					
				}
				return "\nDepartamento criado com sucesso";
			}
			else {
				return "\nA faculdade inserida nao existe";	
			}   		
    	}
    	else {
    		return "\nO departamento inserido ja existe";
    	}
    }
    
    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de uma faculdade, cria a mesma e coloca-a na base de dados.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a criacao da faculdade e o processo deve comecar de novo.
    * </p>
    * @param nome uma String com o nome da faculdade
    * @param ntlf uma String com o contacto telefonico da faculdade
    * @param morada uma String com a morada da faculdade
    * @param email uma String com o contacto eletronico da faculdade
    * </p>
    * @return "\nFaculdade criada com sucesso" se a faculdade
    * for criada e adicionada a base de dados
    */
    @Override
    public synchronized String inserirFac(String nome, String morada, String ntlf, String email) throws RemoteException {
    	int check = 0;
    	Faculdade fac = verificaFacExiste(nome);
		if(fac == null) {
			Faculdade novaF = new Faculdade(nome, morada, ntlf, email);
			bdFac.add(novaF);
	    	try {
				escreveFac(bdFac, "faculdades.txt");
			} catch (IOException e) {
				// Erro na escrita do ficheiro
				System.out.println("Erro na escrita do ficheiro \"faculdades.txt\". ");
				bdFac.remove(novaF);
				return "\nErro no registo da Faculdade. Falha na comunicacao com a BD";
			}
			return "\nFaculdade criada com sucesso";
		}
		else {
			return "\nA faculdade inserida ja existe";	
		}
    }
    
    @Override
    public synchronized String alterarPessoa(String cc, String campo, String info) throws RemoteException {
        int check = 0, check1 = 0;
        for(Pessoa x: bdPessoas) {
         if(x.nCC.equals(cc)) {
          switch(campo) {
           case "1": 
            x.morada = info;
            check=1;
            break;
           case "2":
            x.ntelef = info;
            check=1;
            break;
           case "3":
            for(Departamento d: bdDep) {
             if(d.nome.equals(info)) {
              x.dep = d;
              check1 = 1;
             }
            }
            if(check1 == 0) {
             return "\nO departamento inserido nao existe";
            }
            check = 1;
            break;
           case "4":
        x.password = info;
        check = 1;
        break;
          } 
         }
        }
        
        if(check == 0) {
         return "\nO numero de CC inserido nao existe";
        }
        try {
      escrevePessoas(bdPessoas, "pessoas.txt");
     } catch (IOException e) {
      // Erro na escrita do ficheiro
      System.out.println("Erro na escrita do ficheiro \"pessoas.txt\". ");
      return "\nErro na alteracao do eleitor. Falha na comunicacao com a BD";
     }
        return "\nInformacao alterada com sucesso";
       }
    
    
    
    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de um departamento para serem alteradas e faz a sua atualizacao.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a atualizacao do departamento e o processo deve recomecar.
    * </p>
    * @param nome uma String com o nome do departamento
    * @param campo uma String que permite identificar qual o campo que vai
    * ser atualizado
    * @param info uma String com a nova informacao
    * </p>
    * @return "\nInformacao alterada com sucesso" se a informacao for
    * atualizada na base de dados.
    */
    @Override
    public synchronized String alterarDep(String nome, String campo, String info) throws RemoteException {
    	int check = 0;
    	for(Departamento d: bdDep) {
    		if(d.nome.equals(nome)) {
    			switch(campo) {
    			case "1": 
    				d.morada = info;
    				break;
    			case "2":
    				d.ntlf = info;
    				break;
    			case "3":
    				d.email = info;
    				break;
    			case "4":
    				for(Faculdade f: bdFac) {
    					if(f.nome.equals(nome)) {
    						d.fac = f;
    						check = 1;
    					}
    				}
    				if(check == 0) {
    					return "\nA faculdade inserida nao existe";
    				}
    			}
    			check = 1;
    			break;
    		}
    	}
    	
    	if(check == 0) {
    		return "\nO departamento inserido nao existe";
    	}
    	try {
			escreveDep(bdDep, "departamentos.txt");
		} catch (IOException e) {
			// Erro na escrita do ficheiro
			System.out.println("Erro na escrita do ficheiro \"departamentos.txt\". ");
			return "\nErro na alteracao do departamento. Falha na comunicacao com a BD";
		}
    	return "\nInformacao alterada com sucesso";
    }

    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de uma faculdade para serem alteradas e faz a sua atualizacao.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a atualizacao da faculdade e o processo deve recomecar.
    * </p>
    * @param nome uma String com o nome da faculdade
    * @param campo uma String que permite identificar qual o campo que vai
    * ser atualizado
    * @param info uma String com a nova informacao
    * </p>
    * @return "\nInformacao alterada com sucesso" se a informacao for
    * atualizada na base de dados.
    */
    @Override
    public synchronized String alterarFac(String nome, String campo, String info) throws RemoteException {
    	int check = 0;
    	for(Faculdade f: bdFac) {
    		if(f.nome.equals(nome)) {
    			switch(campo) {
    			case "1": 
    				f.morada = info;
    				break;
    			case "2":
    				f.ntlf = info;
    				break;
    			case "3":
    				f.email = info;
    				break;
    			}
    			check = 1;
    			break;
    		}
    	}
    	if(check == 0) {
    		return "\nA faculdade inserida nao existe";
    	}
    	try {
			escreveFac(bdFac, "faculdades.txt");
		} catch (IOException e) {
			// Erro na escrita do ficheiro
			System.out.println("Erro na escrita do ficheiro \"faculdades.txt\". ");
			return "\nErro na alteracao da faculdade. Falha na comunicacao com a BD";
		}
    	return "\nInformacao alterada com sucesso";
    }

    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de uma eleicao para ser criada e adicionada a base de dados.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a criacao da mesma e o processo deve recomecar.
    * </p>
    * @param tipo um inteiro que indica se a eleicao e para
    * o nucleo de estudantes de um departamento
    * ou para o conselho geral
    * @param id uma String unica para cada eleicao que permite identifica-la
    * @param titulo uma String com o titulo da eleicao
    * @param descricao uma String que descreve a eleicao
    * @param dataI uma String que descreve a data de inicio da eleicao
    * @param dataF uma String que descreve a data de termino da eleicao,
    * deve ser superior a inicial e a atual
    * @param dept uma String com o nome de um departamento para o identificar.
    * NOTA: apenas e preenchida a informacao caso seja uma eleicao
    * para o nucleo, caso contrario e preenchida a null na AdminClient
    * </p>
    * @return "\nEleicao criada com sucesso" se for criada uma eleicao e 
    * adicionada a base de dados
    */
    @Override
    public synchronized String criaEleicao(int tipo, String id, String titulo, String descricao, String dataI, String dataF, String dept) throws RemoteException {
        // TODO Permitir que se associe uma mesa de voto a esta eleicao
        // Eleicao tem de ter um array de mesas de voto
        Date dataInicial = null;
        Date dataFinal = null;
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if(dataValidaHoras(dataI)) {
            try {
                dataInicial = format.parse(dataI);      
            } catch (ParseException e) {
                return "\nData invalida";
            }
        }
        else {
            return "\nData de inicio invalida, deve estar no formato DD/MM/AAAA HH:MM e apos a data atual";
        }
        
        if(dataValidaHoras(dataF)) {
            try {
                dataFinal = format.parse(dataF);        
            } catch (ParseException e) {
                return "\nData invalida.";
            }
        }
        else {
            return "\nData de fim invalida, deve estar no formato DD/MM/AAAA HH:MM e apos a data atual";
        }
        
        if (dataFinal.before(dataInicial) || dataFinal == dataInicial) {
            return "\nData invalida, a hora de terminar a eleicao deve ser apos a hora de inicio";
        }
        
        Eleicao e = verificaIdEleicao(id);
        if(e == null) {
            if(tipo == 1) {
                Departamento d =  verificaDepExiste(dept);
                if(d != null) {
                    Eleicao novaE = new Eleicao(tipo, id, dataInicial, dataFinal, titulo, descricao, d);
                    bdEleicoes.add(novaE);
                    try {
                        escreveEleicoes(bdEleicoes, "eleicoes.txt");
                    } catch (IOException e1) {
                    	// Erro na escrita do ficheiro
    					System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
    					bdEleicoes.remove(novaE);
    					return "\nErro no registo da eleicao. Falha na comunicacao com a BD";
                    }
                    return "\nEleicao criada com sucesso";  
                }
                else {
                    return "\nDepartamento nao encontrado";
                }
            }
            else {
                Eleicao novaE = new Eleicao(tipo, id, dataInicial, dataFinal, titulo, descricao);
                bdEleicoes.add(novaE);
                try {
                    escreveEleicoes(bdEleicoes, "eleicoes.txt");
                } catch (IOException e1) {
                	// Erro na escrita do ficheiro
					System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
					bdEleicoes.remove(novaE);
					return "\nErro no registo da eleicao. Falha na comunicacao com a BD";
                }
                return "\nEleicao criada com sucesso";      
            }       
        }
        else {
            return "\nId de eleicao ja existente na base de dados.";
        }   
    }

    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de uma lista de candidaturas para ser criada
    * e adicionada a base de dados.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a criacao da mesma e o processo deve recomecar.
    * </p>
    * @param id uma String que permite identificar a eleicao
    * para fazer a associacao da lista
    * @param tipoLista uma String que indica se a lista é de
    * estudantes, funcionarios, docentes, votos brancos ou votos nulos.
    * @param nome uma String que contem o nome da lista
    * @param lista um ArrayList com os nomes das pessoas que fazem parte da lista.
    * NOTA: estas Pessoa nao contem informacao adicional, apenas o nome.
    *</p>
    *@return "\nLista criada com sucesso" se a mesma for criada e adicionada
    * a base de dados
    */
    @Override
    public synchronized String criaLista(String id, String tipoLista, String nome, ArrayList<Pessoa> lista) throws RemoteException {
    	Eleicao e = verificaIdEleicao(id);
    	if(e != null) {
    		Lista l = verificaListaCandidat(id, nome);
    		if(l == null) {
    			if ( ((e.tipo) == 1) && (tipoLista.equals("1")) ){
    				Lista listaC = new Lista(id, tipoLista, nome, lista, 0);
    				e.listaCandidaturas.add(listaC);
    				try{
    					escreveEleicoes(bdEleicoes, "eleicoes.txt");
    				} catch (IOException e1) {
    					// Erro na escrita do ficheiro
    					System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
    					e.listaCandidaturas.remove(listaC);
    					return "\nErro no registo da lista. Falha na comunicacao com a BD";
    				}
    				return "\nLista criada com sucesso";
    			}
    			else if ( ((e.tipo) == 2) && (tipoLista.equals("1")) ){
    				Lista listaC = new Lista(id, tipoLista, nome, lista, 0);
    				e.listaCandidaturas.add(listaC);
    				try{
    					escreveEleicoes(bdEleicoes, "eleicoes.txt");
    				} catch (IOException e1) {
    					// Erro na escrita do ficheiro
    					System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
    					e.listaCandidaturas.remove(listaC);
    					return "\nErro no registo da lista. Falha na comunicacao com a BD";
    				}
    				return "\nLista criada com sucesso";
    			}
    			else if ( ((e.tipo) == 2) && (tipoLista.equals("2")) ){
    				Lista listaC = new Lista(id, tipoLista, nome, lista, 0);
    				e.listaCandidaturas.add(listaC);
    				try{
    					escreveEleicoes(bdEleicoes, "eleicoes.txt");
    				} catch (IOException e1) {
    					// Erro na escrita do ficheiro
    					System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
    					e.listaCandidaturas.remove(listaC);
    					return "\nErro no registo da lista. Falha na comunicacao com a BD";
    				}
    				return "\nLista criada com sucesso";
    			}
    			else if ( ((e.tipo) == 2) && (tipoLista.equals("3")) ){
    				Lista listaC = new Lista(id, tipoLista, nome, lista, 0);
    				e.listaCandidaturas.add(listaC);
    				try{
    					escreveEleicoes(bdEleicoes, "eleicoes.txt");
    				} catch (IOException e1) {
    					// Erro na escrita do ficheiro
    					System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
    					e.listaCandidaturas.remove(listaC);
    					return "\nErro no registo da lista. Falha na comunicacao com a BD";
    				}
    				return "\nLista criada com sucesso";
    			}
    			else {
    				return "\nIncompatibilidade da lista com o tipo de eleicao";
    			}
    		}
    		else {
    			return "\nJa existe uma lista com o mesmo titulo nesta eleicao";
    		}
    	}
		else {
			return "\nId de eleicao nao existente na base de dados.";
		}
    
    }

    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de uma eleicao e de uma lista para serem adicionados candidatos
    * e faz a sua atualizacao.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a atualizacao da lista e o processo deve recomecar.
    * </p>
    * @param idEleicao uma String que identifica a eleicao
    * em que a lista esta inserida
    * @param titulo uma String com o titulo da eleicao
    * @param listaCandidatos um ArrayList que contem a informacao
    * das pessoas a serem adicionadas a lista
    * </p>
    * @return "\nMembros adicionados com sucesso" se a informacao for
    * atualizada na base de dados.
    */
    @Override
    public synchronized String adicionaCandidatos(String idEleicao, String titulo, ArrayList<Pessoa> listaCandidatos) throws RemoteException {
    	Eleicao e = verificaIdEleicao(idEleicao);
    	if(e != null) {
    		Lista l = verificaListaCandidat(idEleicao, titulo);
    		if(l != null) {
    			for (Pessoa p: listaCandidatos) {
    				l.listaP.add(p);
    			}
    			try{
    				escreveEleicoes(bdEleicoes, "eleicoes.txt");
    			} catch (IOException e1) {
    				// Erro na escrita do ficheiro
					System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
					l.listaP.clear();
					return "\nErro no registo dos candidatos. Falha na comunicacao com a BD";
    			}
    		}
    		else {
    			return "\nLista não encontrada para a eleicao com este Id";
    		}
    	}
    	else {
    		return "\nId de eleicao nao encontrado";	
    	}
    	return "\nMembros adicionados com sucesso";
    }

    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de uma eleicao e de uma lista para serem removidos candidatos
    * e faz a sua atualizacao.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a atualizacao da lista e o processo deve recomecar.
    * </p>
    * @param idEleicao uma String que identifica a eleicao
    * em que a lista esta inserida
    * @param titulo uma String com o titulo da eleicao
    * @param listaCandidatos um ArrayList que contem a informacao
    * das pessoas a serem removidas da lista
    * </p>
    * @return "\nMembros removidos com sucesso" se a informacao for
    * atualizada na base de dados.
    */
    @Override
    public synchronized String removeCandidatos(String idEleicao, String titulo, ArrayList<Pessoa> listaCandidatos) throws RemoteException {
    	Eleicao e = verificaIdEleicao(idEleicao);
    	if(e != null) {
    		Lista l = verificaListaCandidat(idEleicao, titulo);
    		if(l != null) {
    			for (Pessoa p: listaCandidatos) {
    				l.listaP.remove(p);
    			}
    			try{
    				escreveEleicoes(bdEleicoes, "eleicoes.txt");
    			} catch (IOException e1) {
    				// Erro na escrita do ficheiro
					System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
					return "\nErro no registo dos candidatos. Falha na comunicacao com a BD";
    			}
    		}
    		else {
    			return "\nLista não encontrada para a eleicao com este Id";
    		}
    	}
    	else {
    		return "\nId de eleicao nao encontrado";	
    	}
    	return "\nMembros removidos com sucesso";
    }

    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de uma eleicao e faz a sua atualizacao.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a atualizacao da eleicao e o processo deve recomecar.
    * Tambem proibe a alteracao de informacoes da eleicao caso
    * a eleicao ja tenha comecado.
    * </p>
    * @param idEleicao uma String que identifica a eleicao
    * @param campo uma String que identifica a informacao a ser atualizada
    * @param info uma String que contem os novos dados
    * </p>
    * @return "\nEleicao alterada com sucesso" se a informacao for
    * atualizada na base de dados.
    */
    @Override
    public synchronized String alteraEleicao(String idEleicao, String campo, String info) throws RemoteException {
        Eleicao e = verificaIdEleicao(idEleicao);
        if(e != null) {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date d = new Date();
            Date teste;
            if ((e.dataInicio).after(d)) {
                switch (campo) {
                    case "1":
                        e.titulo = info;
                        try {
                            escreveEleicoes(bdEleicoes, "eleicoes.txt");
                        } catch (IOException e1) {
                            // TODO Tratar exception escrever ficheiro
                            e1.printStackTrace();
                        }
                        break;
                    case "2":
                        e.descricao = info;
                        try {
                            escreveEleicoes(bdEleicoes, "eleicoes.txt");
                        } catch (IOException e1) {
                            // TODO Tratar exception escrever ficheiro
                            e1.printStackTrace();
                        }
                        break;
                    case "3":
                        if(dataValidaHoras(info)) {
                            try {
                                e.dataInicio = format.parse(info);      
                            } catch (ParseException p) {
                                return "\nData invalida.";
                            }
                        }
                        else {
                            return "\nData de inicio invalida, por favor insira uma data no formato DD/MM/AAAA HH:MM apos a data atual";
                        }
                        try {
                            escreveEleicoes(bdEleicoes, "eleicoes.txt");
                        } catch (IOException e1) {
                            // TODO Tratar exception escrever ficheiro
                            e1.printStackTrace();
                        }   
                        break;
                    case "4":
                        if(dataValidaHoras(info)) {
                            try {
                                teste = format.parse(info);
                                if (teste.before(e.dataInicio) || teste == e.dataInicio) {
                                    return "Data invalida, a data deve ser apos a hora de inicio da eleicao";
                                }
                                e.dataFim = teste;
                            } catch (ParseException p) {
                                return "\nData invalida.";
                            }
                        }
                        else {
                            return "\nData de fim invalida, por favor insira uma data no formato DD/MM/AAAA HH:MM apos a data atual";
                        }
                        try {
                            escreveEleicoes(bdEleicoes, "eleicoes.txt");
                        } catch (IOException e1) {
                            // TODO Tratar exception escrever ficheiro
                            e1.printStackTrace();
                        }
                        break;
                }   
            }
            else {
                return "\nA eleicao ja esta a decorrer, como tal, nao e possivel alterar nenhuma das suas propriedades";
            }
        }
        else {
            return "\nId de eleicao nao encontrado";
        }
        return "\nEleicao alterada com sucesso";    
    }


    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de uma mesa de voto e adiciona-a a eleicoes.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite criacao da mesa e o processo deve recomecar.
    * </p>
    * @param id uma String que identifica a eleicao
    * @param nomeDep uma String que identifica o local onde ira estar a mesa
    * NOTA: nao e permitida a insercao de mais que uma mesa no mesmo departamento
    * </p>
    * @return "\nMesa de voto criada com sucesso e adicionada a uma eleicao" se 
    * mesa for criada e adicionada a base de dados.
    */
    @Override
    public synchronized String adicionaMesaVoto(String id, String nomeDep) throws RemoteException {
    	Eleicao e = verificaIdEleicao(id);
    	if(e != null) {
    		if(e.tipo == 1) {
    			Departamento d = verificaDepExiste(nomeDep);
    			if(d!=null) {
    				if(e.dep.nome.equals(nomeDep)) {
    					MesaVoto mesa = new MesaVoto(d);
    	    			e.listaMesas.add(mesa);
    			    	try {
    						escreveEleicoes(bdEleicoes, "eleicoes.txt");
    					} catch (IOException e1) {
    						// Erro na escrita do ficheiro
    						System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
    						e.listaMesas.remove(mesa);
    						return "\nErro no registo da mesa de voto. Falha na comunicacao com a BD";
    					}
    				}
    			}else {
    				return "\nDepartamento nao existente.";
    			}
    		}else{
    			Departamento d = verificaDepExiste(nomeDep);
	    		if (d != null) {
	    			if(verificaMesaEleicao(id,nomeDep)){
		    			MesaVoto mesa = new MesaVoto(d);
		    			e.listaMesas.add(mesa);
				    	try {
							escreveEleicoes(bdEleicoes, "eleicoes.txt");
						} catch (IOException e1) {
							// Erro na escrita do ficheiro
							System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
							e.listaMesas.remove(mesa);
							return "\nErro no registo da mesa de voto. Falha na comunicacao com a BD";
						}
	    			}
	    		}else {
	    			return "\nDepartamento nao existente.";
	    		}
    		}
    	}
    	else {
    		return "\nId de eleicao nao existente";
    	}
    	return "\nMesa de voto criada com sucesso e adicionada a uma eleicao";
    }
    
    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de uma mesa de voto e a remove de uma eleicao.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a remocao da mesa e o processo deve recomecar.
    * </p>
    * @param id uma String que identifica a eleicao
    * @param nomeDep uma String que identifica o local onde esta a mesa
    * </p>
    * @return "\nMesa de voto removida com sucesso da eleicao" se mesa for 
    * removida e a base de dados for atualizada.
    */
	@Override
	public synchronized String removeMesaVoto(String id, String nomeDep) throws RemoteException {
		Eleicao e = verificaIdEleicao(id);
    	if(e != null) {
    		Departamento d = verificaDepExiste(nomeDep);
    		if (d != null) {
    			MesaVoto temp = null;
    			for(MesaVoto y: e.listaMesas){
    				if(y.dep.nome.equals(nomeDep)){
    					temp = y;
    					e.listaMesas.remove(y);
    				}
    			}
		    	try {
					escreveEleicoes(bdEleicoes, "eleicoes.txt");
				} catch (IOException e1) {
					// Erro na escrita do ficheiro
					e.listaMesas.add(temp);
					System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
					return "\nErro na eliminacao da mesa de voto. Falha na comunicacao com a BD";
				}
    		}
    		else {
    			return "\nDepartamento nao existente";
    		}
    	}
    	else {
    		return "\nId de eleicao nao existente";
    	}
    	return "\nMesa de voto removida com sucesso da eleicao";
	}
	
	public synchronized boolean verificaMesaEleicao(String idEleicao, String nomeDep){
		boolean check = true;
		
		for(Eleicao x :bdEleicoes){
			if(x.id.equals(idEleicao)){
				for(MesaVoto y: x.listaMesas){
					if(y.dep.nome.equals(nomeDep)){
						check = false;
						break;
					}
				}
				break;
			}
		}
		return check;
		
	}

    /**
    * Metodo que recebe os dados da AdminClient relativos as informacoes
    * de um eleitor e uma eleicao e procura o seu registo de voto.
    * Se qualquer um dos campos nao for valido ou reconhecido,
    * nao permite a remocao da mesa e o processo deve recomecar.
    * </p>
    * @param cc uma String que identifica o eleitor
    * @param idEleicao uma String que identifica a eleicao
    * </p>
    * @return RegistoVoto contendo a informacao do voto do eleitor
    * nessa eleicao como o local e data.
    * @see RegistoVoto
    */
	public synchronized RegistoVoto pesquisaLocalVoto(String cc, String idEleicao) throws RemoteException{
		for(Eleicao x: bdEleicoes) {
			if(x.id.equals(idEleicao)) {
				for(RegistoVoto y:x.registoVotos) {
					if(y.cc.equals(cc)) {
						return y;
					}
				}
			}
		}
		return null;
	}

    /**
    * Metodo que recebe os dados do TCPServer relativos as informacoes
    * de uma eleicao e de um eleitor.
    * Verifica se a pessoa existe e se pode votar nessa eleicao.
    * </p>
    * @param cc uma String que identifica o eleitor
    * @param idEleicao uma String que identifica a eleicao
    * </p>
    * @return nome uma String com o nome do eleitor
    */
	@Override
	public synchronized String identificarEleitor(String cc, String idEleicao) throws RemoteException {
		int tipoP = 0, check = 0;
		String nome = "null";
		String depEleitor = "";
		
		for(Pessoa x: bdPessoas) {
			if(x.nCC.equals(cc)) {
				//Pessoa existe
				tipoP = x.tipo;
				nome = x.nome;
				depEleitor = x.dep.nome;
				check = 1;
			}
		}
		if(check == 0) {
			return "Pessoa nao existe";
		}
		check = 0;
		for(Eleicao y: bdEleicoes) {
			if(y.id.equals(idEleicao)) {
				//Encontramos a eleicao
				if((y.tipo == 1 && tipoP == 1)) {
					if(y.dep.nome.equals(depEleitor)) {
						if(y.checkCC(cc)) {
							//Ainda nao votou, logo pode votar
							check = 1;
							return nome;
						}
					}
				}else if(y.tipo == 2) {
					if(y.checkCC(cc)) {
						//Ainda nao votou, logo pode votar
						check = 1;
						return nome;
					}
				}
			}
		}
		
		if(check == 0) {
			return "Pessoa nao pode votar nessa eleicao";
		}
		return "null";
	}

    /**
    * Metodo que recebe os dados do TCPServer relativos as informacoes
    * de uma eleicao e de um eleitor.
    * Verifica se a password da pessoa corresponde a correta
    * e se pode votar nessa eleicao.
    * </p>
    * @param username uma String que identifica o eleitor
    * @param password uma String com a password do eleitor
    * @param idEleicao uma String que identifica a eleicao
    * </p>
    * @return temp um ArrayList com as listas candidatas a essa eleicao
    * nas quais o eleitor pode votar, consoante o seu tipo(profissao).
    */
	@Override
	public synchronized ArrayList<Lista> autenticaEleitor(String username, String password, String idEleicao)
			throws RemoteException {
		
		ArrayList<Lista> temp = new ArrayList<>();
		
		for(Pessoa x: bdPessoas) {
			if(x.nCC.equals(username)) {
				if(x.password.equals(password)) {
					//Autenticado
					for(Eleicao y: bdEleicoes) {
						if(y.id.equals(idEleicao)) {
							if(y.tipo == 1) {
								if(x.tipo == 1) {
									return y.listaCandidaturas;
								}
							}else if(y.tipo == 2) {
								for(Lista k: y.listaCandidaturas) {
									if(k.tipoLista.equals(Integer.toString(x.tipo)) || k.tipoLista.equals("4")) {
										temp.add(k);
									}
								}
								return temp;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	
	@Override
	public synchronized String LoginWeb(String username, String password) throws RemoteException {
		
		for(Pessoa x: bdPessoas) {
			if(x.nCC.equals(username)) {
				if(x.password.equals(password)) {
					//Autenticado
					return "normalUser";
				}
			}
		}
		for(Admin x: bdAdmins) {
			if(x.username.equals(username)) {
				if(x.password.equals(password)) {
					//Autenticado
					return "admin";
				}
			}
		}
		
		return "Credenciais invalidas";
	}
	
    
    /**
    * Metodo que recebe do TCPServer um voto.
    * Verifica se a eleicao existe, se a lista selecionada existe e cria um 
    * novo registo de voto. Em seguida adiciona-o a lista de votos da eleicao.
    * </p>
    * @param voto um Voto que contem todos os dados do voto, da eleicao
    * e do eleitor
    * </p>
    * @return "sucesso" se o registo de voto for criado, adicionado a lista
    * de votos da eleicao e a base de dados atualizada
    */
    @Override
    public synchronized String votar(Voto voto) throws RemoteException {
    	
    	Eleicao e = verificaIdEleicao(voto.idEleicao);
    	if(e != null) {
    		Lista l = verificaListaCandidat(voto.idEleicao, voto.nomeLista);
    		if(l != null) {
    			l.contagem++;
    			//Fazer aqui as notificações
    			Date d = new Date();
    			RegistoVoto reg = new RegistoVoto(voto.ccPessoa,voto.nomeMesa,d);
    			e.registoVotos.add(reg);
    			notificaAll("Novo voto\t[Eleicao ID -> "+voto.idEleicao+" ]\t[Mesa -> "+voto.nomeMesa+" ]");
		    	try {
					escreveEleicoes(bdEleicoes, "eleicoes.txt");
				} catch (IOException e1) {
					// Erro na escrita do ficheiro
					System.out.println("Erro na escrita do ficheiro \"eleicoes.txt\". ");
					e.listaMesas.remove(reg);
					l.contagem--;
					return "write_file_error";
				}
    			return "sucesso";
    		}
    		else {
    			return "lista_not_found";
    		}
    	}
    	else {
    		return "eleicao_not_found";
    	}
    }
    
    /**
    * Metodo que cria uma referencia remota da consola no RMIServer,
    * passando o objeto consola para o servidor RMI e guardando este
    * essa referencia numa lista.
    * Metodo que permite implementar RMI callback
    * </p>
    * @return "true"
    */
	@Override
	public synchronized String subscribeConsola(ADMIN_C_I consola) throws RemoteException {
		int index = 0, check = 0;
		do {
			try {
				for(ADMIN_C_I x: listaConsolas) {
					index = listaConsolas.indexOf(x);
					if(x.getId().equals(consola.getId())) {
						return "false";
					}
				}
				check = 1;
			}catch(RemoteException e) {
				//Trata excecao de quando uma consola se desliga
				System.out.println("Consola desligada. A remover referencia...");
				listaConsolas.remove(index);
			}
		}while(check==0);
		System.out.println("Consola "+consola.getId()+" ligada ao Servidor.");
		listaConsolas.add(consola);
		return "true";
	}

	/**
    * Metodo que cria uma referencia remota do servidor TCP no RMIServer,
    * passando o objeto tcp para o servidor RMI e guardando este
    * essa referencia numa lista.
    * Metodo que permite implementar RMI callback
    */
	@Override
	public synchronized String subscribeTCP(TCP_S_I  tcp) throws RemoteException {
		int index = 0, check = 0;
		String temp = "";

		temp = tcp.getDepName();

		do {
			try {
				for(TCP_S_I x: listaMesasVoto) {
					index = listaMesasVoto.indexOf(x);
					if(x.getDepName().equals(temp)) {
						return "false";
					}
				}
				check = 1;
			}catch(RemoteException e) {
				//Trata excecao de quando uma consola se desliga
				System.out.println("Mesa de voto desligada. A remover referencia...");
				listaMesasVoto.remove(index);
			}
		}while(check==0);
		System.out.println("Mesa de voto "+tcp.getDepName()+" ligada ao Servidor.");
		listaMesasVoto.add(tcp);
		printMesasVotoWeb();
		return "true";
	}
	
	@Override
	public synchronized String subscribeWebSocket(WS_I  websocket) throws RemoteException {
		listaWebSockets.add(websocket);
		System.out.println("nova");
		return "true";
	}
	
	@Override
	public synchronized String unsubscribeWebSocket(WS_I  websocket) throws RemoteException {
		listaWebSockets.remove(websocket);
		return "true";
	}

    /**
    * Metodo que retorna um  nome de departamento, se este existir.
    * A pesquisa e feita pelo nome.
    */
	@Override
	public synchronized String retornaDep(String nome) throws RemoteException {
		Departamento ans;
		if((ans = verificaDepExiste(nome))==null) {
			return "null";
		}else {
			return ans.nome;
		}
	}
	
    /**
    * Metodo que imprime as mesas de voto associadas a servidores TCP.
    */
	@Override
	public synchronized void printMesasVoto(String id) throws RemoteException {
		int index1 = 0, index2 = 0, check1=0, check2=0;
		do {
			try {
				for(ADMIN_C_I x: listaConsolas) {
					index1 = listaConsolas.indexOf(x);
					if(x.getId().equals(id)) {
						do {
							try {
								for(TCP_S_I y: listaMesasVoto) {
									index2 = listaMesasVoto.indexOf(y);
									x.print(y.getDepName());	
								}
								check2 = 1;
							}catch(RemoteException e2) {
								//quando um tcpserver nao existe
								System.out.println("Servidor TCP desligado. A remover referencia...");
								listaMesasVoto.remove(index2);
							}
						}while(check2==0);
					}
				}
				check1=1;
			}catch(RemoteException e1) {
				//quando uma consola nao existe
				System.out.println("Consola desligada. A remover referencia...");
				listaConsolas.remove(index1);
			}
		}while(check1==0);
	}
	
	
	@Override
	public synchronized void printMesasVotoWeb() throws RemoteException {
		int index1 = 0, index2 = 0, check1=0, check2=0;
		String m = "mesas**";
		do {
			try {
				for(WS_I x: listaWebSockets) {
					index1 = listaWebSockets.indexOf(x);
					do {
						try {
							for(TCP_S_I y: listaMesasVoto) {
								index2 = listaMesasVoto.indexOf(y);
								m = m + y.getDepName();
							}
							check2 = 1;
						}catch(RemoteException e2) {
							//quando um tcpserver nao existe
							System.out.println("Servidor TCP desligado. A remover referencia...");
							listaMesasVoto.remove(index2);
						}
					}while(check2==0);
					System.out.println(m);
					x.notifica_web(m);
				}
				check1=1;
			}catch(RemoteException e1) {
				//quando uma consola nao existe
				System.out.println("Websocket desligada. A remover referencia...");
				listaWebSockets.remove(index1);
			}
		}while(check1==0);
	}
	
	
    /**
    * Metodo que devolve as eleicoes disponiveis para aquela mesa de voto
    * naquela hora.
    */
	@Override
	public synchronized ArrayList<Message> devolveEleicoes(String dep) throws RemoteException {
		ArrayList<Message> messages = new ArrayList<>();
		for(Eleicao x: bdEleicoes) {
			for(MesaVoto y: x.listaMesas) {
				if(y.dep.nome.equals(dep)) {
					Date atual = new Date();
					if(atual.before(x.dataFim)) {
						System.out.println(x.id);
						if(atual.after(x.dataInicio)) {
							System.out.println(x.id);
							Message m = new Message(dep,x.id);
							messages.add(m);
						}
					}
				}
			}
		}
		return messages;
	}
    
	@Override
	public synchronized ArrayList<Faculdade> getFaculdades() throws RemoteException {
		return bdFac;
	}

	@Override
	public synchronized ArrayList<Departamento> getDepartamentos() throws RemoteException {
		return bdDep;
	}

	@Override
	public synchronized ArrayList<Eleicao> getEleicoes() throws RemoteException {
		return bdEleicoes;
	}
	
	@Override
	public synchronized ArrayList<Eleicao> getEleicoesEleitor(String username) throws java.rmi.RemoteException{
		ArrayList<Eleicao> ret = new ArrayList<>();
		int tipoP = 0, check = 0;
		String nomeDep = "";
		
		for(Pessoa x: bdPessoas){
			if(x.nCC.equals(username)){
				tipoP = x.tipo;
				nomeDep = x.dep.nome;
				break;
			}
		}
		
		if(tipoP != 0){
			Date d = new Date();
			for(Eleicao y: bdEleicoes){
				if(d.after(y.dataInicio) && d.before(y.dataFim)){
					if(y.tipo == 1){
						//Eleicao do nucleo
						if(tipoP == 1 && y.dep.nome.equals(nomeDep)){
							int check1 = 0;
							for(RegistoVoto reg: y.registoVotos){
								if(reg.cc.equals(username)){
									// nao pode votar
									check1 = 1;
								}
							}
							if(check1 == 0){
								//pode votar
								ret.add(y);
							}
						}
					}else if(y.tipo == 2){
						int check1 = 0;
						for(RegistoVoto reg: y.registoVotos){
							if(reg.cc.equals(username)){
								// nao pode votar
								check1 = 1;
							}
						}
						if(check1 == 0){
							//pode votar
							ret.add(y);
						}
					}else{
						//not implemented yet
						continue;
					}
				}
			}
			return ret;
		}else{
			//pessoa errada
			return null;
		}	
	}
		
	@Override
	public synchronized ArrayList<Eleicao> getEleicoesPassadas() throws RemoteException{
		checkEleicoesPassadas();
		return registoEleicoes;
	}
	
	@Override
	public synchronized int moveEleicoesPassadas() throws java.rmi.RemoteException{
		int count = checkEleicoesPassadas();
		return count;
	}
	
	@Override
	public String getTipoUser(String username) throws java.rmi.RemoteException{
		for(Pessoa x: bdPessoas){
			if(x.nCC.equals(username)){
				return Integer.toString(x.tipo);
			}
		}
		return null;
	}
	
	public synchronized void notificaAll(String msg) {
		int index = 0, check = 0;
		do {
			try {
				for(ADMIN_C_I x: listaConsolas) {
					index = listaConsolas.indexOf(x);
					x.notifica(msg);
				}
				check = 1;
			}catch(RemoteException e) {
				System.out.println("Consola desligada. A remover referencia...");
				listaConsolas.remove(index);
			}
		}while(check == 0);
		check = 0;
		do {
			try {
				for(WS_I x: listaWebSockets) {
					index = listaWebSockets.indexOf(x);
					x.notifica_one("voto**"+msg,x.getUsername());
					break;
				}
				check = 1;
			}catch(RemoteException e) {
				System.out.println("Websocket desligada. A remover referencia...");
				listaWebSockets.remove(index);
			}
		}while(check == 0);
	}
	
	
	public Eleicao procuraEleicao(String idEleicao) throws java.rmi.RemoteException{
		for(Eleicao x: bdEleicoes){
			if(x.id.equals(idEleicao)){
				return x;
			}
		}
		return null;
	}
	
	
    public synchronized boolean verificaccPessoa(String cc) {
    	for(Pessoa x: bdPessoas) {
    		if(x.nCC.equals(cc)) {
    			return false;
    		}
    	}
    	return true;
    }
    
    public synchronized boolean verificaUsernameAdmin(String username) {
    	for(Admin x: bdAdmins) {
    		if(x.username.equals(username)) {
    			return false;
    		}
    	}
    	return true;
    }
    
    public synchronized Departamento verificaDepExiste(String nomeDep) {
    	for(Departamento d: bdDep) {
    		if(d.nome.equals(nomeDep)) {
    			return d;
    		}
    	}
    	return null;
    }
    
    public synchronized Faculdade verificaFacExiste(String nomeFac) {
    	for(Faculdade f: bdFac) {
    		if(f.nome.equals(nomeFac)) {
    			return f;
    		}
    	}
    	return null;
    }
    
    public synchronized boolean verificaPwd(String pass) {
    	for(Pessoa x: bdPessoas) {
    		if(x.password.equals(pass)) {
    			return false;
    		}
    	}
    	return true;
    }
    
    public synchronized Eleicao verificaIdEleicao(String idE) {
    	for(Eleicao e: bdEleicoes) {
    		if(e.id.equals(idE)) {
    			return e;
    		}
    	}
    	return null;
    }
    
    public synchronized Lista verificaListaCandidat(String idE, String nome) {
    	for(Eleicao e: bdEleicoes) {
    		if(e.id.equals(idE)) {
    			for (Lista l: e.listaCandidaturas) {
    				if ((l.nome).equals(nome)) {
    					return l;
    				}
    			}
    		}
    	}
    	return null;
    }
    
    public synchronized boolean dataValidaDias(String data) {
    	DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    	Date d = new Date();
    	//format.format(d) para imprimir a data atual
    	int check=0;
        int num = 0;
        StringTokenizer st = new StringTokenizer(data, "/");
        while (st.hasMoreTokens()) {
            String act = st.nextToken().toString();
            try{
                num = Integer.parseInt(act);
            }catch(Exception e){
                return false;
            }
            check++;
            if(check>3){
                return false;
            }
            switch (check){
                case 1:
                    if(num<1 || num>31){
                        return false;
                    }
                    break;
                case 2:
                    if(num<1 || num>12){
                        return false;
                    }
                    break;
                case 3:
                    if(num<2017){
                        return false;
                    }
                    break;
            }   
        }
        Date validadecc = null;
		try {
			TemporalAccessor ta = DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(data);
			validadecc = format.parse(data);
		} catch (DateTimeParseException e) {
			return false;
		} catch (ParseException e) {
			return false;
		}
	    if(validadecc.before(d)) {
	    	return false;
	    }
        return true;
    }
    
    public synchronized boolean dataValidaHoras(String data) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date d = new Date();
        //format.format(d) para imprimir a data atual
        int check=0;
        int num = 0;
        System.out.println(data);
        StringTokenizer st = new StringTokenizer(data, " ");
        String act = st.nextToken().toString();
        StringTokenizer dias = new StringTokenizer(act, "/");
        while (dias.hasMoreTokens()) {
            String nAct = dias.nextToken().toString();
            try{
                num = Integer.parseInt(nAct);
            }catch(Exception e){
                return false;
            }
            check++;
            if(check>3){
                return false;
            }
            switch (check){
                case 1:
                    if(num<1 || num>31){
                        return false;
                    }
                    break;
                case 2:
                    if(num<1 || num>12){
                        return false;
                    }
                    break;
                case 3:
                    if(num<2017){
                        return false;
                    }
                    break;
            }   
        }
        
        check = 0;
        try {
	        String next = st.nextToken().toString();
	        StringTokenizer horas = new StringTokenizer(next, ":");
	        while (horas.hasMoreTokens()) {
	            String nNext = horas.nextToken().toString();
	            try{
	                num = Integer.parseInt(nNext);
	            }catch(Exception e){
	                return false;
	            }
	            check++;
	            if(check>2){
	                return false;
	            }
	            switch (check){
	                case 1:
	                    if(num<0 || num>23){
	                        return false;
	                    }
	                    break;
	                case 2:
	                    if(num<0 || num>59){
	                        return false;
	                    }
	                    break;
	            }
	        }
        } catch(Exception e) {
        	return false;
        }
        
        Date dataTeste = null;
        try {
            TemporalAccessor ta = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").parse(data);
            dataTeste = format.parse(data);
        } catch (DateTimeParseException e) {
            return false;
        } catch (ParseException e) {
            return false;
        }
        if(dataTeste.before(d)) {
            return false;
        }
        return true;
    }
    
    public synchronized static int checkEleicoesPassadas() {
    	Date atual = new Date();
    	int count = 0;
    	for(Eleicao x: bdEleicoes) {
    		if(atual.after(x.dataFim)) {
    			count++;
    			registoEleicoes.add(x);
    			bdEleicoes.remove(x);
    		}
    	}
    	return count;
    }
	
    /**
    * Metodo que contem as funcoes de ler e escrever ficheiros
    */
	public static boolean loadBD(){
		bdAdmins = new ArrayList<>();
	    bdPessoas = new ArrayList<>();
	    bdEleicoes = new ArrayList<>();
	    bdDep = new ArrayList<>();
	    bdFac = new ArrayList<>();
	    registoEleicoes = new ArrayList<>();
	    listaConsolas = new ArrayList<>();
	    listaMesasVoto = new ArrayList<>();
	    listaWebSockets = new ArrayList<>();
	    
	    try{
			bdAdmins = leAdmins(bdAdmins,"admins.txt");
			System.out.println("[BD] - Ficheiro Admins carregado.");
		}catch(IOException | ClassNotFoundException e){
			System.out.println("[ERROR] - Ficheiro Admins");
		}
		try{
			bdPessoas = lePessoas(bdPessoas,"pessoas.txt");
			System.out.println("[BD] - Ficheiro Pessoas carregado.");
		}catch(IOException | ClassNotFoundException e){
			System.out.println("[ERROR] - Ficheiro Pessoas");
		}
		try{
			bdEleicoes = leEleicoes(bdEleicoes,"eleicoes.txt");
			System.out.println("[BD] - Ficheiro Eleicoes carregado.");
		}catch(IOException | ClassNotFoundException e){
			System.out.println("[ERROR] - Ficheiro Eleicoes");
		}
		try{
			bdDep = leDep(bdDep,"departamentos.txt");
			System.out.println("[BD] - Ficheiro Departamentos carregado.");
		}catch(IOException | ClassNotFoundException e){
			System.out.println("[ERROR] - Ficheiro Departamentos");
		}
		try{
			bdFac = leFac(bdFac,"faculdades.txt");
			System.out.println("[BD] - Ficheiro Faculdades carregado.");
		}catch(IOException | ClassNotFoundException e){
			System.out.println("[ERROR] - Ficheiro Faculdades");
		}
		try{
			registoEleicoes = leRegisto(registoEleicoes,"registo.txt");
			System.out.println("[BD] - Ficheiro Registo carregado.");
		}catch(IOException | ClassNotFoundException e){
			System.out.println("[ERROR] - Ficheiro Registo");
		}
		return true;
	}    
    
	public synchronized static ArrayList<Pessoa> lePessoas(ArrayList<Pessoa> pessoas, String nome) throws IOException, ClassNotFoundException {
		try{
			ObjectInputStream ficheiro = new ObjectInputStream(new FileInputStream(nome));
			pessoas = (ArrayList<Pessoa>)ficheiro.readObject(); 
			//ficheiro.close();
		}catch(ClassNotFoundException | IOException f){
		}
		return pessoas;
	}

	public synchronized static void escrevePessoas(ArrayList<Pessoa> pessoa, String nome)throws IOException{
		try{
			ObjectOutputStream ficheiro = new ObjectOutputStream(new FileOutputStream(nome));
			ficheiro.writeObject(pessoa);
			//ficheiro.close();
		}catch(IOException e){
		}
	}
	
	public synchronized static ArrayList<Admin> leAdmins(ArrayList<Admin> admins, String nome) throws IOException, ClassNotFoundException {
		try{
			ObjectInputStream ficheiro = new ObjectInputStream(new FileInputStream(nome));
			admins = (ArrayList<Admin>)ficheiro.readObject(); 
			//ficheiro.close();
		}catch(ClassNotFoundException | IOException f){
		}
		return admins;
	}

	public synchronized static void escreveAdmins(ArrayList<Admin> admins, String nome)throws IOException{
		try{
			ObjectOutputStream ficheiro = new ObjectOutputStream(new FileOutputStream(nome));
			ficheiro.writeObject(admins);
			//ficheiro.close();
		}catch(IOException e){
		}
	}	
	
	public static synchronized ArrayList<Eleicao> leEleicoes(ArrayList<Eleicao> eleicoes, String nome) throws IOException, ClassNotFoundException {
		try{
			ObjectInputStream ficheiro = new ObjectInputStream(new FileInputStream(nome));
			eleicoes = (ArrayList<Eleicao>)ficheiro.readObject(); 
			//ficheiro.close();
		}catch(ClassNotFoundException | IOException f){
		}
		return eleicoes;
	}

	public synchronized static void escreveEleicoes(ArrayList<Eleicao> eleicoes, String nome)throws IOException{
		try{
			ObjectOutputStream ficheiro = new ObjectOutputStream(new FileOutputStream(nome));
			ficheiro.writeObject(eleicoes);
			//ficheiro.close();
		}catch(IOException e){
		}
	}
	public synchronized static ArrayList<Departamento> leDep(ArrayList<Departamento> deps, String nome) throws IOException, ClassNotFoundException {
		try{
			ObjectInputStream ficheiro = new ObjectInputStream(new FileInputStream(nome));
			deps = (ArrayList<Departamento>)ficheiro.readObject(); 
			//ficheiro.close();
		}catch(ClassNotFoundException | IOException f){
		}
		return deps;
	}

	public synchronized static void escreveDep(ArrayList<Departamento> deps, String nome)throws IOException{
		try{
			ObjectOutputStream ficheiro = new ObjectOutputStream(new FileOutputStream(nome));
			ficheiro.writeObject(deps);
			//ficheiro.close();
		}catch(IOException e){
		}
	}
	public synchronized static ArrayList<Faculdade> leFac(ArrayList<Faculdade> facs, String nome) throws IOException, ClassNotFoundException {
		try{
			ObjectInputStream ficheiro = new ObjectInputStream(new FileInputStream(nome));
			facs = (ArrayList<Faculdade>)ficheiro.readObject(); 
			//ficheiro.close();
		}catch(ClassNotFoundException | IOException f){
		}
		return facs;
	}

	public synchronized static void escreveFac(ArrayList<Faculdade> facs, String nome)throws IOException{
		try{
			ObjectOutputStream ficheiro = new ObjectOutputStream(new FileOutputStream(nome));
			ficheiro.writeObject(facs);
			//ficheiro.close();
		}catch(IOException e){
		}
	}
	
	public synchronized static ArrayList<Eleicao> leRegisto(ArrayList<Eleicao> registo, String nome) throws IOException, ClassNotFoundException {
		try{
			ObjectInputStream ficheiro = new ObjectInputStream(new FileInputStream(nome));
			registo = (ArrayList<Eleicao>)ficheiro.readObject(); 
			//ficheiro.close();
		}catch(ClassNotFoundException | IOException f){
		}
		return registo;
	}

	public synchronized static void escreveRegisto(ArrayList<Eleicao> registo, String nome)throws IOException{
		try{
			ObjectOutputStream ficheiro = new ObjectOutputStream(new FileOutputStream(nome));
			ficheiro.writeObject(registo);
			//ficheiro.close();
		}catch(IOException e){
		}
	}
	
    /**
    * Na main, pedimos qual o tipo de servidor que quer ligar,
    * se e o primario ou secundario.
    * Caso seja primario, faz bind ao Registry, cria sockets UDP
    * e espera um ping do servidor secundario.
    * Quando receber o ping responde com um pong.
    * Caso seja o secundario, cria as sockets,
    * envia pacotes para o servidor primario e quando detetar uma
    * falha torna-se primario e faz os passos do mesmo.
    */
    
    @SuppressWarnings("deprecation")
	public static void main(String args[]) {
    	System.getProperties().put("java.security.policy", "policy.all");
		System.setSecurityManager(new RMISecurityManager());
		
		Scanner sc = new Scanner(System.in);
		int check = 0;
		
		do {
			System.out.println("Insira o tipo de Servidor RMI:\n1 - Servidor Primario\n2 - Servidor Secundario");
			sTipo = sc.nextLine();
			if(sTipo.equals("1") || sTipo.equals("2")) {
				check = 1;
			}else {
				System.out.println("Valor invalido. Introduza 1 ou 2.");
			}
		}while(check==0);
		
		carregaConfig();
		
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(udpOwnPort);
		} catch (SocketException e2) {
			System.out.println("Failover indisponivel. Porto errado.");
			return;
		}
			
		while(true) {
			if(sTipo.equals("1")) {
				//Servidor primario
				System.out.println("udpOwnPort: "+udpOwnPort);
				System.out.println("udpOwnIP: "+udpOwnIP);
				System.out.println("udpOtherPort: "+udpOtherPort);
				System.out.println("udpOtherIP: "+udpOtherIP);
				
		    	System.out.println(registryPort);
		        try {
		        	RMIServer h = new RMIServer();
		        	Naming.rebind("rmi://"+registryIP+":"+registryPort+"/rmi", h);
		            System.out.println("RMIServer ready.");
		            
		        } catch (RemoteException | MalformedURLException re) {
		            System.out.println("Erro no bind ao registry. Corriga a porta no ficheiro de configuracoes");
		            return;
		        }
							
				System.out.println("Socket Datagram à escuta no porto "+udpOwnPort);
				System.out.println("A espera que novo Backup RMIServer se ligue.");
				//Recebe primeiro ping -> confirmação de que Backup esta ligado
				byte[] buffer = new byte[1000];
				DatagramPacket request1 = new DatagramPacket(buffer, buffer.length);
				try {
					aSocket.receive(request1);
				} catch (IOException e) {} 
				//recebe o "ping"
				System.out.println("Backup RMIServer ligado.");
				
				while(true) {
					buffer = new byte[1000]; 
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
					try {
						aSocket.receive(request);
					} catch (IOException e) {}
					 //recebe o "ping"
					
					String s=new String(request.getData(), 0, request.getLength());	
					System.out.println("Recebi um ping: " + s);	
					
					//Faz o "pong"
					DatagramPacket reply = new DatagramPacket("pong".getBytes(), 
							request.getLength(), request.getAddress(), request.getPort());
					try {
						aSocket.send(reply);
					} catch (IOException e) {}
					
				}
				
			}else if(sTipo.equals("2")) {
				//Servidor secundário
				System.out.println("udpOwnPort: "+udpOwnPort);
				System.out.println("udpOwnIP: "+udpOwnIP);
				System.out.println("udpOtherPort: "+udpOtherPort);
				System.out.println("udpOtherIP: "+udpOtherIP);
				int pingFails = 0;
				
				InetAddress aHost = null;;
				try {
					aHost = InetAddress.getByName(udpOtherIP);
				} catch (UnknownHostException e2) {
					System.out.println("Erro no udpOtherIP. Corriga o ficheiro de configuracoes.");
					return;
				}
				String texto = "ping";
				
				while(true) {
					try {
						byte [] m = texto.getBytes();
						DatagramPacket request = new DatagramPacket(m,m.length,aHost,udpOtherPort);
					
						try {
							aSocket.send(request); //Faz o ping
							aSocket.setSoTimeout(1000);
							
							//Recebe o pong
							byte[] buffer = new byte[1000];
							DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
							aSocket.receive(reply);
							System.out.println("Recebeu: " + new String(reply.getData(), 0, reply.getLength()));
							
							pingFails = 0; //zera a contagem de fails visto que recebeu o pong
							
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							}
							
						}catch(java.net.SocketTimeoutException e1) {
							//Falhou 1 ping
							pingFails++;
							if(pingFails == 5) {
								//Assumir que RMI Principal crashou. Passar este a Principal
						        aSocket.setSoTimeout(0);
						        sTipo = "1";
						        break;
							}
						}
					}catch(IOException e) {
					}
				}
			}
			
		}
    }
}
