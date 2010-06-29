package br.ufrj.ad20101.src.evento;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoIniciaRecepcao extends Evento{
	public EventoIniciaRecepcao(Double tempoInicio, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setTipoEvento(INICIA_RECEPCAO);
	}
	
	@Override
	public void acao(){
		
	}
}
