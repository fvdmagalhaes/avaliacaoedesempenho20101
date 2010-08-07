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
	 * Esta classe simula a transmiss�o de um quadro, depois do intervalo entre quadros terminado
	 * Caso esteja no Estado Preparando para transferir, transmiss�o inicia normalmente e o estado muda para transmitindo
	 * Caso ele esteja recebendo, significa que o meio ficou ocupado neste instante, pois neste caso a Esta��o que gerou a �ltima
	 * transmiss�o no meio, enviou outro quadro, portanto a Esta��o transmitir�
	 */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO INICIA TRANSMISSAO OCORREU EM " + this.getTempoInicial() + " NA ESTA��O " + this.getEstacao().getIdentificador()+" QUADRO: "+this.getQuantidadeQuadro() + "\n");

		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Esta��o
		if(this.getEstacao().getEstado() == Estacao.ESTADO_PREPARANDO_TRANSFERIR || this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			//n�o foi detectada colis�o, portanto o estado muda para transmitindo
			this.getEstacao().setEstado(Estacao.ESTADO_TRANSFERINDO);
			
			//gera o Evento de inicio de recep��o para cada Esta��o
			for(int i = 0; i < 4; i++){
				//testa se � a Esta��o n�o � a corrente
				if(i + 1 != this.getEstacao().getIdentificador()){
					//testa se a Esta��o est� ou n�o participando da simula��o
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						//adiciona � lista de Eventos o in�cio da recep��o de um quadro da Esta��o selecionada
						listaEventos.add(servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes()));
					}
				}else{
					//gera um Evento de fim de transmiss�o e o adiciona � lista de Eventos
					EventoFimTransmissao eventoFimTransmissao = (EventoFimTransmissao) servicos.geraEvento(FIM_TRANSMISSAO, this.getTempoInicial() + Constantes.TEMPO_QUADRO_ENLACE, this.getEstacao(), this.getEstacoes());
					eventoFimTransmissao.setQuantidadeQuadro(this.getQuantidadeQuadro());
					eventoFimTransmissao.setQuantidadeTentativas(this.getQuantidadeTentativas());
					listaEventos.add(eventoFimTransmissao);
				}
			}
		}/*else if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			//colis�o foi detectada
			//altera Estado da Esta��o para Tratando Colis�o Ocupado
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
			//gera Evento que inicia transmissao do refor�o de colis�o
			EventoIniciaTransReforco eventoIniciaTransReforco = (EventoIniciaTransReforco)servicos.geraEvento(INICIA_TRANS_REFORCO, getTempoInicial(), this.getEstacao(), this.getEstacoes());
			//guarda as informa��es que ser�o necess�rias para o Evento de Colis�o 
			eventoIniciaTransReforco.setQuantidadeQuadro(this.quantidadeQuadro);
			eventoIniciaTransReforco.setQuantidadeTentativas(this.quantidadeTentativas);
			//adiciona � lista de Eventos
			listaEventos.add(eventoIniciaTransReforco);
		}*/else{
			System.out.println("ERRO: Esta��o se encontra num estado n�o existente");
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
