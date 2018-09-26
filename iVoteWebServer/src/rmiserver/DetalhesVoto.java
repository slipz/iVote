package rmiserver;
import java.io.Serializable;
import java.util.Date;

public class DetalhesVoto implements Serializable {;
	public String local;
	public int totalVotos;
	public int totalAlunos;
	public int totalFunc;
	public int totalDocentes;
	
	public DetalhesVoto(String local, int totalVotos, int totalAlunos, int totalFunc, int totalDocentes) {
		this.local = local;
		this.totalVotos = totalVotos;
		this.totalAlunos = totalAlunos;
		this.totalFunc = totalFunc;
		this.totalDocentes = totalDocentes;
	}	
}
