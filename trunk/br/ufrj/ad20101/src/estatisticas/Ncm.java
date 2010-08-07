package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.evento.EventoFimTransmissao;
import br.ufrj.ad20101.src.evento.EventoPrepararTransmissao;

/*
 * Esta classe � respons�vel por calcular o N�mero M�dio de Colis�es
 * */

public class Ncm {
	
	//guarda a quantidade de quadros da mensagem atual
	int quantidadeQuadros;
	//guarda a soma das colis�es que aconteceram de cada quadro da mensagem atual
	int somaColisoes = 0;
	//guarda a quantidade de mensagens
	int quantidadeMensagens = 0;
	//total de colisoes dividido pela quantidade de quadros da mensagem atual
	Double numeroColisoesPorQuadro;
	//guarda a amostra gerada at� o momento da esperan�a do n�mero m�dio de colis�es por quadro
	Double amostra = 0.0;
	
	//Este m�todo calcula tudo referente ao n�mero m�dio de colis�es
	public void coletar (Evento evento){
		if(evento.getTipoEvento() == Evento.PREPARA_TRANSMISSAO){
			//Se este evento est� gerando uma nova mensagem, pegar a quantidade de quadros dessa nova mensagem
			quantidadeQuadros = ((EventoPrepararTransmissao)evento).getQuantidadeQuadro();
		}else if(evento.getTipoEvento() == Evento.FIM_TRANSMISSAO){
			//Se o quadro foi transmitido com sucesso, ent�o a quantidade de tentativas de transmiti-lo informa a quantidade de colis�es
			somaColisoes += ((EventoFimTransmissao)evento).getQuantidadeTentativas() - 1; // menos 1, pois na primeira tentativa ainda n�o ocorreu uma colis�o
		}else if (evento.getTipoEvento() == Evento.FIM_TRANSMISSAO){
			//Se o quadro foi descartado, ent�o ele tamb�m entra na conta
			somaColisoes += 16;
		}else if (evento.getTipoEvento() == Evento.FIM_MENSAGEM){
			//mais uma mensagem pode ser contabilizada
			quantidadeMensagens ++;
			//tira-se o n�mero de colis�es por quadro desta mensagem
			numeroColisoesPorQuadro = (double)somaColisoes/quantidadeQuadros;
			//calcula-se novamente a amostra
			amostra = amostra*(quantidadeMensagens-1) + numeroColisoesPorQuadro;
			amostra = amostra/quantidadeMensagens;
			//zera a somaColisoes, pois come�ar� uma nova mensagem
			somaColisoes = 0;
		}
	}
}
