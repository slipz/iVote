package rmiserver;

import java.io.Serializable;
import java.util.Date;

/**
* Classe RegistoVoto
*</p>
*Esta classe representa um  registo de voto que e feito por um eleitor no terminal.
*Este registo e criado a partir do Voto, e serve para ficarmos com a indicacao
*do local em que cada eleitor votou, e para que eleicao, sem nunca ter acesso
*a lista que foi escolhida pelo eleitor.
*Para isto, o registo de voto e guardado dentro da eleicao.
*</p> 
*O seu construtor contem:
*@param ccPessoa uma String que permite identificar o eleitor que esta a votar, para
*que se saiba onde cada eleitor votou
*@param nomeDep uma String com o nome do departamento em que a mesa de voto esta
* localizada, existindo uma unica mesa por departamento
*@param dataVoto uma Data para sabermos o momento de voto
*
*</p>
*@author Bruna Lopes
*@author Eduardo Andrade
*/

public class RegistoVoto implements Serializable{
	public String cc;
	public String nomeDep;
	public Date dataVoto;
	
	public RegistoVoto(String cc, String nomeDep, Date dataVoto) {
		this.cc = cc;
		this.nomeDep = nomeDep;
		this.dataVoto = dataVoto;
	}
}
