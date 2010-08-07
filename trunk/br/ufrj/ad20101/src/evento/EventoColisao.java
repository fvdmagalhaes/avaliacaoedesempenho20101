package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;
import br.ufrj.ad20101.src.simulador.SimuladorDebug;

public class EventoColisao extends Evento {
	
	private int quantidadeQuadro;
	private int quantidadeTentativas;
	
	public EventoColisao(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(COLISAO);
	}
	
	/*
	 * Esta classe simula o tratamento de uma Colisão.
	 * Este Evento apenas aplica o algoritmo Binary Backoff para retransmitir o quadro
	 * que colidiu.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		SimuladorDebug simulador = new SimuladorDebug();
		simulador.escreveLog("EVENTO COLISAO OCORREU EM " + this.getTempoInicial() + " NA ESTAÇÃO " + this.getEstacao().getIdentificador()+"\n");
		
		//criando a classe de serviço
		Servicos servicos = new Servicos();
		
		//testa se o quadro deve ser descartado
		if(this.getQuantidadeTentativas()<=15){
			//caso não seja descartado, ele testa o Estado da Estação
			if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
				//aplica o Algoritmo Binary Backoff
				Double tempoRetransmissao = servicos.binaryBackoff(this.getQuantidadeTentativas());
				//testa se deve ser transmitido neste instante ou não
				if(tempoRetransmissao.equals(0.0)){
					//altera o Estado para preparando transmissão
					this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
					//gera o evento de início de transmissão do quadro que colidiu
					EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(),this.getEstacoes());
					//o quadro deve ser exatamente o mesmo, pois não houve descarte
					eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
					//incrementa o número de tentativas
					eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas+1);
					//adiciona à lista de Eventos
					listaEventos.add(eventoIniciaTransmissao);
				}else{
					//caso o quadro seja retransmitido no futuro:
					//um Evento de retransmissão é gerado
					//o Estado se mantém em Tratando Colisão, pois ele não pode transmitir nada antes de retransmitir este quadro
					EventoRetransmitir eventoRetransmitir = (EventoRetransmitir)servicos.geraEvento(RETRANSMITIR, this.getTempoInicial() + tempoRetransmissao, this.getEstacao(), this.getEstacoes());
					//o quadro deve ser exatamente o mesmo, pois não houve descarte
					eventoRetransmitir.setQuantidadeQuadro(this.getQuantidadeQuadro());
					//incrementa o número de tentativas
					eventoRetransmitir.setQuantidadeTentativas(this.getQuantidadeTentativas()+1);
					//adiciona à lista de Eventos
					listaEventos.add(eventoRetransmitir);
				}
			}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
				//neste caso o meio está ocupado, portanto independente do que resultar do 
				//Algoritmo Binary Backoff, o quadro deverá aguardar o meio desocupar para transmitir
				//aplica o Algoritmo Binary Backoff
				Double tempoRetransmissao = servicos.binaryBackoff(this.getQuantidadeTentativas());
				//testa se deve ser transmitido neste instante ou não
				if(tempoRetransmissao.equals(0.0)){
					//nesta situação o quadro a ser retransmitido vai ficar aguardando o meio ficar livre
					//o Estado da Estação muda para recebendo e ao fim da recepção retransmitirá este quadro
					this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
					//gera o evento de início de transmissão do quadro que colidiu
					EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, null, this.getEstacao(),this.getEstacoes());
					//o quadro deve ser exatamente o mesmo, pois não houve descarte
					eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
					//incrementa o número de tentativas
					eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas+1);
					//quadro passa a sentir o meio até que desocupe
					this.getEstacao().setQuadroSentindoMeio(eventoIniciaTransmissao);
				}else{
					//nessa situação o tratamento será exatamente o mesmo que se o meio estivesse ocioso:
					//um Evento de retransmissão é gerado
					//o Estado se mantém em Tratando Colisão, pois ele não pode transmitir nada antes de retransmitir este quadro
					EventoRetransmitir eventoRetransmitir = (EventoRetransmitir)servicos.geraEvento(RETRANSMITIR, this.getTempoInicial() + tempoRetransmissao, this.getEstacao(), this.getEstacoes());
					//o quadro deve ser exatamente o mesmo, pois não houve descarte
					eventoRetransmitir.setQuantidadeQuadro(this.getQuantidadeQuadro());
					//incrementa o número de tentativas
					eventoRetransmitir.setQuantidadeTentativas(this.getQuantidadeTentativas()+1);
					//adiciona à lista de Eventos
					listaEventos.add(eventoRetransmitir);
				}
			}else{
				System.out.println("ERRO: Estação se encontra num estado não existente");
				System.exit(0);
			}
		}else{
			//cria o evento que descarta quadro
			EventoDescartaQuadro eventoDescartaQuadro = (EventoDescartaQuadro)servicos.geraEvento(DESCARTA_QUADRO, getTempoInicial(), this.getEstacao(), this.getEstacoes());
			//informa o quadro que foi descartado
			eventoDescartaQuadro.setQuantidadeQuadro(this.getQuantidadeQuadro());
			//adiciona Evento à lista de Eventos
			listaEventos.add(eventoDescartaQuadro);
			
			//testa se o quadro é o último da mensagem
			if(this.quantidadeQuadro > 1){	
				//gera o evento de transmissão do próximo quadro
				EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao)servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(), this.getEstacoes());
				//pega o próximo quadro
				eventoIniciaTransmissao.setQuantidadeQuadro(getQuantidadeQuadro()-1);
				//número de tentativas volta para 1, pois este já é um novo quadro
				eventoIniciaTransmissao.setQuantidadeTentativas(1);
				//testa se o meio está ocupado ou não
				if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
					//se o meio estiver livre, mude o estado para preparando trasmissao e transmita o quadro
					this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
					listaEventos.add(eventoIniciaTransmissao);
				}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
					//caso contrário, o estado será recebendo e continua sentindo o meio
					this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
					this.getEstacao().setQuadroSentindoMeio(eventoIniciaTransmissao);
				}else{
					System.out.println("ERRO: Estação se encontra num estado não existente");
					System.exit(0);
				}
			}else{
				if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
					//Estado será alterado para Ocioso, pois o tratamento da Colisão já terminou e não há retransmissões pendentes
					this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
				}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
					//Estado será alterado para Recebendo, pois o tratamento da Colisão já terminou e não há retransmissões pendentes
					this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
				}
				//o quadro descartado foi o último da mensage, portanto a mensagem acabou 
				EventoFimMensagem eventoFimMensagem = (EventoFimMensagem)servicos.geraEvento(FIM_MENSAGEM, this.getTempoInicial(), this.getEstacao(), this.getEstacoes());
				//adicionar o Evento à lista de Eventos
				listaEventos.add(eventoFimMensagem);
			}
		}
		return listaEventos;
	}

	public void setQuantidadeQuadro(int quantidadeQuadro) {
		this.quantidadeQuadro = quantidadeQuadro;
	}

	public int getQuantidadeQuadro() {
		return quantidadeQuadro;
	}

	public void setQuantidadeTentativas(int quantidadeTentativas) {
		this.quantidadeTentativas = quantidadeTentativas;
	}

	public int getQuantidadeTentativas() {
		return quantidadeTentativas;
	}
}
