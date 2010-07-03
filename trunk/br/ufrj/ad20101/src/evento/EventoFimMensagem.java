package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoFimMensagem extends Evento{
	public EventoFimMensagem(Double tempoInicio, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setTipoEvento(FIM_MENSAGEM);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		return listaEventos;
	}
}