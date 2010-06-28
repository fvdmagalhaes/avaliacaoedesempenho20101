package br.ufrj.ad20101;

public class Evento implements Comparable<Evento> {

	// Define a lista de eventos
	public static int PRONTO_TRANSMITIR;
	public static int INICIA_TRANSMISSAO;
	public static int FIM_TRANSMISSAO;
	public static int COLISAO;
	
	public static int ESTACAO1;
	public static int ESTACAO2;
	public static int ESTACAO3;
	public static int ESTACAO4;

	private Double tempoInicial;
	private int tipoEvento;
	private int estacao;
	
	public Double getTempoInicial() {
		return tempoInicial;
	}
	
	public void setTempoInicial(Double tempoInicial) {
		this.tempoInicial = tempoInicial;
	}
	
	public int getTipoEvento() {
		return tipoEvento;
	}
	
	public void setTipoEvento(int tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	public int getEstacao() {
		return estacao;
	}

	public void setEstacao(int estacao) {
		this.estacao = estacao;
	}

	public int compareTo(Evento ev) {
		if(this.getTempoInicial() < ev.getTempoInicial())
			return -1;
		else if(this.getTempoInicial() > ev.getTempoInicial())
			return 1;
		else
			return 0;
	}
}
