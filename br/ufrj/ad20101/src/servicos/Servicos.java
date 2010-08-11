package br.ufrj.ad20101.src.servicos;

import java.util.ArrayList;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.evento.EventoChegaMensagem;
import br.ufrj.ad20101.src.evento.EventoColisao;
import br.ufrj.ad20101.src.evento.EventoDescartaQuadro;
import br.ufrj.ad20101.src.evento.EventoFimMensagem;
import br.ufrj.ad20101.src.evento.EventoFimRecepReforco;
import br.ufrj.ad20101.src.evento.EventoFimRecepcao;
import br.ufrj.ad20101.src.evento.EventoFimTransReforco;
import br.ufrj.ad20101.src.evento.EventoFimTransmissao;
import br.ufrj.ad20101.src.evento.EventoIniciaRecepReforco;
import br.ufrj.ad20101.src.evento.EventoIniciaRecepcao;
import br.ufrj.ad20101.src.evento.EventoIniciaTransReforco;
import br.ufrj.ad20101.src.evento.EventoIniciaTransmissao;
import br.ufrj.ad20101.src.evento.EventoInterrompeTransmissao;
import br.ufrj.ad20101.src.evento.EventoPrepararTransmissao;
import br.ufrj.ad20101.src.evento.EventoRetransmitir;

public class Servicos {
	
	//Este método encontra o maior valor dentro de um array de Double
	public Double maior(Double[] elementos, int quantidade){
		Double maior = 0.0;
		for(int i = 0; i < quantidade; i ++){
			if(elementos[i] > maior){
				maior = elementos[i];
			}
		}
		return maior;
	}
	
	//Este método encontra o menor valor dentro de um array de Double
	public Double menor(Double[] elementos, int quantidade){
		Double menor = elementos[0];
		for(int i = 1; i < quantidade; i ++){
			if(elementos[i] < menor){
				menor = elementos[i];
			}
		}
		return menor;
	}
	
	//Este método calcula a média de um conjunto de valores dado o conjunto e a quantidade
	public Double media(Double[] elementos, int quantidade){
		Double soma = 0.0;
		for(int i = 0; i < quantidade; i ++){
			soma += elementos[i];
		}
		return soma/quantidade;
	}
	
	//Este metodo calcula o desvio padrao dado a quantidade de elementos, os elementos em um vetor e a media
	public Double desvioPadrao(Double[] elementos, int quantidade, double media){
		Double desvioPadrao = 0.0;
		for(int i = 0; i < quantidade; i++){
			desvioPadrao += Math.pow(elementos[i] - media, 2);
		}
		return Math.sqrt(desvioPadrao/(quantidade-1));
	}
	
	// Este método efetua o algoritmo binary backoff
	public Double binaryBackoff(int quantidadeColisoes){
		Double random = Math.random();
		int i=1;
		if(quantidadeColisoes > 10){
			quantidadeColisoes = 10;
		}
		for(; random > i/Math.pow(2,quantidadeColisoes); i++);
		return (i-1)*Constantes.TEMPO_BINARY_BACKOFF;
	}
	
	// Este método deleta um determinado evento da lista de eventos
	public ArrayList<Evento> deletaEvento(ArrayList<Evento> listaEventos, int tipoEvento, Estacao estacao){
		for(int i = 0; i < listaEventos.size(); i++){
			if(listaEventos.get(i).getTipoEvento() == tipoEvento && listaEventos.get(i).getEstacao().getIdentificador() == estacao.getIdentificador()){
				listaEventos.remove(i);
				return listaEventos;
			}
		}
		System.out.println("ERRO: Não foi possível encontrar o Evento especificado.");
		System.exit(0);
		return null;
	}
	
	// Este método retorna o evento especificado da lista de eventos
	public Evento retornaEvento(ArrayList<Evento> listaEventos, int tipoEvento, Estacao estacao){
		for(int i = 0; i < listaEventos.size(); i++){
			if(listaEventos.get(i).getTipoEvento() == tipoEvento && listaEventos.get(i).getEstacao().getIdentificador() == estacao.getIdentificador()){
				return listaEventos.get(i);
			}
		}
		return null;
	}
	
	// Este método gera a quantidade de quadros paras as mensagens de uma determinada Estação
	public int geraQuantidadeQuadros(Estacao estacao){
		if(estacao.getQuantidadeQuadros() >=1 ){
			return estacao.getQuantidadeQuadros().intValue();
		}else if(estacao.getQuantidadeQuadros() > 0 ){
			return geraAmostraGeometrica(estacao.getQuantidadeQuadros());
		}else{
			System.out.println("ERRO: Quantidade de quadros não especificado para a Estação " + estacao.getIdentificador());
			System.exit(0);
			return 0;
		}
	}
	
	// Este método gera uma geométrica
	private int geraAmostraGeometrica(Double probabilidade){
		Double pdfGeometrica = 0.0;
		Double amostraUniforme = Math.random();
		int i = 0;
		for(; amostraUniforme > pdfGeometrica; i++){
			pdfGeometrica += Math.pow(1-probabilidade, i)*probabilidade;
		}
		return i;
	}
	
	// Este método gera o tempo de chegada (deterministico ou exponencial) da próxima mensagem da Estação especificada
	public Double geraProximaMensagem(Estacao estacao, Double tempoAtual){
		if(estacao.getTipoChegada() == Estacao.DETERMINISTICO){
			return tempoAtual + estacao.getIntervaloEntreChegadas();
		}else if(estacao.getTipoChegada() == Estacao.EXPONENCIAL){
			return gerarAmostraExponencial(estacao.getIntervaloEntreChegadas()) + tempoAtual;
		}else{
			System.out.println("ERRO: Tipo de chegada não especificado para a Estação " + estacao.getIdentificador());
			System.exit(0);
			return null;
		}
	}
	
	// Este método gera uma amostra exponencial
	private Double gerarAmostraExponencial(Double intervaloMedio){
		return new Double(-intervaloMedio*Math.log(1.0-Math.random()));
	}
	
	//Este método gera um evento de qualquer tipo
	public Evento geraEvento(int tipoEvento, Double tempoInicial, Estacao estacao, ArrayList<Estacao> estacoes, int rodada){
		if(tipoEvento == Evento.RETRANSMITIR){
			return new EventoRetransmitir(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.INICIA_TRANSMISSAO){
			return new EventoIniciaTransmissao(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.FIM_TRANSMISSAO){
			return new EventoFimTransmissao(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.INICIA_RECEPCAO){
			return new EventoIniciaRecepcao(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.FIM_RECEPCAO){
			return new EventoFimRecepcao(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.CHEGA_MENSAGEM){
			return new EventoChegaMensagem(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.FIM_MENSAGEM){
			return new EventoFimMensagem(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.COLISAO){
			return new EventoColisao(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.DESCARTA_QUADRO){
			return new EventoDescartaQuadro(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.PREPARA_TRANSMISSAO){
			return new EventoPrepararTransmissao(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.INTERROMPE_TRANSMISSAO){
			return new EventoInterrompeTransmissao(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.INICIA_TRANS_REFORCO){
			return new EventoIniciaTransReforco(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.FIM_TRANS_REFORCO){
			return new EventoFimTransReforco(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.INICIA_RECEP_REFORCO){
			return new EventoIniciaRecepReforco(tempoInicial,estacoes, estacao, rodada);
		}else if(tipoEvento == Evento.FIM_RECEP_REFORCO){
			return new EventoFimRecepReforco(tempoInicial,estacoes, estacao, rodada);
		}else{
			System.out.println("ERRO: O tipo de evento especificado para geração não existe.");
			System.exit(0);
			return null;
		}
	}
}
