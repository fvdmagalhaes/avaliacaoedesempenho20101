package br.ufrj.ad20101.src.estatisticas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
	int contador;
	File file = new File("EstatísticasCenario2.txt");
	FileOutputStream fos;
	
	//editar o construtor para inicializar todos os vetores
	public ColetaEstatistica (){
		try {
			 fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contador = 1;
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
		vazao[indice].coletar(evento);
		if(evento.getEstacao().getIdentificador() == 1){
			utilizacao.coletar(evento.getEstacao(), evento.getTempoInicial());
		}
		
		
		if(evento.getTempoInicial()>10000*contador){
			try {
				fos.write(new String(">>Estatisticas: " + 10*contador + " segundos\n\n").getBytes());
				//System.out.println("Número de Rodadas: " + (numRodada-1));
				fos.write(new String("Utilização Médio: " + utilizacao.amostra).getBytes());
				//System.out.printf("Utilização Intervalo: (%.10f , %.10f)\n\n", utilMedia+(utilLarguraIC/2), utilMedia-(utilLarguraIC/2));
				for(int i = 0; i < 4; i++){
					fos.write(new String ("\nEstação " + (i+1) +":\n").getBytes());
					fos.write(new String("TAp Médio: " + tap[i].amostra).getBytes());
					//System.out.printf("TAp Intervalo: (%.10f , %.10f)\n\n", tapMedia[i]+(tapLarguraIC[i]/2), tapMedia[i]-(tapLarguraIC[i]/2));
					fos.write(new String("\n\nTAm Médio: " + tam[i].amostra).getBytes());
					//System.out.printf("TAm Intervalo: (%.10f , %.10f)\n\n", tamMedia[i]+(tamLarguraIC[i]/2), tamMedia[i]-(tamLarguraIC[i]/2));
					fos.write(new String("\n\nNCm Médio: " + ncm[i].amostra).getBytes());
					//System.out.printf("NCm Intervalo: (%.10f , %.10f)\n\n", ncmMedia[i]+(ncmLarguraIC[i]/2), ncmMedia[i]-(ncmLarguraIC[i]/2));
					fos.write(new String("\n\nVazão Médio: " + vazao[i].amostra + "\n\n").getBytes());
					//System.out.printf("Vazão Intervalo: (%.10f , %.10f)\n\n", vazMedia[i]+(vazLarguraIC[i]/2), vazMedia[i]-(vazLarguraIC[i]/2));
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			contador ++;
		}
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
