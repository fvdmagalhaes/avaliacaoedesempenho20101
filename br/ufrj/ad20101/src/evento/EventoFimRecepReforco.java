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
	 * Esta classe simula o final da recep��o do Refor�o de colis�o por uma Esta��o
	 * Este Evento representa a detec��o do fim do sinal de Refor�o no meio
	 * Se a Esta��o estiver em Estado de tratamento de Colis�o, o tratramento de fato
	 * come�a agora. Caso contr�rio, poder� transmitir alguma pendencia, caso exista.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO FIM RECEPCAO REFORCO OCORREU EM " + this.getTempoInicial() + " NA ESTA��O " + this.getEstacao().getIdentificador()+"\n");

		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//Com o fim da recep��o o meio foi detectado livre, portanto este tempo deve ser setado na Esta��o
		this.getEstacao().setTempoUltimaRecepcao(this.getTempoInicial());
		
		//testa o estado em que se encontra a Esta��o
		if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO)
		{
			//Pelo Estado da Esta��o, nota-se que ela n�o tem colis�es para tratar, ent�o
			//resta ver se h� alguma transmiss�o pendente
			EventoIniciaTransmissao eventoIniciaTransmiss�o = this.getEstacao().getQuadroSentindoMeio();
			if(eventoIniciaTransmiss�o != null)
			{
				//caso a Esta��o esteja aguardando o meio ficar livre para transmitir um quadro
				//ser� transmitido agora
				//setando o tempo
				eventoIniciaTransmiss�o.setTempoInicial(this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS);
				//adiciona � lista de Eventos
				listaEventos.add(eventoIniciaTransmiss�o);
				//altera o Estado da Esta��o
				this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
			}
			else if(!this.getEstacao().getMensagensPendentes().isEmpty())
			{
				//caso contr�rio, se houver uma mensagem na fila de mensagens, ela ser� transmitida
				//recupera a mensagem da lista
				EventoPrepararTransmissao pegaEvPrepTransm = (EventoPrepararTransmissao)this.getEstacao().getMensagensPendentes().get(0);
				//seta o tempo
				pegaEvPrepTransm.setTempoInicial(this.getTempoInicial());
				//remove da fila
				this.getEstacao().getMensagensPendentes().remove(0);
				//adiciona � lista de Eventos
				listaEventos.add(pegaEvPrepTransm);
				//altera o Estado da Esta��o
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}
			else
			{
				//Caso n�o haja nada para ser transmitido, Esta��o vai apra o Estado Ocioso e aguarda novos eventos
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}
			
		}
		else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO)
		{
			//testa se a gera��o do Evento de Colis�o est� pendente ou n�o
			if(isColisaoPendente())
			{
				//Caso haja:
				//muda o Estado para Tratando Colis�o Ocioso, pois o fim do sinal de Refor�o
				//indica exatamente que o meio est� livre
				this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
				//Gera o Evento de Colis�o, que come�ar� a ser tratado neste instante
				EventoColisao eventoColisao = (EventoColisao) servicos.geraEvento(COLISAO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes());

				//passa as informa��es necess�rias para o Evento de Colis�o
				eventoColisao.setQuantidadeQuadro(this.quantidadeQuadro);
				eventoColisao.setQuantidadeTentativas(this.quantidadeTentativas);
				 //adiciona o Evento de Colis�o � lista de Eventos
				listaEventos.add(eventoColisao);
				return listaEventos;
			}
			else
			{
				//caso contr�rio, apenas muda para o Estado Ocioso e Tratando Colis�o, pois o Evento
				//de Colis�o ser� tratado logo
				this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
			}
		}
		else
		{
			System.out.println("ERRO: Esta��o se encontra em um estado inexistente!");
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