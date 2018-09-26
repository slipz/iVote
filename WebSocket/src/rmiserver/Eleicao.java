package rmiserver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
* Classe Eleicao
* </p>
* Esta classe representa uma eleicao, podendo ser de nucleo de estudantes ou conselho geral.
* </p> 
* Contem 2 contrutores, porque se a eleicao for de tipo 1, significa que e para nucleo de estudantes.
* Como essas eleicoes so decorrem no departamento a que o nucleo pertence, um dos construtores acrescenta o
* @param dep, para que nessa eleicao nao se possam ligar mesas noutros departamentos.
* </p>
* @param tipo uma String que contem o tipo da eleicao, ou seja, se a eleicao e
* para o nucleo de estudantes (1) ou conselho geral (2)
* @param id uma String com o id da Eleicao, sendo que o id deve ser unico
* @param dataInicio uma Date no formato DD/MM/AAAA HH:MM, que deve ser superior a
* hora do sistema e deve respeitar estritamente o formato indicado
* @param dataFim uma Date no formato DD/MM/AAAA HH:MM, que deve ser superior a
* hora do sistema e a hora de inicio da eleicao e deve respeitar estritamente o formato indicado
* @param titulo uma String com o titulo da eleicao, so pode conter letras
* @param descricao uma String com a descricao da eleicao, so pode conter letras
* @param listaCandidaturas um ArrayList que guarda as listas candidatas a essa eleicao
* @param listaMesas um ArrayList que guarda a lista de mesas associadas a essa eleicao,
* sendo que no caso de a eleicao ser de tipo 1, nao se pode acrescentar mais que uma mesa
* @param registoVotos um ArrayList que guarda objetos do tipo RegistoVoto,
* que contem informacao acerca do local e data em que um eleitor votou
* @param nulo e @param branco Listas de candidaturas sem elementos,
* servem apenas para fins de estatisticos de contagem de votos.
* Sao adicionadas automaticamente a todas as eleicoes porque todas elas aceitam esse tipo de voto
* </p>
* @see RegistoVoto
* </p>
* @author Bruna Lopes
* @author Eduardo Andrade
*/

public class Eleicao implements Serializable {

	private static final long serialVersionUID = 1L;
	public int tipo;
    public Date dataInicio, dataFim;
    public String titulo, descricao, id;
	public ArrayList<Lista> listaCandidaturas;
	public ArrayList<MesaVoto> listaMesas;
	public ArrayList<RegistoVoto> registoVotos;
	public Departamento dep;

    public Eleicao(int tipo, String id, Date dataInicio, Date dataFim, String titulo, String descricao) {
        this.tipo = tipo;
        this.id = id;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.titulo = titulo;
        this.descricao = descricao;
        this.listaCandidaturas = new ArrayList <>();
        this.listaMesas = new ArrayList <>();
        this.registoVotos = new ArrayList<>();
        Lista nulo = new Lista(id,"4","Nulo",null,0);
        Lista branco = new Lista(id,"4","Branco",null,0);
        this.listaCandidaturas.add(nulo);
        this.listaCandidaturas.add(branco);
    }
    
    public Eleicao(int tipo, String id, Date dataInicio, Date dataFim, String titulo, String descricao, Departamento dep) {
        this.tipo = tipo;
        this.id = id;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.titulo = titulo;
        this.descricao = descricao;
        this.listaCandidaturas = new ArrayList <>();
        this.listaMesas = new ArrayList <>();
        this.registoVotos = new ArrayList<>();
        this.dep = dep;
        Lista nulo = new Lista(id,"4","Nulo",null,0);
        Lista branco = new Lista(id,"4","Branco",null,0);
        this.listaCandidaturas.add(nulo);
        this.listaCandidaturas.add(branco);
    }
    
    /**
    * Metodo boolean que verifica se um determinado eleitor ja votou nessa eleicao, 
    * para que nao exista duplicacao de dados. Percorre a lista em que registamos os votos
    * da eleicao e verifica se esse eleitor ja la esta. Se estiver, nao o deixa votar novamente.
    * @param cc uma String que contem o numero de cartao de cidadao do eleitor
    * @return    true se o eleitor puder votar nessa eleicao
    */
    public boolean checkCC(String cc) {
    	for(RegistoVoto x:registoVotos){
    		if(x.cc.equals(cc)) {
    			return false;
    		}
    	}
    	return true;
    }
}
