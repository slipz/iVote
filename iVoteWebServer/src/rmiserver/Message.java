package rmiserver;

import java.io.Serializable;

/**
* Classe Message
* </p>
* Esta classe representa uma mensagem entre duas classes, nomeadamente o
* RMIServer e o TCPServer. E usada, por exemplo, para devolver as eleicoes
* a partir do RMI para o TCP saber quais as que existem ao identificar o eleitor
* </p> 
* @see RMIServer
* @see TCPServer
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/

public class Message implements Serializable {

	String m1,m3; 
	int m2;
	
	private static final long serialVersionUID = 1L;
	
	public Message(String m1, String m3) {
		this.m1 = m1;
		this.m3 = m3;
	}
}
