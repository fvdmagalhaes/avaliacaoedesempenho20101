package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoIniciaRecepcao extends Evento{
	
	private Double tempoRecepcao;
	
	public EventoIniciaRecepcao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(INICIA_RECEPCAO);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		Servicos servicos = new Servicos();
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO || this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO || this.getEstacao().getEstado() == Estacao.ESTADO_PREPARANDO_TRANSFERIR){
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_RECEBENDO);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
			for(int i = 0; i < 4; i++){
				if(i + 1 != this.getEstacao().getIdentificador()){
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						EventoIniciaRecepcao eventoIniciaRecepcao = (EventoIniciaRecepcao) servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes());
						eventoIniciaRecepcao.setTempoRecepcao(Constantes.TEMPO_REFORCO_ENLACE);
						listaEventos.add(servicos.geraEvento(FIM_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacoes().get(i), this.getEstacoes()));
						listaEventos.add(eventoIniciaRecepcao);
					}
				}else{
					EventoColisao eventoColisao = (EventoColisao) servicos.geraEvento(COLISAO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_COLISAO, this.getEstacoes().get(i), this.getEstacoes());
					EventoFimTransmissao eventoFimTransmissao = (EventoFimTransmissao) servicos.retornaEvento(listaEventos, FIM_TRANSMISSAO, this.getEstacoes().get(i));
					eventoColisao.setQuantidadeQuadro(eventoFimTransmissao.getQuantidadeQuadro()+1);
					eventoColisao.setQuantidadeTentativas(eventoFimTransmissao.getQuantidadeTentativas());
					listaEventos = servicos.deletaEvento(listaEventos, FIM_TRANSMISSAO, this.getEstacoes().get(i));
					listaEventos.add(eventoColisao);
				}
			}
		}else if (this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
		}else if (this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
		}else{
			System.out.println("ERRO: Estação se encontra num estado não existente");
			System.exit(0);
		}
		return listaEventos;
	}

	public void setTempoRecepcao(Double tempoRecepcao) {
		this.tempoRecepcao = tempoRecepcao;
	}

	public Double getTempoRecepcao() {
		return tempoRecepcao;
	}
}
