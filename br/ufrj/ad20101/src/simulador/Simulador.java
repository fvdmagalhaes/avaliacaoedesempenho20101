package br.ufrj.ad20101.src.simulador;

import java.util.ArrayList;
import java.util.Collections;

import br.ufrj.ad20101.src.evento.Evento;

public class Simulador {
	
	private ArrayList<Evento> listaEventos = new ArrayList<Evento>();
	
	public void start(){
		while(!listaEventos.isEmpty()){
			Collections.sort(this.listaEventos);
			this.listaEventos = this.listaEventos.get(0).acao(this.listaEventos);
			this.listaEventos.remove(0);
		}
	}

	public void setListaEventos(ArrayList<Evento> listaEventos) {
		this.listaEventos = listaEventos;
	}

	public ArrayList<Evento> getListaEventos() {
		return listaEventos;
	}
}
