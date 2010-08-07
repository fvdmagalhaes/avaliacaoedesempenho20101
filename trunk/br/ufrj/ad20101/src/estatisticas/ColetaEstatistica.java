package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.evento.Evento;

/*
 * Este Classe � respons�vel por gerenciar a coleta e o c�lculo de todas as Estat�sticas solicitadas
 * */

public class ColetaEstatistica {
	
	//n�mero m�dio de colis�es por quadro
	private Ncm[] ncm = new Ncm[4];
	//tempo de acesso de uma mensagem
	private Tam[] tam = new Tam[4];
	//tempo de acesso de um quadro
	private Tap[] tap = new Tap[4];
	//utiliza��o do Ethernet
	private Utilizacao utilizacao = new Utilizacao();
	//rela��o entre o n�mero de quadros transmitidos com sucesso na esta��o e o tempo de simula��o
	private Vazao[] vazao = new Vazao[4];
	
	int indice; //indica a esta��o que ocorreu o evento
	
	//editar o construtor para inicializar todos os vetores
	public ColetaEstatistica (){
		for(int i = 0; i < 4; i++){
			ncm[i] = new Ncm();
			tam[i] = new Tam();
			tap[i] = new Tap();
			vazao[i] = new Vazao();
		}
	}
	
	//Este m�todo pega o Evento do par�metro e retira as informa��es necess�rias para coletar as estat�sticas
	public void coletar (Evento evento){
		indice = evento.getEstacao().getIdentificador()-1;
		ncm[indice].coletar(evento);
		tam[indice].coletar(evento);
		tap[indice].coletar(evento);
		utilizacao.coletar(evento);
		vazao[indice].coletar(evento);
	}
	
	public Ncm[] getNcm() {
		return ncm;
	}
	public void setNcm(Ncm[] ncm) {
		this.ncm = ncm;
	}
	public Tap[] getTap() {
		return tap;
	}
	public void setTap(Tap[] tap) {
		this.tap = tap;
	}
	public Tam[] getTam() {
		return tam;
	}
	public void setTam(Tam[] tam) {
		this.tam = tam;
	}
	public Utilizacao getUtilizacao() {
		return utilizacao;
	}
	public void setUtilizacao(Utilizacao utilizacao) {
		this.utilizacao = utilizacao;
	}
	public Vazao[] getVazao() {
		return vazao;
	}
	public void setVazao(Vazao[] vazao) {
		this.vazao = vazao;
	}
}
