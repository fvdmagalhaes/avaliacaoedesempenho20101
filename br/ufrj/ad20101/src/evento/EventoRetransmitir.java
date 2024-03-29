package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoRetransmitir extends Evento{
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;
	
	public EventoRetransmitir(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao, int rodada){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setRodada(rodada);
		this.setTipoEvento(RETRANSMITIR);
	}
	
	/*
	 * Esta classe simula a retransmiss�o de um quadro.
	 * Este Evento testa se o meio est� livre. Caso esteja, o quadro ser� retransmitido
	 * imdiatamente. Caso contr�rio, a Esta��o continua sentindo o meio at� que este
	 * desocupe para retransmitir o quadro pendente. 
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO RETRANSMITIR OCORREU EM " + this.getTempoInicial() + " NA ESTA��O " + this.getEstacao().getIdentificador()+"\n");

		//cria a classe de servi�o
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Esta��o
		if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
			//meio est� desocupado, quadro ser� retransmitido imediatamente
			//altera Estado da Esta��o, pois este � o fim do tratamento da Colis�o
			this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
			//gera o Evento que inicia a transmiss�o do quadro pendente 
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao)servicos.geraEvento(INICIA_TRANSMISSAO, getTempoInicial(), this.getEstacao(), getEstacoes(), this.getRodada());
			//antes deve-se testar se o intervalo entre quadros j� foi respeitado ou n�o
			if(this.getTempoInicial() - this.getEstacao().getTempoUltimaRecepcao() < Constantes.INTERVALO_ENTRE_QUADROS){
				//caso ainda nao tenha terminado o tempo, aguardar at� o final
				eventoIniciaTransmissao.setTempoInicial(this.getEstacao().getTempoUltimaRecepcao() + Constantes.INTERVALO_ENTRE_QUADROS);
			}
			//seta as informa��es deste quadro
			eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
			eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas);
			//adiciona � lista de Eventos
			listaEventos.add(eventoIniciaTransmissao);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
			//meio est� ocupado, Esta��o continua sentindo o meio at� que ele dique livre
			//altera o Estado da Esta��o, pois este � o fim do tratamento da Colis�o 
			this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
			//gera o evento de in�cio de transmiss�o do quadro que colidiu
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, null, this.getEstacao(),this.getEstacoes(), this.getRodada());
			//seta as informa��es deste quadro
			eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
			eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas);
			//quadro passa a sentir o meio at� que desocupe
			this.getEstacao().setQuadroSentindoMeio(eventoIniciaTransmissao);
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
