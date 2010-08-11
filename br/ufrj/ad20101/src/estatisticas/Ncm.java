package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.evento.EventoFimTransmissao;
import br.ufrj.ad20101.src.evento.EventoPrepararTransmissao;

/*
 * Esta classe é responsável por calcular o Número Médio de Colisões
 * */

public class Ncm {
	
	//este flag indica que uma mensagem já foi iniciada e não foi finalizada
	boolean coletando = false;
	//guarda a quantidade de quadros da mensagem atual
	int quantidadeQuadros;
	//guarda a soma das colisões que aconteceram de cada quadro da mensagem atual
	int somaColisoes = 0;
	//guarda a quantidade de mensagens
	int quantidadeMensagens = 0;
	//total de colisoes dividido pela quantidade de quadros da mensagem atual
	Double numeroColisoesPorQuadro;
	//guarda a amostra, gerada até o momento, da esperança do número médio de colisões por quadro
	public Double amostra = 0.0;
	
	//Este método calcula tudo referente ao número médio de colisões
	public void coletar (Evento evento){
		if(evento.getTipoEvento() == Evento.PREPARA_TRANSMISSAO && !coletando){
			//Se este evento está gerando uma nova mensagem, pegar a quantidade de quadros dessa nova mensagem
			quantidadeQuadros = ((EventoPrepararTransmissao)evento).getQuantidadeQuadro();
			//seta o flag
			coletando = true;
		}else if(evento.getTipoEvento() == Evento.FIM_TRANSMISSAO && coletando){
			//Se o quadro foi transmitido com sucesso, então a quantidade de tentativas de transmiti-lo informa a quantidade de colisões
			somaColisoes += ((EventoFimTransmissao)evento).getQuantidadeTentativas() - 1; // menos 1, pois na primeira tentativa ainda não ocorreu uma colisão
		}else if (evento.getTipoEvento() == Evento.DESCARTA_QUADRO && coletando){
			//Se o quadro foi descartado, então ele também entra na conta
			somaColisoes += 16;
		}else if (evento.getTipoEvento() == Evento.FIM_MENSAGEM && coletando){
			//mais uma mensagem pode ser contabilizada
			quantidadeMensagens ++;
			//tira-se o número de colisões por quadro desta mensagem
			numeroColisoesPorQuadro = (double)somaColisoes/quantidadeQuadros;
			//calcula-se novamente a amostra
			amostra = amostra*(quantidadeMensagens-1) + numeroColisoesPorQuadro;
			amostra = amostra/quantidadeMensagens;
			//zera a somaColisoes, pois começará uma nova mensagem
			somaColisoes = 0;
			//desabilitao flag novamente
			coletando = false;
		}
	}
}
