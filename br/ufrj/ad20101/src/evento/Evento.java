package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;

public class Evento implements Comparable<Evento> {

	// Define a lista de eventos
	public static int PRONTO_TRANSMITIR = 1;
	public static int INICIA_TRANSMISSAO = 2;
	public static int FIM_TRANSMISSAO = 3;
	public static int INICIA_RECEPCAO = 4;
	public static int FIM_RECEPCAO = 5;
	public static int CHEGA_MENSAGEM = 6;
	public static int FIM_MENSAGEM = 7;
	public static int COLISAO = 8;
	public static int DESCARTA_QUADRO = 9;

	
	private Double tempoInicial;
	private int tipoEvento;
	private Estacao estacao;
	private ArrayList<Estacao> estacoes;
	
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
	
	public void setEstacoes(ArrayList<Estacao> estacoes) {
		this.estacoes = estacoes;
	}

	public ArrayList<Estacao> getEstacoes() {
		return estacoes;
	}

	public int compareTo(Evento ev) {
		if(this.getTempoInicial() < ev.getTempoInicial())
			return -1;
		else if(this.getTempoInicial() > ev.getTempoInicial())
			return 1;
		else
			return 0;
	}
	
	public ArrayList<Evento> acao(ArrayList<Evento> listaEvento){
		return null;
	}
}
