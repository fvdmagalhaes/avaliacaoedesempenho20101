package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoIniciaTransmissao extends Evento{
	
	private int quantidadeQuadro;

	public EventoIniciaTransmissao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(INICIA_TRANSMISSAO);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		Servicos servicos = new Servicos();
		System.out.printf("TEMPO: " + "%.10f" + " segundos; ESTA��O: Esta��o " + this.getEstacao().getIdentificador() + "; EVENTO: In�cio de Transmiss�o de um Quadro;",this.getTempoInicial()/Constantes.SEGUNDO_EM_MILISSEGUNDOS);
		if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			System.out.println("Esta��o " + this.getEstacao().getIdentificador() + " inicia a transmiss�o!");
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_TRANSFERINDO);
			for(int i = 0; i < 4; i++){
				if(i + 1 != this.getEstacao().getIdentificador()){
					if(this.getEstacoes().get(i).getTipoChegada() != 0){
						listaEventos.add(servicos.geraEvento(INICIA_RECEPCAO, this.getTempoInicial() + (this.getEstacao().getDistancia() + this.getEstacoes().get(i).getDistancia())*Constantes.PROPAGACAO_ELETRICA, this.getEstacoes().get(i), this.getEstacoes()));
					}
				}else{
					EventoFimTransmissao eventoFimTransmissao = (EventoFimTransmissao) servicos.geraEvento(FIM_TRANSMISSAO, this.getTempoInicial() + Constantes.TEMPO_QUADRO_ENLACE, this.getEstacoes().get(this.getEstacao().getIdentificador()-1), this.getEstacoes());
					eventoFimTransmissao.setQuantidadeQuadro(this.quantidadeQuadro -1);
					listaEventos.add(eventoFimTransmissao);
				}
			}
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			System.out.println("Colis�o! Esta��o " + this.getEstacao().getIdentificador() + " detectou colis�o!");
			listaEventos.add(servicos.geraEvento(COLISAO, this.getTempoInicial() + Constantes.TEMPO_REFORCO_COLISAO, this.getEstacao(), this.getEstacoes()));
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
}
