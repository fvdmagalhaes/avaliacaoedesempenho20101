package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoFimMensagem extends Evento{
	public EventoFimMensagem(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(FIM_MENSAGEM);
	}
	
	/* Esta classe simula o fim da transmissão de uma mensagem.
	 * Caso hajam mais mensagens na lista de pendentes desta Estação, 
	 * elas devem ser tratadas nesse momento.
	 */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		// testa o estado em que se encontra a Estação
		/* TODO Esta condição não deve existir, se ele chegar com outro estado aqui,
		 * significa que os Eventos ocorreram no mesmo instante e por esse motivo 
		 * a mensagem pode ser encerrada sem qualquer problema
		 */
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			/*Deve-se testar se há mensagens na fila de espera, porém
			 *não é necessário testar se há quadros aguardando o meio desocupar, pois ele seria
			 *transmitido antes de qualquer outro quadro ou mensagem
			 */
			if(!this.getEstacao().getMensagensPendentes().isEmpty()){
				//caso existam mensagens pendentes, Estação começa a transmitir a próxima mensagem da fila
				EventoPrepararTransmissao eventoPrepararTransmissao = (EventoPrepararTransmissao) this.getEstacao().getMensagensPendentes().get(0);
				//seta o tempo inicial do Evento para o tempo atual, pois ele deve começar a ser tratado imediatamente
				eventoPrepararTransmissao.setTempoInicial(this.getTempoInicial());
				//remove a mensagem da lista de mensagens pendentes
				this.getEstacao().getMensagensPendentes().remove(0);
				//adiciona a mensagem à lista de Eventos
				listaEventos.add(eventoPrepararTransmissao);
			}
		}else{
			System.out.println("ERRO: Estação se encontra num estado não existente");
			System.exit(0);
		}
		return listaEventos;
	}
}
