package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoPrepararTransmissao extends Evento{
	
	public EventoPrepararTransmissao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(PREPARA_TRANSMISSAO);
	}
	
	/*
	 * Esta classe simula a chegada do primeiro quadro de uma mensagem em uma determinada Esta��o
	 * Caso a Esta��o esteja ociosa, o quadro poder� come�ar a ser transmitido em 9.6 microSegundos
	 * Caso esteja recebendo, o quadro deve continuar observando o meio
	 * Caso contr�rio, a mensagem entrar� na fila de espera de mensagens da Esta��o
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Esta��o
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			//Esta��o muda para Estado "Preparando para transmitir"
			this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
			//gera um Evento que prepara a mensagem para transmiss�o e o adiona � lista de Eventos
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(), this.getEstacoes());
			eventoIniciaTransmissao.setQuantidadeQuadro(servicos.geraQuantidadeQuadros(this.getEstacao()));
			//1 pois ser� a primeira tentativa de enviar o primeiro quadro
			//tentativa � importante para saber se o quadro deve ou n�o ser descartado
			eventoIniciaTransmissao.setQuantidadeTentativas(1);
			listaEventos.add(eventoIniciaTransmissao);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			//indica que o quadro est� aguardando o meio ficar ocioso para transmitir
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(), this.getEstacoes());
			eventoIniciaTransmissao.setQuantidadeQuadro(servicos.geraQuantidadeQuadros(this.getEstacao()));
			eventoIniciaTransmissao.setQuantidadeTentativas(1);
			this.getEstacao().setQuadroSentindoMeio(eventoIniciaTransmissao);
		}else{
			//adiciona a mensagem � lista de espera da Esta��o
			ArrayList<Evento> mensagensPendentes = this.getEstacao().getMensagensPendentes();
			mensagensPendentes.add(servicos.geraEvento(PREPARA_TRANSMISSAO, null, this.getEstacao(),this.getEstacoes()));
			this.getEstacao().setMensagensPendentes(mensagensPendentes);
		}
		return listaEventos;
	}
}
