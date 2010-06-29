package br.ufrj.ad20101.src.estacao;

public class Estacao {
	
	// Constantes com o tipo de chegada das mensagens
	public static int DETERMINISTICO;
	public static int EXPONENCIAL;
	
	// Constantes com o estado que a Estação está no momento
	public static int ESTADO_TRANSFERINDO;
	public static int ESTADO_RECEBENDO;
	public static int ESTADO_OCIOSO;
	public static int ESTADO_TRATANDO_COLISAO;
	
	// Constantes com o identificador de cada Estação
	public static int ESTACAO1;
	public static int ESTACAO2;
	public static int ESTACAO3;
	public static int ESTACAO4;
	
	
	private int tipoChegada;
	private Double intervaloEntreChegadas;
	private int distancia; //distancia entra a Estação e o HUB em metros
	private int identificador; //Número da Estação (1, 2, 3 ou 4)
	private int estado; //Indica em que estado a Estação se encontra
	
	
	public Estacao(){	
	}
	
	public Estacao(int tipoChegada, Double intervaloEntreChegadas, int distancia, int identificador){
		this.tipoChegada = tipoChegada;
		this.intervaloEntreChegadas = intervaloEntreChegadas;
		this.distancia = distancia;
		this.identificador = identificador;
		this.estado = ESTADO_OCIOSO;
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
}
