package br.ufrj.ad20101.src.simulador;

import java.util.ArrayList;
import java.util.Collections;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.evento.EventoDescartaQuadro;
import br.ufrj.ad20101.src.evento.EventoFimTransmissao;
import br.ufrj.ad20101.src.evento.EventoIniciaTransmissao;
import br.ufrj.ad20101.src.servicos.Servicos;

public class Simulador {
	
	private ArrayList<Evento> listaEventos = new ArrayList<Evento>();
	
	private int indiceEstacao;
	private int indiceTipoEvento;
	private int quantidadeMensagens;
	private int numRodada = 0;
	private boolean fimSimulacao = false;
	private boolean faseTransiente = true;
	private boolean[] flagCor = new boolean[4];
	private Double tempoInicialRodada;
	private Double tempoFinalRodada;
	
	//variaveis para calcular o TAP
	
	private Double[] tapMedia = new Double[4];
	private Double[] tapDP = new Double[4];
	private Double[][] tapAmostras = new Double[4][200];
	private Double[] tapLarguraIC = new Double[4];
	
	private boolean[] tapFlag = new boolean[4];
	private Double[] tapSoma = new Double[4];
	private Double[] tapInicial = new Double[4];
	private Double[] tapFinal = new Double[4];
	private int[] tapQuantidade = new int[4];
	private int[] tapQuadro = new int[4];
	
	//variaveis para calcular o TAM
	
	private Double[] tamMedia = new Double[4];
	private Double[] tamDP = new Double[4];
	private Double[] tamLarguraIC = new Double[4];
	private Double[][] tamAmostras = new Double[4][200];
	
	private boolean[] tamFlag = new boolean[4];
	private Double[] tamSoma = new Double[4];
	private Double[] tamInicial = new Double[4];
	private Double[] tamFinal = new Double[4];
	private int[] tamQuantidade = new int[4];
	private int[] tamQuadro = new int[4];
	
	//variaveis para calcular o NCM
	
	private Double[] ncmMedia = new Double[4];
	private Double[] ncmDP = new Double[4];
	private Double[] ncmLarguraIC = new Double[4];
	private Double[][] ncmAmostras = new Double[4][200];
	
	private boolean[] ncmFlag = new boolean[4];
	private Double[] ncmSoma = new Double[4];
	private int[] ncmColisoes = new int[4];
	private int[] ncmQuantidade = new int[4];
	private int[] ncmQuadro = new int[4];
	
	//variaveis para calcular a utilização
	
	private Double utilMedia = 0.0;
	private Double utilDP = 0.0;
	private Double utilLarguraIC;
	private Double[] utilAmostras = new Double[200];
	
	private boolean utilFlag = false;
	private Double utilSoma = 0.0;
	private Double utilAnterior = 0.0;
	private Double utilInicial = 0.0;
	private Double utilFinal = 0.0;
	
	//variaveis para calcular a vazao
	
	private Double[] vazMedia = new Double[4];
	private Double[] vazDP = new Double [4];
	private Double[] vazLarguraIC = new Double[4];
	private Double[][] vazAmostras = new Double[4][200];
	
	private int[] vazQuadro = new int[4];
	
	public void start(){
		Servicos servicos = new Servicos();
		Double tempoFinal = new Double (0.0);
		for(int i = 0; i < 4; i++){
			tapMedia[i] = 0.0;
			tamMedia[i] = 0.0;
			ncmMedia[i] = 0.0;
			vazMedia[i] = 0.0;
			tapDP[i] = 0.0;
			tamDP[i] = 0.0;
			ncmDP[i] = 0.0;
			vazDP[i] = 0.0;
		}
		while(!fimSimulacao){
			quantidadeMensagens = 0;
			for(int i = 0; i < 4; i++){
				flagCor[i] = true;
				//TAP
				tapFlag[i] = false;
				tapSoma[i] = 0.0;
				tapQuantidade[i] = 0;
				tapQuadro[i] = 0;
				//TAM
				tamFlag[i] = false;
				tamSoma[i] = 0.0;
				tamQuantidade[i] = 0;
				tamQuadro[i] = 0;
				//NCM
				ncmFlag[i] = false;
				ncmSoma[i] = 0.0;
				ncmColisoes[i] = 0;
				ncmQuantidade[i] = 0;
				ncmQuadro[i] = 0;
				//VAZAO
				vazQuadro[i] = 0;
				//UTILIZACAO
				utilSoma = 0.0;
			}
			while(quantidadeMensagens < 20000){
				Collections.sort(this.listaEventos);
				this.listaEventos = this.listaEventos.get(0).acao(this.listaEventos);
				indiceEstacao = this.listaEventos.get(0).getEstacao().getIdentificador() -1;
				indiceTipoEvento = this.listaEventos.get(0).getTipoEvento();
				if(indiceTipoEvento == Evento.INICIA_TRANSMISSAO){
					if(!flagCor[indiceEstacao]){
						//TAP
						if(!tapFlag[indiceEstacao]){
							tapInicial[indiceEstacao] = this.listaEventos.get(0).getTempoInicial();
							tapFinal[indiceEstacao] = tapInicial[indiceEstacao];
							tapQuadro[indiceEstacao] = ((EventoIniciaTransmissao)this.listaEventos.get(0)).getQuantidadeQuadro();
							tapFlag[indiceEstacao] = true;
							tapQuantidade[indiceEstacao] ++;
						}else{
							if(tapQuadro[indiceEstacao] == ((EventoIniciaTransmissao)this.listaEventos.get(0)).getQuantidadeQuadro()){
								tapFinal[indiceEstacao] = this.listaEventos.get(0).getTempoInicial();
							}
						}
						//TAM
						if(tamQuadro[indiceEstacao] == ((EventoIniciaTransmissao)this.listaEventos.get(0)).getQuantidadeQuadro()){
							if(!tamFlag[indiceEstacao]){
								tamInicial[indiceEstacao] = this.listaEventos.get(0).getTempoInicial();
								tamFlag[indiceEstacao] = true;
								tamQuantidade[indiceEstacao] ++;
							}
						}
						if(1 == ((EventoIniciaTransmissao)this.listaEventos.get(0)).getQuantidadeQuadro()){
							if(tamFlag[indiceEstacao]){
								tamFinal[indiceEstacao] = this.listaEventos.get(0).getTempoInicial();
							}
						}
					}
				}else if(indiceTipoEvento == Evento.FIM_TRANSMISSAO){
					if(!flagCor[indiceEstacao]){
						//TAP
						if(tapQuadro[indiceEstacao] == ((EventoFimTransmissao)this.listaEventos.get(0)).getQuantidadeQuadro()+1){
							tapFlag[indiceEstacao] = false;
							tapSoma[indiceEstacao] += tapFinal[indiceEstacao] - tapInicial[indiceEstacao];
						}
						//TAM
						if(1 == ((EventoFimTransmissao)this.listaEventos.get(0)).getQuantidadeQuadro()+1){
							tamFlag[indiceEstacao] = false;
							tamSoma[indiceEstacao] += tamFinal[indiceEstacao] - tamInicial[indiceEstacao];
						}
						//VAZAO
						vazQuadro[indiceEstacao] ++;
					}
				}else if(indiceTipoEvento == Evento.DESCARTA_QUADRO){
					if(!flagCor[indiceEstacao]){
						//TAP
						if(tapQuadro[indiceEstacao] == ((EventoDescartaQuadro)this.listaEventos.get(0)).getQuantidadeQuadro()){
							tapFlag[indiceEstacao] = false;
							tapQuantidade[indiceEstacao] --;
						}
						//TAM
						if(1 == ((EventoDescartaQuadro)this.listaEventos.get(0)).getQuantidadeQuadro()){
							tamFlag[indiceEstacao] = false;
							tamQuantidade[indiceEstacao] --;
						}
					}
				}else if(indiceTipoEvento == Evento.CHEGA_MENSAGEM){
					if(flagCor[indiceEstacao]){
						flagCor[indiceEstacao] = false;
					}
					//TAM
					if(!tamFlag[indiceEstacao]){
						tamQuadro[indiceEstacao] = servicos.geraQuantidadeQuadros(this.listaEventos.get(0).getEstacao());
					}
					//NCM
					if(!ncmFlag[indiceEstacao]){
						ncmQuadro[indiceEstacao] = servicos.geraQuantidadeQuadros(this.listaEventos.get(0).getEstacao());
						ncmQuantidade[indiceEstacao] ++;
						ncmFlag[indiceEstacao] = true;
					}
				}else if(indiceTipoEvento == Evento.COLISAO){
					if(!flagCor[indiceEstacao]){
						//NCM
						if(ncmFlag[indiceEstacao]){
							ncmColisoes[indiceEstacao] ++;
						}
					}
				}else if(indiceTipoEvento == Evento.FIM_MENSAGEM){
					if(!flagCor[indiceEstacao]){
						//NCM
						if(ncmFlag[indiceEstacao]){
							ncmFlag[indiceEstacao] = false;
							ncmSoma[indiceEstacao] += (double)ncmColisoes[indiceEstacao]/ncmQuadro[indiceEstacao];
							ncmColisoes[indiceEstacao] = 0;
						}
						//Rodada
						quantidadeMensagens ++;
					}
				}
				//UTILIZAÇÃO
				if(this.getListaEventos().get(0).getEstacoes().get(0).getEstado() != Estacao.ESTADO_OCIOSO){
					if(!utilFlag){
						utilFlag = true;
						utilInicial = this.listaEventos.get(0).getTempoInicial();
					}
				}else{
					if(utilFlag){
						utilFlag = false;
						utilFinal = this.listaEventos.get(0).getTempoInicial();
						utilSoma += utilFinal - utilInicial;
						//testar se a fase transiente chegou ao fim
						if(faseTransiente){
							if((utilSoma/utilFinal - utilAnterior > -0.00005*utilAnterior) && (utilSoma/utilFinal - utilAnterior < 0.00005*utilAnterior) ){
								this.listaEventos.remove(0);
								tempoFinal = this.listaEventos.get(0).getTempoInicial();
								break;
							}
						}
						utilAnterior = utilSoma/utilFinal;
					}
				}
				tempoFinal = this.listaEventos.get(0).getTempoInicial();
				this.listaEventos.remove(0);
			}
			tempoFinalRodada = tempoFinal;
			if(faseTransiente){
				faseTransiente = false;				
			}else{
				for(int i = 0; i < 4; i++){
					//TAP MEDIA/DESVIO PADRAO
					if(tapQuantidade[i] > 0)
						tapAmostras[i][numRodada-1] = tapSoma[i]/tapQuantidade[i];
					tapMedia[i] = (tapMedia[i]*(numRodada-1) + tapAmostras[i][numRodada-1])/numRodada;
					
					//TAM MEDIA/DESVIO PADRAO
					if(tamQuantidade[i] > 0)
						tamAmostras[i][numRodada-1] = tamSoma[i]/tamQuantidade[i];
					tamMedia[i] = (tamMedia[i]*(numRodada-1) + tamAmostras[i][numRodada-1])/numRodada;
					tamDP[i] = servicos.desvioPadrao(tamAmostras[i], numRodada, tamMedia[i]);
					
					//NCM MEDIA/DESVIO PADRAO
					if(ncmQuantidade[i] > 0)
						ncmAmostras[i][numRodada-1] = ncmSoma[i]/ncmQuantidade[i];
					ncmMedia[i] = (ncmMedia[i]*(numRodada-1) + ncmAmostras[i][numRodada-1])/numRodada;
					ncmDP[i] = servicos.desvioPadrao(ncmAmostras[i], numRodada, ncmMedia[i]);
					
					//VAZ MEDIA/DESVIO PADRAO
					vazAmostras[i][numRodada-1] = vazQuadro[i]/(tempoFinalRodada - tempoInicialRodada);
					vazMedia[i] = (vazMedia[i]*(numRodada-1) + vazAmostras[i][numRodada-1])/numRodada;
					vazDP[i] = servicos.desvioPadrao(vazAmostras[i], numRodada, vazMedia[i]);
					
					//UTIL MEDIA/DESVIO PADRAO
					utilAmostras[numRodada-1] = utilSoma/(tempoFinalRodada - tempoInicialRodada);
					utilMedia = (utilMedia*(numRodada-1) + utilAmostras[numRodada-1])/numRodada;
					utilDP = servicos.desvioPadrao(utilAmostras, numRodada, utilMedia);
				}
				if(numRodada >= 30){
					fimSimulacao = true;
					utilLarguraIC = 2*1.96*utilDP/Math.sqrt(numRodada);
					if(utilLarguraIC < 0.1*utilMedia || utilMedia==0){
						for(int i = 0; i < 4 && this.getListaEventos().get(0).getEstacoes().get(i).getTipoChegada()!=0; i ++){
							//CALCULA LARGURA DO INTERVALO TAM
							tamLarguraIC[i] = 2*(1.96*tamDP[i]/Math.sqrt(numRodada));
							if(tamLarguraIC[i] >= 0.1*tamMedia[i] && tamMedia[i] != 0.0){
								fimSimulacao = false;
								break;
							}
							//CALCULA LARGURA DO INTERVALO TAP
							tapLarguraIC[i] = 2*(1.96*tapDP[i]/Math.sqrt(numRodada));
							if(tapLarguraIC[i] >= 0.1*tapMedia[i] && tapMedia[i] != 0.0){
								fimSimulacao = false;
								break;
							}
							////CALCULA LARGURA DO INTERVALO NCM
							ncmLarguraIC[i] = 2*(1.96*ncmDP[i]/Math.sqrt(numRodada));
							if(ncmLarguraIC[i] >= 0.1*ncmMedia[i] && ncmMedia[i] != 0.0){
								fimSimulacao = false;
								break;
							}
							//CALCULA LARGURA DO INTERVALO VAZ
							vazLarguraIC[i] = 2*(1.96*vazDP[i]/Math.sqrt(numRodada));
							if(vazLarguraIC[i] >= 0.1*vazMedia[i] && vazMedia[i] != 0.0){
								fimSimulacao = false;
								break;
							}
						}
					}else{
						fimSimulacao = false;
					}
				}
			}
			numRodada ++;
			tempoInicialRodada = tempoFinal;
		}
		System.out.printf(">>Estatisticas:\n\n");
		System.out.println("Número de Rodadas: " + (numRodada-1));
		System.out.printf("Utilização Médio: %.10f\n", utilMedia);
		System.out.printf("Utilização Intervalo: (%.10f , %.10f)\n\n", utilMedia+(utilLarguraIC/2), utilMedia-(utilLarguraIC/2));
		for(int i = 0; i < 4; i++){
			System.out.printf("Estação " + (i+1) +":\n");
			System.out.printf("TAp Médio: %.10f\n", tapMedia[i]);
			System.out.printf("TAp Intervalo: (%.10f , %.10f)\n\n", tapMedia[i]+(tapLarguraIC[i]/2), tapMedia[i]-(tapLarguraIC[i]/2));
			System.out.printf("TAm Médio: %.10f\n", tamMedia[i]);
			System.out.printf("TAm Intervalo: (%.10f , %.10f)\n\n", tamMedia[i]+(tamLarguraIC[i]/2), tamMedia[i]-(tamLarguraIC[i]/2));
			System.out.printf("NCm Médio: %.10f\n", ncmMedia[i]);
			System.out.printf("NCm Intervalo: (%.10f , %.10f)\n\n", ncmMedia[i]+(ncmLarguraIC[i]/2), ncmMedia[i]-(ncmLarguraIC[i]/2));
			System.out.printf("Vazão Médio: %.10f\n", vazMedia[i]);
			System.out.printf("Vazão Intervalo: (%.10f , %.10f)\n\n", vazMedia[i]+(vazLarguraIC[i]/2), vazMedia[i]-(vazLarguraIC[i]/2));
		}
	}

	public void setListaEventos(ArrayList<Evento> listaEventos) {
		this.listaEventos = listaEventos;
	}

	public ArrayList<Evento> getListaEventos() {
		return listaEventos;
	}
}
