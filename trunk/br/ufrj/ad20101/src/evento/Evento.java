package br.ufrj.ad20101.src.evento;

import br.ufrj.ad20101.src.estacao.Estacao;

public class Evento implements Comparable<Evento> {

	// Define a lista de eventos
	public static int PRONTO_TRANSMITIR;
	public static int INICIA_TRANSMISSAO;
	public static int FIM_TRANSMISSAO;
	public static int INICIA_RECEPCAO;
	public static int FIM_RECEPCAO;
	public static int COLISAO;
	public static int DESCARTA_QUADRO;
	
	// Eu acho melhor fazer a variável 'estacao' ser do tipo 'Estacao', creio qe vá nos facilitar em algumas coisas
	// Nesse caso, as contantes abaixo serão desnecessárias.
	/*public static int ESTACAO1;
	public static int ESTACAO2;
	public static int ESTACAO3;
	public static int ESTACAO4;*/

	private Double tempoInicial;
	private int tipoEvento;
	private Estacao estacao;
	
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

	public Estacao getEstacao() {
		return estacao;
	}

	public void setEstacao(Estacao estacao) {
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
	
	public void acao(){
	}
}
