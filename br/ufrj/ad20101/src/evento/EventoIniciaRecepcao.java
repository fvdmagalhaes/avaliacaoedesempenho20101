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
	 * Esta classe simula o início da recepção de um quadro
	 * Caso a estação esteja transmitindo, então foi detectada uma colisão
	 * Caso esteja tratando colisao, então ela passará para trantando colisão ocupado, que indica que o meio está ocupado
	 * Caso esteja recebendo alguma quadro, significa que houve uma colisão, mas ela não está envolvida, portanto nada deve ser feito
	 * Caso esteja ocioso ou preparando para transmitir, receberá a mensagem normalmente
	 * TODO acho que é melhor diferenciar os tipos de recepção, pois ele pode receber um quadro ou um reforço de colisão. Talvez seja melhor criar um novo evento para tratar isso
	 */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Estação
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO || this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO || this.getEstacao().getEstado() == Estacao.ESTADO_PREPARANDO_TRANSFERIR){
			//são os Estados que a Estação continua recebendo (ou começa a receber) normalmente
			this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			//Colisão detectada
			//Estação passa para o Estado tratando colisão
			//TODO devia ser ocupado e não ocioso, pois o meio está ocupado
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
			for(int i = 0; i < 4; i++){
				if(i + 1 != this.getEstacao().getIdentificador()){
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						//TODO talvez seja melhor gerar um evento inicia recepção reforço colisao
						EventoIniciaRecepcao eventoIniciaRecepcao = (EventoIniciaRecepcao) servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes());
						eventoIniciaRecepcao.setTempoRecepcao(Constantes.TEMPO_REFORCO_ENLACE);
						listaEventos.add(servicos.geraEvento(FIM_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA + Constantes.TEMPO_REFORCO_ENLACE, this.getEstacoes().get(i), this.getEstacoes()));
						listaEventos.add(eventoIniciaRecepcao);
					}
				}else{
					//Houve uma interrupção na transmissão do quadro que estava sendo transmitido
					//O evento que determina o fim da transmissão deve ser retirado da lista de Eventos, pois este quadro
					//não será mais transmitido com sucesso
					//TODO Talvez criar um evento de interrupção de transmissão seja interessante
					EventoColisao eventoColisao = (EventoColisao) servicos.geraEvento(COLISAO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_COLISAO, this.getEstacoes().get(i), this.getEstacoes());
					EventoFimTransmissao eventoFimTransmissao = (EventoFimTransmissao) servicos.retornaEvento(listaEventos, FIM_TRANSMISSAO, this.getEstacoes().get(i));
					eventoColisao.setQuantidadeQuadro(eventoFimTransmissao.getQuantidadeQuadro());
					eventoColisao.setQuantidadeTentativas(eventoFimTransmissao.getQuantidadeTentativas());
					listaEventos = servicos.deletaEvento(listaEventos, FIM_TRANSMISSAO, this.getEstacoes().get(i));
					listaEventos.add(eventoColisao);
				}
			}
		}else if (this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
			//Estado tratando colisão com meio ocioso, agora o meio esta ocupado
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
		}else if (this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
		}else{
			System.out.println("ERRO: Estação se encontra num estado não existente");
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
