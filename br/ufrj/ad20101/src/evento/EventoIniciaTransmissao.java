package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

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
	 * Caso esteja no Estado Recebendo, significa que foi detectada uma colis�o
	 * 
	 */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Esta��o
		//TODO as condi��es deste if devem ser revistas
		if(this.getEstacao().getEstado() == Estacao.ESTADO_PREPARANDO_TRANSFERIR || this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO || this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			//n�o foi detectada colis�o, portanto o estado muda para transmitindo
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRANSFERINDO);
			
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
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			//colis�o foi detectada
			//altera Estado da Esta��o para Tratando Colis�o Ocupado
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
			//gera refor�o de colis�o para todas as Esta��es
			for(int i = 0; i < 4; i++){				
				if(i + 1 != this.getEstacao().getIdentificador()){
					//as demais Esta��es receber�o o refor�o de colis�o
					//TODO Rever esta parte
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						EventoIniciaRecepcao eventoIniciaRecepcao = (EventoIniciaRecepcao) servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes());
						eventoIniciaRecepcao.setTempoRecepcao(Constantes.TEMPO_REFORCO_ENLACE);
						listaEventos.add(servicos.geraEvento(FIM_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacoes().get(i), this.getEstacoes()));
						listaEventos.add(eventoIniciaRecepcao);
					}
					//TODO At� aqui
				}else{
					//gera o Evento de Colis�o
					EventoColisao eventoColisao = (EventoColisao) servicos.geraEvento(COLISAO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_COLISAO, this.getEstacoes().get(i), this.getEstacoes());
					eventoColisao.setQuantidadeQuadro(this.quantidadeQuadro);
					eventoColisao.setQuantidadeTentativas(this.quantidadeTentativas);
					listaEventos.add(eventoColisao);
				}
			}
		}else{
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
