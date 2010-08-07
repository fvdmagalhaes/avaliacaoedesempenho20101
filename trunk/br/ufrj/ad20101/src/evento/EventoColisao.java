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
	 * Esta classe simula o tratamento de uma Colis�o.
	 * Este Evento apenas aplica o algoritmo Binary Backoff para retransmitir o quadro
	 * que colidiu.
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		SimuladorDebug simulador = new SimuladorDebug();
		simulador.escreveLog("EVENTO COLISAO OCORREU EM " + this.getTempoInicial() + " NA ESTA��O " + this.getEstacao().getIdentificador()+"\n");
		
		//criando a classe de servi�o
		Servicos servicos = new Servicos();
		
		//testa se o quadro deve ser descartado
		if(this.getQuantidadeTentativas()<=15){
			//caso n�o seja descartado, ele testa o Estado da Esta��o
			if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
				//aplica o Algoritmo Binary Backoff
				Double tempoRetransmissao = servicos.binaryBackoff(this.getQuantidadeTentativas());
				//testa se deve ser transmitido neste instante ou n�o
				if(tempoRetransmissao.equals(0.0)){
					//altera o Estado para preparando transmiss�o
					this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
					//gera o evento de in�cio de transmiss�o do quadro que colidiu
					EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(),this.getEstacoes());
					//o quadro deve ser exatamente o mesmo, pois n�o houve descarte
					eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
					//incrementa o n�mero de tentativas
					eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas+1);
					//adiciona � lista de Eventos
					listaEventos.add(eventoIniciaTransmissao);
				}else{
					//caso o quadro seja retransmitido no futuro:
					//um Evento de retransmiss�o � gerado
					//o Estado se mant�m em Tratando Colis�o, pois ele n�o pode transmitir nada antes de retransmitir este quadro
					EventoRetransmitir eventoRetransmitir = (EventoRetransmitir)servicos.geraEvento(RETRANSMITIR, this.getTempoInicial() + tempoRetransmissao, this.getEstacao(), this.getEstacoes());
					//o quadro deve ser exatamente o mesmo, pois n�o houve descarte
					eventoRetransmitir.setQuantidadeQuadro(this.getQuantidadeQuadro());
					//incrementa o n�mero de tentativas
					eventoRetransmitir.setQuantidadeTentativas(this.getQuantidadeTentativas()+1);
					//adiciona � lista de Eventos
					listaEventos.add(eventoRetransmitir);
				}
			}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
				//neste caso o meio est� ocupado, portanto independente do que resultar do 
				//Algoritmo Binary Backoff, o quadro dever� aguardar o meio desocupar para transmitir
				//aplica o Algoritmo Binary Backoff
				Double tempoRetransmissao = servicos.binaryBackoff(this.getQuantidadeTentativas());
				//testa se deve ser transmitido neste instante ou n�o
				if(tempoRetransmissao.equals(0.0)){
					//nesta situa��o o quadro a ser retransmitido vai ficar aguardando o meio ficar livre
					//o Estado da Esta��o muda para recebendo e ao fim da recep��o retransmitir� este quadro
					this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
					//gera o evento de in�cio de transmiss�o do quadro que colidiu
					EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao) servicos.geraEvento(INICIA_TRANSMISSAO, null, this.getEstacao(),this.getEstacoes());
					//o quadro deve ser exatamente o mesmo, pois n�o houve descarte
					eventoIniciaTransmissao.setQuantidadeQuadro(this.quantidadeQuadro);
					//incrementa o n�mero de tentativas
					eventoIniciaTransmissao.setQuantidadeTentativas(this.quantidadeTentativas+1);
					//quadro passa a sentir o meio at� que desocupe
					this.getEstacao().setQuadroSentindoMeio(eventoIniciaTransmissao);
				}else{
					//nessa situa��o o tratamento ser� exatamente o mesmo que se o meio estivesse ocioso:
					//um Evento de retransmiss�o � gerado
					//o Estado se mant�m em Tratando Colis�o, pois ele n�o pode transmitir nada antes de retransmitir este quadro
					EventoRetransmitir eventoRetransmitir = (EventoRetransmitir)servicos.geraEvento(RETRANSMITIR, this.getTempoInicial() + tempoRetransmissao, this.getEstacao(), this.getEstacoes());
					//o quadro deve ser exatamente o mesmo, pois n�o houve descarte
					eventoRetransmitir.setQuantidadeQuadro(this.getQuantidadeQuadro());
					//incrementa o n�mero de tentativas
					eventoRetransmitir.setQuantidadeTentativas(this.getQuantidadeTentativas()+1);
					//adiciona � lista de Eventos
					listaEventos.add(eventoRetransmitir);
				}
			}else{
				System.out.println("ERRO: Esta��o se encontra num estado n�o existente");
				System.exit(0);
			}
		}else{
			//cria o evento que descarta quadro
			EventoDescartaQuadro eventoDescartaQuadro = (EventoDescartaQuadro)servicos.geraEvento(DESCARTA_QUADRO, getTempoInicial(), this.getEstacao(), this.getEstacoes());
			//informa o quadro que foi descartado
			eventoDescartaQuadro.setQuantidadeQuadro(this.getQuantidadeQuadro());
			//adiciona Evento � lista de Eventos
			listaEventos.add(eventoDescartaQuadro);
			
			//testa se o quadro � o �ltimo da mensagem
			if(this.quantidadeQuadro > 1){	
				//gera o evento de transmiss�o do pr�ximo quadro
				EventoIniciaTransmissao eventoIniciaTransmissao = (EventoIniciaTransmissao)servicos.geraEvento(INICIA_TRANSMISSAO, this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS, this.getEstacao(), this.getEstacoes());
				//pega o pr�ximo quadro
				eventoIniciaTransmissao.setQuantidadeQuadro(getQuantidadeQuadro()-1);
				//n�mero de tentativas volta para 1, pois este j� � um novo quadro
				eventoIniciaTransmissao.setQuantidadeTentativas(1);
				//testa se o meio est� ocupado ou n�o
				if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
					//se o meio estiver livre, mude o estado para preparando trasmissao e transmita o quadro
					this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
					listaEventos.add(eventoIniciaTransmissao);
				}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
					//caso contr�rio, o estado ser� recebendo e continua sentindo o meio
					this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
					this.getEstacao().setQuadroSentindoMeio(eventoIniciaTransmissao);
				}else{
					System.out.println("ERRO: Esta��o se encontra num estado n�o existente");
					System.exit(0);
				}
			}else{
				if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO){
					//Estado ser� alterado para Ocioso, pois o tratamento da Colis�o j� terminou e n�o h� retransmiss�es pendentes
					this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
				}else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO){
					//Estado ser� alterado para Recebendo, pois o tratamento da Colis�o j� terminou e n�o h� retransmiss�es pendentes
					this.getEstacao().setEstado(Estacao.ESTADO_RECEBENDO);
				}
				//o quadro descartado foi o �ltimo da mensage, portanto a mensagem acabou 
				EventoFimMensagem eventoFimMensagem = (EventoFimMensagem)servicos.geraEvento(FIM_MENSAGEM, this.getTempoInicial(), this.getEstacao(), this.getEstacoes());
				//adicionar o Evento � lista de Eventos
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
