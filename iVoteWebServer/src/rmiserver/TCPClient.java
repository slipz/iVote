package rmiserver;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class establishes a TCP connection to a specified server, and loops
 * sending/receiving strings to/from the server.
 * <p>
 * The main() method receives two arguments specifying the server address and
 * the listening port.
 * <p>
 * The usage is similar to the 'telnet <address> <port>' command found in most
 * operating systems, to the 'netcat <host> <port>' command found in Linux,
 * and to the 'nc <hostname> <port>' found in macOS.
 *
 * Classe TCPClient. Sofreu poucas alteracoes relativamente a fornecida
 * pelos docentes. Acrescentado o metodo carregaConfig para ler configuracoes
 * de um ficheiro, algumas variasveis para guardar essas configuracoes
 * e criacao de uma nova thread.
 *
 *
 * @author Raul Barbosa
 * @author Alcides Fonseca
 * @author Bruna Lopes
 * @author Eduardo Andrade
 * @version 2.0
 */
public class TCPClient {
	
	public static int tcpPort;
	public static String tcpIP;
	
	public static void main(String[] args) {
		final TerminalVoto tv = new TerminalVoto();
		Socket socket;
		PrintWriter out;
		BufferedReader inFromServer = null;
		
		carregaConfig();
		
		try {
			// connect to the specified address:port (default is localhost:12345)
			socket = new Socket(tcpIP, tcpPort);

			// create streams for writing to and reading from the socket
			out = new PrintWriter(socket.getOutputStream(), true);
			// create a thread for reading from the keyboard and writing to the server
			Reader reader = new Reader(socket, tv);
			
			Scanner keyboardScanner = new Scanner(System.in);
			out.println("type|ready ; term_no|1");
			while(!socket.isClosed()) {
				String readKeyboard = keyboardScanner.nextLine();
				if(tv.value()) {
					System.out.println("Terminal bloqueado. N�o � permitido efetuar qualquer opera��o.");
				}else {
					out.println(readKeyboard);
				}
			}
		
			// the main thread loops reading from the server and writing to System.out

		} catch (IOException e) {
			System.out.println("Erro na ligacao a mesa de voto.");
			return;
		} 
	}
	
	public static void carregaConfig(){
		String file = "TCPClientConfig.txt";
		String line;
		String[] l1;
		System.out.println("Uploding TCPClient configurations...");
		try{
			FileReader inputFile = new FileReader(file);
			BufferedReader buffer = new BufferedReader(inputFile);
			
			line = buffer.readLine();
			l1 = line.split("=");
			if(l1[0].equals("tcpServerPort")) {
				tcpPort = Integer.parseInt(l1[1]);
			}
			
			line = buffer.readLine();
			l1 = line.split("=");
			if(l1[0].equals("tcpServerIP")) {
				tcpIP = l1[1];
			}		
			
		}catch(FileNotFoundException e){
			System.out.println("File "+file+" not found");
			System.exit(0);
		}catch(IOException e){
			System.out.println("Erro na leitura das configuracoes");
			System.exit(0);
		}
		System.out.println("TCPClientConfig.txt successfully uploaded.");
	}
}

/**
 *
 * Classe Reader extends Thread, e responsavel por ler as mensagens vindas do
 * servidor, processa-las e apresenta-las ao utilizador. Corre no TCPClient
 *
 * @see TCPClient
 * @author Bruna Lopes
 * @author Eduardo Andrade
 *
 */
class Reader extends Thread {
	BufferedReader in = null;
	Socket socket;
	TerminalVoto tv;

	public Reader (Socket socket, TerminalVoto tv1) {
		this.tv = tv1;
		this.socket = socket;
		try {
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Erro na ligacao a mesa de voto.");
		}
		this.start();

	}


	/**
 	*
 	* Metodo principal da thread Reader. Responsavel por receber e processar 
 	* as mensagens recebidas do servidor TCP. Consoante a mensagem recebida
 	* e apos o processamento, mostra-as no ecra de forma legivel para o 
 	* utilizador
 	* </p>
 	* @see TCPClient
 	* </p>
 	* @author Bruna Lopes
 	* @author Eduardo Andrade
 	*
 	*/
	public void run(){
		String m, idEleicao = "", username = "";
		HashMap<String, String> hashmap = new HashMap<String, String>();
			try {
				while(true) {
					m = in.readLine();
					try {
						hashmap = messageParser(m);
					} catch (Exception e) {
						System.out.println("Erro de ligacao a mesa de voto. Terminal necessita ser reiniciado.");
						System.exit(0);
					}
					if(hashmap.get("type").equals("unlock")) {
						username = hashmap.get("username");
						idEleicao = hashmap.get("idEleicao");
						System.out.println("Terminal desbloqueado para user="+username);
						tv.unblock();
					}
					else if(hashmap.get("type").equals("lock")) {
						System.out.println("Terminal bloqueado.");
						idEleicao = "";
						username = "";
						tv.block();
					}
					else if(hashmap.get("type").equals("auth")) {
						if(hashmap.get("result").equals("true")) {
							System.out.println("Identidade confirmada. A receber boletim de voto.");
						}
						if(hashmap.get("result").equals("false")) {
							//TODO Tratar caso em que autenticacao nao deu true
							System.out.println("Identidade nao confirmada. Insira password novamente.");
						}
					}else if(hashmap.get("type").equals("boletim")) {
						String lnome = "";
						int num = Integer.parseInt(hashmap.get("item_count"));
						System.out.println("Listas candidatas para a eleicao escolhida [EleicaoID -> "+idEleicao+"]:");
						for(int i = 0; i<num; i++) {
							lnome = hashmap.get("item"+Integer.toString(i));
							System.out.println("\n\t Lista "+Integer.toString(i)+": "+lnome);
						}
					}else if(hashmap.get("type").equals("wrong_cmd")) {
						System.out.println("Comando introduzido errado. Esperado: \""+hashmap.get("expected").replace("=", "|").replace(".", ";")+"\"");
					}else if(hashmap.get("type").equals("vote")) {
						//voto efetuado com sucesso
						if(hashmap.get("result").equals("true")) {
							System.out.println("Voto efetuado com sucesso!");
							idEleicao = "";
							username = "";
							tv.block();
						}else {
							System.out.println("Erro a registar o voto. Tente novamente.");
						}
					}else if(hashmap.get("type").equals("vote_error")){
						if(hashmap.get("msg").equals("list_not_found")) {
							System.out.println("A lista selecionada nao existe. Selecione uma pertencente a eleicao.");
						}else if(hashmap.get("msg").equals("eleicao_not_found")) {
							System.out.println("A Eleicao selecionada nao existe.");
						}
					}
					hashmap.clear();
				}
			} catch (IOException e) {
				System.out.println("Erro na ligacao a mesa de voto. Necessario reeniciar o terminal.");
				return;
			}
	}
	
	private HashMap<String, String> messageParser(String m) throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, String> hashMap= new HashMap<String, String>();
		String[] m1 = m.split(";");
		for(String x: m1) {
			String[] m2 = x.split("\\|");
			hashMap.put(m2[0].trim(), m2[1].trim());
		}
		//System.out.println(hashMap.toString());
		
		return hashMap;
	}
}




