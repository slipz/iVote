package rmiserver;

/**
* Classe TerminalVoto
* </p>
* Esta classe representa um terminal de voto, onde um eleitor pode autenticar-se
* e votar na eleicao que escolheu na mesa de voto.
* </p> 
* Classe contem a variavel blocked, que representa um boolean.
* Se o blocked estiver a true, significa que o terminal esta bloqueado, ou seja,
* nao esta la nenhum eleitor, portanto esta livre para receber um.
*
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/

public class TerminalVoto {
	boolean blocked;
	
	public TerminalVoto() {
		this.blocked = true;
	}
	
	public void block() {
		this.blocked = true;
	}
	
	public void unblock() {
		this.blocked = false;
	}
	
	public boolean value() {
		return blocked;
	}
}
