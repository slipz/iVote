package rmiserver;

import java.io.Serializable;

/**
* Classe Faculdade
* </p>
* Esta classe representa uma faculdade.
* </p> 
* O seu construtor contem:
* @param nome uma String com o nome do departamento, so pode conter letras
* @param morada uma String com a morada do departamento, pode conter letras ou numeros
* @param ntlf uma String com o numero de contacto do departamento, so pode conter
* numeros e deve ter um total de 9 digitos
* @param email uma String com o email de contacto do departamento, pode conter letras ou numeros
* </p>
* Contem uma implementaçao do metodo toString, para imprimir as informacaoes da faculdade.
*
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/

public class Faculdade implements Serializable {

	private static final long serialVersionUID = 1L;
	public String nome;
    public String morada;
    public String ntlf;
    public String email;

    public Faculdade(String nome, String morada, String ntlf, String email) {
        this.nome = nome;
        this.morada = morada;
        this.ntlf = ntlf;
        this.email = email;
    }
    
	@Override
	public String toString() {
		return "===========================\nFaculdade \nNome:" + nome + "\nMorada:" + morada + "\nNumero de contacto:" + ntlf + "\nEmail:" + email;
	}
}
