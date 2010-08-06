package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;

public class EventoFimRecepReforco extends Evento{
	public EventoFimRecepReforco(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(FIM_RECEP_REFORCO);
	}
	
	/*TODO
	 * Esta classe simula o in�cio da transmiss�o do refor�o de colis�o por uma Esta��o
	 * que acabou de detectar colis�o.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		return listaEventos;
	}
}