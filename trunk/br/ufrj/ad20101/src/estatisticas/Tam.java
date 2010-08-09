package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.evento.EventoDescartaQuadro;
import br.ufrj.ad20101.src.evento.EventoIniciaTransmissao;

/*
 * Esta classe é responsável por calcular o Tempo de Acesso de uma Mensagem
 * */

public class Tam {
	//este flag indica que uma mensagem já foi iniciada e não foi finalizada
	boolean coletando = false;
	//este flag serve para indicar o último quadro da mensagem foi descartado e 
	//por este motivo esta mensagem deve ser desconsiderada das contas
	boolean descartado = false;
	//guarda o tempo em que o primeiro quadro foi considerado para transmissão
	Double tempoPrimeiroQuadro;
	//guarda o tempo em que o último quadro começou a ser transmitido com sucesso
	Double tempoUltimoQuadro;
	//intervalo: tempoPrimeiroQuadro - tempoUltimoQuadro
	Double intervaloPrimeiroUltimo;
	//guarda a quantidade de mensagens
	int quantidadeMensagens = 0;
	//guarda a amostra, gerada até o momento, da esperança do tempo de acesso de uma Mensagem
	Double amostra = 0.0;
	
	//Este método calcula tudo referente ao Tempo de Acesso de uma Mensagem
	public void coletar (Evento evento){
		if(evento.getTipoEvento() == Evento.PREPARA_TRANSMISSAO && !coletando){
			//Momento em que o primeiro quadro é considerado para transmissão
			tempoPrimeiroQuadro = evento.getTempoInicial();
			//seta o flag de descartado para false e o de coletando para true
			descartado = false;
			coletando = true;
		}else if(evento.getTipoEvento() == Evento.INICIA_TRANSMISSAO){
			//testa se este é o último quadro da mensagem
			if (((EventoIniciaTransmissao)evento).getQuantidadeQuadro() == 1){
				tempoUltimoQuadro = evento.getTempoInicial();
			}
		}else if(evento.getTipoEvento() == Evento.DESCARTA_QUADRO){
			//testa se este é o último quadro da mensagem
			if (((EventoDescartaQuadro)evento).getQuantidadeQuadro() == 1){
				descartado = true;
			}
		}else if(evento.getTipoEvento() == Evento.FIM_MENSAGEM && !descartado){
			//mais uma mensagem pode ser contabilizada
			quantidadeMensagens ++;
			//calcula o intervalo entre o primeiro quadro e o último
			intervaloPrimeiroUltimo = tempoUltimoQuadro - tempoPrimeiroQuadro;
			//calcula-se novamente a amostra
			amostra = amostra*(quantidadeMensagens-1) + intervaloPrimeiroUltimo;
			amostra = amostra/quantidadeMensagens;
			coletando = false;
		}
	}
}
