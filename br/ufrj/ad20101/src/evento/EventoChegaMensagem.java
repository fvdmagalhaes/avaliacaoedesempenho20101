package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Servicos;

/* 
 * 
 * */
public class EventoChegaMensagem extends Evento{
	
	/* Cria um novo evento do tipo Chegada de Mensagem
	 * Recebe como par�metro a esta��o em que ocorre o evento. 
	 * A necessidade da lista de esta��es do segundo par�metro apenas auxilia a gera��o de eventos futuros.
	 *  																									*/
	public EventoChegaMensagem(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(CHEGA_MENSAGEM);
	}
	
	/*
	 * Esta classe simula a chegada de uma mensagem em uma determinada Esta��o
	 * A id�ia � gerar a pr�xima chegada de mensagem nesta Esta��o
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//gerando a chegada da pr�xima mensagem para esta Esta��o
		EventoChegaMensagem eventoChegaMensagem = (EventoChegaMensagem) servicos.geraEvento(Evento.CHEGA_MENSAGEM, servicos.geraProximaMensagem(this.getEstacao(), this.getTempoInicial()), this.getEstacao(), this.getEstacoes());
		//adicionando a nova chegada � lista de Eventos
		listaEventos.add(eventoChegaMensagem);
		
		return listaEventos;
	}
}