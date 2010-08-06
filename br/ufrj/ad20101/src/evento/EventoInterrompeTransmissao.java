package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoInterrompeTransmissao extends Evento{
	public EventoInterrompeTransmissao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(INTERROMPE_TRANSMISSAO);
	}
	
	/*
	 * Esta classe simula a interrupção de uma transmissão por ser detectada uma colisão.
	 * Este Evento substituirá o Evento FimTransmissão que já está na lista de Eventos.
	 * Ele gerará um Evento IniciaTransReforco, que representa o envio do sinal de reforço de colisão. 
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		SimuladorDebug simulador = new SimuladorDebug();
		simulador.escreveLog("EVENTO INTERROMPE TRANSMISSÃO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador());

		//criando a classe de serviço
		Servicos servicos = new Servicos();
		//gera o Evento que iniciará a transmissão do sinal de reforço
		EventoIniciaTransReforco eventoIniciaTransReforco = (EventoIniciaTransReforco) servicos.geraEvento(INICIA_TRANS_REFORCO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes());
		//recupera da lista de Eventos o FimTransmissão
		EventoFimTransmissao eventoFimTransmissao = (EventoFimTransmissao) servicos.retornaEvento(listaEventos, FIM_TRANSMISSAO, this.getEstacao());
		//guarda as informações que serão necessárias para o Evento de Colisão
		eventoIniciaTransReforco.setQuantidadeQuadro(eventoFimTransmissao.getQuantidadeQuadro());
		eventoIniciaTransReforco.setQuantidadeTentativas(eventoFimTransmissao.getQuantidadeTentativas());
		//deleta o evento de FimTransmissão da lista de Eventos, pois ele está sendo interrompido
		listaEventos = servicos.deletaEvento(listaEventos, FIM_TRANSMISSAO, this.getEstacao());
		//adiciona o evento de iniciar a transmitir o reforço de colisão
		listaEventos.add(eventoIniciaTransReforco);
		return listaEventos;
	}
}
