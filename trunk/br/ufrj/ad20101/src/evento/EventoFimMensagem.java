package br.ufrj.ad20101.src.evento;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoFimMensagem extends Evento{
	public EventoFimMensagem(Double tempoInicio, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setTipoEvento(FIM_MENSAGEM);
	}
	
	@Override
	public void acao(){
		
	}
}
