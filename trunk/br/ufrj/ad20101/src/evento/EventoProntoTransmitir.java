package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoProntoTransmitir extends Evento{
	
	public EventoProntoTransmitir(Double tempoInicio, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setTipoEvento(PRONTO_TRANSMITIR);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		return listaEventos;
	}
}
