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
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO || this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			if(this.getEstacao().getMensagensPendentes().isEmpty() && this.getEstacao().getQuadrosPendentes().isEmpty()){
				this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_OCIOSO);
			}else{
				Servicos servicos = new Servicos();
				if(!this.getEstacao().getQuadrosPendentes().isEmpty()){
					this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
					EventoIniciaTransmissao eventoIniciaTransmissao =  (EventoIniciaTransmissao) servicos.retornaEvento(this.getEstacao().getQuadrosPendentes(), INICIA_TRANSMISSAO, this.getEstacao());
					eventoIniciaTransmissao.setTempoInicial(this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS);
					this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setQuadrosPendentes(servicos.deletaEvento(this.getEstacao().getQuadrosPendentes(), INICIA_TRANSMISSAO, this.getEstacao()));
					listaEventos.add(eventoIniciaTransmissao);
				}else{
					this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_OCIOSO);
					EventoChegaMensagem eventoChegaMensagem = (EventoChegaMensagem) servicos.retornaEvento(this.getEstacao().getMensagensPendentes(), CHEGA_MENSAGEM, this.getEstacao());
					eventoChegaMensagem.setTempoInicial(this.getTempoInicial());
					this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setMensagensPendentes(servicos.deletaEvento(this.getEstacao().getMensagensPendentes(), CHEGA_MENSAGEM, this.getEstacao()));
					listaEventos.add(eventoChegaMensagem);
				}
			}
		}else if (this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
		}else{
		}
		return listaEventos;
	}
}
