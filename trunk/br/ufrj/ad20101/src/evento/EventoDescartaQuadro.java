package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoDescartaQuadro extends Evento{
	public EventoDescartaQuadro(Double tempoInicio, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setTipoEvento(DESCARTA_QUADRO);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		return listaEventos;
	}
}
