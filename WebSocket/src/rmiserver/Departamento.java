package rmiserver;

import java.io.Serializable;

/**
* Classe Departamento
* </p>
* Classe que representa um departamento.
* </p> 
* O seu construtor contem:
* param nome uma String com o nome do departamento, so pode conter letras
* @param morada uma String com a morada do departamento, pode conter letras ou numeros
* @param ntlf uma String com o numero de contacto do departamento, so pode conter
* numeros e deve ter um total de 9 digitos
* @param email uma String com o email de contacto do departamento, pode conter letras ou numeros
* @param fac uma Faculdade para sabermos a qual o departamento pertence
* </p>
* Contem uma implementaçao do metodo toString, para imprimir as informacaoes do departamento.
* </p>
* @see Faculdade
*
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/

public class Departamento implements Serializable {

	private static final long serialVersionUID = 1L;
	public String nome;
    public String morada;
    public String ntlf;
    public String email;
    public Faculdade fac;

    public Departamento(String nome, String morada, String ntlf, String email, Faculdade fac) {
        this.nome = nome;
        this.morada = morada;
        this.ntlf = ntlf;
        this.email = email;
        this.fac = fac;
    }

	@Override
	public String toString() {
		return "==========================\nDepartamento \nNome:" + nome + "\nMorada:" + morada + "\nNumero de contacto:" + ntlf + "\nEmail:" + email + "\nFaculdade em que se insere: " + fac.toString();
	}
}
