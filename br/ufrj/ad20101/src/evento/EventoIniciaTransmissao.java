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
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		Servicos servicos = new Servicos();
		System.out.printf("TEMPO: " + "%.10f" + " segundos; ESTAÇÃO: Estação " + this.getEstacao().getIdentificador() + "; EVENTO: Início de Transmissão de um Quadro;\n",this.getTempoInicial()/Constantes.SEGUNDO_EM_MILISSEGUNDOS);
		if(this.quantidadeTentativas == 0){
			this.quantidadeTentativas = 1;
		}
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			System.out.println("Estação " + this.getEstacao().getIdentificador() + " inicia a transmissão!");
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRANSFERINDO);
			for(int i = 0; i < 4; i++){
				if(i + 1 != this.getEstacao().getIdentificador()){
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						listaEventos.add(servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes()));
					}
				}else{
					EventoFimTransmissao eventoFimTransmissao = (EventoFimTransmissao) servicos.geraEvento(FIM_TRANSMISSAO, this.getTempoInicial() + Constantes.TEMPO_QUADRO_ENLACE, this.getEstacoes().get(i), this.getEstacoes());
					eventoFimTransmissao.setQuantidadeQuadro(this.quantidadeQuadro -1);
					eventoFimTransmissao.setQuantidadeTentativas(this.quantidadeTentativas);
					listaEventos.add(eventoFimTransmissao);
				}
			}
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			System.out.println("Colisão! Estação " + this.getEstacao().getIdentificador() + " detectou colisão!");
			for(int i = 0; i < 4; i++){
				if(i + 1 != this.getEstacao().getIdentificador()){
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						EventoIniciaRecepcao eventoIniciaRecepcao = (EventoIniciaRecepcao) servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes());
						eventoIniciaRecepcao.setTempoRecepcao(Constantes.TEMPO_REFORCO_ENLACE);
						listaEventos.add(eventoIniciaRecepcao);
					}
				}else{
					EventoColisao eventoColisao = (EventoColisao) servicos.geraEvento(COLISAO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_COLISAO, this.getEstacoes().get(i), this.getEstacoes());
					eventoColisao.setQuantidadeQuadro(this.quantidadeQuadro);
					eventoColisao.setQuantidadeTentativas(this.quantidadeTentativas);
					listaEventos.add(eventoColisao);
				}
			}
		}else{
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
