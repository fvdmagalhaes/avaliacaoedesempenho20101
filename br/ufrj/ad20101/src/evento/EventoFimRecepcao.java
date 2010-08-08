package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

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
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO FIM RECEPÇÃO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+" COM ESTADO: "+this.getEstacao().getEstado()+"\n");
		
		//Com o fim da recepção o meio foi detectado livre, portanto este tempo deve ser setado na Estação
		this.getEstacao().setTempoUltimaRecepcao(this.getTempoInicial());
		//testa o estado em que se encontra a Estação
		if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			//testa se há alguma transmissão pendente
			EventoIniciaTransmissao eventoIniciaTransmissão = this.getEstacao().getQuadroSentindoMeio();
			if(eventoIniciaTransmissão != null){
				//caso a Estação esteja aguardando o meio ficar livre para transmitir um quadro
				//será transmitido agora
				//setando o tempo
				eventoIniciaTransmissão.setTempoInicial(this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS);
				//adiciona à lista de Eventos
				listaEventos.add(eventoIniciaTransmissão);
				//altera o Estado da Estação
				this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
			}else if(!this.getEstacao().getMensagensPendentes().isEmpty()){
				//caso contrário, se houver uma mensagem na fila de mensagens, ela será transmitida
				//recupera a mensagem da lista
				EventoPrepararTransmissao pegaEvPrepTransm = (EventoPrepararTransmissao)this.getEstacao().getMensagensPendentes().get(0);
				//seta o tempo
				pegaEvPrepTransm.setTempoInicial(this.getTempoInicial());
				//remove da fila
				this.getEstacao().getMensagensPendentes().remove(0);
				//adiciona à lista de Eventos
				listaEventos.add(pegaEvPrepTransm);
				//altera o Estado da Estação
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}
			else{
				//Caso não haja nada para ser transmitido, Estação vai apra o Estado Ocioso e aguarda novos eventos
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
			//Caso a estação esteja tratando colisão, o meio agora esta desocupado
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			//a única forma de acontecer isso é se a estação esta retransmitindo algo após o intervalo
			//estipulado pelo algoritmo Binary Backoff e, nesse caso, ela transmite independente do meio,
			//portanto, nessa situação, nada muda para a estação
		}else{
			System.out.println("ERRO: Estação se encontra num estado não existente");
			System.exit(0);
		}
		return listaEventos;
	}
}
