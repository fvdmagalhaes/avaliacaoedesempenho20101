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
	 * Esta classe simula a chegada de uma mensagem em uma determinada Esta��o
	 * A id�ia � gerar a pr�xima chegada de mensagem nesta Esta��o
	 * Caso a Esta��o esteja ociosa, a mensagem pode come�ar a ser transmitida
	 * Caso contr�rio, a mensagem entrar� na fila de espera de mensagens da Esta��o
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//gerando a chegada da pr�xima mensagem para esta Esta��o
		EventoChegaMensagem eventoChegaMensagem = (EventoChegaMensagem) servicos.geraEvento(Evento.CHEGA_MENSAGEM, servicos.geraProximaMensagem(this.getEstacao(), this.getTempoInicial()), this.getEstacao(), this.getEstacoes());
		//adicionando a nova chegada � lista de Eventos
		listaEventos.add(eventoChegaMensagem);
		
		//testa o estado em que se encontra a Esta��o
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			//gera um Evento que prepara a mensagem para transmiss�o e o adiona � lista de Eventos
			EventoPrepararTransmissao eventoPreparaTransmissao = (EventoPrepararTransmissao) servicos.geraEvento(PREPARA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(), this.getEstacoes());
			eventoPreparaTransmissao.setQuantidadeQuadro(servicos.geraQuantidadeQuadros(this.getEstacao()));
			//1 pois ser� a primeira tentativa de enviar o primeiro quadro
			//tentativa � importante para saber se o quadro deve ou n�o ser descartado
			eventoPreparaTransmissao.setQuantidadeTentativas(1);
			listaEventos.add(eventoPreparaTransmissao);
		}else{
			//adciona a mensagem � lista de espera da Esta��o
			ArrayList<Evento> mensagensPendentes = this.getEstacao().getMensagensPendentes();
			mensagensPendentes.add(servicos.geraEvento(PREPARA_TRANSMISSAO, null, this.getEstacao(),this.getEstacoes()));
			this.getEstacao().setMensagensPendentes(mensagensPendentes);
		}
		return listaEventos;
	}
}