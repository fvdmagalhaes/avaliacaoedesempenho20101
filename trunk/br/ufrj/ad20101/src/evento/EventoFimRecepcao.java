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
	 * Esta classe simula o fim da recepção de um quadro corretamente
	 * No ponto de vista de quem está recebendo, ao fim de cada quadro que ele recebe,
	 * o meio fica ocioso e, caso ele tenha algo para transmitir, transmitirá neste momento.
	 * Caso a Estação esteja tratando colisão, então o estado será tratando colisão com meio ocioso
	 * Caso contrário, a Recepção termina, o meio fica ocioso e a Estação transmite pendencias.
	 * TODO recepções de reforço de colisão e interrupções de recepção serão tratados em novos Eventos 
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//testa o estado em que se encontra a Estação
		//TODO provavelmente a condição de estar ocioso não será mais necessária
		if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO || this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			if(this.getEstacao().getMensagensPendentes().isEmpty() && this.getEstacao().getQuadrosPendentes().isEmpty()){
				//Caso não haja nenhuma pendencia, a Estação se encontra Ociosa
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}else{
				//cria a classe de serviço
				Servicos servicos = new Servicos();
				//TODO quadros pendentes não existirá mais, será substituido para quadros aguardando o meio desocupar
				//ideia desse if é dar prioridade ao quadro que não foi transmitido pelo fato do meio estar ocupado 
				if(!this.getEstacao().getQuadrosPendentes().isEmpty()){
					this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
					EventoIniciaTransmissao eventoIniciaTransmissao =  (EventoIniciaTransmissao) servicos.retornaEvento(this.getEstacao().getQuadrosPendentes(), INICIA_TRANSMISSAO, this.getEstacao());
					eventoIniciaTransmissao.setTempoInicial(this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS);
					this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setQuadrosPendentes(servicos.deletaEvento(this.getEstacao().getQuadrosPendentes(), INICIA_TRANSMISSAO, this.getEstacao()));
					listaEventos.add(eventoIniciaTransmissao);
				}else{
					//Caso não haja quadros pendentes, começa a transmissão da próxima mensagem na fila
					this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
					EventoPrepararTransmissao eventoPrepararTransmissao = (EventoPrepararTransmissao) this.getEstacao().getMensagensPendentes().get(0);
					//seta o tempo inicial do Evento para o tempo atual, pois ele deve começar a ser tratado imediatamente
					eventoPrepararTransmissao.setTempoInicial(this.getTempoInicial());
					//remove a mensagem da lista de mensagens pendentes
					this.getEstacao().getMensagensPendentes().remove(0);
					//adiciona a mensagem à lista de Eventos
					listaEventos.add(eventoPrepararTransmissao);
				}
			}
		}else if (this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
			//Caso a estação esteja tratando colisão, o meio agora esta desocupado
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
		}else{
		}
		return listaEventos;
	}
}
