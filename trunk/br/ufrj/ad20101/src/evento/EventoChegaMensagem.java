package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoChegaMensagem extends Evento{
	public EventoChegaMensagem(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(CHEGA_MENSAGEM);
	}
	
	/*
	 * Esta classe simula a chegada de uma mensagem em uma determinada Estação
	 * A idéia é gerar a próxima chegada de mensagem nesta Estação
	 * Caso a Estação esteja ociosa, a mensagem pode começar a ser transmitida
	 * Caso contrário, a mensagem entrará na fila de espera de mensagens da Estação
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//gerando a chegada da próxima mensagem para esta Estação
		EventoChegaMensagem eventoChegaMensagem = (EventoChegaMensagem) servicos.geraEvento(Evento.CHEGA_MENSAGEM, servicos.geraProximaMensagem(this.getEstacao(), this.getTempoInicial()), this.getEstacao(), this.getEstacoes());
		//adicionando a nova chegada à lista de Eventos
		listaEventos.add(eventoChegaMensagem);
		
		//testa o estado em que se encontra a Estação
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			//gera um Evento que prepara a mensagem para transmissão e o adiona à lista de Eventos
			EventoPrepararTransmissao eventoPreparaTransmissao = (EventoPrepararTransmissao) servicos.geraEvento(PREPARA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(), this.getEstacoes());
			eventoPreparaTransmissao.setQuantidadeQuadro(servicos.geraQuantidadeQuadros(this.getEstacao()));
			//1 pois será a primeira tentativa de enviar o primeiro quadro
			//tentativa é importante para saber se o quadro deve ou não ser descartado
			eventoPreparaTransmissao.setQuantidadeTentativas(1);
			listaEventos.add(eventoPreparaTransmissao);
		}else{
			//adciona a mensagem à lista de espera da Estação
			ArrayList<Evento> mensagensPendentes = this.getEstacao().getMensagensPendentes();
			mensagensPendentes.add(servicos.geraEvento(PREPARA_TRANSMISSAO, null, this.getEstacao(),this.getEstacoes()));
			this.getEstacao().setMensagensPendentes(mensagensPendentes);
		}
		return listaEventos;
	}
}