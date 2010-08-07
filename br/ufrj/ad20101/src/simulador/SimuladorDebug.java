package br.ufrj.ad20101.src.simulador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import br.ufrj.ad20101.src.evento.Evento;

public class SimuladorDebug {
	
	File arquivoLog = new File("logSimulador.txt");
	FileOutputStream fos;
	
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
	
	public void escreveLog(String mensagem)
	{		
		try 
		{
			fos = new FileOutputStream(arquivoLog,true);
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
