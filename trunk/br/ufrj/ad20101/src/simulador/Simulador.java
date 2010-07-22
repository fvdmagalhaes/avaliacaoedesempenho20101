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
	
	//variaveis para calcular o TAP
	
	private boolean[] tapFlag = new boolean[4];
	private double[] tapSoma = new double[4];
	private double[] tapInicial = new double[4];
	private double[] tapFinal = new double[4];
	private int[] tapQuantidade = new int[4];
	private int[] tapQuadro = new int[4];
	
	//variaveis para calcular o TAM
	
	private boolean[] tamFlag = new boolean[4];
	private double[] tamSoma = new double[4];
	private double[] tamInicial = new double[4];
	private double[] tamFinal = new double[4];
	private int[] tamQuantidade = new int[4];
	private int[] tamQuadro = new int[4];
	
	//variaveis para calcular o NCM
	
	private boolean[] ncmFlag = new boolean[4];
	private double[] ncmSoma = new double[4];
	private int[] ncmColisoes = new int[4];
	private int[] ncmQuantidade = new int[4];
	private int[] ncmQuadro = new int[4];
	
	//variaveis para calcular a utilização
	
	private boolean utilFlag = false;
	private double utilSoma = 0.0;
	private double utilInicial = 0.0;
	private double utilFinal = 0.0;
	
	//variaveis para calcular a vazao
	
	private int[] vazQuadro = new int[4];
	
	public void start(){
		Servicos servicos = new Servicos();
		int quantidadeEstacoes = listaEventos.size();
		for(int i = 0; i < 4; i++){
			//TAP
			tapFlag[i] = false;
			tapSoma[i] = 0;
			tapQuantidade[i] = 0;
			tapQuadro[i] = 0;
			//TAM
			tamFlag[i] = false;
			tamSoma[i] = 0;
			tamQuantidade[i] = 0;
			tamQuadro[i] = 0;
			//NCM
			ncmFlag[i] = false;
			ncmSoma[i] = 0;
			ncmColisoes[i] = 0;
			ncmQuantidade[i] = 0;
			ncmQuadro[i] = 0;
			//VAZAO
			vazQuadro[i] = 0;
		}
		while(true/*listaEventos.size()>= quantidadeEstacoes*/){
			Collections.sort(this.listaEventos);
			this.listaEventos = this.listaEventos.get(0).acao(this.listaEventos);
			indiceEstacao = this.listaEventos.get(0).getEstacao().getIdentificador() -1;
			indiceTipoEvento = this.listaEventos.get(0).getTipoEvento();
			if(indiceTipoEvento == Evento.INICIA_TRANSMISSAO){
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
			}else if(indiceTipoEvento == Evento.FIM_TRANSMISSAO){
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
			}else if(indiceTipoEvento == Evento.DESCARTA_QUADRO){
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
			}else if(indiceTipoEvento == Evento.CHEGA_MENSAGEM){
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
				//NCM
				if(ncmFlag[indiceEstacao]){
					ncmColisoes[indiceEstacao] ++;
				}
			}else if(indiceTipoEvento == Evento.FIM_MENSAGEM){
				//NCM
				if(ncmFlag[indiceEstacao]){
					ncmFlag[indiceEstacao] = false;
					ncmSoma[indiceEstacao] += (double)ncmColisoes[indiceEstacao]/ncmQuadro[indiceEstacao];
					ncmColisoes[indiceEstacao] = 0;
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
				}
			}
			this.listaEventos.remove(0);
		}
		//System.out.println("Erro!");
	}

	public void setListaEventos(ArrayList<Evento> listaEventos) {
		this.listaEventos = listaEventos;
	}

	public ArrayList<Evento> getListaEventos() {
		return listaEventos;
	}
}
