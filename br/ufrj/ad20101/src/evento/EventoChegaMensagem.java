package br.ufrj.ad20101.src.evento;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoChegaMensagem extends Evento{
	public EventoChegaMensagem(Double tempoInicio, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setTipoEvento(CHEGA_MENSAGEM);
	}
	
	@Override
	public void acao(){
		
	}
}
