package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.evento.Evento;

/*
 * Esta classe é responsável por calcular a Vazão
 * */

public class Vazao {
	
	//este flag indica se a coleta já começou a ser feita
	boolean coletando = false;
	//guarda o tempo inicial da coleta
	Double tempoInicial;
	//guarda o tempo final da coleta
	Double tempoFinal;
	//guarda a quantidade de quadros transmitidos com sucesso
	int quantidadeQuadros = 0;
	//guarda uma amostra da vazao
	public Double amostra;
	
	//Este método calcula tudo referente à Vazão
	public void coletar (Evento evento){
		//se ainda não havia começado a coleta, começar agora
		if(coletando){
			//se está coletando, atualizar o tempo final da coleta
			tempoFinal = evento.getTempoInicial();
			//calcula-se avazão neste instante
			amostra = quantidadeQuadros/(tempoFinal - tempoInicial);
		}else{
			//setar tempo inicial da coleta
			tempoInicial = evento.getTempoInicial();
			//setar flag que indica que está coletando
			coletando = true;
		}
		if(evento.getTipoEvento() == Evento.FIM_TRANSMISSAO){
			//quadro transmitido com sucesso detectado, incrementa o contador
			quantidadeQuadros ++;
		}
	}
}
