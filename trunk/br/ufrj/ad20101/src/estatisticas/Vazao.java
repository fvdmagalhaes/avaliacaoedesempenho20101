package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.evento.Evento;

/*
 * Esta classe � respons�vel por calcular a Vaz�o
 * */

public class Vazao {
	
	//este flag indica se a coleta j� come�ou a ser feita
	boolean coletando = false;
	//guarda o tempo inicial da coleta
	Double tempoInicial;
	//guarda o tempo final da coleta
	Double tempoFinal;
	//guarda a quantidade de quadros transmitidos com sucesso
	int quantidadeQuadros = 0;
	//guarda uma amostra da vazao
	public Double amostra;
	
	//Este m�todo calcula tudo referente � Vaz�o
	public void coletar (Evento evento){
		//se ainda n�o havia come�ado a coleta, come�ar agora
		if(coletando){
			//se est� coletando, atualizar o tempo final da coleta
			tempoFinal = evento.getTempoInicial();
			//calcula-se avaz�o neste instante
			amostra = quantidadeQuadros/(tempoFinal - tempoInicial);
		}else{
			//setar tempo inicial da coleta
			tempoInicial = evento.getTempoInicial();
			//setar flag que indica que est� coletando
			coletando = true;
		}
		if(evento.getTipoEvento() == Evento.FIM_TRANSMISSAO){
			//quadro transmitido com sucesso detectado, incrementa o contador
			quantidadeQuadros ++;
		}
	}
}
