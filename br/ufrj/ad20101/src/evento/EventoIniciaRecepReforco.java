package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoIniciaRecepReforco extends Evento{
	public EventoIniciaRecepReforco(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(INICIA_RECEP_REFORCO);
	}
	
	/*
	 * Este evento simplesmente trata a recep��o de um refor�o de colis�o por uma esta��o da rede.
	 * Esta��o gera um evento de Fim de Refor�o de Colis�o e adiciona a lista de eventos. Se a esta��o
	 * estiver transmitindo na chegada deste evento, o evento Fim de Transmiss�o tamb�m � cancelado da lista
	 * de eventos.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		SimuladorDebug simulador = new SimuladorDebug();
		simulador.escreveLog("EVENTO INICIA RECEP��O REFOR�O OCORREU EM " + this.getTempoInicial() + " NA ESTA��O " + this.getEstacao().getIdentificador()+"\n");
		
		//Criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Esta��o
		if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO ||
				this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO)
		{ 
			//primeiro passo, nessa situa��o, � verificar se j� existe um Evento para FimRecep��oRefor�o
			EventoFimRecepReforco eventoFimRecepReforco = (EventoFimRecepReforco)servicos.retornaEvento(listaEventos,Evento.FIM_RECEP_REFORCO,this.getEstacao());
			if(eventoFimRecepReforco != null){
				//nesse caso, a �nica coisa que deve ser feita � alterar o tempo do Evento
				//deletando o antigo da lista de eventos
				listaEventos = servicos.deletaEvento(listaEventos,Evento.FIM_RECEP_REFORCO,this.getEstacao());
				//alterando o tempo
				eventoFimRecepReforco.setTempoInicial(this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE);
				//adicionando o novo � lista de Eventos
				listaEventos.add(eventoFimRecepReforco);
				
				//fazer o mesmo teste para a Colisao e alterar o tempo de inicio, caso haja
				EventoColisao eventoColisao = (EventoColisao)servicos.retornaEvento(listaEventos,Evento.COLISAO,this.getEstacao());
				if(eventoColisao != null){
					//deletando o antigo da lista de eventos
					listaEventos = servicos.deletaEvento(listaEventos,Evento.COLISAO,this.getEstacao());
					//alterando o tempo
					eventoColisao.setTempoInicial(this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE);
					//adicionando o novo � lista de Eventos
					listaEventos.add(eventoColisao);
				}
			}else{
				//nesse caso, a Esta��o mant�m o mesmo Estado, pois o meio continua ocupado
				//devido � transmiss�o de outra Esta��o
				EventoFimRecepReforco novoEvFimRecepRef = (EventoFimRecepReforco)servicos.geraEvento(FIM_RECEP_REFORCO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacao(), this.getEstacoes());
				//adiciona o fim da recep��o � lista de Eventos
				listaEventos.add(novoEvFimRecepRef);
			}
			//Agora deve-se testar se a Esta��o estava preparada para transmitir algum quadro
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao)servicos.retornaEvento(listaEventos, INICIA_TRANSMISSAO, this.getEstacao());
			//testa se a Esta��o estava programada para transmitir algum quadro
			if(eventoIniciaTransmissao != null){
				//altera o tempo do in�cio da transmiss�o do quadro, que ser� ap�s o fim do Refor�o e somado ao tempo de intervalo entre quadros
				eventoIniciaTransmissao.setTempoInicial(this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE + Constantes.INTERVALO_ENTRE_QUADROS);
				//deletando o antigo da lista de eventos
				listaEventos = servicos.deletaEvento(listaEventos, INICIA_TRANSMISSAO, this.getEstacao());
				//adiciona Evento alterado � lista de Eventos
				listaEventos.add(eventoIniciaTransmissao);
			}
		}
		else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO)
		{
			//Este Estado indica que a Esta��o detectou a Colis�o a partir do Refor�o
			//Nesse caso, a Esta��o simplesmente para de transmitir e trata a Colis�o ap�s o fim do Refor�o
			//o estado deve ser alterado para Tratando Colis�o Ocupado, pois de fato uma colis�o acabou de ser detectada por esta Esta��o
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
			//gera o Evento de fim de recep��o do Refor�o
			EventoFimRecepReforco novoEvFimRecepRef = (EventoFimRecepReforco)servicos.geraEvento(FIM_RECEP_REFORCO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacao(), this.getEstacoes());
			//recupera da lista de Eventos o FimTransmiss�o
			EventoFimTransmissao evFimTransmissao = (EventoFimTransmissao)servicos.retornaEvento(listaEventos,Evento.FIM_TRANSMISSAO,this.getEstacao());
			//guarda as informa��es que ser�o necess�rias para o Evento de Colis�o
			novoEvFimRecepRef.setQuantidadeQuadro(evFimTransmissao.getQuantidadeQuadro());
			novoEvFimRecepRef.setQuantidadeTentativas(evFimTransmissao.getQuantidadeTentativas());
			//flag indica que o Evento de tratar Colis�o ainda n�o foi gerado
			novoEvFimRecepRef.setColisaoPendente(true);
			//deleta o evento de FimTransmiss�o da lista de Eventos, pois ele est� sendo interrompido
			listaEventos = servicos.deletaEvento(listaEventos,Evento.FIM_TRANSMISSAO,this.getEstacao());
			//adiciona o evento de iniciar a transmitir o refor�o de colis�o
			listaEventos.add(novoEvFimRecepRef);
		}
		else
		{
			System.out.println("ERRO: Esta��o se encontra em um estado inexistente!");
			System.exit(0);
		}
		
		return listaEventos;
	}
}