package br.ufrj.ad20101.src.estatisticas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
	int contador;
	File file = new File("Estat�sticasCenario2.txt");
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
	
	//Este m�todo pega o Evento do par�metro e retira as informa��es necess�rias para coletar as estat�sticas
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
				//System.out.println("N�mero de Rodadas: " + (numRodada-1));
				fos.write(new String("Utiliza��o M�dio: " + utilizacao.amostra).getBytes());
				//System.out.printf("Utiliza��o Intervalo: (%.10f , %.10f)\n\n", utilMedia+(utilLarguraIC/2), utilMedia-(utilLarguraIC/2));
				for(int i = 0; i < 4; i++){
					fos.write(new String ("\nEsta��o " + (i+1) +":\n").getBytes());
					fos.write(new String("TAp M�dio: " + tap[i].amostra).getBytes());
					//System.out.printf("TAp Intervalo: (%.10f , %.10f)\n\n", tapMedia[i]+(tapLarguraIC[i]/2), tapMedia[i]-(tapLarguraIC[i]/2));
					fos.write(new String("\n\nTAm M�dio: " + tam[i].amostra).getBytes());
					//System.out.printf("TAm Intervalo: (%.10f , %.10f)\n\n", tamMedia[i]+(tamLarguraIC[i]/2), tamMedia[i]-(tamLarguraIC[i]/2));
					fos.write(new String("\n\nNCm M�dio: " + ncm[i].amostra).getBytes());
					//System.out.printf("NCm Intervalo: (%.10f , %.10f)\n\n", ncmMedia[i]+(ncmLarguraIC[i]/2), ncmMedia[i]-(ncmLarguraIC[i]/2));
					fos.write(new String("\n\nVaz�o M�dio: " + vazao[i].amostra + "\n\n").getBytes());
					//System.out.printf("Vaz�o Intervalo: (%.10f , %.10f)\n\n", vazMedia[i]+(vazLarguraIC[i]/2), vazMedia[i]-(vazLarguraIC[i]/2));
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
