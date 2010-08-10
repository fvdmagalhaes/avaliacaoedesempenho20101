package br.ufrj.ad20101.src.simulador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.estatisticas.ColetaEstatistica;
import br.ufrj.ad20101.src.evento.Evento;
import br.ufrj.ad20101.src.servicos.Servicos;

/*
 * Esta Classe representa o simulador no modo Debug.
 * Utilizada para as correções.
 * Log em arquivo de texto (logSimulador.txt) na pasta raíz do projeto.
 * */

public class SimuladorDebug {
	
	// Define o arquivo de texto aonde sera gravado o resultado do log
	static File arquivoLog = new File("logSimulador.txt");
	static FileOutputStream fos;
	
	//tamanho da rodada foi definido como 3 minutos (180 segundos)
	final static Double tamanhoRodada = 60000.0; 
	
	// Flag que indica a classe simulador se o modo de gravação de log está ativo ou não
	private static boolean isDebbuging = false;
	
	//arraylist que contém a lista de eventos do simulador
	private ArrayList<Evento> listaEventos = new ArrayList<Evento>();
	
	//arrays que guardarão as amostras geradas em cada rodada
	Double[][] amostrasTap = new Double[4][100];
	Double[][] amostrasTam = new Double[4][100];
	Double[][] amostrasNcm = new Double[4][100];
	Double[][] amostrasVazao = new Double[4][100];
	Double[] amostrasUtilizacao = new Double[100];
	
	//Método que inicia o simulador
	public void start(){
		//instancia a classe que vai calcular todas as estatísticas solicitadas
		ColetaEstatistica coletaEstatistica = new ColetaEstatistica();
		coletaEstatistica.setFaseTransiente(true);
		//armazena o evento atual
		Evento eventoCorrente = null;
		//este flag informa se todos os intervalos estão dentro do desejado, ou seja, se a simulação deve ser encerrada
		boolean fimSimulacao = false;
		//este flag indica se a fase transiente ainda não terminou
		boolean faseTransiente = true;
		//esta variável guarda o valor da rodada atual
		int rodada = 0;
		//esta variável guarda o tempo inicial da rodada
		Double tempoInicioRodada = 0.0;
		
		//primeiramente o simulador roda a fase transiente para desprezar os dados gerados nela
		while(faseTransiente){
			
			//A lista de Eventos é ordenada por tempo do menor para o maior
			Collections.sort(this.listaEventos);
			//O primeiro Evento da lista, ou seja, aquele que deve acontecer antes de todos os outros é executado
			this.listaEventos = this.listaEventos.get(0).acao(this.listaEventos);
			//em seguida ele é retirado da lista de Eventos
			eventoCorrente = this.listaEventos.remove(0);
			//e, por último, usado no cálculo das estatísticas
			coletaEstatistica.coletar(eventoCorrente, rodada);
			//verifica se a fase transiente chegou ao fim
			faseTransiente = coletaEstatistica.isFaseTransiente();
		
		}
		
		//com o fim da fase transiente, começa a coleta das estatísticas
		//enquanto algum dos intervalos não estiver dentro do desejado, a simulação continua
		while(!fimSimulacao){
			
			//início da próxima rodada
			rodada ++;
			//seta inicial da rodada
			tempoInicioRodada = eventoCorrente.getTempoInicial();
			//a cada nova rodada as estatisticas geradas devem ser zeradas
			coletaEstatistica = new ColetaEstatistica();
			
			//este loop só acaba quando a rodada corrente chega ao final
			do{
				
				//A lista de Eventos é ordenada por tempo do menor para o maior
				Collections.sort(this.listaEventos);
				//toda nova chegada que acontecer durante essa rodada deve ter o código dela
				if(listaEventos.get(0).getTipoEvento() == Evento.CHEGA_MENSAGEM){
					listaEventos.get(0).setRodada(rodada);
				}
				//O primeiro Evento da lista, ou seja, aquele que deve acontecer antes de todos os outros é executado
				this.listaEventos = this.listaEventos.get(0).acao(this.listaEventos);
				//em seguida ele é retirado da lista de Eventos
				eventoCorrente = this.listaEventos.remove(0);
				//e, por último, usado no cálculo das estatísticas
				coletaEstatistica.coletar(eventoCorrente, rodada);
			
			}while(eventoCorrente.getTempoInicial() - tempoInicioRodada < tamanhoRodada);
			
			//armazena todas as amostras geradas
			for(int i = 0; i < 4; i ++){
				
				amostrasTap[i][rodada-1] = coletaEstatistica.getTap()[i].amostra;
				amostrasTam[i][rodada-1] = coletaEstatistica.getTam()[i].amostra;
				amostrasNcm[i][rodada-1] = coletaEstatistica.getNcm()[i].amostra;
				amostrasVazao[i][rodada-1] = coletaEstatistica.getVazao()[i].amostra;
			
			}
			amostrasUtilizacao[rodada-1] = coletaEstatistica.getUtilizacao().amostra;
			
			//testa se deve calcular o intervalo de confiança para ver se está de acordo com o pedido
			if(rodada >= 30){
				
				fimSimulacao = calculaIntervalo (rodada, eventoCorrente.getEstacoes());
			
			}
		}
		//apenas informa que a simulação chegou ao fim
		System.out.println("Fim da simulação");
	}
	
	
	//este método calcula os intervalos de confiança e verifica se estão de acordo com o desejado
	private boolean calculaIntervalo (int rodadas, ArrayList<Estacao> estacoes){
		
		Servicos servicos = new Servicos();
		//media da utilizacao
		Double media = servicos.media(amostrasUtilizacao, rodadas);
		//largura do intervalo de confiança da utilizacao
		Double intervalo = 2*1.96*servicos.desvioPadrao(amostrasUtilizacao, rodadas, media)/Math.sqrt(rodadas);
		if(intervalo <= 0.1*media){
			for(int i = 0; i < 4; i ++){
				if(estacoes.get(i).getTipoChegada() != 0){
					//media do tap
					media = servicos.media(amostrasTap[i], rodadas);
					//largura do intervalo de confiança do tap
					intervalo = 2*1.96*servicos.desvioPadrao(amostrasTap[i], rodadas, media)/Math.sqrt(rodadas);
					if(intervalo > 0.1*media){
						return false;
					}
					
					//media do tam
					media = servicos.media(amostrasTam[i], rodadas);
					//largura do intervalo de confiança do tam
					intervalo = 2*1.96*servicos.desvioPadrao(amostrasTam[i], rodadas, media)/Math.sqrt(rodadas);
					if(intervalo > 0.1*media){
						return false;
					}
					
					//media do ncm
					media = servicos.media(amostrasNcm[i], rodadas);
					//largura do intervalo de confiança do ncm
					intervalo = 2*1.96*servicos.desvioPadrao(amostrasNcm[i], rodadas, media)/Math.sqrt(rodadas);
					if(intervalo > 0.1*media){
						return false;
					}
					
					//media da vazão
					media = servicos.media(amostrasVazao[i], rodadas);
					//largura do intervalo de confiança da vazão
					intervalo = 2*1.96*servicos.desvioPadrao(amostrasVazao[i], rodadas, media)/Math.sqrt(rodadas);
					if(intervalo > 0.1*media){
						return false;
					}
				}
			}
		}else{
			return false;
		}
		return true;
	}
	
	
	public void setListaEventos(ArrayList<Evento> listaEventos) {
		this.listaEventos = listaEventos;
	}

	public ArrayList<Evento> getListaEventos() {
		return listaEventos;
	}
	
	public static boolean isDebbuging() {
		return isDebbuging;
	}

	public void setDebbuging(boolean isDebbuging) {
		SimuladorDebug.isDebbuging = isDebbuging;
	}
	
	/* Cria um buffer responsável pela escrita no arquivo de log. O parâmetro false indica que o arquivo de 
	 * texto, se existir, será sobrescrito ao gerar um novo log
	 * */		
	public SimuladorDebug() 
	{
		try {
			fos = new FileOutputStream(arquivoLog,false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}

	/*
	 * Cada evento chama esta função EscreveLog passando a mensagem que deverá ser inserida no log.
	 * Simplesmente utiliza o buffer "fos" de escrita do arquivo criado anteriormente e escreve a mensagem
	 * no arquivo.
	 */
	public static void escreveLog(String mensagem)
	{		
		try 
		{
			fos.write(mensagem.getBytes());			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * A função encerraLog é responsável apenas por fechar o arquivo. É invocada no final da simulação,
	 * quando não há mais nada a ser escrito no arquivo.
	 */
	public void encerraLog()
	{
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
