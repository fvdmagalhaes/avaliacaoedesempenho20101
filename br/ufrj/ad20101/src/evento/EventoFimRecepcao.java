package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;

public class EventoFimRecepcao extends Evento{
	public EventoFimRecepcao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(FIM_RECEPCAO);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		System.out.printf("TEMPO: " + "%.10f" + " segundos; ESTA��O: Esta��o " + this.getEstacao().getIdentificador() + "; EVENTO: Fim da Recep��o;\n",this.getTempoInicial()/Constantes.SEGUNDO_EM_MILISSEGUNDOS);
		if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_OCIOSO);
		}else{
			System.out.println("ERRO: Esta��o se encontra num estado n�o existente");
			System.exit(0);
		}
		return listaEventos;
	}
}
