package br.ufrj.ad20101.src.evento;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoFimRecepcao extends Evento{
	public EventoFimRecepcao(Double tempoInicio, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setTipoEvento(FIM_RECEPCAO);
	}
	
	@Override
	public void acao(){
		
	}
}
