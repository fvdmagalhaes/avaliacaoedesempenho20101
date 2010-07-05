package br.ufrj.ad20101.src.servicos;

public class Constantes {
	public static int TAMANHO_QUADRO = 1000; //unidade = bytes
	public static int TAMANHO_REFORCO = 4; //unidade = bytes
	public static Double SEGUNDO_EM_MILISSEGUNDOS = 1000.0; // unidade = milisegundos
	public static Double TEMPO_REFORCO_COLISAO = 0.0032; //milisegundos
	public static Double INTERVALO_ENTRE_QUADROS = 0.0096; //unidade = milisegundos
	public static Double PROPAGACAO_ELETRICA = 0.005/1000; //unidade = microSegundos/metro
	public static Double VELOCIDADE_ETHERNET = 10.0*1024*1024/1000; //unidade = bytes/milisegundos
	public static Double TEMPO_QUADRO_ENLACE = TAMANHO_QUADRO/VELOCIDADE_ETHERNET; // unidade = milisegundos
	public static Double TEMPO_REFORCO_ENLACE = TAMANHO_REFORCO/VELOCIDADE_ETHERNET; // unidade = milisegundos
}
