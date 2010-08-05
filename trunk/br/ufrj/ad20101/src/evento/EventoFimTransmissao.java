package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoFimTransmissao extends Evento{
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;

	public EventoFimTransmissao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(FIM_TRANSMISSAO);
	}
	
	/*
	 * Esta classe simula o fim da transmissão de um quadro corretamente, ou seja,
	 * não houve detecção de colisão durante a transmissão deste quadro
	 */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//testa o estado em que se encontra a Estação
		//TODO este if não deveria ser necessário
		if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			//gera um evento indicando o fim da recepção de um quadro para as demais Estações
			for(int i = 0; i < 4; i++){
				if(i + 1 != this.getEstacao().getIdentificador()){
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						listaEventos.add(servicos.geraEvento(FIM_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes()));
					}
				}else{
					//testa se ainda existem quadros da mensagem corrente para enviar 
					if(this.quantidadeQuadro > 0){
						//caso haja, aguardar o intervalo entre quadros e começar a transmitir
						this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
						EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacoes().get(this.getEstacao().getIdentificador() -1), this.getEstacoes());
						eventoIniciaTransmissao.setQuantidadeQuadro(this.getQuantidadeQuadro());
						listaEventos.add(eventoIniciaTransmissao);
					}else{
						//caso não haja mais quadros chama o Evento que indica o fim da transmissão de uma mensagem
						this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
						listaEventos.add(servicos.geraEvento(FIM_MENSAGEM, getTempoInicial(), this.getEstacao(), this.getEstacoes()));
					}
				}
			}
		}else{
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
