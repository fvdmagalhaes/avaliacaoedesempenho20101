package br.ufrj.ad20101.src.simulador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import br.ufrj.ad20101.src.evento.Evento;

public class SimuladorDebug {
	
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
	
	public void start(){
		while(this.listaEventos.get(0).getTempoInicial() < 15000){
			Collections.sort(this.listaEventos);
			this.listaEventos = this.listaEventos.get(0).acao(this.listaEventos);
			this.listaEventos.remove(0);
		}
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
