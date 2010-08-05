package br.ufrj.ad20101.src;

import java.util.ArrayList;
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

		// Declara��o das vari�veis que receber�o as entradas digitadas pelo usu�rio 
		int opcaoInt;
		String opcaoChar;
		Double opcaoDouble;
		
		/* Instancia um objeto do tipo Servi�o que tem por objetivo gerar um evento do tipo Chegada de Mensagem,
		   que ser� o primeiro evento da lista de eventos, a partir do qual os outros eventos ser�o gerados */
		Servicos servicos = new Servicos();

		// Cria uma lista com as quatro esta��es participantes do ambiente de simula��o
		ArrayList <Estacao> estacoes = new ArrayList<Estacao>(4);
		
		// Cria um objeto da classe simulador, respons�vel pelo loop principal de envio de mensagens 
		// pelas esta��es e pela coleta das estat�sticas
		Simulador simulador = new Simulador();
		
		// Cria a vari�vel leTeclado para ler os dados fornecidos pelo usu�rio no inicio da simula��o
		Scanner leTeclado = new Scanner(System.in);
		
		// Cria a lista de eventos inicialmente vazia
		ArrayList<Evento> listaEventos = new ArrayList<Evento>();

		System.out.println("Simulador de Avalia��o e Desempenho");
		System.out.println("Grupo: Fernando, Peter, Victor, Zaedy\n");		
		System.out.println("Favor entrar com os dados para a inicializa��o do simulador");
		
		/* Este trecho de c�digo apenas faz a leitura via console dos par�metros necess�rios para iniciar a simula��o. Dentre esses
		 * par�metros, temos por exemplo a participa��o ou n�o de cada esta��o com tr�fego, se o tipo de chegada das mensagens � 
		 * determin�stica ou exponencial, qual o intervalo (m�dio no caso da chegada exponencial) entre a chegada das mensagens e 
		 * o n�mero de quadros que uma mensagem de uma esta��o conter�															*/
		for(int i = 0; i < 4; i++){
			estacoes.add(i,new Estacao(i+1));
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
							estacoes.get(i).setTipoChegada(Estacao.DETERMINISTICO);
						}else if(opcaoChar.equalsIgnoreCase("E")){
							estacoes.get(i).setTipoChegada(Estacao.EXPONENCIAL);
						}else {
							System.out.println("Op��o inv�lida.\nTr�fego determin�stico (D) ou exponencial (E) ?");
							opcaoChar = "Erro";
						}
					}while(opcaoChar.equalsIgnoreCase("Erro"));
					System.out.println("Digite o intervalo m�dio (em segundos) entre as chegadas das mensagens na Esta��o " + (i+1) + ":");
					do{
						try{
							opcaoDouble = Double.parseDouble(leTeclado.next());
							estacoes.get(i).setIntervaloEntreChegadas(opcaoDouble*Constantes.SEGUNDO_EM_MILISSEGUNDOS);
						}catch(Exception e){
							System.out.println("Tempo inv�lido.\nDigite o intervalo m�dio (em segundos) entre as chegadas das mensagens na Esta��o " + (i+1) + ":");
							opcaoDouble = -1.0;
						}
					}while(opcaoDouble.equals(-1.0));
					System.out.println("Digite o quantidade de quadros das mensagens na Esta��o " + (i+1) + ":");
					do{
						try{
							opcaoDouble = Double.parseDouble(leTeclado.next());
							estacoes.get(i).setQuantidadeQuadros(opcaoDouble);
						}catch(Exception e){
							System.out.println("Tempo inv�lido.\nDigite o quantidade de quadros das mensagens na Esta��o " + (i+1) + ":");
							opcaoDouble = -1.0;
						}
					}while(opcaoDouble.equals(-1.0));
					listaEventos.add(servicos.geraEvento(Evento.CHEGA_MENSAGEM, servicos.geraProximaMensagem(estacoes.get(i), 0.0), estacoes.get(i), estacoes));
				}else if(opcaoInt != 0){
					System.out.print("Op��o inv�lida.\nEsta��o " + (i+1) + " - Sem tr�fego (0) ou Com tr�fego (1) ?\n");
					opcaoInt = -1;
				}
			}while (opcaoInt == -1);
		}
		
		/* Envia para o objeto simulador a lista de eventos montada com o primeiro evento apenas, que � a chegada da primeira mensagem
		 * programada dependendo do tipo de chegada (determin�stica ou exponencial) e inicia a simula��o. Novos eventos ser�o gerados
		 * e adicionados a lista de eventos conforme necess�rio																		*/
		simulador.setListaEventos(listaEventos);
		simulador.start();
	}
}
