package br.ufrj.ad20101.src.evento;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoFimTransmissao extends Evento{
	public EventoFimTransmissao(Double tempoInicio, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setTipoEvento(FIM_TRANSMISSAO);
	}
	
	@Override
	public void acao(){
		
	}
}
