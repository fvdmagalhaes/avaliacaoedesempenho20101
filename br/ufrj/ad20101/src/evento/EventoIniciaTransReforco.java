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
	 * Esta classe simula o in�cio da transmiss�o do refor�o de colis�o por uma Esta��o
	 * que acabou de detectar colis�o.
	 * Este Evento gera o in�cio da recep��o do refor�o nas demais Esta��es
	 * e o fim da transmiss�o do refor�o para esta Esta��o
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO INICIA TRANSMISS�O REFOR�O OCORREU EM " + this.getTempoInicial() + " NA ESTA��O " + this.getEstacao().getIdentificador()+"\n");
		
		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//gera o in�cio da recep��o de refor�o para todas as Esta��es
		//e o fim da transmiss�o do refor�o para esta Esta��o
		for(int i = 0; i < 4; i++){				
			if(i + 1 != this.getEstacao().getIdentificador()){
				//as demais Esta��es receber�o o refor�o de colis�o
				if(this.getEstacoes().get(i).getTipoChegada() != 0){
					EventoIniciaRecepReforco eventoIniciaRecepReforco = (EventoIniciaRecepReforco) servicos.geraEvento(INICIA_RECEP_REFORCO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes());
					listaEventos.add(eventoIniciaRecepReforco);
				}
			}else{
				//gera o Evento de fim de Transmiss�o do Refor�o
				EventoFimTransReforco eventoFimTransReforco = (EventoFimTransReforco) servicos.geraEvento(FIM_TRANS_REFORCO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacao(), this.getEstacoes());
				//guarda as informa��es que ser�o necess�rias para o Evento de Colis�o
				eventoFimTransReforco.setQuantidadeQuadro(this.quantidadeQuadro);
				eventoFimTransReforco.setQuantidadeTentativas(this.quantidadeTentativas);
				//adiciona � lista de Eventos
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
