package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;

public class Evento implements Comparable<Evento> {

	// Define a lista de eventos com todos os eventos poss�veis na simula��o
	public static int RETRANSMITIR = 1;
	public static int INICIA_TRANSMISSAO = 2;
	public static int FIM_TRANSMISSAO = 3;
	public static int INICIA_RECEPCAO = 4;
	public static int FIM_RECEPCAO = 5;
	public static int CHEGA_MENSAGEM = 6;
	public static int FIM_MENSAGEM = 7;
	public static int COLISAO = 8;
	public static int DESCARTA_QUADRO = 9;
	public static int PREPARA_TRANSMISSAO = 10;
	public static int INTERROMPE_TRANSMISSAO = 11;
	public static int INICIA_TRANS_REFORCO = 12;
	public static int FIM_TRANS_REFORCO = 13;
	public static int INICIA_RECEP_REFORCO = 14;
	public static int FIM_RECEP_REFORCO = 15;
	

	/* Declara vari�veis comuns a todos os eventos existentes na simula��o.
	 *  - Tempo em que ocorre o evento
	 *  - Tipo do evento que ocorreu
	 *  - Esta��o em que ocorreu o evento
	 *  - Lista de Esta��es (passado como par�metro caso alguma informa��o de uma esta��o seja necess�ria para o 
	 *  						tratamento do evento)
	 */
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

	/* M�todo respons�vel por ordenar a lista de eventos. Executado a cada inser��o na lista de eventos para ordenar
	 * os eventos por ordem cronol�gica. Tempo inicial = tempo em que ocorre o evento 
	 * */
	public int compareTo(Evento ev) {
		if(this.getTempoInicial() < ev.getTempoInicial())
			return -1;
		else if(this.getTempoInicial() > ev.getTempoInicial())
			return 1;
		else
			return 0;
	}
	
	/* Define um m�todo a��o que ser� implementado nas classes filhas da classe principal Evento.
	 * Este m�todo identifica a a��o que ser� tomada ao processar cada um dos eventos espec�ficos. No caso da classe pai
	 * evento, nenhuma a��o ser� tomada. Os filhos da classe Evento herdam o m�todo e t�m sua implementa��o espec�fica.
	 * */
	public ArrayList<Evento> acao(ArrayList<Evento> listaEvento){
		return null;
	}
}
