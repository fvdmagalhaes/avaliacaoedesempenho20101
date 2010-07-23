package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoIniciaTransmissao extends Evento{
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;

	public EventoIniciaTransmissao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(INICIA_TRANSMISSAO);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		Servicos servicos = new Servicos();
		if(this.quantidadeTentativas == 0){
			this.quantidadeTentativas = 1;
		}
		if(this.getEstacao().getEstado() == Estacao.ESTADO_PREPARANDO_TRANSFERIR || this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO || this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRANSFERINDO);
			for(int i = 0; i < 4; i++){
				if(i + 1 != this.getEstacao().getIdentificador()){
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						listaEventos.add(servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes()));
					}
				}else{
					EventoFimTransmissao eventoFimTransmissao = (EventoFimTransmissao) servicos.geraEvento(FIM_TRANSMISSAO, this.getTempoInicial() + Constantes.TEMPO_QUADRO_ENLACE, this.getEstacoes().get(i), this.getEstacoes());
					eventoFimTransmissao.setQuantidadeQuadro(this.getQuantidadeQuadro() -1);
					eventoFimTransmissao.setQuantidadeTentativas(this.getQuantidadeTentativas());
					listaEventos.add(eventoFimTransmissao);
				}
			}
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
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
					eventoColisao.setQuantidadeQuadro(this.quantidadeQuadro);
					eventoColisao.setQuantidadeTentativas(this.quantidadeTentativas);
					listaEventos.add(eventoColisao);
				}
			}
		}else{
			System.out.println("ERRO: Esta��o se encontra num estado n�o existente");
			System.exit(0);
		}
		
		return listaEventos;
	}
	
	public int getQuantidadeQuadro() {
		return quantidadeQuadro;
	}

	public void setQuantidadeQuadro(int quantidadeQuadro) {
		this.quantidadeQuadro = quantidadeQuadro;
	}

	public void setQuantidadeTentativas(int quantidadeTentativas) {
		this.quantidadeTentativas = quantidadeTentativas;
	}

	public int getQuantidadeTentativas() {
		return quantidadeTentativas;
	}
}
