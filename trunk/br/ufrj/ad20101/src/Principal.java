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

		// Declaração das variáveis que receberão as entradas digitadas pelo usuário 
		int opcaoInt;
		String opcaoChar;
		Double opcaoDouble;
		
		/* Instancia um objeto do tipo Serviço que tem por objetivo gerar um evento do tipo Chegada de Mensagem,
		   que será o primeiro evento da lista de eventos, a partir do qual os outros eventos serão gerados */
		Servicos servicos = new Servicos();

		// Cria uma lista com as quatro estações participantes do ambiente de simulação
		ArrayList <Estacao> estacoes = new ArrayList<Estacao>(4);
		
		// Cria um objeto da classe simulador, responsável pelo loop principal de envio de mensagens 
		// pelas estações e pela coleta das estatísticas
		Simulador simulador = new Simulador();
		
		// Cria a variável leTeclado para ler os dados fornecidos pelo usuário no inicio da simulação
		Scanner leTeclado = new Scanner(System.in);
		
		// Cria a lista de eventos inicialmente vazia
		ArrayList<Evento> listaEventos = new ArrayList<Evento>();

		System.out.println("Simulador de Avaliação e Desempenho");
		System.out.println("Grupo: Fernando, Peter, Victor, Zaedy\n");		
		System.out.println("Favor entrar com os dados para a inicialização do simulador");
		
		/* Este trecho de código apenas faz a leitura via console dos parâmetros necessários para iniciar a simulação. Dentre esses
		 * parâmetros, temos por exemplo a participação ou não de cada estação com tráfego, se o tipo de chegada das mensagens é 
		 * determinística ou exponencial, qual o intervalo (médio no caso da chegada exponencial) entre a chegada das mensagens e 
		 * o número de quadros que uma mensagem de uma estação conterá															*/
		for(int i = 0; i < 4; i++){
			estacoes.add(i,new Estacao(i+1));
			System.out.print("Estação " + (i+1) + " - Sem tráfego (0) ou Com tráfego (1) ?\n");
			do{
				try{
					opcaoInt = Integer.parseInt(leTeclado.next());
				}catch(Exception e){
					opcaoInt = -1;
				}
				if(opcaoInt == 1){
					System.out.println("Para a estação "+ (i+1) + ":");
					System.out.println("Tráfego determinístico (D) ou exponencial (E) ?");
					do{
						opcaoChar = leTeclado.next();
						if(opcaoChar.equalsIgnoreCase("D")){
							estacoes.get(i).setTipoChegada(Estacao.DETERMINISTICO);
						}else if(opcaoChar.equalsIgnoreCase("E")){
							estacoes.get(i).setTipoChegada(Estacao.EXPONENCIAL);
						}else {
							System.out.println("Opção inválida.\nTráfego determinístico (D) ou exponencial (E) ?");
							opcaoChar = "Erro";
						}
					}while(opcaoChar.equalsIgnoreCase("Erro"));
					System.out.println("Digite o intervalo médio (em segundos) entre as chegadas das mensagens na Estação " + (i+1) + ":");
					do{
						try{
							opcaoDouble = Double.parseDouble(leTeclado.next());
							estacoes.get(i).setIntervaloEntreChegadas(opcaoDouble*Constantes.SEGUNDO_EM_MILISSEGUNDOS);
						}catch(Exception e){
							System.out.println("Tempo inválido.\nDigite o intervalo médio (em segundos) entre as chegadas das mensagens na Estação " + (i+1) + ":");
							opcaoDouble = -1.0;
						}
					}while(opcaoDouble.equals(-1.0));
					System.out.println("Digite o quantidade de quadros das mensagens na Estação " + (i+1) + ":");
					do{
						try{
							opcaoDouble = Double.parseDouble(leTeclado.next());
							estacoes.get(i).setQuantidadeQuadros(opcaoDouble);
						}catch(Exception e){
							System.out.println("Tempo inválido.\nDigite o quantidade de quadros das mensagens na Estação " + (i+1) + ":");
							opcaoDouble = -1.0;
						}
					}while(opcaoDouble.equals(-1.0));
					listaEventos.add(servicos.geraEvento(Evento.CHEGA_MENSAGEM, servicos.geraProximaMensagem(estacoes.get(i), 0.0), estacoes.get(i), estacoes));
				}else if(opcaoInt != 0){
					System.out.print("Opção inválida.\nEstação " + (i+1) + " - Sem tráfego (0) ou Com tráfego (1) ?\n");
					opcaoInt = -1;
				}
			}while (opcaoInt == -1);
		}
		
		/* Envia para o objeto simulador a lista de eventos montada com o primeiro evento apenas, que é a chegada da primeira mensagem
		 * programada dependendo do tipo de chegada (determinística ou exponencial) e inicia a simulação. Novos eventos serão gerados
		 * e adicionados a lista de eventos conforme necessário																		*/
		simulador.setListaEventos(listaEventos);
		simulador.start();
	}
}
