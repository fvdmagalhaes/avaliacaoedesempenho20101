package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoIniciaRecepcao extends Evento{
	
	private Double tempoRecepcao;
	
	public EventoIniciaRecepcao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(INICIA_RECEPCAO);
	}
	
	/*
	 * Esta classe simula o in�cio da recep��o de um quadro
	 * Caso a esta��o esteja transmitindo, ent�o foi detectada uma colis�o
	 * Caso esteja tratando colisao, ent�o ela passar� para trantando colis�o ocupado, que indica que o meio est� ocupado
	 * Caso esteja recebendo alguma quadro, significa que houve uma colis�o, mas ela n�o est� envolvida, portanto nada deve ser feito
	 * Caso esteja ocioso ou preparando para transmitir, receber� a mensagem normalmente
	 * TODO acho que � melhor diferenciar os tipos de recep��o, pois ele pode receber um quadro ou um refor�o de colis�o. Talvez seja melhor criar um novo evento para tratar isso
	 */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Esta��o
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO || this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO || this.getEstacao().getEstado() == Estacao.ESTADO_PREPARANDO_TRANSFERIR){
			//s�o os Estados que a Esta��o continua recebendo (ou come�a a receber) normalmente
			this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			//Colis�o detectada
			//Esta��o passa para o Estado tratando colis�o
			//TODO devia ser ocupado e n�o ocioso, pois o meio est� ocupado
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
			for(int i = 0; i < 4; i++){
				if(i + 1 != this.getEstacao().getIdentificador()){
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						//TODO talvez seja melhor gerar um evento inicia recep��o refor�o colisao
						EventoIniciaRecepcao eventoIniciaRecepcao = (EventoIniciaRecepcao) servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes());
						eventoIniciaRecepcao.setTempoRecepcao(Constantes.TEMPO_REFORCO_ENLACE);
						listaEventos.add(servicos.geraEvento(FIM_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacoes().get(i), this.getEstacoes()));
						listaEventos.add(eventoIniciaRecepcao);
					}
				}else{
					//Houve uma interrup��o na transmiss�o do quadro que estava sendo transmitido
					//O evento que determina o fim da transmiss�o deve ser retirado da lista de Eventos, pois este quadro
					//n�o ser� mais transmitido com sucesso
					//TODO Talvez criar um evento de interrup��o de transmiss�o seja interessante
					EventoColisao eventoColisao = (EventoColisao) servicos.geraEvento(COLISAO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_COLISAO, this.getEstacoes().get(i), this.getEstacoes());
					EventoFimTransmissao eventoFimTransmissao = (EventoFimTransmissao) servicos.retornaEvento(listaEventos, FIM_TRANSMISSAO, this.getEstacoes().get(i));
					eventoColisao.setQuantidadeQuadro(eventoFimTransmissao.getQuantidadeQuadro());
					eventoColisao.setQuantidadeTentativas(eventoFimTransmissao.getQuantidadeTentativas());
					listaEventos = servicos.deletaEvento(listaEventos, FIM_TRANSMISSAO, this.getEstacoes().get(i));
					listaEventos.add(eventoColisao);
				}
			}
		}else if (this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
			//Estado tratando colis�o com meio ocioso, agora o meio esta ocupado
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
		}else if (this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
		}else{
			System.out.println("ERRO: Esta��o se encontra num estado n�o existente");
			System.exit(0);
		}
		return listaEventos;
	}

	public void setTempoRecepcao(Double tempoRecepcao) {
		this.tempoRecepcao = tempoRecepcao;
	}

	public Double getTempoRecepcao() {
		return tempoRecepcao;
	}
}
