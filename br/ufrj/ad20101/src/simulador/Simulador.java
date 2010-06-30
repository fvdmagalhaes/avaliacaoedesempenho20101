package br.ufrj.ad20101.src.simulador;

import java.util.ArrayList;

import br.ufrj.ad20101.src.evento.Evento;

public class Simulador {
	public void start(ArrayList<Evento> listaEventos){
		while(!listaEventos.isEmpty()){
			listaEventos.get(0).acao();
			listaEventos.remove(0);
		}
	}
}
