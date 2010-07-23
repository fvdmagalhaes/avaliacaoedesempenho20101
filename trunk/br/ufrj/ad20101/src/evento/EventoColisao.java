package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoColisao extends Evento {
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;
	
	public EventoColisao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(COLISAO);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		Servicos servicos = new Servicos();
		if(this.getQuantidadeTentativas()>15){
			this.setQuantidadeQuadro(getQuantidadeQuadro()-1);
			this.setQuantidadeTentativas(0);
			EventoDescartaQuadro eventoDescartaQuadro = (EventoDescartaQuadro)servicos.geraEvento(DESCARTA_QUADRO, getTempoInicial(), this.getEstacao(), this.getEstacoes());
			eventoDescartaQuadro.setQuantidadeQuadro(getQuantidadeQuadro() +1);
			listaEventos.add(eventoDescartaQuadro);
		}
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO || this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO || this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
			Double tempoRetransmissao = servicos.binaryBackoff(this.getQuantidadeTentativas());
			if(tempoRetransmissao.equals(0.0)){
				this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
				EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial(), this.getEstacao(),this.getEstacoes());
				eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
				eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas+1);
				eventoIniciaTransmissao.setTempoInicial(this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS);
				listaEventos.add(eventoIniciaTransmissao);
			}else{
				this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
				EventoRetransmitir eventoRetransmitir = (EventoRetransmitir)servicos.geraEvento(RETRANSMITIR, this.getTempoInicial() + tempoRetransmissao, this.getEstacao(), this.getEstacoes());
				eventoRetransmitir.setQuantidadeQuadro(this.getQuantidadeQuadro());
				eventoRetransmitir.setQuantidadeTentativas(this.getQuantidadeTentativas()+1);
				listaEventos.add(eventoRetransmitir);
			}
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO || this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
			Double tempoRetransmissao = servicos.binaryBackoff(this.getQuantidadeTentativas());
			if(tempoRetransmissao.equals(0.0)){
				this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_RECEBENDO);
				ArrayList<Evento> quadrosPendentes = this.getEstacao().getQuadrosPendentes();
				EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + servicos.binaryBackoff(this.getQuantidadeTentativas()), this.getEstacao(),this.getEstacoes());
				eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
				eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas+1);
				quadrosPendentes.add(eventoIniciaTransmissao);
				this.getEstacao().setQuadrosPendentes(quadrosPendentes);
			}else{
				this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
				EventoRetransmitir eventoRetransmitir = (EventoRetransmitir)servicos.geraEvento(RETRANSMITIR, this.getTempoInicial() + tempoRetransmissao, this.getEstacao(), this.getEstacoes());
				eventoRetransmitir.setQuantidadeQuadro(this.getQuantidadeQuadro());
				eventoRetransmitir.setQuantidadeTentativas(this.getQuantidadeTentativas()+1);
				listaEventos.add(eventoRetransmitir);
			}
		}else{
			System.out.println("ERRO: Estação se encontra num estado não existente");
			System.exit(0);
		}
		return listaEventos;
	}

	public void setQuantidadeQuadro(int quantidadeQuadro) {
		this.quantidadeQuadro = quantidadeQuadro;
	}

	public int getQuantidadeQuadro() {
		return quantidadeQuadro;
	}

	public void setQuantidadeTentativas(int quantidadeTentativas) {
		this.quantidadeTentativas = quantidadeTentativas;
	}

	public int getQuantidadeTentativas() {
		return quantidadeTentativas;
	}
}
