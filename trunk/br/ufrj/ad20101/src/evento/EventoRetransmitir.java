package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoRetransmitir extends Evento{
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;
	
	public EventoRetransmitir(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(RETRANSMITIR);
	}
	
	/*
	 * Esta classe simula a retransmissão de um quadro.
	 * Este Evento testa se o meio está livre. Caso esteja, o quadro será retransmitido
	 * imdiatamente. Caso contrário, a Estação continua sentindo o meio até que este
	 * desocupe para retransmitir o quadro pendente. 
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		SimuladorDebug simulador = new SimuladorDebug();
		simulador.escreveLog("EVENTO RETRANSMITIR OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+"\n");

		//cria a classe de serviço
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Estação
		if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
			//meio está desocupado, quadro será retransmitido imediatamente
			//altera Estado da Estação, pois este é o fim do tratamento da Colisão
			this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
			//gera o Evento que inicia a transmissão do quadro pendente 
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao)servicos.geraEvento(INICIA_TRANSMISSAO, getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacoes().get(this.getEstacao().getIdentificador()-1), getEstacoes());
			//seta as informações deste quadro
			eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
			eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas);
			//adiciona à lista de Eventos
			listaEventos.add(eventoIniciaTransmissao);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
			//meio está ocupado, Estação continua sentindo o meio até que ele dique livre
			//altera o Estado da Estação, pois este é o fim do tratamento da Colisão 
			this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
			//gera o evento de início de transmissão do quadro que colidiu
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, null, this.getEstacao(),this.getEstacoes());
			//seta as informações deste quadro
			eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
			eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas);
			//quadro passa a sentir o meio até que desocupe
			this.getEstacao().setQuadroSentindoMeio(eventoIniciaTransmissao);
		}else{
			System.out.println("ERRO: Estação se encontra num estado não existente");
			System.exit(0);
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
