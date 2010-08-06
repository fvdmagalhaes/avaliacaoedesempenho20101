package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoIniciaRecepReforco extends Evento{
	public EventoIniciaRecepReforco(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(INICIA_RECEP_REFORCO);
	}
	
	/*
	 * Este evento simplesmente trata a recepção de um reforço de colisão por uma estação da rede.
	 * Estação gera um evento de Fim de Reforço de Colisão e adiciona a lista de eventos. Se a estação
	 * estiver transmitindo na chegada deste evento, o evento Fim de Transmissão também é cancelado da lista
	 * de eventos.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		
		//Criando a classe de serviço
		Servicos servicos = new Servicos();
		
		if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO ||
				this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO)
		{ 
			EventoFimRecepReforco novoEvFimRecepRef = (EventoFimRecepReforco)servicos.geraEvento(FIM_RECEP_REFORCO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacao(), this.getEstacoes());
			listaEventos.add(novoEvFimRecepRef);
		}
		else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO)
		{
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
			EventoFimRecepReforco novoEvFimRecepRef = (EventoFimRecepReforco)servicos.geraEvento(FIM_RECEP_REFORCO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacao(), this.getEstacoes());
			EventoFimTransmissao evFimTransmissao = (EventoFimTransmissao)servicos.retornaEvento(listaEventos,Evento.FIM_TRANSMISSAO,this.getEstacao());
			novoEvFimRecepRef.setQuantidadeQuadro(evFimTransmissao.getQuantidadeQuadro());
			novoEvFimRecepRef.setQuantidadeTentativas(evFimTransmissao.getQuantidadeTentativas());
			novoEvFimRecepRef.setColisaoPendente(true);
			listaEventos = servicos.deletaEvento(listaEventos,Evento.FIM_TRANSMISSAO,this.getEstacao());
			listaEventos.add(novoEvFimRecepRef);
		}
		else
		{
			System.out.println("ERRO: Estação se encontra em um estado inexistente!");
		}
		
		return listaEventos;
	}
}