package br.ufrj.ad20101.src.simulador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.estatisticas.ColetaEstatistica;
import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.servicos.Servicos;

/*
 * Esta Classe representa o simulador no modo Debug.
 * Utilizada para as corre��es.
 * Log em arquivo de texto (logSimulador.txt) na pasta ra�z do projeto.
 * */

public class SimuladorDebug {
	
	// Define o arquivo de texto aonde sera gravado o resultado do log
	static File arquivoLog = new File("logSimulador.txt");
	static FileOutputStream fos;
	
	//tamanho da rodada foi definido como 1 minutos e meio (90 segundos)
	final static Double tamanhoRodada = 90000.0; 
	
	// Flag que indica a classe simulador se o modo de grava��o de log est� ativo ou n�o
	private static boolean isDebbuging = false;
	
	//arraylist que cont�m a lista de eventos do simulador
	private ArrayList<Evento> listaEventos = new ArrayList<Evento>();
	
	//arrays que guardar�o as amostras geradas em cada rodada
	Double[][] amostrasTap = new Double[4][100];
	Double[][] amostrasTam = new Double[4][100];
	Double[][] amostrasNcm = new Double[4][100];
	Double[][] amostrasVazao = new Double[4][100];
	Double[] amostrasUtilizacao = new Double[100];
	
	//M�todo que inicia o simulador
	public void start(){
		//instancia a classe que vai calcular todas as estat�sticas solicitadas
		ColetaEstatistica coletaEstatistica = new ColetaEstatistica();
		coletaEstatistica.setFaseTransiente(true);
		//armazena o evento atual
		Evento eventoCorrente = null;
		//este flag informa se todos os intervalos est�o dentro do desejado, ou seja, se a simula��o deve ser encerrada
		boolean fimSimulacao = false;
		//este flag indica se a fase transiente ainda n�o terminou
		boolean faseTransiente = true;
		//esta vari�vel guarda o valor da rodada atual
		int rodada = 0;
		//esta vari�vel guarda o tempo inicial da rodada
		Double tempoInicioRodada = 0.0;
		
		//primeiramente o simulador roda a fase transiente para desprezar os dados gerados nela
		while(faseTransiente){
			
			//A lista de Eventos � ordenada por tempo do menor para o maior
			Collections.sort(this.listaEventos);
			//O primeiro Evento da lista, ou seja, aquele que deve acontecer antes de todos os outros � executado
			this.listaEventos = this.listaEventos.get(0).acao(this.listaEventos);
			//em seguida ele � retirado da lista de Eventos
			eventoCorrente = this.listaEventos.remove(0);
			//e, por �ltimo, usado no c�lculo das estat�sticas
			coletaEstatistica.coletar(eventoCorrente, rodada);
			//verifica se a fase transiente chegou ao fim
			faseTransiente = coletaEstatistica.isFaseTransiente();
		
		}
		
		//com o fim da fase transiente, come�a a coleta das estat�sticas
		//enquanto algum dos intervalos n�o estiver dentro do desejado, a simula��o continua
		while(!fimSimulacao){
			
			//in�cio da pr�xima rodada
			rodada ++;
			//seta inicial da rodada
			tempoInicioRodada = eventoCorrente.getTempoInicial();
			//a cada nova rodada as estatisticas geradas devem ser zeradas
			coletaEstatistica = new ColetaEstatistica();
			
			//este loop s� acaba quando a rodada corrente chega ao final
			do{
				
				//A lista de Eventos � ordenada por tempo do menor para o maior
				Collections.sort(this.listaEventos);
				//toda nova chegada que acontecer durante essa rodada deve ter o c�digo dela
				if(listaEventos.get(0).getTipoEvento() == Evento.CHEGA_MENSAGEM){
					listaEventos.get(0).setRodada(rodada);
				}
				//O primeiro Evento da lista, ou seja, aquele que deve acontecer antes de todos os outros � executado
				this.listaEventos = this.listaEventos.get(0).acao(this.listaEventos);
				//em seguida ele � retirado da lista de Eventos
				eventoCorrente = this.listaEventos.remove(0);
				//e, por �ltimo, usado no c�lculo das estat�sticas
				coletaEstatistica.coletar(eventoCorrente, rodada);
			
			}while(eventoCorrente.getTempoInicial() - tempoInicioRodada < tamanhoRodada);
			
			//armazena todas as amostras geradas
			for(int i = 0; i < 4; i ++){
				
				amostrasTap[i][rodada-1] = coletaEstatistica.getTap()[i].amostra;
				amostrasTam[i][rodada-1] = coletaEstatistica.getTam()[i].amostra;
				amostrasNcm[i][rodada-1] = coletaEstatistica.getNcm()[i].amostra;
				amostrasVazao[i][rodada-1] = coletaEstatistica.getVazao()[i].amostra;
			
			}
			amostrasUtilizacao[rodada-1] = coletaEstatistica.getUtilizacao().amostra;
			
			//testa se deve calcular o intervalo de confian�a para ver se est� de acordo com o pedido
			if(rodada >= 10){
				
				fimSimulacao = calculaIntervalo (rodada, eventoCorrente.getEstacoes());
			
			}
		}
		//chama o m�todo que imprime as estat�sticas no console
		imprimeResultado(rodada, eventoCorrente.getEstacoes());
	}
	
	
	//este m�todo imprime na tela todas as informa��es coletada
	private void imprimeResultado(int rodadas, ArrayList<Estacao> estacoes){
		
		//instancia a classe de servi�os
		Servicos servicos = new Servicos();
		//vari�vel que guarda a m�dia das amostras
		Double media;
		//vari�vel que guarda o desvio padrao das amostras
		Double desvioPadrao;
		//vari�vel que guarda o limite superior do intervalo de confian�a
		Double limiteSuperior;
		//vari�vel que guarda o limite inferior do intervalo de confian�a
		Double limiteInferior;
		//variavel que guarda o maior valor entre as amostras
		Double maiorAmostra;
		//variavel que guarda o menos valor entre as amostras
		Double menorAmostra;
		
		//primeiro imprimir a utiliza��o que � �nica para o cen�rio
		System.out.println("\nDADOS ESTAT�STICOS\n");
		System.out.println(">>Quantidade de Rodadas: " + rodadas + "\n");
		System.out.println("Utiliza��o do Ethernet:");
		media = servicos.media(amostrasUtilizacao, rodadas);
		System.out.printf("-> M�dia: %.10f\n", media);
		desvioPadrao = servicos.desvioPadrao(amostrasUtilizacao, rodadas, media);
		System.out.printf("-> Desvio Padr�o: %.10f\n", desvioPadrao);
		System.out.printf("-> Vari�ncia: %.10f\n", Math.pow(desvioPadrao, 2));
		limiteSuperior = media + 1.96*desvioPadrao/Math.sqrt(rodadas);
		limiteInferior = media - 1.96*desvioPadrao/Math.sqrt(rodadas);
		System.out.printf("-> Intervalo de Confina�a: [ %.10f , %.10f ]\n", limiteInferior, limiteSuperior);
		maiorAmostra = servicos.maior(amostrasUtilizacao, rodadas);
		System.out.printf("-> Maior Amostra: %.10f\n", maiorAmostra);
		menorAmostra = servicos.menor(amostrasUtilizacao, rodadas);
		System.out.printf("-> Menor Amostra: %.10f\n\n", menorAmostra);
		
		//percorrer todas as esta��es que tiveram participa��o no cen�rio criado
		for(int i = 0; i <4; i ++){
			if(estacoes.get(i).getTipoChegada() != 0){
				System.out.println("Esta��o " + (i+1));
				
				//imprimir dados das amostras de TAp
				System.out.println("TAp:");
				media = servicos.media(amostrasTap[i], rodadas);
				System.out.printf("-> M�dia: %.10f\n", media);
				desvioPadrao = servicos.desvioPadrao(amostrasTap[i], rodadas, media);
				System.out.printf("-> Desvio Padr�o: %.10f\n", desvioPadrao);
				System.out.printf("-> Vari�ncia: %.10f\n", Math.pow(desvioPadrao, 2));
				limiteSuperior = media + 1.96*desvioPadrao/Math.sqrt(rodadas);
				limiteInferior = media - 1.96*desvioPadrao/Math.sqrt(rodadas);
				System.out.printf("-> Intervalo de Confina�a: [ %.10f , %.10f ]\n", limiteInferior, limiteSuperior);
				maiorAmostra = servicos.maior(amostrasTap[i], rodadas);
				System.out.printf("-> Maior Amostra: %.10f\n", maiorAmostra);
				menorAmostra = servicos.menor(amostrasTap[i], rodadas);
				System.out.printf("-> Menor Amostra: %.10f\n\n", menorAmostra);
				
				//imprimir dados das amostras de TAm
				System.out.println("TAm:");
				media = servicos.media(amostrasTam[i], rodadas);
				System.out.printf("-> M�dia: %.10f\n", media);
				desvioPadrao = servicos.desvioPadrao(amostrasTam[i], rodadas, media);
				System.out.printf("-> Desvio Padr�o: %.10f\n", desvioPadrao);
				System.out.printf("-> Vari�ncia: %.10f\n", Math.pow(desvioPadrao, 2));
				limiteSuperior = media + 1.96*desvioPadrao/Math.sqrt(rodadas);
				limiteInferior = media - 1.96*desvioPadrao/Math.sqrt(rodadas);
				System.out.printf("-> Intervalo de Confina�a: [ %.10f , %.10f ]\n", limiteInferior, limiteSuperior);
				maiorAmostra = servicos.maior(amostrasTam[i], rodadas);
				System.out.printf("-> Maior Amostra: %.10f\n", maiorAmostra);
				menorAmostra = servicos.menor(amostrasTam[i], rodadas);
				System.out.printf("-> Menor Amostra: %.10f\n\n", menorAmostra);
				
				//imprimir dados das amostras de NCm
				System.out.println("NCm:");
				media = servicos.media(amostrasNcm[i], rodadas);
				System.out.printf("-> M�dia: %.10f\n", media);
				desvioPadrao = servicos.desvioPadrao(amostrasNcm[i], rodadas, media);
				System.out.printf("-> Desvio Padr�o: %.10f\n", desvioPadrao);
				System.out.printf("-> Vari�ncia: %.10f\n", Math.pow(desvioPadrao, 2));
				limiteSuperior = media + 1.96*desvioPadrao/Math.sqrt(rodadas);
				limiteInferior = media - 1.96*desvioPadrao/Math.sqrt(rodadas);
				System.out.printf("-> Intervalo de Confina�a: [ %.10f , %.10f ]\n", limiteInferior, limiteSuperior);
				maiorAmostra = servicos.maior(amostrasNcm[i], rodadas);
				System.out.printf("-> Maior Amostra: %.10f\n", maiorAmostra);
				menorAmostra = servicos.menor(amostrasNcm[i], rodadas);
				System.out.printf("-> Menor Amostra: %.10f\n\n", menorAmostra);
				
				//imprimir dados das amostras de Vaz�o
				System.out.println("Vaz�o:");
				media = servicos.media(amostrasVazao[i], rodadas);
				System.out.printf("-> M�dia: %.10f\n", media);
				desvioPadrao = servicos.desvioPadrao(amostrasVazao[i], rodadas, media);
				System.out.printf("-> Desvio Padr�o: %.10f\n", desvioPadrao);
				System.out.printf("-> Vari�ncia: %.10f\n", Math.pow(desvioPadrao, 2));
				limiteSuperior = media + 1.96*desvioPadrao/Math.sqrt(rodadas);
				limiteInferior = media - 1.96*desvioPadrao/Math.sqrt(rodadas);
				System.out.printf("-> Intervalo de Confina�a: [ %.10f , %.010f ]\n", limiteInferior, limiteSuperior);
				maiorAmostra = servicos.maior(amostrasVazao[i], rodadas);
				System.out.printf("-> Maior Amostra: %.10f\n", maiorAmostra);
				menorAmostra = servicos.menor(amostrasVazao[i], rodadas);
				System.out.printf("-> Menor Amostra: %.10f\n\n", menorAmostra);
			}
		}
		System.out.println("FIM DAS ESTAT�STICAS");
		encerraLog();
	}
	
	
	//este m�todo calcula os intervalos de confian�a e verifica se est�o de acordo com o desejado
	private boolean calculaIntervalo (int rodadas, ArrayList<Estacao> estacoes){
		
		Servicos servicos = new Servicos();
		//media da utilizacao
		Double media = servicos.media(amostrasUtilizacao, rodadas);
		//largura do intervalo de confian�a da utilizacao
		Double intervalo = 2*1.96*servicos.desvioPadrao(amostrasUtilizacao, rodadas, media)/Math.sqrt(rodadas);
		if(intervalo <= 0.1*media){
			for(int i = 0; i < 4; i ++){
				if(estacoes.get(i).getTipoChegada() != 0){
					//media do tap
					media = servicos.media(amostrasTap[i], rodadas);
					//largura do intervalo de confian�a do tap
					intervalo = 2*1.96*servicos.desvioPadrao(amostrasTap[i], rodadas, media)/Math.sqrt(rodadas);
					if(intervalo > 0.1*media){
						return false;
					}
					
					//media do tam
					media = servicos.media(amostrasTam[i], rodadas);
					//largura do intervalo de confian�a do tam
					intervalo = 2*1.96*servicos.desvioPadrao(amostrasTam[i], rodadas, media)/Math.sqrt(rodadas);
					if(intervalo > 0.1*media){
						return false;
					}
					
					//media do ncm
					media = servicos.media(amostrasNcm[i], rodadas);
					//largura do intervalo de confian�a do ncm
					intervalo = 2*1.96*servicos.desvioPadrao(amostrasNcm[i], rodadas, media)/Math.sqrt(rodadas);
					if(intervalo > 0.1*media){
						return false;
					}
					
					//media da vaz�o
					media = servicos.media(amostrasVazao[i], rodadas);
					//largura do intervalo de confian�a da vaz�o
					intervalo = 2*1.96*servicos.desvioPadrao(amostrasVazao[i], rodadas, media)/Math.sqrt(rodadas);
					if(intervalo > 0.1*media){
						return false;
					}
				}
			}
		}else{
			return false;
		}
		return true;
	}
	
	
	public void setListaEventos(ArrayList<Evento> listaEventos) {
		this.listaEventos = listaEventos;
	}

	public ArrayList<Evento> getListaEventos() {
		return listaEventos;
	}
	
	public static boolean isDebbuging() {
		return isDebbuging;
	}

	public void setDebbuging(boolean isDebbuging) {
		SimuladorDebug.isDebbuging = isDebbuging;
	}
	
	/* Cria um buffer respons�vel pela escrita no arquivo de log. O par�metro false indica que o arquivo de 
	 * texto, se existir, ser� sobrescrito ao gerar um novo log
	 * */		
	public SimuladorDebug() 
	{
		try {
			fos = new FileOutputStream(arquivoLog,false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}

	/*
	 * Cada evento chama esta fun��o EscreveLog passando a mensagem que dever� ser inserida no log.
	 * Simplesmente utiliza o buffer "fos" de escrita do arquivo criado anteriormente e escreve a mensagem
	 * no arquivo.
	 */
	public static void escreveLog(String mensagem)
	{		
		try 
		{
			fos.write(mensagem.getBytes());			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * A fun��o encerraLog � respons�vel apenas por fechar o arquivo. � invocada no final da simula��o,
	 * quando n�o h� mais nada a ser escrito no arquivo.
	 */
	public void encerraLog()
	{
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
