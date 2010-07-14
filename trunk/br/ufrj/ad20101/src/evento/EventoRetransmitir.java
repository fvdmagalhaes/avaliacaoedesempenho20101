package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoRetransmitir extends Evento{
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;
	
	public EventoRetransmitir(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(RETRANSMITIR);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		Servicos servicos = new Servicos();
		System.out.printf("TEMPO: " + "%.10f" + " segundos; ESTA��O: Esta��o " + this.getEstacao().getIdentificador() + "; EVENTO: In�cio da Retransmiss�o do Quadro " + this.getQuantidadeQuadro() + ";\n",this.getTempoInicial()/Constantes.SEGUNDO_EM_MILISSEGUNDOS);
		if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
				this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
				EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao)servicos.geraEvento(INICIA_TRANSMISSAO, getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacoes().get(this.getEstacao().getIdentificador()-1), getEstacoes());
				eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
				eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas);
				listaEventos.add(eventoIniciaTransmissao);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_RECEBENDO);
				ArrayList<Evento> quadrosPendentes = this.getEstacao().getQuadrosPendentes();
				EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao)servicos.geraEvento(INICIA_TRANSMISSAO, null, this.getEstacoes().get(this.getEstacao().getIdentificador()-1), getEstacoes());
				eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
				eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas);
				quadrosPendentes.add(eventoIniciaTransmissao);
				this.getEstacao().setQuadrosPendentes(quadrosPendentes);
		}else{
			System.out.println("ERRO: Esta��o se encontra num estado n�o existente");
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
