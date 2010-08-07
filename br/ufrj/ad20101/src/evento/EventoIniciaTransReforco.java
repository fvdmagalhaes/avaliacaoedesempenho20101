package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoIniciaTransReforco extends Evento{
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;
	
	public EventoIniciaTransReforco(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(INICIA_TRANS_REFORCO);
	}
	
	/*
	 * Esta classe simula o início da transmissão do reforço de colisão por uma Estação
	 * que acabou de detectar colisão.
	 * Este Evento gera o início da recepção do reforço nas demais Estações
	 * e o fim da transmissão do reforço para esta Estação
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO INICIA TRANSMISSÃO REFORÇO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+"\n");
		
		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//gera o início da recepção de reforço para todas as Estações
		//e o fim da transmissão do reforço para esta Estação
		for(int i = 0; i < 4; i++){				
			if(i + 1 != this.getEstacao().getIdentificador()){
				//as demais Estações receberão o reforço de colisão
				if(this.getEstacoes().get(i).getTipoChegada() != 0){
					EventoIniciaRecepReforco eventoIniciaRecepReforco = (EventoIniciaRecepReforco) servicos.geraEvento(INICIA_RECEP_REFORCO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes());
					listaEventos.add(eventoIniciaRecepReforco);
				}
			}else{
				//gera o Evento de fim de Transmissão do Reforço
				EventoFimTransReforco eventoFimTransReforco = (EventoFimTransReforco) servicos.geraEvento(FIM_TRANS_REFORCO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacao(), this.getEstacoes());
				//guarda as informações que serão necessárias para o Evento de Colisão
				eventoFimTransReforco.setQuantidadeQuadro(this.quantidadeQuadro);
				eventoFimTransReforco.setQuantidadeTentativas(this.quantidadeTentativas);
				//adiciona à lista de Eventos
				listaEventos.add(eventoFimTransReforco);
			}
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
