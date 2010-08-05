package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Servicos;

/* 
 * 
 * */
public class EventoChegaMensagem extends Evento{
	
	/* Cria um novo evento do tipo Chegada de Mensagem
	 * Recebe como parâmetro a estação em que ocorre o evento. 
	 * A necessidade da lista de estações do segundo parâmetro apenas auxilia a geração de eventos futuros.
	 *  																									*/
	public EventoChegaMensagem(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(CHEGA_MENSAGEM);
	}
	
	/*
	 * Esta classe simula a chegada de uma mensagem em uma determinada Estação
	 * A idéia é gerar a próxima chegada de mensagem nesta Estação
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//gerando a chegada da próxima mensagem para esta Estação
		EventoChegaMensagem eventoChegaMensagem = (EventoChegaMensagem) servicos.geraEvento(Evento.CHEGA_MENSAGEM, servicos.geraProximaMensagem(this.getEstacao(), this.getTempoInicial()), this.getEstacao(), this.getEstacoes());
		//adicionando a nova chegada à lista de Eventos
		listaEventos.add(eventoChegaMensagem);
		
		return listaEventos;
	}
}