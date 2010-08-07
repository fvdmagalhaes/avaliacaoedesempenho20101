package br.ufrj.ad20101.src.simulador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import br.ufrj.ad20101.src.evento.Evento;

/*
 * Esta Classe representa o simulador no modo Debug.
 * Utilizada para as correções.
 * Log em arquivo de texto (logSimulador.txt) na pasta raíz do projeto.
 * */

public class SimuladorDebug {
	
	//Esta variável informa o tempo que durará a simulação em miliSegundos.
	//Ela não deve ter um valor muito alto, pois esta classe usa escrita em arquivo,
	//portanto pode demorar demais para terminar a simulação. 
	private Double tempoSimulacao = 4000.0;
	
	static File arquivoLog = new File("logSimulador.txt");
	static FileOutputStream fos;
	private static boolean isDebbuging = false;

	public static boolean isDebbuging() {
		return isDebbuging;
	}

	public void setDebbuging(boolean isDebbuging) {
		SimuladorDebug.isDebbuging = isDebbuging;
	}

	private ArrayList<Evento> listaEventos = new ArrayList<Evento>();
	
	//Método que inicia o simulador
	public void start(){
		//Simulador funcionará por um determinado tempo indicado na variável "tempoSimulacao"
		while(this.listaEventos.get(0).getTempoInicial() < tempoSimulacao){
			//A lista de Eventos é ordenada por tempo do menor para o maior
			Collections.sort(this.listaEventos);
			//O primeiro Evento da lista, ou seja, aquele que deve acontecer antes de todos os outros é executado
			this.listaEventos = this.listaEventos.get(0).acao(this.listaEventos);
			//em seguida ele é retirado da lista de Eventos
			this.listaEventos.remove(0);
		}
		//apenas informa que a simulação chegou ao fim
		System.out.println("Fim da simulação");
	}
	
	public void setListaEventos(ArrayList<Evento> listaEventos) {
		this.listaEventos = listaEventos;
	}

	public ArrayList<Evento> getListaEventos() {
		return listaEventos;
	}
	
		
	public SimuladorDebug() 
	{
		try {
			fos = new FileOutputStream(arquivoLog,false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}

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
