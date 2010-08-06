package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoFimTransReforco extends Evento{
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;
	
	public EventoFimTransReforco(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(FIM_TRANS_REFORCO);
	}
	
	/*
	 * Esta classe simula o fim da transmiss�o do refor�o de colis�o.
	 * Este Evento gera o Evento Colis�o para esta Esta��o.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//gera o Evento de Colis�o, que come�ar� a ser tratado neste instante
		EventoColisao eventoColisao = (EventoColisao) servicos.geraEvento(COLISAO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes());
		//passa as informa��es necess�rias para o Evento de Colis�o
		eventoColisao.setQuantidadeQuadro(this.quantidadeQuadro);
		eventoColisao.setQuantidadeTentativas(this.quantidadeTentativas);
		//adiciona o Evento de Colis�o � lista de Eventos
		listaEventos.add(eventoColisao);
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
