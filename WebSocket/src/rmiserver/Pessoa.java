package rmiserver;

import java.io.Serializable;
import java.util.Date;

/**
* Classe Pessoa
* </p>
* Esta classe representa eleitor ou um candidato de uma lista.
* </p> 
* Contem 2 construtores, sendo que um deles apenas contem o @param nome uma String.
* O outro construtor contem:
* @param tipo um int que representa a profissao da pessoa em questao, podendo ser 
* estudante (1), funcionario (2) ou docente (3)
* @param nome uma String com o nome da Pessoa, so pode conter letras
* @param password uma String que representa a password de acesso do eleitor ao sistema de votos
* @param dep um Departamento em que a Pessoa se insere
* @param ntlf uma String com o numero de contacto do eleitor, so pode conter
* numeros e deve ter um total de 9 digitos
* @param morada uma String com a morada do eleitor, pode conter letras ou numeros
* @param nCC uma String com o numero do cartao de cidadao do eleitor, que permite 
* a sua identificacao no sistema
* @param validadeCC uma Date no formato DD/MM/AAAA, que deve ser superior a
* hora do sistema e deve respeitar estritamente o formato indicado
* </p>
* Contem duas implementaçoes do metodo toString, uma, toString() retorna todas as informacoes de um eleitor
* e a outra, toStringCandidato() retorna apenas o nome de um candidato de uma lista.
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/

public class Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int tipo;            //define se e estudante(1), funcionario(2) ou docente(3)
    public String nome;
    public String password;
    public Departamento dep;
    public String ntelef;
    public String morada;
    public String nCC;
    public Date validadeCC;

	public Pessoa(int tipo, String nome, String password, Departamento dep, String ntelef, String morada, String nCC, Date validadeCC) {
        this.tipo = tipo;
        this.nome = nome;
        this.password = password;
        this.dep = dep;
        this.ntelef = ntelef;
        this.morada = morada;
        this.nCC = nCC;
        this.validadeCC = validadeCC;
    }
    
    public Pessoa (String nome) {
    	this.nome = nome;
    }
    
    @Override
	public String toString() {
		return "=======================\nPessoa: \nTipo:" + tipo + "\nNome:" + nome + "\nDepartamento a que pertence:" + dep.nome + "\nNumero de contacto:"
				+ ntelef + "\nMorada: " + morada + "\nNumero do CC:" + nCC + "\nValidade do CC:" + validadeCC;
	}
    
    public String toStringCandidato() {
    	return "\nNome:" + nome;
    }
    
}
