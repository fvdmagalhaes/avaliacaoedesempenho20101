package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoFimMensagem extends Evento{
	public EventoFimMensagem(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(FIM_MENSAGEM);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		Servicos servicos = new Servicos();
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			this.setEstacao(this.getEstacoes().get(this.getEstacao().getIdentificador()-1));
			if(!this.getEstacao().getMensagensPendentes().isEmpty()){
				this.getEstacao().getMensagensPendentes().remove(0);
				listaEventos.add(servicos.geraEvento(CHEGA_MENSAGEM, getTempoInicial(), this.getEstacao(), this.getEstacoes()));
			}
		}else{
			System.out.println("ERRO: Estação se encontra num estado não existente");
			System.exit(0);
		}
		return listaEventos;
	}
}
