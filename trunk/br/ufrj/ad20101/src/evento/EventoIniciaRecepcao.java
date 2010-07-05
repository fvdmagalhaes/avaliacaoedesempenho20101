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
		System.out.printf("TEMPO: " + "%.10f" + " segundos; ESTAÇÃO: Estação " + this.getEstacao().getIdentificador() + "; EVENTO: Início da Recepção de um Quadro;\n",this.getTempoInicial()/Constantes.SEGUNDO_EM_MILISSEGUNDOS);
		if(this.tempoRecepcao == null){
			this.tempoRecepcao = Constantes.TEMPO_QUADRO_ENLACE;
		}
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO || this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_RECEBENDO);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			for(int i = 0; i < 4; i++){
				if(i + 1 != this.getEstacao().getIdentificador()){
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						EventoIniciaRecepcao eventoIniciaRecepcao = (EventoIniciaRecepcao) servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes());
						eventoIniciaRecepcao.setTempoRecepcao(Constantes.TEMPO_REFORCO_ENLACE);
						listaEventos.add(servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes()));
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
