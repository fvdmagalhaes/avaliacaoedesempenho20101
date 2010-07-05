package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoFimTransmissao extends Evento{
	
	private int quantidadeQuadro;

	public EventoFimTransmissao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(FIM_TRANSMISSAO);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		Servicos servicos = new Servicos();
		
		System.out.printf("TEMPO: " + "%.10f" + " segundos; ESTAÇÃO: Estação " + this.getEstacao().getIdentificador() + "; EVENTO: Fim da Transmissão de um Quadro;",this.getTempoInicial()/Constantes.SEGUNDO_EM_MILISSEGUNDOS);
		if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			this.getEstacoes().get(this.getEstacao().getIdentificador()-1).setEstado(Estacao.ESTADO_OCIOSO);
			if(this.quantidadeQuadro > 0){
				System.out.println("Meio Livre! Mensagem começará a ser transmitida dentro de 9,6 micro-segundos");
				EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacoes().get(this.getEstacao().getIdentificador() -1), this.getEstacoes());
				eventoIniciaTransmissao.setQuantidadeQuadro(servicos.geraQuantidadeQuadros(this.getEstacao()));
				listaEventos.add(eventoIniciaTransmissao);
			}else{
				listaEventos.add(servicos.geraEvento(FIM_MENSAGEM, getTempoInicial(), this.getEstacoes().get(this.getEstacao().getIdentificador() -1), this.getEstacoes()));
			}
		}else{
			System.out.println("ERRO: Estação se encontra num estado não existente");
			System.exit(0);
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
