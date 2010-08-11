package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoFimTransmissao extends Evento{
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;

	public EventoFimTransmissao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao, int rodada){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setRodada(rodada);
		this.setTipoEvento(FIM_TRANSMISSAO);
	}
	
	/*
	 * Esta classe simula o fim da transmissão de um quadro corretamente, ou seja,
	 * não houve detecção de colisão durante a transmissão deste quadro
	 */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		if(SimuladorDebug.isDebbuging())
			SimuladorDebug.escreveLog("EVENTO FIM TRANSMISSAO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+"\n");

		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//Com o fim da recepção o meio foi detectado livre, portanto este tempo deve ser setado na Estação
		this.getEstacao().setTempoUltimaRecepcao(this.getTempoInicial());
		
		//testa o estado em que se encontra a Estação
		if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			//gera um evento indicando o fim da recepção de um quadro para as demais Estações
			for(int i = 0; i < 4; i++){
				if(i + 1 != this.getEstacao().getIdentificador()){
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						//antes de gerar um Evento do tipo Fim Recepção ele testa se já existe um Evento deste tipo
						//para esta Estação na lista de Eventos
						EventoFimRecepcao eventoFimRecepcao = (EventoFimRecepcao)servicos.retornaEvento(listaEventos, FIM_RECEPCAO, this.getEstacoes().get(i));
						if(eventoFimRecepcao == null){
							//caso não exista, gera um Evento de fim de recepção
							listaEventos.add(servicos.geraEvento(FIM_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes(), this.getRodada()));
						}else{
							//caso exista, a única coisa que deve ser feita é alterar o tempo do Evento que já está lá
							//deleta o Evento antigo
							listaEventos = servicos.deletaEvento(listaEventos, FIM_RECEPCAO, this.getEstacao());
							//altera o tempo do Evento
							eventoFimRecepcao.setTempoInicial(this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA);
							//adiciona o novo Evento à lista de Eventos
							listaEventos.add(eventoFimRecepcao);
						}
					}
				}else{
					//testa se ainda existem quadros da mensagem corrente para enviar 
					if(this.quantidadeQuadro > 1){
						//caso haja, aguardar o intervalo entre quadros e começar a transmitir
						this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
						EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(), this.getEstacoes(),this.getRodada());
						eventoIniciaTransmissao.setQuantidadeQuadro(this.getQuantidadeQuadro()-1);
						eventoIniciaTransmissao.setQuantidadeTentativas(1);
						listaEventos.add(eventoIniciaTransmissao);
					}else{
						//caso não haja mais quadros chama o Evento que indica o fim da transmissão de uma mensagem
						this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
						listaEventos.add(servicos.geraEvento(FIM_MENSAGEM, getTempoInicial(), this.getEstacao(), this.getEstacoes(),this.getRodada()));
					}
				}
			}
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
