package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.servicos.Constantes;

/*
 * Esta classe é responsável por calcular o Tempo de Acesso de um Quadro
 * */

public class Tap {
	
	//este flag indica que um quadro já foi iniciado e não foi terminado
	boolean coletando = false;
	//guarda o tempo em que o quadro foi considerado para transmissão
	Double tempoInicioQuadro;
	//guarda o tempo em que o quadro começou a ser transmitido com sucesso
	Double tempoFimQuadro;
	//intervalo: tempoPrimeiroQuadro - tempoUltimoQuadro
	Double intervaloInicioFim;
	//guarda a quantidade de mensagens
	int quantidadeQuadros = 0;
	//guarda a amostra, gerada até o momento, da esperança do tempo de acesso de uma Mensagem
	Double amostra = 0.0;
	
	//Este método calcula tudo referente ao Tempo de Acesso de um Quadro
	public void coletar (Evento evento){
		if((evento.getTipoEvento() == Evento.PREPARA_TRANSMISSAO || evento.getTipoEvento() == Evento.INICIA_TRANSMISSAO)&& !coletando){
			//Momento em que o quadro é considerado para transmissão
			tempoInicioQuadro = evento.getTempoInicial();
			//seta o flag de coletando para true
			coletando = true;
		}else if(evento.getTipoEvento() == Evento.FIM_TRANSMISSAO){
			//quadro transmitido com sucesso, subtrai o tempo do tempo do quadro no enlace e descobre-se o 
			//tempo em que ele começou a ser transmitido com sucesso
			tempoFimQuadro = evento.getTempoInicial() - Constantes.TEMPO_QUADRO_ENLACE;
			//mais um quadro pode ser contabilizada
			quantidadeQuadros ++;
			//calcula o intervalo entre o primeiro quadro e o último
			intervaloInicioFim = tempoFimQuadro - tempoInicioQuadro;
			//calcula-se novamente a amostra
			amostra = amostra*(quantidadeQuadros-1) + intervaloInicioFim;
			amostra = amostra/quantidadeQuadros;
			coletando = false;
		}else if(evento.getTipoEvento() == Evento.DESCARTA_QUADRO){
			//se o quadro foi descartado, então não coleta mais nada dele
			coletando = false;
		}
	}
}
