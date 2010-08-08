package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.estacao.Estacao;

/*
 * Esta classe � respons�vel por calcular a Utiliza��o do Ethernet
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
	//flag que indica se o meio est� ocupado
	boolean ocupado = false;
	//guarda a utiliza��o de fato
	//Sempre atualizada ao fim de um intervalo (ocupado ou ocioso)
	Double utilizacao;
	
	//Este m�todo calcula tudo referente � Utiliza��o do Ethernet
	public void coletar (Estacao estacao, Double tempoAtual){
		if((estacao.getEstado() == Estacao.ESTADO_RECEBENDO || estacao.getEstado() == Estacao.ESTADO_TRANSFERINDO || estacao.getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO)){
			//caso entre aqui o meio est� ocupado
			//este if testa se o meio n�o estava ocupado antes
			if(!ocupado){
				//se antes o meio estava ocioso, ent�o d�-se in�cio a um novo intervalo ocupado
				inicioIntervaloOcupado = tempoAtual;
				//como agora o meio est� ocupado, d�-se fim ao intervalo ocioso
				fimIntervaloOcioso = tempoAtual;
				//acrescenta o intervalo ocioso ao tempo ocioso total
				tempoOcioso += fimIntervaloOcioso - inicioIntervaloOcioso;
				//seta o flag de meio ocupado
				ocupado = true;
				//calcula-se a utiliza��o
				utilizacao = tempoOcupado/(tempoOcupado + tempoOcioso);
			}
		}else{
			//caso entre aqui o meio est� ocioso
			//este if testa se o meio estava ocupado antes
			if(ocupado){
				//se antes o meio estava ocupado, ent�o d�-se in�cio a um novo intervalo ocioso
				inicioIntervaloOcioso = tempoAtual;
				//como agora o meio est� ocioso, d�-se fim ao intervalo ocupado
				fimIntervaloOcupado = tempoAtual;
				//acrescenta o intervalo ocupado ao tempo ocupado total
				tempoOcupado += fimIntervaloOcupado - inicioIntervaloOcupado;
				//seta o flag de meio ocupado
				ocupado = false;
				//calcula-se a utiliza��o
				utilizacao = tempoOcupado/(tempoOcupado + tempoOcioso);
			}
		}
		if(tempoAtual > 800000){
			tempoAtual = tempoAtual;
		}
	}
}