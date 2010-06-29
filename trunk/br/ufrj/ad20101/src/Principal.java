package br.ufrj.ad20101.src;

import java.util.Scanner;

import br.ufrj.ad20101.src.estacao.Estacao;

public class Principal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner leTeclado = new Scanner(System.in);
		int opcaoInt;
		String opcaoChar;
		Double opcaoDouble;
		
		System.out.println("Simulador de Avaliação e Desempenho");
		System.out.println("Grupo: Fernando, Peter, Victor, Zaedy\n");		
		System.out.println("Favor entrar com os dados para a inicialização do simulador");
		
		System.out.print("Estação 1 - Sem tráfego (0) ou Com tráfego (1) ? ");
		opcaoInt = leTeclado.nextInt();
		if(opcaoInt == 1)
		{
			Estacao estacao1 = new Estacao();
			System.out.println("Para a estação 1:");
			System.out.println("Tráfego determinístico (D) ou exponencial (E) ? ");
			opcaoChar = leTeclado.next();
			if(opcaoChar.equals("D"))
				estacao1.setTipoChegada(Estacao.DETERMINISTICO);
			else if(opcaoChar.equals("E"))
				estacao1.setTipoChegada(Estacao.EXPONENCIAL);
			else
			{
				System.out.println("Opção inválida. O simulador irá terminar");
				System.exit(1);
			}
			System.out.println("Digite o intervalo entre chegada das mensagens (A1): ");
			opcaoDouble = leTeclado.nextDouble();
			estacao1.setIntervaloEntreChegadas(opcaoDouble);
		}
	}

}
