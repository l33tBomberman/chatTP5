/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.cliente;

import chattrabalho.LeandroUteis.Uteis;
import chattrabalho.Mensagens.Exception.ServicoInexistenteException;
import chattrabalho.Mensagens.Mensagem;
import chattrabalho.Mensagens.MensagemClientesConectados;
import chattrabalho.Mensagens.MensagemEnviarMensagem;
import chattrabalho.Mensagens.MensagemMudarApelido;
import chattrabalho.Mensagens.MensagemOla;
import chattrabalho.Mensagens.MensagemRequisitaApelido;
import chattrabalho.Mensagens.MensagemServicoNegado;
import chattrabalho.Mensagens.MensagemTchau;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro_000
 */
public class BufferRecebidos implements Runnable {
    private final ArrayList<Mensagem> listaDeMensagens;
    private final ArrayList<String>   mensagemPosTratada;
    
    //Essa classe pode escrever no apelido e no ID do ClienteNegocios, logo precisa
    //poder referenciá-los.
    private final ClienteNegocios cliente;
    
    public BufferRecebidos(ClienteNegocios cliente){
        this.listaDeMensagens   = new ArrayList<>();
        this.mensagemPosTratada = new ArrayList<>();
        this.cliente            = cliente;
    }
    
    private Mensagem retornaMensagem(){
        return listaDeMensagens.remove(0);
    }
    
    public String pegaMensagem(){
        return this.mensagemPosTratada.remove(0);
    }
    
    public boolean temMensagemTratada(){
        return this.mensagemPosTratada.isEmpty();
    }
    
    public synchronized void adicionaMensagem(Mensagem mensagem){
        listaDeMensagens.add(mensagem);
        notifyAll();
    }
    
    public void adicionaMensagemTratada(String texto){
        this.mensagemPosTratada.add(texto);
        System.out.println(texto);
    }

    @SuppressWarnings("FinalizeCalledExplicitly")
    public void leMensagem() {
        Mensagem mensagemASerInterpretada = this.retornaMensagem();
        try {
            switch(mensagemASerInterpretada.retornaServico()){
                case (byte)0x01:
                    //Ola
                    MensagemOla M01 = (MensagemOla)mensagemASerInterpretada;  
                    String Texto01;
                    
                    if(!this.cliente.verificaSeIDJaFoiSetado()){
                        int ID = Uteis.converteVetorBytesEmInt(M01.retornaDados());
                        Texto01 = "Seu ID é " + ID;
                        this.cliente.setaID(ID);
                    } else {
                        Texto01 = "Recebeu mensagem de olá, contudo seu ID já foi setado";
                    }
                    
                    this.adicionaMensagemTratada(Texto01);
                    break;
                    
                case (byte)0x02:
                    //Mudar apelido
                    MensagemMudarApelido M02 = (MensagemMudarApelido)mensagemASerInterpretada;
                    byte[] dados02 = mensagemASerInterpretada.retornaDados();
                    
                    byte[] quemMudou = new byte[4];
                    byte[] apelidoNovo = new  byte[dados02.length - 4];
                    
                    System.arraycopy(dados02, 0, quemMudou, 0, quemMudou.length);
                    System.arraycopy(dados02, 4, apelidoNovo, 0, apelidoNovo.length);
                    
                    int ID = Uteis.converteVetorBytesEmInt(quemMudou);
                    String apelido = Uteis.converteVetorBytesEmString(apelidoNovo);
                    
                    String Texto02;
                    Texto02 = "O cliente de ID " + ID + " agora se identifica por " + apelido;
                    
                    this.adicionaMensagemTratada(Texto02);
                    break;
                    
                case (byte)0x03:
                    //Clientes Conectados
                    MensagemClientesConectados M03 = (MensagemClientesConectados)mensagemASerInterpretada;
                    
                    int tamanho = M03.retornaTamanho();
                    byte[] dados03 = M03.retornaDados();
                    ArrayList<Integer> Array = new ArrayList<>();
                    
                    for (int i = 0; i < (tamanho / 4); i++){
                        byte[] inteiro = new byte[4];
                        System.arraycopy(dados03, i*4, inteiro, 0, inteiro.length);
                        int Inteiro = Uteis.converteVetorBytesEmInt(inteiro);
                        Array.add(Inteiro);
                    }
                    
                    this.cliente.setaConectados(Array);
                    System.out.println("Lista nova foi adicionada");
                    break;
                    
                case (byte)0x04:
                    //Requisita Apelid
                    MensagemRequisitaApelido M04 = (MensagemRequisitaApelido)mensagemASerInterpretada;
                    byte[] dados04 = M04.retornaDados();
                    String apelido04 = Uteis.converteVetorBytesEmString(dados04);
                    this.cliente.AdicionaApelidoALista(apelido04);
                    
                    System.out.println("O apelido " + apelido04 +" foi adicionado a lista de apelidos");
                    break;
                    
                case (byte)0x05:
                    //Requisita Apelido
                    MensagemEnviarMensagem M05 = (MensagemEnviarMensagem)mensagemASerInterpretada;
                    byte[] dados            = mensagemASerInterpretada.retornaDados();
                    byte[] remetenteByte    = new byte[4];
                    byte[] destinatarioByte = new byte[4];
                    byte[] conteudoByte     = new byte[dados.length - 8];
                    
                    System.arraycopy(dados, 0, remetenteByte,    0, 4);
                    System.arraycopy(dados, 4, destinatarioByte, 0, 4);
                    System.arraycopy(dados, 8, conteudoByte,     0, conteudoByte.length);
                    
                    int remetente    = Uteis.converteVetorBytesEmInt(remetenteByte);
                    int destinatario = Uteis.converteVetorBytesEmInt(destinatarioByte);
                    String conteudo  = Uteis.converteVetorBytesEmString(conteudoByte);
                    
                    
                    String remetenteString;
                    String destinatarioString;
                    
                    remetenteString = this.cliente.getApelidoByID(remetente);
                    if (destinatario != 0){
                        destinatarioString = this.cliente.getApelidoByID(destinatario);
                    } else destinatarioString = "todos";
                    
                    String Texto05 = "< "+ remetenteString +" para " + destinatarioString +" > " + conteudo;
                    this.adicionaMensagemTratada(Texto05);
                    break;
                    
                case (byte)0x0A:
                    //Tchau
                    MensagemTchau M0A = (MensagemTchau)mensagemASerInterpretada;
                    int IDSaida = Uteis.converteVetorBytesEmInt(M0A.retornaDados());
                    
                    if ( IDSaida == this.cliente.retornaID() ){
                        System.out.println("É hora de dizer tchau!");
                        this.cliente.finalize();
                    } else {
                        String tchau = "O cliente de ID " + IDSaida + " acabo de deixar o chat";
                        this.adicionaMensagemTratada(tchau);
                    }
                    
                    break;
                    
                case (byte)0x0F:
                    //Serviço negado
                    MensagemServicoNegado M0F = (MensagemServicoNegado)mensagemASerInterpretada;
                    byte nack = M0F.getNack();
                    switch(nack){
                        case (byte)0xBB:
                            String TextoNackBB = "Nack 0xBB: Cliente já registrado";
                            this.adicionaMensagemTratada(TextoNackBB);
                        break;
                        case (byte)0xCC:
                            String TextoNackCC = "Nack 0xCC: Cliente não identificado";
                            this.adicionaMensagemTratada(TextoNackCC);
                        break;
                        case (byte)0xDD:
                            String TextoNackDD = "Nack 0xDD: Mensagm mal formada";
                            this.adicionaMensagemTratada(TextoNackDD);
                        break;
                        case (byte)0xEE:
                            String TextoNackEE = "Nack 0xEE: Cliente já registrado";
                            this.adicionaMensagemTratada(TextoNackEE);
                        break;
                        case (byte)0xFF:
                            String TextoNackFF = "Nack 0xFF: Cliente já registrado";
                            this.adicionaMensagemTratada(TextoNackFF);
                        break;
                        case (byte)0x02:
                            String TextoNack02 = "Nack 0x02: Cliente já registrado";
                            this.adicionaMensagemTratada(TextoNack02);
                        break;
                        default:
                            String DefaultNack = "O servidor enviou um nack não catalogado pelo protocolo.";
                            this.adicionaMensagemTratada(DefaultNack);
                        break;
                    }
                    break;                    
                default:
                    throw new ServicoInexistenteException();
            } 
        } catch (ServicoInexistenteException ex) {
            System.out.println("Erro Inesperado: O serviço recebido não consta na lista");
        } catch (Throwable ex) {
            Logger.getLogger(BufferRecebidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        while (true){
            if (this.listaDeMensagens.isEmpty()){
                synchronized(this){
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        // TODO
                        System.out.println("Deu algum problema aqui! InterruptedException!");
                    }
                }
            }
            this.leMensagem();
        }
    }
    
}
