package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoIniciaTransmissao extends Evento{
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;

	public EventoIniciaTransmissao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(INICIA_TRANSMISSAO);
	}
	
	/*
	 * Esta classe simula a transmissão de um quadro, depois do intervalo entre quadros terminado
	 * Caso esteja no Estado Preparando para transferir, transmissão inicia normalmente e o estado muda para transmitindo
	 * Caso ele esteja recebendo, significa que o meio ficou ocupado neste instante, pois neste caso a Estação que gerou a última
	 * transmissão no meio, enviou outro quadro, portanto a Estação transmitirá
	 */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO INICIA TRANSMISSAO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+" QUADRO: "+this.getQuantidadeQuadro() + "\n");

		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Estação
		if(this.getEstacao().getEstado() == Estacao.ESTADO_PREPARANDO_TRANSFERIR || this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			//não foi detectada colisão, portanto o estado muda para transmitindo
			this.getEstacao().setEstado(Estacao.ESTADO_TRANSFERINDO);
			
			//gera o Evento de inicio de recepção para cada Estação
			for(int i = 0; i < 4; i++){
				//testa se é a Estação não é a corrente
				if(i + 1 != this.getEstacao().getIdentificador()){
					//testa se a Estação está ou não participando da simulação
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						//adiciona à lista de Eventos o início da recepção de um quadro da Estação selecionada
						listaEventos.add(servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes()));
					}
				}else{
					//gera um Evento de fim de transmissão e o adiciona à lista de Eventos
					EventoFimTransmissao eventoFimTransmissao = (EventoFimTransmissao) servicos.geraEvento(FIM_TRANSMISSAO, this.getTempoInicial() + Constantes.TEMPO_QUADRO_ENLACE, this.getEstacao(), this.getEstacoes());
					eventoFimTransmissao.setQuantidadeQuadro(this.getQuantidadeQuadro());
					eventoFimTransmissao.setQuantidadeTentativas(this.getQuantidadeTentativas());
					listaEventos.add(eventoFimTransmissao);
				}
			}
		}/*else if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			//colisão foi detectada
			//altera Estado da Estação para Tratando Colisão Ocupado
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
			//gera Evento que inicia transmissao do reforço de colisão
			EventoIniciaTransReforco eventoIniciaTransReforco = (EventoIniciaTransReforco)servicos.geraEvento(INICIA_TRANS_REFORCO, getTempoInicial(), this.getEstacao(), this.getEstacoes());
			//guarda as informações que serão necessárias para o Evento de Colisão 
			eventoIniciaTransReforco.setQuantidadeQuadro(this.quantidadeQuadro);
			eventoIniciaTransReforco.setQuantidadeTentativas(this.quantidadeTentativas);
			//adiciona à lista de Eventos
			listaEventos.add(eventoIniciaTransReforco);
		}*/else{
			System.out.println("ERRO: Estação se encontra num estado não existente");
			System.exit(0);
		}
		
		return listaEventos;
	}
	
	public int getQuantidadeQuadro() {
		return quantidadeQuadro;
	}

	public void setQuantidadeQuadro(int quantidadeQuadro) {
		this.quantidadeQuadro = quantidadeQuadro;
	}

	public void setQuantidadeTentativas(int quantidadeTentativas) {
		this.quantidadeTentativas = quantidadeTentativas;
	}

	public int getQuantidadeTentativas() {
		return quantidadeTentativas;
	}
}
