package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoFimRecepcao extends Evento{
	public EventoFimRecepcao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(FIM_RECEPCAO);
	}
	
	/*
	 * Esta classe simula o fim da recep��o de um quadro corretamente
	 * No ponto de vista de quem est� recebendo, ao fim de cada quadro que ele recebe,
	 * o meio fica ocioso e, caso ele tenha algo para transmitir, transmitir� neste momento.
	 * Caso a Esta��o esteja tratando colis�o, ent�o o estado ser� tratando colis�o com meio ocioso
	 * Caso contr�rio, a Recep��o termina, o meio fica ocioso e a Esta��o transmite pendencias.
	 * TODO recep��es de refor�o de colis�o e interrup��es de recep��o ser�o tratados em novos Eventos 
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//testa o estado em que se encontra a Esta��o
		//TODO provavelmente a condi��o de estar ocioso n�o ser� mais necess�ria
		if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO || this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			if(this.getEstacao().getMensagensPendentes().isEmpty() && this.getEstacao().getQuadrosPendentes().isEmpty()){
				//Caso n�o haja nenhuma pendencia, a Esta��o se encontra Ociosa
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}else{
				//cria a classe de servi�o
				Servicos servicos = new Servicos();
				//TODO quadros pendentes n�o existir� mais, ser� substituido para quadros aguardando o meio desocupar
				//ideia desse if � dar prioridade ao quadro que n�o foi transmitido pelo fato do meio estar ocupado 
				if(!this.getEstacao().getQuadrosPendentes().isEmpty()){
					this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
					EventoIniciaTransmissao eventoIniciaTransmissao =  (EventoIniciaTransmissao) servicos.retornaEvento(this.getEstacao().getQuadrosPendentes(), INICIA_TRANSMISSAO, this.getEstacao());
					eventoIniciaTransmissao.setTempoInicial(this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS);
					this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setQuadrosPendentes(servicos.deletaEvento(this.getEstacao().getQuadrosPendentes(), INICIA_TRANSMISSAO, this.getEstacao()));
					listaEventos.add(eventoIniciaTransmissao);
				}else{
					//Caso n�o haja quadros pendentes, come�a a transmiss�o da pr�xima mensagem na fila
					this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
					EventoPrepararTransmissao eventoPrepararTransmissao = (EventoPrepararTransmissao) this.getEstacao().getMensagensPendentes().get(0);
					//seta o tempo inicial do Evento para o tempo atual, pois ele deve come�ar a ser tratado imediatamente
					eventoPrepararTransmissao.setTempoInicial(this.getTempoInicial());
					//remove a mensagem da lista de mensagens pendentes
					this.getEstacao().getMensagensPendentes().remove(0);
					//adiciona a mensagem � lista de Eventos
					listaEventos.add(eventoPrepararTransmissao);
				}
			}
		}else if (this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
			//Caso a esta��o esteja tratando colis�o, o meio agora esta desocupado
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
		}else{
		}
		return listaEventos;
	}
}
