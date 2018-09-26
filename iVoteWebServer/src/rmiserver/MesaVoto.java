package rmiserver;

import java.io.Serializable;
import java.util.ArrayList;

/**
* Classe MesaVoto
* </p>
* Classe que representa uma mesa de voto, onde e possivel identificar um eleitor e 
* listar as eleicoes em que ele pode votar.
* </p>
* @param dep um Departamento para sabermos onde a mesa de voto esta localizada
* e quais as eleicoes que estao a decorrer nessa mesa
* @param listaTerminais um ArrayList com os terminais que estao ligados a mesa de voto
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/

public class MesaVoto implements Serializable {

	private static final long serialVersionUID = 1L;
	public Departamento dep;
	public ArrayList<TerminalVoto> listaTerminais;
	
	public MesaVoto(Departamento dep) {
		this.dep = dep;
		this.listaTerminais = new ArrayList <>();
	}
    
}
