package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoDescartaQuadro extends Evento{
	
	private int quantidadeQuadro;
	
	public EventoDescartaQuadro(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(DESCARTA_QUADRO);
	}
	
	/*
	 * Esta classe simula o descarte de um quadro
	 * Este Evento não faz nada, pois o tratamento de quadros descartados não está
	 * incluindo neste simulador, portanto esta classe existe apenas para representar
	 * o quadro que está sendo descartado.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO DESCARTA QUADRO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+" QUADRO: "+this.getQuantidadeQuadro() + "\n");
		
		return listaEventos;
	}

	public void setQuantidadeQuadro(int quantidadeQuadro) {
		this.quantidadeQuadro = quantidadeQuadro;
	}

	public int getQuantidadeQuadro() {
		return quantidadeQuadro;
	}
}
