package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoFimRecepReforco extends Evento{
	
	 private int quantidadeQuadro;
	 private int quantidadeTentativas;
	 private boolean colisaoPendente=false;

	public EventoFimRecepReforco(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(FIM_RECEP_REFORCO);
	}
	
	/*
	 * Esta classe simula o final da recepção do Reforço de colisão por uma Estação
	 * Este Evento representa a detecção do fim do sinal de Reforço no meio
	 * Se a Estação estiver em Estado de tratamento de Colisão, o tratramento de fato
	 * começa agora. Caso contrário, poderá transmitir alguma pendencia, caso exista.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO FIM RECEPCAO REFORCO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+"\n");

		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//Com o fim da recepção o meio foi detectado livre, portanto este tempo deve ser setado na Estação
		this.getEstacao().setTempoUltimaRecepcao(this.getTempoInicial());
		
		//testa o estado em que se encontra a Estação
		if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO)
		{
			//Pelo Estado da Estação, nota-se que ela não tem colisões para tratar, então
			//resta ver se há alguma transmissão pendente
			EventoIniciaTransmissao eventoIniciaTransmissão = this.getEstacao().getQuadroSentindoMeio();
			if(eventoIniciaTransmissão != null)
			{
				//caso a Estação esteja aguardando o meio ficar livre para transmitir um quadro
				//será transmitido agora
				//setando o tempo
				eventoIniciaTransmissão.setTempoInicial(this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS);
				//adiciona à lista de Eventos
				listaEventos.add(eventoIniciaTransmissão);
				//altera o Estado da Estação
				this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
			}
			else if(!this.getEstacao().getMensagensPendentes().isEmpty())
			{
				//caso contrário, se houver uma mensagem na fila de mensagens, ela será transmitida
				//recupera a mensagem da lista
				EventoPrepararTransmissao pegaEvPrepTransm = (EventoPrepararTransmissao)this.getEstacao().getMensagensPendentes().get(0);
				//seta o tempo
				pegaEvPrepTransm.setTempoInicial(this.getTempoInicial());
				//remove da fila
				this.getEstacao().getMensagensPendentes().remove(0);
				//adiciona à lista de Eventos
				listaEventos.add(pegaEvPrepTransm);
				//altera o Estado da Estação
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}
			else
			{
				//Caso não haja nada para ser transmitido, Estação vai apra o Estado Ocioso e aguarda novos eventos
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}
			
		}
		else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO)
		{
			//testa se a geração do Evento de Colisão está pendente ou não
			if(isColisaoPendente())
			{
				//Caso haja:
				//muda o Estado para Tratando Colisão Ocioso, pois o fim do sinal de Reforço
				//indica exatamente que o meio está livre
				this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
				//Gera o Evento de Colisão, que começará a ser tratado neste instante
				EventoColisao eventoColisao = (EventoColisao) servicos.geraEvento(COLISAO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes());

				//passa as informações necessárias para o Evento de Colisão
				eventoColisao.setQuantidadeQuadro(this.quantidadeQuadro);
				eventoColisao.setQuantidadeTentativas(this.quantidadeTentativas);
				 //adiciona o Evento de Colisão à lista de Eventos
				listaEventos.add(eventoColisao);
				return listaEventos;
			}
			else
			{
				//caso contrário, apenas muda para o Estado Ocioso e Tratando Colisão, pois o Evento
				//de Colisão será tratado logo
				this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
			}
		}
		else
		{
			System.out.println("ERRO: Estação se encontra em um estado inexistente!");
		}
		
		return listaEventos;
	}
	
	public int getQuantidadeQuadro() {
		return quantidadeQuadro;
	}

	public void setQuantidadeQuadro(int quantidadeQuadro) {
		this.quantidadeQuadro = quantidadeQuadro;
	}

	public int getQuantidadeTentativas() {
		return quantidadeTentativas;
	}

	public void setQuantidadeTentativas(int quantidadeTentativas) {
		this.quantidadeTentativas = quantidadeTentativas;
	}

	public boolean isColisaoPendente() {
		return colisaoPendente;
	}

	public void setColisaoPendente(boolean colisaoPendente) {
		this.colisaoPendente = colisaoPendente;
	}
}