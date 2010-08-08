package br.ufrj.ad20101.src.simulador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import br.ufrj.ad20101.src.estatisticas.ColetaEstatistica;
import br.ufrj.ad20101.src.evento.Evento;

/*
 * Esta Classe representa o simulador no modo Debug.
 * Utilizada para as corre��es.
 * Log em arquivo de texto (logSimulador.txt) na pasta ra�z do projeto.
 * */

public class SimuladorDebug {
	
	//Esta vari�vel informa o tempo que durar� a simula��o em miliSegundos.
	//Ela n�o deve ter um valor muito alto, pois esta classe usa escrita em arquivo,
	//portanto pode demorar demais para terminar a simula��o. 
	private Double tempoSimulacao = 4000000.0;
	
	// Define o arquivo de texto aonde sera gravado o resultado do log
	static File arquivoLog = new File("logSimulador.txt");
	static FileOutputStream fos;
	
	// Flag que indica a classe simulador se o modo de grava��o de log est� ativo ou n�o
	private static boolean isDebbuging = false;

	public static boolean isDebbuging() {
		return isDebbuging;
	}

	public void setDebbuging(boolean isDebbuging) {
		SimuladorDebug.isDebbuging = isDebbuging;
	}

	private ArrayList<Evento> listaEventos = new ArrayList<Evento>();
	
	//M�todo que inicia o simulador
	public void start(){
		//TODO ISSO N�O VAI FICAR AQUI!!!!
		ColetaEstatistica coletaEstatistica = new ColetaEstatistica();
		//Simulador funcionar� por um determinado tempo indicado na vari�vel "tempoSimulacao"
		while(this.listaEventos.get(0).getTempoInicial() < tempoSimulacao){
			//A lista de Eventos � ordenada por tempo do menor para o maior
			Collections.sort(this.listaEventos);
			//O primeiro Evento da lista, ou seja, aquele que deve acontecer antes de todos os outros � executado
			this.listaEventos = this.listaEventos.get(0).acao(this.listaEventos);
			//TODO ISSO N�O VAI FICAR AQUI!!!!
			coletaEstatistica.coletar(listaEventos.get(0));
			//em seguida ele � retirado da lista de Eventos
			this.listaEventos.remove(0);
		}
		//apenas informa que a simula��o chegou ao fim
		System.out.println("Fim da simula��o");
	}
	
	public void setListaEventos(ArrayList<Evento> listaEventos) {
		this.listaEventos = listaEventos;
	}

	public ArrayList<Evento> getListaEventos() {
		return listaEventos;
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
