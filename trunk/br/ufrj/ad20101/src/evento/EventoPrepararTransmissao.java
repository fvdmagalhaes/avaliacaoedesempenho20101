package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoPrepararTransmissao extends Evento{
	
	private int quantidadeQuadro;
	
	public EventoPrepararTransmissao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao, int rodada){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setRodada(rodada);
		this.setTipoEvento(PREPARA_TRANSMISSAO);
	}
	
	/*
	 * Esta classe simula a chegada do primeiro quadro de uma mensagem em uma determinada Estação
	 * Caso a Estação esteja ociosa, ele teste se faz 9.6 mircosegundos desde a última vez em que o meio estava ocupado
	 * em caso positivo ele programa o início da transmissão para este exato momento, caso contrário aguarda o fim do intervalo
	 * Caso contrário, se estiver ainda transmitindo a mensagem anterior, tratando uma possível colisão ou recebendo alguma coisa, 
	 * a mensagem entrará na fila de espera de mensagens da Estação
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO PREPARAR TRANSMISSÃO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+"\n");

		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Estação
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			//Estação muda para Estado "Preparando para transmitir"
			this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
			//gera um Evento que prepara a mensagem para transmissão e o adiona à lista de Eventos
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes(), this.getRodada());
			//antes deve-se testar se o intervalo entre quadros já foi respeitado ou não
			if(this.getTempoInicial() - this.getEstacao().getTempoUltimaRecepcao() < Constantes.INTERVALO_ENTRE_QUADROS){
				//caso ainda nao tenha terminado o tempo, aguardar até o final
				eventoIniciaTransmissao.setTempoInicial(this.getEstacao().getTempoUltimaRecepcao() + Constantes.INTERVALO_ENTRE_QUADROS);
			}
			//seta quantidade quadros
			eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
			//1 pois será a primeira tentativa de enviar o primeiro quadro
			//tentativa é importante para saber se o quadro deve ou não ser descartado
			eventoIniciaTransmissao.setQuantidadeTentativas(1);
			listaEventos.add(eventoIniciaTransmissao);
		}else{
			//adiciona a mensagem à lista de espera da Estação
			ArrayList<Evento> mensagensPendentes = this.getEstacao().getMensagensPendentes();
			// Tempo do evento criado é nulo pois só será setado de fato ao sair da lista de espera 
			EventoPrepararTransmissao eventoPrepararTransmissao = (EventoPrepararTransmissao)servicos.geraEvento(PREPARA_TRANSMISSAO, null, this.getEstacao(), this.getEstacoes(), this.getRodada());
			eventoPrepararTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
			mensagensPendentes.add(eventoPrepararTransmissao);
			this.getEstacao().setMensagensPendentes(mensagensPendentes);
		}
		return listaEventos;
	}

	public void setQuantidadeQuadro(int quantidadeQuadro) {
		this.quantidadeQuadro = quantidadeQuadro;
	}

	public int getQuantidadeQuadro() {
		return quantidadeQuadro;
	}
}
