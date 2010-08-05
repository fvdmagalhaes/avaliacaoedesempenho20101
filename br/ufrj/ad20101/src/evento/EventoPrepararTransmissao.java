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
	 * Esta classe simula a chegada do primeiro quadro de uma mensagem em uma determinada Estação
	 * Caso a Estação esteja ociosa, o quadro poderá começar a ser transmitido em 9.6 microSegundos
	 * Caso esteja recebendo, o quadro deve continuar observando o meio
	 * Caso contrário, a mensagem entrará na fila de espera de mensagens da Estação
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Estação
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			//Estação muda para Estado "Preparando para transmitir"
			this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
			//gera um Evento que prepara a mensagem para transmissão e o adiona à lista de Eventos
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(), this.getEstacoes());
			eventoIniciaTransmissao.setQuantidadeQuadro(servicos.geraQuantidadeQuadros(this.getEstacao()));
			//1 pois será a primeira tentativa de enviar o primeiro quadro
			//tentativa é importante para saber se o quadro deve ou não ser descartado
			eventoIniciaTransmissao.setQuantidadeTentativas(1);
			listaEventos.add(eventoIniciaTransmissao);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			//indica que o quadro está aguardando o meio ficar ocioso para transmitir
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(), this.getEstacoes());
			eventoIniciaTransmissao.setQuantidadeQuadro(servicos.geraQuantidadeQuadros(this.getEstacao()));
			eventoIniciaTransmissao.setQuantidadeTentativas(1);
			this.getEstacao().setQuadroSentindoMeio(eventoIniciaTransmissao);
		}else{
			//adiciona a mensagem à lista de espera da Estação
			ArrayList<Evento> mensagensPendentes = this.getEstacao().getMensagensPendentes();
			mensagensPendentes.add(servicos.geraEvento(PREPARA_TRANSMISSAO, null, this.getEstacao(),this.getEstacoes()));
			this.getEstacao().setMensagensPendentes(mensagensPendentes);
		}
		return listaEventos;
	}
}
