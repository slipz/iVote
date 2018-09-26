package rmiserver;

import java.io.Serializable;
import java.util.Date;

/**
* Classe Voto
* </p>
* Esta classe representa um  voto que e feito por um eleitor no terminal.
* O voto é enviado do TCPServer para o RMIServer, para que seja contabilizado,
* servindo isto para calcular o numero de votos de cada lista da eleicao.
*  objeto voto nao e guardado.
* </p> 
* O seu construtor contem:
* @param idEleicao uma String que identifica cada eleicao de forma unica
* @param nomeLista uma String que identifica a lista que esta a receber aquele voto
* @param nomeMesa uma String com o nome do departamento em que a mesa de voto esta
* esta localizada, existindo uma unica mesa por departamento
* @param ccPessoa uma String que permite identificar o eleitor que esta a votar
* @see Eleicao
* @see Pessoa
* @see Lista
*
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/

public class Voto implements Serializable {

	private static final long serialVersionUID = 1L;
	public String idEleicao;
	public String nomeLista;
	public String nomeMesa;
	public String ccPessoa;
	
	public Voto(String idEleicao, String nomeLista, String mesa, String ccPessoa) {
        this.idEleicao = idEleicao;
        this.nomeLista = nomeLista;
        this.nomeMesa = mesa;
        this.ccPessoa = ccPessoa;
	}    
}
