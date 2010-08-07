package br.ufrj.ad20101.src.servicos;

public class Constantes {
	
	/* Tamanho de um quadro em uma mensagem, constante e igual a 1000 bytes 						*/
	public static int TAMANHO_QUADRO = 1000; //unidade = bytes
	
	/* Tamanho do reforço de colisão equivale a transmitir 32 bits, ou 4 bytes 						*/
	public static int TAMANHO_REFORCO = 4; //unidade = bytes
	
	/* 1 segundo = 1000 milisegundos 																*/
	public static Double SEGUNDO_EM_MILISSEGUNDOS = 1000.0; // unidade = milisegundos
	
	/* Tempo que dura o reforço de colisão. É igual a 32 bits/10M (velocidade do enlace Ethernet) = 
	 * = 0,0000032 segundos = 0,0032 milisegundos													*/
	public static Double TEMPO_REFORCO_COLISAO = 0.0032; // unidade = milisegundos
	
	/* Constante que representa o intervalo entre quadros, que a estação vai aguardar para transmitir o 
	 * próximo quadro */
	public static Double INTERVALO_ENTRE_QUADROS = 0.0096; //unidade = milisegundos
	
	/* Velocidade de propagação elétrica no meio físico = 5 microsegundos/km
	 * = 0.005/1000 milisegundos/metro																*/
	public static Double PROPAGACAO_ELETRICA = 0.005/1000; //unidade = milisegundos/metro
	
	/* Velocidade do enlace Ethernet = 10Mbps. Convertendo para bytes/milisegundos temos
	 * (10.0*1000*1000)/8000 bytes/milisegundos														*/
	public static Double VELOCIDADE_ETHERNET = (10.0*1000*1000)/8000; //unidade = bytes/milisegundos
	
	/* Tempo de transmissão de um quadro no enlace. Igual ao tamanho do quadro dividido pela velocidade
	 * do ethernet																					*/
	public static Double TEMPO_QUADRO_ENLACE = TAMANHO_QUADRO/VELOCIDADE_ETHERNET; // unidade = milisegundos
	
	/* Tempo de transmissão do reforço de colisão no enlace. Igual ao tamanho do reforço de colisão em bytes
	 * dividido pela velocidade do ethernet em bytes/milisegundos									*/
	public static Double TEMPO_REFORCO_ENLACE = TAMANHO_REFORCO/VELOCIDADE_ETHERNET; // unidade = milisegundos
	
	/* Intervalo de tempo de 51,2 microsegundos gerado pelo algoritmo do Binary Backoff convertido 
	 * para milisegundos = 0.0512 milisegundos														*/
	public static Double TEMPO_BINARY_BACKOFF = 0.0512; // unidade = milisegundos
}
