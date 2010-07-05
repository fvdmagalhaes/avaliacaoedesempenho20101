package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;
import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.servicos.Constantes;

public class EventoChegaMensagem extends Evento{
	public EventoChegaMensagem(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(CHEGA_MENSAGEM);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		System.out.printf("TEMPO: " + "%.10f" + " segundos; ESTAÇÃO: Estação " + this.getEstacao().getIdentificador() + "; EVENTO: Chegada de Mensagem;",this.getTempoInicial()/Constantes.SEGUNDO_EM_MILISSEGUNDOS);
		Servicos servicos = new Servicos();
		if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			System.out.println("Meio está Ocupado! Evento será resolvido quando o meio estiver livre.");
			ArrayList<Evento> mensagensPendentes = this.getEstacao().getMensagensPendentes();
			mensagensPendentes.add(servicos.geraEvento(CHEGA_MENSAGEM, null, this.getEstacao(),this.getEstacoes()));
			this.getEstacao().setMensagensPendentes(mensagensPendentes);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			System.out.println("Estação já está Transferindo! Evento será resolvido quando a estação terminar a mensagem anterior.");
			ArrayList<Evento> mensagensPendentes = this.getEstacao().getMensagensPendentes();
			mensagensPendentes.add(servicos.geraEvento(CHEGA_MENSAGEM, null, this.getEstacao(),this.getEstacoes()));
			this.getEstacao().setMensagensPendentes(mensagensPendentes);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			System.out.println("Meio Livre! Mensagem começará a ser transmitida dentro de 9,6 micro-segundos");
			EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(), this.getEstacoes());
			eventoIniciaTransmissao.setQuantidadeQuadro(servicos.geraQuantidadeQuadros(this.getEstacao()));
			listaEventos.add(eventoIniciaTransmissao);
		}else{
			System.out.println("ERRO: Estação se encontra num estado não existente");
			System.exit(0);
		}
		return listaEventos;
	}
}