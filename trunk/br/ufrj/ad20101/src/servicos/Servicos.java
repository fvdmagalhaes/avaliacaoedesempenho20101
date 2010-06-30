package br.ufrj.ad20101.src.servicos;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.evento.EventoChegaMensagem;
import br.ufrj.ad20101.src.evento.EventoColisao;
import br.ufrj.ad20101.src.evento.EventoDescartaQuadro;
import br.ufrj.ad20101.src.evento.EventoFimMensagem;
import br.ufrj.ad20101.src.evento.EventoFimRecepcao;
import br.ufrj.ad20101.src.evento.EventoFimTransmissao;
import br.ufrj.ad20101.src.evento.EventoIniciaRecepcao;
import br.ufrj.ad20101.src.evento.EventoIniciaTransmissao;
import br.ufrj.ad20101.src.evento.EventoProntoTransmitir;

public class Servicos {
	
	// Este método gera o tempo de chegada (deterministico ou exponencial) da próxima mensagem da Estação especificada
	public Double geraProximaMensagem(Estacao estacao, Double tempoAtual){
		if(estacao.getTipoChegada() == Estacao.DETERMINISTICO){
			return tempoAtual + estacao.getIntervaloEntreChegadas();
		}else if(estacao.getTipoChegada() == Estacao.EXPONENCIAL){
			return gerarAmostraExponencial(estacao.getIntervaloEntreChegadas()) + tempoAtual;
		}else{
			System.out.println("ERRO: Tipo de chegada nao especificado para a Estação " + estacao.getIdentificador());
			System.exit(0);
			return null;
		}
	}
	
	// Este método gera a amostra exponencial
	private Double gerarAmostraExponencial(Double intervaloMedio){
		return new Double(-intervaloMedio*Math.log(1.0-Math.random()));
	}
	
	//Este método gera um evento de qualquer tipo
	public Evento geraEvento(int tipoEvento, Double tempoInicial, Estacao estacao){
		if(tipoEvento == Evento.PRONTO_TRANSMITIR){
			return new EventoProntoTransmitir(tempoInicial,estacao);
		}else if(tipoEvento == Evento.INICIA_TRANSMISSAO){
			return new EventoIniciaTransmissao(tempoInicial,estacao);
		}else if(tipoEvento == Evento.FIM_TRANSMISSAO){
			return new EventoFimTransmissao(tempoInicial,estacao);
		}else if(tipoEvento == Evento.INICIA_RECEPCAO){
			return new EventoIniciaRecepcao(tempoInicial,estacao);
		}else if(tipoEvento == Evento.FIM_RECEPCAO){
			return new EventoFimRecepcao(tempoInicial,estacao);
		}else if(tipoEvento == Evento.CHEGA_MENSAGEM){
			return new EventoChegaMensagem(tempoInicial,estacao);
		}else if(tipoEvento == Evento.FIM_MENSAGEM){
			return new EventoFimMensagem(tempoInicial,estacao);
		}else if(tipoEvento == Evento.COLISAO){
			return new EventoColisao(tempoInicial,estacao);
		}else if(tipoEvento == Evento.DESCARTA_QUADRO){
			return new EventoDescartaQuadro(tempoInicial,estacao);
		}else{
			System.out.println("ERRO: O tipo de evento especificado para geração não existe.");
			System.exit(0);
			return null;
		}
	}
}
