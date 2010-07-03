package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;
import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.servicos.Constantes;

public class EventoChegaMensagem extends Evento{
	public EventoChegaMensagem(Double tempoInicio, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setTipoEvento(CHEGA_MENSAGEM);
	}
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		System.out.println("TEMPO: " + this.getTempoInicial() + " segundos; ESTA��O: Esta��o" + this.getEstacao().getIdentificador() + "; EVENTO: Chegada de Mensagem;");
		Servicos servicos = new Servicos();
		if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO){
			System.out.println("Meio est� Ocupado! Evento ser� resolvido quando o meio estiver livre.");
			ArrayList<Evento> mensagensPendentes = this.getEstacao().getMensagensPendentes();
			mensagensPendentes.add(servicos.geraEvento(CHEGA_MENSAGEM, null, this.getEstacao()));
			this.getEstacao().setMensagensPendentes(mensagensPendentes);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRANSFERINDO){
			System.out.println("Esta��o j� est� Transferindo! Evento ser� resolvido quando a esta��o terminar a mensagem anterior.");
			ArrayList<Evento> mensagensPendentes = this.getEstacao().getMensagensPendentes();
			mensagensPendentes.add(servicos.geraEvento(CHEGA_MENSAGEM, null, this.getEstacao()));
			this.getEstacao().setMensagensPendentes(mensagensPendentes);
		}else if(this.getEstacao().getEstado() == Estacao.ESTADO_OCIOSO){
			System.out.println("Meio Livre! Mensagem come�ar� a ser transmitida dentro de 9,6 micro-segundos");
			listaEventos.add(servicos.geraEvento(INICIA_TRANSMISSAO, (this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS)/Constantes.SEGUNDO_EM_MILISSEGUNDOS, this.getEstacao()));
		}else{
			System.out.println("ERRO: Esta��o se encontra num estado n�o existente");
			System.exit(0);
		}
		return listaEventos;
	}
}