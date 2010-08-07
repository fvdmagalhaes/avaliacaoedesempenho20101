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
	 * Este evento simplesmente trata a recepção de um reforço de colisão por uma estação da rede.
	 * Estação gera um evento de Fim de Reforço de Colisão e adiciona a lista de eventos. Se a estação
	 * estiver transmitindo na chegada deste evento, o evento Fim de Transmissão também é cancelado da lista
	 * de eventos.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		SimuladorDebug simulador = new SimuladorDebug();
		simulador.escreveLog("EVENTO INICIA RECEPÇÃO REFORÇO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+"\n");
		
		//Criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Estação
		if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO ||
				this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO)
		{ 
			//primeiro passo, nessa situação, é verificar se já existe um Evento para FimRecepçãoReforço
			EventoFimRecepReforco eventoFimRecepReforco = (EventoFimRecepReforco)servicos.retornaEvento(listaEventos,Evento.FIM_RECEP_REFORCO,this.getEstacao());
			if(eventoFimRecepReforco != null){
				//nesse caso, a única coisa que deve ser feita é alterar o tempo do Evento
				//deletando o antigo da lista de eventos
				listaEventos = servicos.deletaEvento(listaEventos,Evento.FIM_RECEP_REFORCO,this.getEstacao());
				//alterando o tempo
				eventoFimRecepReforco.setTempoInicial(this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE);
				//adicionando o novo à lista de Eventos
				listaEventos.add(eventoFimRecepReforco);
				
				//fazer o mesmo teste para a Colisao e alterar o tempo de inicio, caso haja
				EventoColisao eventoColisao = (EventoColisao)servicos.retornaEvento(listaEventos,Evento.COLISAO,this.getEstacao());
				if(eventoColisao != null){
					//deletando o antigo da lista de eventos
					listaEventos = servicos.deletaEvento(listaEventos,Evento.COLISAO,this.getEstacao());
					//alterando o tempo
					eventoColisao.setTempoInicial(this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE);
					//adicionando o novo à lista de Eventos
					listaEventos.add(eventoColisao);
				}
			}else{
				//nesse caso, a Estação mantém o mesmo Estado, pois o meio continua ocupado
				//devido à transmissão de outra Estação
				EventoFimRecepReforco novoEvFimRecepRef = (EventoFimRecepReforco)servicos.geraEvento(FIM_RECEP_REFORCO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacao(), this.getEstacoes());
				//adiciona o fim da recepção à lista de Eventos
				listaEventos.add(novoEvFimRecepRef);
			}
			//Agora deve-se testar se a Estação estava preparada para transmitir algum quadro
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao)servicos.retornaEvento(listaEventos, INICIA_TRANSMISSAO, this.getEstacao());
			//testa se a Estação estava programada para transmitir algum quadro
			if(eventoIniciaTransmissao != null){
				//altera o tempo do início da transmissão do quadro, que será após o fim do Reforço e somado ao tempo de intervalo entre quadros
				eventoIniciaTransmissao.setTempoInicial(this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE + Constantes.INTERVALO_ENTRE_QUADROS);
				//deletando o antigo da lista de eventos
				listaEventos = servicos.deletaEvento(listaEventos, INICIA_TRANSMISSAO, this.getEstacao());
				//adiciona Evento alterado à lista de Eventos
				listaEventos.add(eventoIniciaTransmissao);
			}
		}
		else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO)
		{
			//Este Estado indica que a Estação detectou a Colisão a partir do Reforço
			//Nesse caso, a Estação simplesmente para de transmitir e trata a Colisão após o fim do Reforço
			//o estado deve ser alterado para Tratando Colisão Ocupado, pois de fato uma colisão acabou de ser detectada por esta Estação
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
			//gera o Evento de fim de recepção do Reforço
			EventoFimRecepReforco novoEvFimRecepRef = (EventoFimRecepReforco)servicos.geraEvento(FIM_RECEP_REFORCO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacao(), this.getEstacoes());
			//recupera da lista de Eventos o FimTransmissão
			EventoFimTransmissao evFimTransmissao = (EventoFimTransmissao)servicos.retornaEvento(listaEventos,Evento.FIM_TRANSMISSAO,this.getEstacao());
			//guarda as informações que serão necessárias para o Evento de Colisão
			novoEvFimRecepRef.setQuantidadeQuadro(evFimTransmissao.getQuantidadeQuadro());
			novoEvFimRecepRef.setQuantidadeTentativas(evFimTransmissao.getQuantidadeTentativas());
			//flag indica que o Evento de tratar Colisão ainda não foi gerado
			novoEvFimRecepRef.setColisaoPendente(true);
			//deleta o evento de FimTransmissão da lista de Eventos, pois ele está sendo interrompido
			listaEventos = servicos.deletaEvento(listaEventos,Evento.FIM_TRANSMISSAO,this.getEstacao());
			//adiciona o evento de iniciar a transmitir o reforço de colisão
			listaEventos.add(novoEvFimRecepRef);
		}
		else
		{
			System.out.println("ERRO: Estação se encontra em um estado inexistente!");
			System.exit(0);
		}
		
		return listaEventos;
	}
}