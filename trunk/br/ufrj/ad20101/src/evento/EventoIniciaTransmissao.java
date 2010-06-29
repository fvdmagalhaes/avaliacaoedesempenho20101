package br.ufrj.ad20101.src.evento;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoIniciaTransmissao extends Evento{
	public EventoIniciaTransmissao(Double tempoInicio, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setTipoEvento(INICIA_TRANSMISSAO);
	}
	
	@Override
	public void acao(){
		
	}
}
