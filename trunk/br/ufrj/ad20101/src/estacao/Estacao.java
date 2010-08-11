package br.ufrj.ad20101.src.estacao;

import java.util.ArrayList;
import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.evento.EventoIniciaTransmissao;

public class Estacao {
	
	// Constantes com o tipo de chegada das mensagens
	public static int DETERMINISTICO = 1;
	public static int EXPONENCIAL = 2;
	
	// Constantes com o estado que a Estação está no momento
	public static int ESTADO_TRANSFERINDO = 1;
	public static int ESTADO_RECEBENDO = 2;
	public static int ESTADO_OCIOSO = 3;
	public static int ESTADO_TRATANDO_COLISAO_OCIOSO = 4;
	public static int ESTADO_TRATANDO_COLISAO_OCUPADO = 5;
	public static int ESTADO_PREPARANDO_TRANSFERIR = 6;
	
	// Constantes com o identificador de cada Estação
	public static int ESTACAO1 = 1;
	public static int ESTACAO2 = 2;
	public static int ESTACAO3 = 3;
	public static int ESTACAO4 = 4;
	
	
	private int tipoChegada; //indica tipo de chegada de mensagens; Zero indica que a estação não transmite
	private int distancia; //distancia entra a Estação e o HUB em metros
	private int identificador; //Número da Estação (1, 2, 3 ou 4)
	private int estado; //Indica em que estado a Estação se encontra
	private Double tempoUltimaRecepcao = 0.0; //Indica quando foi que o meio ficou livre pela última vez
	private Double intervaloEntreChegadas;
	private Double quantidadeQuadros;
	private ArrayList<Evento> mensagensPendentes = new ArrayList<Evento>(); //mensagens na fila de espera
	private EventoIniciaTransmissao quadroSentindoMeio; //quadro aguardando o meio desocupar; 'get' alterado para tornar null depois que retornar o evento
	
	// Estabelece a distância entre cada estação e o HUB de acordo com o enunciado do trabalho de simulação
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
		}else{ // Nunca vai entrar neste else, serve apenas para identificar um possível erro de forma mais fácil
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

	public void setTempoUltimaRecepcao(Double tempoUltimaRecepcao) {
		this.tempoUltimaRecepcao = tempoUltimaRecepcao;
	}

	public Double getTempoUltimaRecepcao() {
		return tempoUltimaRecepcao;
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

	public void setQuadroSentindoMeio(EventoIniciaTransmissao quadroSentindoMeio) {
		this.quadroSentindoMeio = quadroSentindoMeio;
	}	
	
	/* Pega o evento que está aguardando o início da transmissão assim que o meio é detectado
	   livre e limpa a variável para ser usado por um próximo evento */
	public EventoIniciaTransmissao getQuadroSentindoMeio() {
		EventoIniciaTransmissao eventoIniciaTransmissao = quadroSentindoMeio;
		this.setQuadroSentindoMeio(null);
		return eventoIniciaTransmissao;
	}
}
