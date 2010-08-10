package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.estacao.Estacao;

/*
 * Esta classe � respons�vel por calcular a Utiliza��o do Ethernet
 * */

public class Utilizacao {
	
	//este flag serve apenas para pegar o tempo inicial da coleta
	boolean coletando = false;
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
	public Double amostra = 0.0;
	//flag indica se a fase transiente ainda n�o chegou ao fim
	boolean faseTransiente = true;
	
	//Este m�todo calcula tudo referente � Utiliza��o do Ethernet
	public void coletar (Estacao estacao, Double tempoAtual){
		if(!coletando){
			//setar tempo inicial da coleta
			inicioIntervaloOcioso = inicioIntervaloOcupado = tempoAtual;
			//setar flag que indica que est� coletando
			coletando = true;
		}
		if((estacao.getEstado() == Estacao.ESTADO_RECEBENDO || estacao.getEstado() == Estacao.ESTADO_TRANSFERINDO || estacao.getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO)){
			//caso entre aqui o meio est� ocupado
			//este if testa se o meio n�o estava ocupado antes
			if(!ocupado){
				//se antes o meio estava ocioso, ent�o d�-se in�cio a um novo intervalo ocupado
				inicioIntervaloOcupado = tempoAtual;
				//como agora o meio est� ocupado, d�-se fim ao intervalo ocioso
				fimIntervaloOcioso = tempoAtual;
				//seta o flag de meio ocupado
				ocupado = true;
				//acrescenta o intervalo ocioso ao tempo ocioso total
				tempoOcioso += fimIntervaloOcioso - inicioIntervaloOcioso;
				//testa se a fase transiente chegou ao fim
				if(faseTransiente){
					if(amostra != 0){
						if(amostra - tempoOcupado/(tempoOcupado + tempoOcioso) < 0.01*amostra && amostra - tempoOcupado/(tempoOcupado + tempoOcioso) > -0.01*amostra){
							faseTransiente = false;
						}
					}
				}
				//calcula-se a utiliza��o
				amostra = tempoOcupado/(tempoOcupado + tempoOcioso);
			}
		}else{
			//caso entre aqui o meio est� ocioso
			//este if testa se o meio estava ocupado antes
			if(ocupado){
				//se antes o meio estava ocupado, ent�o d�-se in�cio a um novo intervalo ocioso
				inicioIntervaloOcioso = tempoAtual;
				//como agora o meio est� ocioso, d�-se fim ao intervalo ocupado
				fimIntervaloOcupado = tempoAtual;
				//seta o flag de meio ocupado
				ocupado = false;
				//acrescenta o intervalo ocupado ao tempo ocupado total
				tempoOcupado += fimIntervaloOcupado - inicioIntervaloOcupado;
				//testa se a fase transiente chegou ao fim
				if(faseTransiente){
					if(amostra - tempoOcupado/(tempoOcupado + tempoOcioso) < 0.01*amostra && amostra - tempoOcupado/(tempoOcupado + tempoOcioso) > -0.01*amostra){
						faseTransiente = false;
					}
				}
				//calcula-se a utiliza��o
				amostra = tempoOcupado/(tempoOcupado + tempoOcioso);
			}
		}
	}
}