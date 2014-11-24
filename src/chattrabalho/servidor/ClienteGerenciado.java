/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.servidor;

import chattrabalho.servidor.Exception.ServicoInesperadoException;
import chattrabalho.LeandroUteis.Uteis;
import chattrabalho.Mensagens.Exception.CheckSumErradoException;
import chattrabalho.Mensagens.Exception.MensagemMuitoCurtaException;
import chattrabalho.Mensagens.Exception.MensagemMuitoGrandeException;
import chattrabalho.Mensagens.Mensagem;
import chattrabalho.Mensagens.MensagemClientesConectados;
import chattrabalho.Mensagens.MensagemEnviarMensagem;
import chattrabalho.Mensagens.MensagemMudarApelido;
import chattrabalho.Mensagens.MensagemOla;
import chattrabalho.Mensagens.MensagemRequisitaApelido;
import chattrabalho.Mensagens.MensagemServicoNegado;
import chattrabalho.Mensagens.MensagemTchau;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro
 */
public class ClienteGerenciado implements Runnable {
    private final Socket socket;
    private String Apelido = null;
    private final int clientID; 
    private static int lastClientID = 0;
    private final DataInputStream entrada;
    private final DataOutputStream saida;
    private static final ArrayList<MensagemTupla> mensagensRecebidas = new ArrayList<>(); 
   
   /**
    * Cria um objeto Client que representa uma conexão com um cliente do servidor.
    * @param socket Socket correspondente à conexão com o cliente.
     * @throws java.io.IOException
    */
   public ClienteGerenciado(Socket socket) throws IOException {
      this.socket = socket;
      this.entrada = new DataInputStream(this.socket.getInputStream());
      this.saida = new DataOutputStream(this.socket.getOutputStream());
      this.clientID = ++lastClientID;
      GerenteClientes.adicionaCliente(this);
   }
   
   public void adicionaMensagem(Mensagem mensagem){
       MensagemTupla mensagemASerAdicionada = new MensagemTupla(clientID, mensagem);
       ClienteGerenciado.mensagensRecebidas.add(mensagemASerAdicionada);
   }
   
   public int retornaID(){
       return this.clientID;
   }
   
   public String retornaApelido(){
       return this.Apelido;
   }
   
   public void enviaByte(byte b) throws IOException{
       if (!this.socket.isClosed()){
            this.saida.writeByte(b);
       } else {
           System.out.println("Não está conectado, mensagem descartada");
       }
   }
   
   public boolean temApelido(){
       return (this.Apelido == null);
   }
   
   public boolean osApelidosSaoIguais(String apelidoRecebido){
       return (this.Apelido == null ? apelidoRecebido == null : this.Apelido.equals(apelidoRecebido));
   }
   
   public static MensagemTupla pegaMensagemParaTratar(){
       return ClienteGerenciado.mensagensRecebidas.remove(0);
   }
   
   public static boolean vetorVazio(){
       return ClienteGerenciado.mensagensRecebidas.isEmpty();
   }

   public Socket getSocket() {
       return this.socket; 
   }
   
   public static void adicionaMensagemServicoNegado(byte Nack, int ID){
       MensagemServicoNegado MSN = new MensagemServicoNegado(Nack);
       MensagemTupla MT = new MensagemTupla(ID, MSN);
       ClienteGerenciado.mensagensRecebidas.add(MT);
   }
   
   public void adicionaMensagemServicoNegado(byte Nack){      
       MensagemServicoNegado MSN = new MensagemServicoNegado(Nack);
       this.adicionaMensagem(MSN);
   }
   
   public boolean temIDIgual(int ID){
       return this.clientID == ID;
   }
   
   public void setaApelido(String apelido){
       this.Apelido = apelido;
   }
   
   /**
    * Método chamada para iniciar o tratamento da thread.
    */
 @Override
 public void run() {
        while (true){
            if (!this.socket.isClosed()){
                try {
                    this.leMensagem();
                } catch (IOException ex) {
                    return;
                }
            } else {
                return; 
            }
        } 
   }

 public void leMensagem() throws IOException {
    try {
        byte servico = this.entrada.readByte();
        int tamanho  = this.entrada.readUnsignedShort();
        byte[] dados = new byte[tamanho];

        for(int i = 0; i < tamanho; i++){
            dados[i] = entrada.readByte();
        }

        int checksum = entrada.readUnsignedShort();

        switch(servico){
            case (byte)0x01:
                //Ola
                String apelido = Uteis.converteVetorBytesEmString(dados);
                MensagemOla M01  = new MensagemOla(apelido);
                if ((Mensagem.calculaCheckSum(M01) == checksum)){
                    this.adicionaMensagem(M01);
                } else {
                    throw new CheckSumErradoException();
                }
                break;

            case (byte)0x02:
                //Mudar apelido
                String apelidoCaso2 = Uteis.converteVetorBytesEmString(dados);
                MensagemMudarApelido M02 = new MensagemMudarApelido(apelidoCaso2);
                if (Mensagem.calculaCheckSum(M02) == checksum) {
                    this.adicionaMensagem(M02);
                } else {
                    throw new CheckSumErradoException();
                }

                break;

            case (byte)0x03:
                //Clientes Conectados
                MensagemClientesConectados M03 = new MensagemClientesConectados(tamanho, dados);
                if (Mensagem.calculaCheckSum(M03) == checksum){
                    this.adicionaMensagem(M03);
                } else {
                    throw new CheckSumErradoException();
                }

                break;

            case (byte)0x04:
                //Requisita Apelido
                String apelidoCaso4          = Uteis.converteVetorBytesEmString(dados);
                MensagemRequisitaApelido M04 = new MensagemRequisitaApelido(apelidoCaso4);
                if (Mensagem.calculaCheckSum(M04) == checksum){
                    this.adicionaMensagem(M04);
                } else {
                    throw new CheckSumErradoException();
                }

                break;

            case (byte)0x05:
                //Envia Mensagem
                byte[] remetenteByte    = new byte[4];
                byte[] destinatarioByte = new byte[4];
                byte[] conteudoByte     = new byte[dados.length - 8];

                System.arraycopy(dados, 0, remetenteByte,    0, 4);
                System.arraycopy(dados, 4, destinatarioByte, 0, 4);
                System.arraycopy(dados, 8, conteudoByte,     0, conteudoByte.length);

                int remetente    = Uteis.converteVetorBytesEmInt(remetenteByte);
                int destinatario = Uteis.converteVetorBytesEmInt(destinatarioByte);
                String conteudo  = Uteis.converteVetorBytesEmString(conteudoByte);

                MensagemEnviarMensagem M05 = new MensagemEnviarMensagem(remetente, destinatario, conteudo);
                if (Mensagem.calculaCheckSum(M05) == checksum){
                    this.adicionaMensagem(M05);
                } else {
                    throw new CheckSumErradoException();
                }
                break;

            case (byte)0x0A:
                //Tchau
                MensagemTchau M0A = new MensagemTchau();
                if(Mensagem.calculaCheckSum(M0A) == checksum){
                    this.adicionaMensagem(M0A);
                } else {
                    throw new CheckSumErradoException();
                }
                
                break;
            default:
                throw new ServicoInesperadoException();
        } 
    } catch (ServicoInesperadoException | MensagemMuitoGrandeException | MensagemMuitoCurtaException ex) {
        this.adicionaMensagemServicoNegado((byte)0xDD);        
    } catch (CheckSumErradoException ex) {
        this.adicionaMensagemServicoNegado((byte)0xFF);
    }
 }
 
   @Override
   @SuppressWarnings("FinalizeDeclaration")
   protected void finalize() throws Throwable {
      super.finalize();
      try {
         this.entrada.close();
      } catch (IOException e) { }
      
      try {
         this.saida.close();
      } catch (IOException e) { }
      
      try {
         this.socket.close();
      } catch (IOException e) { }
   }

}
