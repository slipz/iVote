package rmiserver;

import java.io.Serializable;
import java.util.ArrayList;

/**
* Classe Lista
* </p>
* Esta classe representa uma lista de candidatos a uma eleicao.
* O nome de uma lista e unico para cada eleicao.
* </p> 
* O seu construtor contem:
* @param tipoLista uma String que contem o tipo da lista, ou seja, se a lista e formada
* por estudantes, funcionarios ou docentes
* Nota: um tipoLista = 4 representa a lista de votos em branco e nulos, para fins de
* contagem de votos
* @param idEleicao uma String com o id da Eleicao, sendo que o tipo de Lista deve ser
* de um tipo que seja compativel com a eleicao, logo nao permite que se criem listas
* de funcionarios para eleicoes de nucleo de estudantes, por exemplo
* @param nome uma String com o nome da lista, so pode conter letras
* @param listaP um ArrayList que contem a lista de pessoas que pertencem a essa lista,
* nao tendo estas pessoas que estar registadas no sistema, basta o seu nome
* @param contagem um int que acumula o numero de votos que essa lista ja tem
* </p>
* Contem uma implementaçao do metodo toString, para imprimir as informacaoes da lista.
* </p>
* @see Eleicao
* @see Pessoa
* @see RegistoVoto
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/

public class Lista implements Serializable {
    
	private static final long serialVersionUID = 1L;
	public String idEleicao, tipoLista;
	public String nome;
    public ArrayList<Pessoa> listaP;
    public int contagem;

    public Lista(String idEleicao, String tipoLista, String nome, ArrayList<Pessoa> listaP, int contagem) {
        this.idEleicao = idEleicao;
        this.tipoLista = tipoLista;
    	this.nome = nome;
        this.listaP = listaP;
        this.contagem = contagem;
    }
    
    @Override
	public String toString() {
    	System.out.println("\nLista:"+ nome + "\nPertencente a eleicao com o id:" + idEleicao +"\nTipo:"+tipoLista+ "\nLista de Candidatos:");
		if(!(tipoLista.equals("4"))) {
	    	for (Pessoa p: listaP) {
				System.out.println(p.toStringCandidato());
			}	
		}
    	return "";
	}
    
    
}
