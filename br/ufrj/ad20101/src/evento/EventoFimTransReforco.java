package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoFimTransReforco extends Evento{
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;
	
	public EventoFimTransReforco(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao, int rodada){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setRodada(rodada);
		this.setTipoEvento(FIM_TRANS_REFORCO);
	}
	
	/*
	 * Esta classe simula o fim da transmissão do reforço de colisão.
	 * Este Evento gera o Evento Colisão para esta Estação.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO FIM TRANSMISSAO REFORÇO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+"\n");

		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//primeiramente deve ser testado se o meio está de fato livre para que o tratamento da colisão possa ser feito
		EventoFimRecepReforco eventoFimRecepReforco = (EventoFimRecepReforco)servicos.retornaEvento(listaEventos, FIM_RECEP_REFORCO, this.getEstacao());
		//gera o Evento de Colisão, que será tratado
		EventoColisao eventoColisao = (EventoColisao) servicos.geraEvento(COLISAO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes(),this.getRodada());
		//passa as informações necessárias para o Evento de Colisão
		eventoColisao.setQuantidadeQuadro(this.quantidadeQuadro);
		eventoColisao.setQuantidadeTentativas(this.quantidadeTentativas);
		//testa se o meio está livre de fato
		if(eventoFimRecepReforco != null){
			//se não estiver, o tempo inicial da colisao será quando o meio estiver livre
			if(eventoFimRecepReforco.getTempoInicial() > 2*(this.getEstacao().getDistancia()*Constantes.PROPAGACAO_ELETRICA)){
				//significa que ele vai detectar o meio livre apenas quando acabar o reforço de outra estação
				eventoColisao.setTempoInicial(eventoFimRecepReforco.getTempoInicial());
			}else{
				//significa que ele vai detectar o meio livre quando acabar o reforço que ele mesmo gerou
				eventoColisao.setTempoInicial(this.getTempoInicial() + 2*(this.getEstacao().getDistancia()*Constantes.PROPAGACAO_ELETRICA));
			}
		}else{
			//se o meio estiver livre e a estação estiver tratando colisão, muda o Estado para tratando colisao ocioso
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
			//e seta o tempo inicial do tratamento de colisão para depois que ele terminar de receber o próprio sinal de reforço
			eventoColisao.setTempoInicial(this.getTempoInicial() + 2*(this.getEstacao().getDistancia()*Constantes.PROPAGACAO_ELETRICA));
		}
		//adiciona o Evento de Colisão à lista de Eventos
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
