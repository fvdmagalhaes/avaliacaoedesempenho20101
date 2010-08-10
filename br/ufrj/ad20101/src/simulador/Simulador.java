package br.ufrj.ad20101.src.simulador;

import java.util.ArrayList;
import java.util.Collections;

import br.ufrj.ad20101.src.estatisticas.ColetaEstatistica;
import br.ufrj.ad20101.src.evento.Evento;

public class Simulador {
	
	private ArrayList<Evento> listaEventos = new ArrayList<Evento>();
	
	public void start(){
		ColetaEstatistica coletaEstatistica = new ColetaEstatistica();
		Evento eventoCorrente;
		do{
			Collections.sort(this.listaEventos);
			this.listaEventos = this.listaEventos.get(0).acao(this.listaEventos);
			coletaEstatistica.coletar(listaEventos.get(0));
			eventoCorrente = this.listaEventos.remove(0);
		}while(eventoCorrente.getTempoInicial() < 3600000);
	}

	public void setListaEventos(ArrayList<Evento> listaEventos) {
		this.listaEventos = listaEventos;
	}

	public ArrayList<Evento> getListaEventos() {
		return listaEventos;
	}
}
