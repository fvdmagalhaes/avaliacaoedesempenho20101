package br.ufrj.ad20101.src.estatisticas;

import br.ufrj.ad20101.src.evento.Evento;

/*
 * Este Classe é responsável por gerenciar a coleta e o cálculo de todas as Estatísticas solicitadas
 * */

public class ColetaEstatistica {
	
	//número médio de colisões por quadro
	private Ncm[] ncm = new Ncm[4];
	//tempo de acesso de uma mensagem
	private Tam[] tam = new Tam[4];
	//tempo de acesso de um quadro
	private Tap[] tap = new Tap[4];
	//utilização do Ethernet
	private Utilizacao utilizacao = new Utilizacao();
	//relação entre o número de quadros transmitidos com sucesso na estação e o tempo de simulação
	private Vazao[] vazao = new Vazao[4];
	
	int indice; //indica a estação que ocorreu o evento
	
	//editar o construtor para inicializar todos os vetores
	public ColetaEstatistica (){
		for(int i = 0; i < 4; i++){
			ncm[i] = new Ncm();
			tam[i] = new Tam();
			tap[i] = new Tap();
			vazao[i] = new Vazao();
		}
	}
	
	//Este método pega o Evento do parâmetro e retira as informações necessárias para coletar as estatísticas
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
