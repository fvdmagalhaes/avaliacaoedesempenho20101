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
	 * Esta classe simula a interrup��o de uma transmiss�o por ser detectada uma colis�o.
	 * Este Evento substituir� o Evento FimTransmiss�o que j� est� na lista de Eventos.
	 * Ele gerar� um Evento IniciaTransReforco, que representa o envio do sinal de refor�o de colis�o. 
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		SimuladorDebug simulador = new SimuladorDebug();
		simulador.escreveLog("EVENTO INTERROMPE TRANSMISS�O OCORREU EM " + this.getTempoInicial() + " NA ESTA��O " + this.getEstacao().getIdentificador());

		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		//gera o Evento que iniciar� a transmiss�o do sinal de refor�o
		EventoIniciaTransReforco eventoIniciaTransReforco = (EventoIniciaTransReforco) servicos.geraEvento(INICIA_TRANS_REFORCO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes());
		//recupera da lista de Eventos o FimTransmiss�o
		EventoFimTransmissao eventoFimTransmissao = (EventoFimTransmissao) servicos.retornaEvento(listaEventos, FIM_TRANSMISSAO, this.getEstacao());
		//guarda as informa��es que ser�o necess�rias para o Evento de Colis�o
		eventoIniciaTransReforco.setQuantidadeQuadro(eventoFimTransmissao.getQuantidadeQuadro());
		eventoIniciaTransReforco.setQuantidadeTentativas(eventoFimTransmissao.getQuantidadeTentativas());
		//deleta o evento de FimTransmiss�o da lista de Eventos, pois ele est� sendo interrompido
		listaEventos = servicos.deletaEvento(listaEventos, FIM_TRANSMISSAO, this.getEstacao());
		//adiciona o evento de iniciar a transmitir o refor�o de colis�o
		listaEventos.add(eventoIniciaTransReforco);
		return listaEventos;
	}
}
