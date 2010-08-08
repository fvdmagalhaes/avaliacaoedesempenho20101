package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.servicos.Constantes;

/*
 * Esta classe � respons�vel por calcular o Tempo de Acesso de um Quadro
 * */

public class Tap {
	
	//este flag indica que um quadro j� foi iniciado e n�o foi terminado
	boolean coletando = false;
	//guarda o tempo em que o quadro foi considerado para transmiss�o
	Double tempoInicioQuadro;
	//guarda o tempo em que o quadro come�ou a ser transmitido com sucesso
	Double tempoFimQuadro;
	//intervalo: tempoPrimeiroQuadro - tempoUltimoQuadro
	Double intervaloInicioFim;
	//guarda a quantidade de mensagens
	int quantidadeQuadros = 0;
	//guarda a amostra, gerada at� o momento, da esperan�a do tempo de acesso de uma Mensagem
	Double amostra = 0.0;
	
	//Este m�todo calcula tudo referente ao Tempo de Acesso de um Quadro
	public void coletar (Evento evento){
		if((evento.getTipoEvento() == Evento.PREPARA_TRANSMISSAO || evento.getTipoEvento() == Evento.INICIA_TRANSMISSAO)&& !coletando){
			//Momento em que o quadro � considerado para transmiss�o
			tempoInicioQuadro = evento.getTempoInicial();
			//seta o flag de coletando para true
			coletando = true;
		}else if(evento.getTipoEvento() == Evento.FIM_TRANSMISSAO){
			//quadro transmitido com sucesso, subtrai o tempo do tempo do quadro no enlace e descobre-se o 
			//tempo em que ele come�ou a ser transmitido com sucesso
			tempoFimQuadro = evento.getTempoInicial() - Constantes.TEMPO_QUADRO_ENLACE;
			//mais um quadro pode ser contabilizada
			quantidadeQuadros ++;
			//calcula o intervalo entre o primeiro quadro e o �ltimo
			intervaloInicioFim = tempoFimQuadro - tempoInicioQuadro;
			//calcula-se novamente a amostra
			amostra = amostra*(quantidadeQuadros-1) + intervaloInicioFim;
			amostra = amostra/quantidadeQuadros;
			coletando = false;
		}else if(evento.getTipoEvento() == Evento.DESCARTA_QUADRO){
			//se o quadro foi descartado, ent�o n�o coleta mais nada dele
			coletando = false;
		}
	}
}
