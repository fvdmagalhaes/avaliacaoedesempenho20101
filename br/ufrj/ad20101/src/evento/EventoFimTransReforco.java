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
	 * Esta classe simula o fim da transmiss�o do refor�o de colis�o.
	 * Este Evento gera o Evento Colis�o para esta Esta��o.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO FIM TRANSMISSAO REFOR�O OCORREU EM " + this.getTempoInicial() + " NA ESTA��O " + this.getEstacao().getIdentificador()+"\n");

		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//primeiramente deve ser testado se o meio est� de fato livre para que o tratamento da colis�o possa ser feito
		EventoFimRecepReforco eventoFimRecepReforco = (EventoFimRecepReforco)servicos.retornaEvento(listaEventos, FIM_RECEP_REFORCO, this.getEstacao());
		//gera o Evento de Colis�o, que ser� tratado
		EventoColisao eventoColisao = (EventoColisao) servicos.geraEvento(COLISAO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes(),this.getRodada());
		//passa as informa��es necess�rias para o Evento de Colis�o
		eventoColisao.setQuantidadeQuadro(this.quantidadeQuadro);
		eventoColisao.setQuantidadeTentativas(this.quantidadeTentativas);
		//testa se o meio est� livre de fato
		if(eventoFimRecepReforco != null){
			//se n�o estiver, o tempo inicial da colisao ser� quando o meio estiver livre
			if(eventoFimRecepReforco.getTempoInicial() > 2*(this.getEstacao().getDistancia()*Constantes.PROPAGACAO_ELETRICA)){
				//significa que ele vai detectar o meio livre apenas quando acabar o refor�o de outra esta��o
				eventoColisao.setTempoInicial(eventoFimRecepReforco.getTempoInicial());
			}else{
				//significa que ele vai detectar o meio livre quando acabar o refor�o que ele mesmo gerou
				eventoColisao.setTempoInicial(this.getTempoInicial() + 2*(this.getEstacao().getDistancia()*Constantes.PROPAGACAO_ELETRICA));
			}
		}else{
			//se o meio estiver livre e a esta��o estiver tratando colis�o, muda o Estado para tratando colisao ocioso
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
			//e seta o tempo inicial do tratamento de colis�o para depois que ele terminar de receber o pr�prio sinal de refor�o
			eventoColisao.setTempoInicial(this.getTempoInicial() + 2*(this.getEstacao().getDistancia()*Constantes.PROPAGACAO_ELETRICA));
		}
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
