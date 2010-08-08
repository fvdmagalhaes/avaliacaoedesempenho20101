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
	 * Esta classe simula o fim da recep��o de um quadro corretamente
	 * No ponto de vista de quem est� recebendo, ao fim de cada quadro que ele recebe,
	 * o meio fica ocioso e, caso ele tenha algo para transmitir, transmitir� neste momento.
	 * Caso a Esta��o esteja tratando colis�o, ent�o o estado ser� tratando colis�o com meio ocioso
	 * Caso contr�rio, a Recep��o termina, o meio fica ocioso e a Esta��o transmite pendencias. 
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO FIM RECEP��O OCORREU EM " + this.getTempoInicial() + " NA ESTA��O " + this.getEstacao().getIdentificador()+" COM ESTADO: "+this.getEstacao().getEstado()+"\n");
		
		//Com o fim da recep��o o meio foi detectado livre, portanto este tempo deve ser setado na Esta��o
		this.getEstacao().setTempoUltimaRecepcao(this.getTempoInicial());
		//testa o estado em que se encontra a Esta��o
		if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			//testa se h� alguma transmiss�o pendente
			EventoIniciaTransmissao eventoIniciaTransmiss�o = this.getEstacao().getQuadroSentindoMeio();
			if(eventoIniciaTransmiss�o != null){
				//caso a Esta��o esteja aguardando o meio ficar livre para transmitir um quadro
				//ser� transmitido agora
				//setando o tempo
				eventoIniciaTransmiss�o.setTempoInicial(this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS);
				//adiciona � lista de Eventos
				listaEventos.add(eventoIniciaTransmiss�o);
				//altera o Estado da Esta��o
				this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
			}else if(!this.getEstacao().getMensagensPendentes().isEmpty()){
				//caso contr�rio, se houver uma mensagem na fila de mensagens, ela ser� transmitida
				//recupera a mensagem da lista
				EventoPrepararTransmissao pegaEvPrepTransm = (EventoPrepararTransmissao)this.getEstacao().getMensagensPendentes().get(0);
				//seta o tempo
				pegaEvPrepTransm.setTempoInicial(this.getTempoInicial());
				//remove da fila
				this.getEstacao().getMensagensPendentes().remove(0);
				//adiciona � lista de Eventos
				listaEventos.add(pegaEvPrepTransm);
				//altera o Estado da Esta��o
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}
			else{
				//Caso n�o haja nada para ser transmitido, Esta��o vai apra o Estado Ocioso e aguarda novos eventos
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
			//Caso a esta��o esteja tratando colis�o, o meio agora esta desocupado
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			//a �nica forma de acontecer isso � se a esta��o esta retransmitindo algo ap�s o intervalo
			//estipulado pelo algoritmo Binary Backoff e, nesse caso, ela transmite independente do meio,
			//portanto, nessa situa��o, nada muda para a esta��o
		}else{
			System.out.println("ERRO: Esta��o se encontra num estado n�o existente");
			System.exit(0);
		}
		return listaEventos;
	}
}
