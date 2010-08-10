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
	 * Esta classe simula o in�cio da recep��o de um quadro
	 * Caso a esta��o esteja transmitindo, ent�o foi detectada uma colis�o
	 * Caso esteja tratando colisao, ent�o ela passar� para trantando colis�o ocupado, que indica que o meio est� ocupado
	 * Caso esteja recebendo algum quadro, significa que houve uma colis�o, mas ela n�o est� envolvida, portanto nada deve ser feito
	 * Caso esteja ocioso ou preparando para transmitir, receber� a mensagem normalmente
	 */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO INICIA RECEP��O OCORREU EM " + this.getTempoInicial() + " NA ESTA��O " + this.getEstacao().getIdentificador()+" COM ESTADO: "+this.getEstacao().getEstado()+"\n");
		
		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Esta��o
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO || this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO || this.getEstacao().getEstado() == Estacao.ESTADO_PREPARANDO_TRANSFERIR){
			//s�o os Estados que a Esta��o continua recebendo (ou come�a a receber) normalmente
			this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			//Colis�o detectada
			//Esta��o passa para o Estado tratando colis�o
			this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO);
			//gera o Evento que interrompe a transmiss�o
			EventoInterrompeTransmissao eventoInterrompeTransmissao = (EventoInterrompeTransmissao)servicos.geraEvento(INTERROMPE_TRANSMISSAO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes(), this.getRodada());
			listaEventos.add(eventoInterrompeTransmissao);
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
