package br.ufrj.ad20101.src.evento;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoColisao extends Evento {
	public EventoColisao(Double tempoInicio, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setTipoEvento(COLISAO);
	}
	
	@Override
	public void acao(){
		
	}
}
