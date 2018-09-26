package rmiserver;

import java.net.*;
import java.io.*;
import java.util.*;

/**
* Classe Receiver
* </p>
* Esta classe extends Thread, e uma thread que e responsavel por ficar a escuta de novas ligações 
* de terminais de voto ao servidor TCP que a criou.
* </p> 
* @param tcpPort um inteiro que representa o porto onde se vai criar a socket para escutar pedidos
* @param terminais uma ArrayList<Connection>, serve como lista partilhada por todas as thread e 
* contem todos os terminais ligados ao servidor
* @param rmiserver RMI_S_I, é o objeto do servido RMI
* @param nomeMesa é uma String que guarda o nome da mesa, ou seja, o Departamento a que está associada
* @param server é um objeto TCPServer que representa o servidor tcp
* </p>
* @see TCPServer
* @see Connection
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/

public class Receiver extends Thread{
	RMI_S_I rmiserver;
	TCPServer server;
    ArrayList<Connection> terminais;
    int tcpPort;
    int numero;
    String nomeMesa;
    
    public Receiver (int tcpPort, ArrayList<Connection> terminais, RMI_S_I rmiserver, String nomeMesa, TCPServer server) {
    	this.rmiserver = rmiserver;
    	this.server = server;
    	this.numero = 0;
    	this.tcpPort = tcpPort;
    	this.terminais = terminais;
    	this.nomeMesa = nomeMesa;
        this.start();
    }

    /**
    * Metodo principal da thread Receiver. Método que é executado quando a thread é lancada
    * e apenas fica a espera de novas ligacoes ao servidor por outros terminais
    *
    */    
    public void run(){
  	  // Codigo executado pelo TCPServer para tratar cada TCPClient
    	ServerSocket listenSocket;
		try {
			listenSocket = new ServerSocket(tcpPort);
			System.out.println("LISTEN SOCKET="+listenSocket);
            while(true) {
                Socket clientSocket = listenSocket.accept(); 
                System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
                numero++;
                Connection conn = new Connection(clientSocket, numero, rmiserver, terminais, Integer.toString(numero), nomeMesa, server);
                terminais.add(conn);
            }
		} catch (IOException e) {
			System.out.println("Erro na mesa de voto. Necessario reiniciar para restabeler a ligacao.");
			System.exit(0);
		}

    }
    

    public void setTcpServer(TCPServer server) {
    	this.server = server;
    }
    
    public void setRmiServer(RMI_S_I rmiserver) {
    	this.rmiserver = rmiserver;
    }
}
