package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

/* 
 * 
 * */
public class EventoChegaMensagem extends Evento{
	
	/* Cria um novo evento do tipo Chegada de Mensagem
	 * Recebe como parâmetro a estação em que ocorre o evento. 
	 * A necessidade da lista de estações do segundo parâmetro apenas auxilia a geração de eventos futuros.
	 *  																									*/
	public EventoChegaMensagem(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao, int rodada){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setRodada(rodada);
		this.setTipoEvento(CHEGA_MENSAGEM);
	}
	
	/*
	 * Esta classe simula a chegada de uma mensagem em uma determinada Estação
	 * A idéia é gerar a próxima chegada de mensagem nesta Estação
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO CHEGA MENSAGEM OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+"\n");
		
		//criando a classe de serviço
		Servicos servicos = new Servicos();
		//gerando o início da transmissão da mensagem
		EventoPrepararTransmissao eventoPrepararTransmissao = (EventoPrepararTransmissao)servicos.geraEvento(PREPARA_TRANSMISSAO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes(), this.getRodada());
		//chama um serviço para gerar a quantidade desejada de quadros por mensagem
		eventoPrepararTransmissao.setQuantidadeQuadro(servicos.geraQuantidadeQuadros(this.getEstacao()));
		//adiciona a lista de Eventos
		listaEventos.add(eventoPrepararTransmissao);
		//gerando a chegada da próxima mensagem para esta Estação
		EventoChegaMensagem eventoChegaMensagem = (EventoChegaMensagem) servicos.geraEvento(Evento.CHEGA_MENSAGEM, servicos.geraProximaMensagem(this.getEstacao(), this.getTempoInicial()), this.getEstacao(), this.getEstacoes(), this.getRodada());
		//adicionando a nova chegada à lista de Eventos
		listaEventos.add(eventoChegaMensagem);
		
		return listaEventos;
	}
}