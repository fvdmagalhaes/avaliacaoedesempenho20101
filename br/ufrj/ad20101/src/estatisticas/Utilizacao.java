package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.estacao.Estacao;

/*
 * Esta classe é responsável por calcular a Utilização do Ethernet
 * */

public class Utilizacao {
	
	//guarda o tempo em que o meio esteve ocioso
	Double tempoOcioso = 0.0;
	//guarda o tempo em que o meio esteve ocupado
	Double tempoOcupado = 0.0;
	//marca o inicio do intervalo em que o meio esteve ocioso
	Double inicioIntervaloOcioso = 0.0;
	//marca o fim do intervalo em que o meio esteve ocioso
	Double fimIntervaloOcioso = 0.0;
	//marca o inicio do intervalo em que o meio esteve ocupado
	Double inicioIntervaloOcupado;
	//marca o fim do intervalo em que o meio esteve ocupado
	Double fimIntervaloOcupado;
	//flag que indica se o meio está ocupado
	boolean ocupado = false;
	//guarda a utilização de fato
	//Sempre atualizada ao fim de um intervalo (ocupado ou ocioso)
	Double utilizacao;
	
	//Este método calcula tudo referente à Utilização do Ethernet
	public void coletar (Estacao estacao, Double tempoAtual){
		if((estacao.getEstado() == Estacao.ESTADO_RECEBENDO || estacao.getEstado() == Estacao.ESTADO_TRANSFERINDO || estacao.getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO)){
			//caso entre aqui o meio está ocupado
			//este if testa se o meio não estava ocupado antes
			if(!ocupado){
				//se antes o meio estava ocioso, então dá-se início a um novo intervalo ocupado
				inicioIntervaloOcupado = tempoAtual;
				//como agora o meio está ocupado, dá-se fim ao intervalo ocioso
				fimIntervaloOcioso = tempoAtual;
				//acrescenta o intervalo ocioso ao tempo ocioso total
				tempoOcioso += fimIntervaloOcioso - inicioIntervaloOcioso;
				//seta o flag de meio ocupado
				ocupado = true;
				//calcula-se a utilização
				utilizacao = tempoOcupado/(tempoOcupado + tempoOcioso);
			}
		}else{
			//caso entre aqui o meio está ocioso
			//este if testa se o meio estava ocupado antes
			if(ocupado){
				//se antes o meio estava ocupado, então dá-se início a um novo intervalo ocioso
				inicioIntervaloOcioso = tempoAtual;
				//como agora o meio está ocioso, dá-se fim ao intervalo ocupado
				fimIntervaloOcupado = tempoAtual;
				//acrescenta o intervalo ocupado ao tempo ocupado total
				tempoOcupado += fimIntervaloOcupado - inicioIntervaloOcupado;
				//seta o flag de meio ocupado
				ocupado = false;
				//calcula-se a utilização
				utilizacao = tempoOcupado/(tempoOcupado + tempoOcioso);
			}
		}
		if(tempoAtual > 800000){
			tempoAtual = tempoAtual;
		}
	}
}