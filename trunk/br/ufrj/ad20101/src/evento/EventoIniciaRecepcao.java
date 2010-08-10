package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoIniciaRecepcao extends Evento{
	
	private Double tempoRecepcao;
	
	public EventoIniciaRecepcao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao, int rodada){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setRodada(rodada);
		this.setTipoEvento(INICIA_RECEPCAO);
	}
	
	/*
	 * Esta classe simula o início da recepção de um quadro
	 * Caso a estação esteja transmitindo, então foi detectada uma colisão
	 * Caso esteja tratando colisao, então ela passará para trantando colisão ocupado, que indica que o meio está ocupado
	 * Caso esteja recebendo algum quadro, significa que houve uma colisão, mas ela não está envolvida, portanto nada deve ser feito
	 * Caso esteja ocioso ou preparando para transmitir, receberá a mensagem normalmente
	 */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO INICIA RECEPÇÃO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+" COM ESTADO: "+this.getEstacao().getEstado()+"\n");
		
		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Estação
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO || this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO || this.getEstacao().getEstado() == Estacao.ESTADO_PREPARANDO_TRANSFERIR){
			//são os Estados que a Estação continua recebendo (ou começa a receber) normalmente
			this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			//Colisão detectada
			//Estação passa para o Estado tratando colisão
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
			//gera o Evento que interrompe a transmissão
			EventoInterrompeTransmissao eventoInterrompeTransmissao = (EventoInterrompeTransmissao)servicos.geraEvento(INTERROMPE_TRANSMISSAO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes(), this.getRodada());
			listaEventos.add(eventoInterrompeTransmissao);
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
