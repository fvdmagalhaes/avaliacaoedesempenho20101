package br.ufrj.ad20101.src.estacao;

import java.util.ArrayList;
import br.ufrj.ad20101.src.evento.Evento;

public class Estacao {
	
	// Constantes com o tipo de chegada das mensagens
	public static int DETERMINISTICO = 1;
	public static int EXPONENCIAL = 2;
	
	// Constantes com o estado que a Estação está no momento
	public static int ESTADO_TRANSFERINDO = 1;
	public static int ESTADO_RECEBENDO = 2;
	public static int ESTADO_OCIOSO = 3;
	public static int ESTADO_TRATANDO_COLISAO = 4;
	
	// Constantes com o identificador de cada Estação
	public static int ESTACAO1 = 1;
	public static int ESTACAO2 = 2;
	public static int ESTACAO3 = 3;
	public static int ESTACAO4 = 4;
	
	
	private int tipoChegada;
	private int distancia; //distancia entra a Estação e o HUB em metros
	private int identificador; //Número da Estação (1, 2, 3 ou 4)
	private int estado; //Indica em que estado a Estação se encontra
	private Double intervaloEntreChegadas;
	private Double quantidadeQuadros;
	private ArrayList<Evento> mensagensPendentes;
	
	public Estacao(int identificador){
		this.identificador = identificador;
		this.estado = ESTADO_OCIOSO;
		if(identificador == ESTACAO1){
			this.distancia = 100;
		}else if(identificador == ESTACAO2){
			this.distancia = 80;
		}else if(identificador == ESTACAO3){
			this.distancia = 60;
		}else if(identificador == ESTACAO4){
			this.distancia = 40;
		}else{
			System.out.println("ERRO: Estação com identificador incorreto");
			System.exit(0);
		}
	}
	
	public Estacao(int tipoChegada, Double intervaloEntreChegadas, int distancia, int identificador){
		this.tipoChegada = tipoChegada;
		this.intervaloEntreChegadas = intervaloEntreChegadas;
		this.distancia = distancia;
		this.identificador = identificador;
		this.estado = ESTADO_OCIOSO;
		if(identificador == ESTACAO1){
			this.distancia = 100;
		}else if(identificador == ESTACAO2){
			this.distancia = 80;
		}else if(identificador == ESTACAO3){
			this.distancia = 60;
		}else if(identificador == ESTACAO4){
			this.distancia = 40;
		}else{
			System.out.println("ERRO: Estação com identificador incorreto");
			System.exit(0);
		}
	}

	public int getTipoChegada() {
		return tipoChegada;
	}

	public void setTipoChegada(int tipoChegada) {
		this.tipoChegada = tipoChegada;
	}

	public Double getIntervaloEntreChegadas() {
		return intervaloEntreChegadas;
	}

	public void setIntervaloEntreChegadas(Double intervaloEntreChegadas) {
		this.intervaloEntreChegadas = intervaloEntreChegadas;
	}
	
	public int getDistancia() {
		return distancia;
	}

	public void setDistancia(int distancia) {
		this.distancia = distancia;
	}

	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}

	public int getIdentificador() {
		return identificador;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public int getEstado() {
		return estado;
	}

	public void setMensagensPendentes(ArrayList<Evento> mensagensPendentes) {
		this.mensagensPendentes = mensagensPendentes;
	}

	public ArrayList<Evento> getMensagensPendentes() {
		return mensagensPendentes;
	}

	public void setQuantidadeQuadros(Double quantidadeQuadros) {
		this.quantidadeQuadros = quantidadeQuadros;
	}

	public Double getQuantidadeQuadros() {
		return quantidadeQuadros;
	}
}
