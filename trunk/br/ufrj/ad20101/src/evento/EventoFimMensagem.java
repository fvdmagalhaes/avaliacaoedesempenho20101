package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoFimMensagem extends Evento{
	public EventoFimMensagem(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao, int rodada){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setRodada(rodada);
		this.setTipoEvento(FIM_MENSAGEM);
	}
	
	/* Esta classe simula o fim da transmiss�o de uma mensagem.
	 * Caso hajam mais mensagens na lista de pendentes desta Esta��o, 
	 * elas devem ser tratadas nesse momento.
	 */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO FIM MENSAGEM OCORREU EM " + this.getTempoInicial() + " NA ESTA��O " + this.getEstacao().getIdentificador()+" COM ESTADO: "+this.getEstacao().getEstado()+"\n");
		
		// testa o estado em que se encontra a Esta��o
		/* TODO Esta condi��o n�o deve existir, se ele chegar com outro estado aqui,
		 * significa que os Eventos ocorreram no mesmo instante e por esse motivo 
		 * a mensagem pode ser encerrada sem qualquer problema
		 * EDITADO: esta condi��o vai ficar por enquanto para ajudar no debug;
		 */
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			/*Deve-se testar se h� mensagens na fila de espera, por�m
			 *n�o � necess�rio testar se h� quadros aguardando o meio desocupar, pois ele seria
			 *transmitido antes de qualquer outro quadro ou mensagem
			 */
			if(!this.getEstacao().getMensagensPendentes().isEmpty()){
				//caso existam mensagens pendentes, Esta��o come�a a transmitir a pr�xima mensagem da fila
				EventoPrepararTransmissao eventoPrepararTransmissao = (EventoPrepararTransmissao) this.getEstacao().getMensagensPendentes().get(0);
				//seta o tempo inicial do Evento para o tempo atual, pois ele deve come�ar a ser tratado imediatamente
				eventoPrepararTransmissao.setTempoInicial(this.getTempoInicial());
				//remove a mensagem da lista de mensagens pendentes
				this.getEstacao().getMensagensPendentes().remove(0);
				//adiciona a mensagem � lista de Eventos
				listaEventos.add(eventoPrepararTransmissao);
			}
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			//nessa situa��o o �ltimo quadro desta mensagem foi descartado e o meio est� ocupado
			//da� este Evento n�o faz nada
		}else{
			System.out.println("ERRO: Esta��o se encontra num estado n�o existente");
			System.exit(0);
		}
		return listaEventos;
	}
}
