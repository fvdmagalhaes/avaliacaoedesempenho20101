package br.ufrj.ad20101;

public class Estacao {
	
	// Constante com o tipo de chegada das mensagens
	public static int DETERMINISTICO;
	public static int EXPONENCIAL;
	
	private int tipoChegada;
	private Double intervaloEntreChegadas;
	
	
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
	
}
