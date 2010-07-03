package br.ufrj.ad20101.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.Simulador;

public class Principal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int opcaoInt;
		String opcaoChar;
		Double opcaoDouble;
		Servicos servicos = new Servicos();
		Estacao[] estacoes = new Estacao[4];
		Simulador simulador = new Simulador();
		Scanner leTeclado = new Scanner(System.in);
		ArrayList<Evento> listaEventos = new ArrayList<Evento>();
		
		System.out.println("Simulador de Avalia��o e Desempenho");
		System.out.println("Grupo: Fernando, Peter, Victor, Zaedy\n");		
		System.out.println("Favor entrar com os dados para a inicializa��o do simulador");
		for(int i = 0; i < 4; i++){
			estacoes[i] = new Estacao(i+1);
			System.out.print("Esta��o " + (i+1) + " - Sem tr�fego (0) ou Com tr�fego (1) ?\n");
			do{
				try{
					opcaoInt = Integer.parseInt(leTeclado.next());
				}catch(Exception e){
					opcaoInt = -1;
				}
				if(opcaoInt == 1){
					System.out.println("Para a esta��o "+ (i+1) + ":");
					System.out.println("Tr�fego determin�stico (D) ou exponencial (E) ?");
					do{
						opcaoChar = leTeclado.next();
						if(opcaoChar.equalsIgnoreCase("D")){
							estacoes[i].setTipoChegada(Estacao.DETERMINISTICO);
						}else if(opcaoChar.equalsIgnoreCase("E")){
							estacoes[i].setTipoChegada(Estacao.EXPONENCIAL);
						}else {
							System.out.println("Op��o inv�lida.\nTr�fego determin�stico (D) ou exponencial (E) ?");
							opcaoChar = "Erro";
						}
					}while(opcaoChar.equalsIgnoreCase("Erro"));
					System.out.println("Digite o intervalo m�dio (em segundos) entre as chegadas das mensagens na Esta��o " + (i+1) + ":");
					do{
						try{
							opcaoDouble = Double.parseDouble(leTeclado.next());
							estacoes[i].setIntervaloEntreChegadas(opcaoDouble*Constantes.SEGUNDO_EM_MILISSEGUNDOS);
						}catch(Exception e){
							System.out.println("Tempo inv�lido.\nDigite o intervalo m�dio (em segundos) entre as chegadas das mensagens na Esta��o " + (i+1) + ":");
							opcaoDouble = -1.0;
						}
					}while(opcaoDouble.equals(-1.0));
					listaEventos.add(servicos.geraEvento(Evento.CHEGA_MENSAGEM, servicos.geraProximaMensagem(estacoes[i], 0.0), estacoes[i]));
				}else if(opcaoInt != 0){
					System.out.print("Op��o inv�lida.\nEsta��o " + (i+1) + " - Sem tr�fego (0) ou Com tr�fego (1) ?\n");
					opcaoInt = -1;
				}
			}while (opcaoInt == -1);
		}
		Collections.sort(listaEventos);
		simulador.start(listaEventos);
	}
}
