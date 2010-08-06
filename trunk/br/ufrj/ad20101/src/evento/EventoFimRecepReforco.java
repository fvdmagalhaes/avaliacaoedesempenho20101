package br.ufrj.ad20101.src.evento;

import java.util.ArrayList;

import br.ufrj.ad20101.src.estacao.Estacao;
import br.ufrj.ad20101.src.servicos.Constantes;
import br.ufrj.ad20101.src.servicos.Servicos;

public class EventoFimRecepReforco extends Evento{
	
	 private int quantidadeQuadro;
	 private int quantidadeTentativas;
	 private boolean colisaoPendente=false;
	
	public int getQuantidadeQuadro() {
		return quantidadeQuadro;
	}

	public void setQuantidadeQuadro(int quantidadeQuadro) {
		this.quantidadeQuadro = quantidadeQuadro;
	}

	public int getQuantidadeTentativas() {
		return quantidadeTentativas;
	}

	public void setQuantidadeTentativas(int quantidadeTentativas) {
		this.quantidadeTentativas = quantidadeTentativas;
	}

	public boolean isColisaoPendente() {
		return colisaoPendente;
	}

	public void setColisaoPendente(boolean colisaoPendente) {
		this.colisaoPendente = colisaoPendente;
	}

	public EventoFimRecepReforco(Double tempoInicio, ArrayList<Estacao> estacoes, Estacao estacao){
		this.setTempoInicial(tempoInicio);
		this.setEstacao(estacao);
		this.setEstacoes(estacoes);
		this.setTipoEvento(FIM_RECEP_REFORCO);
	}
	
	/*TODO
	 * Esta classe simula o final da transmiss�o do refor�o de colis�o por uma Esta��o
	 * que acabou de receber o refor�o de colis�o
	 * */
	
	@Override
	public ArrayList<Evento> acao(ArrayList<Evento> listaEventos){
		
		Servicos servicos = new Servicos();
		
		if(this.getEstacao().getEstado() == Estacao.ESTADO_RECEBENDO)
		{
			EventoIniciaTransmissao eventoIniciaTransmiss�o = this.getEstacao().getQuadroSentindoMeio();
			if(eventoIniciaTransmiss�o != null)
			{
				eventoIniciaTransmiss�o.setTempoInicial(this.getTempoInicial() + Constantes.INTERVALO_ENTRE_QUADROS);
				listaEventos.add(eventoIniciaTransmiss�o);
				this.getEstacao().setEstado(Estacao.ESTADO_PREPARANDO_TRANSFERIR);
			}
			else if(!this.getEstacao().getMensagensPendentes().isEmpty())
			{
				EventoPrepararTransmissao pegaEvPrepTransm = (EventoPrepararTransmissao)this.getEstacao().getMensagensPendentes().get(0);
				pegaEvPrepTransm.setTempoInicial(this.getTempoInicial());
				this.getEstacao().getMensagensPendentes().remove(0);
				listaEventos.add(pegaEvPrepTransm);
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}
			else
			{
				this.getEstacao().setEstado(Estacao.ESTADO_OCIOSO);
			}
			
		}
		else if(this.getEstacao().getEstado() == Estacao.ESTADO_TRATANDO_COLISAO_OCUPADO)
		{
			if(isColisaoPendente()) //TODO pensar sobre o caso de mais de duas esta��es lan�arem refor�o
			{
				//Gera o Evento de Colis�o, que come�ar� a ser tratado neste instante
				EventoColisao eventoColisao = (EventoColisao) servicos.geraEvento(COLISAO, this.getTempoInicial(), this.getEstacao(), this.getEstacoes());

				//passa as informa��es necess�rias para o Evento de Colis�o
				eventoColisao.setQuantidadeQuadro(this.quantidadeQuadro);
				eventoColisao.setQuantidadeTentativas(this.quantidadeTentativas);
				 //adiciona o Evento de Colis�o � lista de Eventos
				listaEventos.add(eventoColisao);
				return listaEventos;
			}
			else
			{
				this.getEstacao().setEstado(Estacao.ESTADO_TRATANDO_COLISAO_OCIOSO);
			}
		}
		else
		{
			System.out.println("ERRO: Esta��o se encontra em um estado inexistente!");
		}
		
		return listaEventos;
	}
}