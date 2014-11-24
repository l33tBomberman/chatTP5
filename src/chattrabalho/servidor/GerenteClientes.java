/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chattrabalho.servidor;

import chattrabalho.servidor.Exception.ClienteSemOlaException;
import chattrabalho.servidor.Exception.ClienteJaRegistradoException;
import chattrabalho.servidor.Exception.IncapazDeEscreverException;
import chattrabalho.servidor.Exception.ClienteJaTemApelido;
import chattrabalho.servidor.Exception.ClienteInexistenteException;
import chattrabalho.LeandroUteis.Uteis;
import chattrabalho.Mensagens.Exception.MensagemMuitoCurtaException;
import chattrabalho.Mensagens.Exception.MensagemMuitoGrandeException;
import chattrabalho.Mensagens.Exception.ServicoInexistenteException;
import chattrabalho.Mensagens.Mensagem;
import chattrabalho.Mensagens.MensagemClientesConectados;
import chattrabalho.Mensagens.MensagemEnviarMensagem;
import chattrabalho.Mensagens.MensagemMudarApelido;
import chattrabalho.Mensagens.MensagemOla;
import chattrabalho.Mensagens.MensagemRequisitaApelido;
import chattrabalho.Mensagens.MensagemServicoNegado;
import chattrabalho.Mensagens.MensagemTchau;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro
 */
public class GerenteClientes implements Runnable {
   private static final ArrayList <ClienteGerenciado> listaClientes   = new ArrayList<>();
   public int ID;
   
   public void ImprimeBytes(byte[] test){
        for (int j = 0; j < test.length; j++) {
           System.out.format("%02X ", test[j]);
        }
        System.out.println();
   }
   
   public void ImprimeTupla(MensagemTupla m){
       byte[] mensagem = Mensagem.obtemMensagemComoVetorBytes(m.getMensagem());
       this.ImprimeBytes(mensagem);
   }
   
   public ClienteGerenciado getClienteByID(int ID){
       for (ClienteGerenciado listaCliente : GerenteClientes.listaClientes) {
           if (listaCliente.temIDIgual(ID)) {
               return listaCliente;
           }
       }
       return null;
   }
   
   public boolean apelidoJaExiste(String apelido){
       return GerenteClientes.listaClientes.stream().anyMatch((listaCliente) -> (listaCliente.osApelidosSaoIguais(apelido)));
   }

   public static void adicionaCliente(ClienteGerenciado c) {
      listaClientes.add(c);
   }
   
   public void enviaMensagem(Mensagem mensagem, ClienteGerenciado cliente) throws IOException{
       byte[] vetor = Mensagem.obtemMensagemComoVetorBytes(mensagem);
       for(int i = 0; i < vetor.length; i++){
           cliente.enviaByte(vetor[i]);
       }
   }
   
   public ArrayList<Integer> getClientesConectadosID(){
       ArrayList<Integer> ArrayRetorno = new ArrayList<>();
       
       
       //Netbeans fez para mim
       GerenteClientes.listaClientes.stream().map((listaCliente) -> listaCliente.retornaID()).forEach((IDLocal) -> {
           ArrayRetorno.add(IDLocal);
       });
       
       return ArrayRetorno;
   }
   
   public void enviaMensagemTodos(Mensagem mensagem) {
       GerenteClientes.listaClientes.stream().forEach((listaCliente) -> {
           try {
               this.enviaMensagem(mensagem, listaCliente);
           } catch (IOException ex) {
               System.out.println("Falhou ao enviar mensagem para o cliente" + listaCliente.retornaID());
           }
       });
   }
   
   @SuppressWarnings("FinalizeCalledExplicitly")
   public void removeClienteByID(int ID){
       for (int i = 0; i < GerenteClientes.listaClientes.size(); i++){
           if (GerenteClientes.listaClientes.get(i).temIDIgual(ID)){
               try {
                    ClienteGerenciado removido = GerenteClientes.listaClientes.remove(i);
                    removido.finalize();
                    
               } catch (Throwable ex) {
                   System.out.println("Falhou ao finalizar o cliente removido");
               }
           }
       }
   }
   
   @Override
   @SuppressWarnings("SleepWhileInLoop")
   public void run() {
      while(true) {
         if (!ClienteGerenciado.vetorVazio()) {
            this.leMensagem();
         } else {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                System.out.println("Não esperou");
            }
         }
      }
   }
   
   @SuppressWarnings("FinalizeCalledExplicitly")
    public void leMensagem(){
        try {
            MensagemTupla mensagem  = ClienteGerenciado.pegaMensagemParaTratar();
            this.ImprimeTupla(mensagem);

            Mensagem MensagemTratar = mensagem.getMensagem();
            this.ID                 = mensagem.getID();

            //Pega quem enviou
            ClienteGerenciado remetente = getClienteByID(ID);
            if (remetente == null){
                throw new ClienteInexistenteException();
            }
        
            switch(MensagemTratar.retornaServico()){
                case (byte)0x01:
                    //Ola    
                    if(!remetente.temApelido()){
                        throw new ClienteJaTemApelido();
                    } else {
                        String apelido = Uteis.converteVetorBytesEmString(MensagemTratar.retornaDados());
                        
                        if (this.apelidoJaExiste(apelido)){
                            throw new ClienteJaRegistradoException();
                        }
                        
                        remetente.setaApelido(apelido);
                        MensagemOla aceito =  new MensagemOla(remetente.retornaID());
                        try {
                            this.enviaMensagem(aceito, remetente);
                        } catch (IOException ex) {
                            throw new IncapazDeEscreverException();
                        }
                    }
                    break;
                    
                case (byte)0x02:
                    //Mudar apelido
                    if(remetente.temApelido()){
                        throw new ClienteSemOlaException();
                    }
                    String apelidoMuda = Uteis.converteVetorBytesEmString(MensagemTratar.retornaDados());
                    remetente.setaApelido(apelidoMuda);
                    
                    MensagemMudarApelido mudaApelido = new MensagemMudarApelido(ID, apelidoMuda);
                    this.enviaMensagemTodos(mudaApelido);
                    break;
                    
                case (byte)0x03:
                    //Clientes Conectados
                    if(remetente.temApelido()){
                        throw new ClienteSemOlaException();
                    }
                    
                    ArrayList<Integer> conectados = this.getClientesConectadosID();
                    MensagemClientesConectados mensagemConectados = new MensagemClientesConectados(conectados);
                    
                    try {
                        this.enviaMensagem(mensagemConectados, remetente);
                    } catch (IOException ex) {
                        throw new IncapazDeEscreverException();
                    }
                    break;
                    
                case (byte)0x04:
                    //Requisita Apelido
                    if(remetente.temApelido()){
                        throw new ClienteSemOlaException();
                    }
                    
                    int IDRecebido = Uteis.converteVetorBytesEmInt(MensagemTratar.retornaDados());
                    ClienteGerenciado cGerenciado = getClienteByID(IDRecebido);
                    String apelido = cGerenciado.retornaApelido();
                    
                    MensagemRequisitaApelido requisitaApelido = new MensagemRequisitaApelido(apelido);
                    try {
                        this.enviaMensagem(requisitaApelido, remetente);
                    } catch (IOException ex){
                        throw new IncapazDeEscreverException();
                    }
                    break;
                    
                case (byte)0x05:
                    //Requisita Apelido
                    if(remetente.temApelido()){
                        throw new ClienteSemOlaException();
                    }
                    
                    byte[] dados            = MensagemTratar.retornaDados();
                    byte[] remetenteByte    = new byte[4];
                    byte[] destinatarioByte = new byte[4];
                    byte[] conteudoByte     = new byte[dados.length - 8];
                    
                    System.arraycopy(dados, 0, remetenteByte,    0, 4);
                    System.arraycopy(dados, 4, destinatarioByte, 0, 4);
                    System.arraycopy(dados, 8, conteudoByte,     0, conteudoByte.length);
                    
                    int remetenteInt    = Uteis.converteVetorBytesEmInt(remetenteByte);
                    int destinatarioInt = Uteis.converteVetorBytesEmInt(destinatarioByte);
                    String conteudo     = Uteis.converteVetorBytesEmString(conteudoByte);
                    
                    MensagemEnviarMensagem enviar = new MensagemEnviarMensagem(remetenteInt, destinatarioInt, conteudo);
                    if(destinatarioInt == 0){
                        this.enviaMensagemTodos(enviar);
                    } else {
                        try {
                            this.enviaMensagem(enviar, this.getClienteByID(destinatarioInt));
                        } catch (IOException ex) {
                            throw new IncapazDeEscreverException();
                        }
                    }
                    break;
                    
                case (byte)0x0A:
                    //Tchau
                    if(remetente.temApelido()){
                        throw new ClienteSemOlaException();
                    }
                    MensagemTchau tchau = new MensagemTchau(ID);
                    this.enviaMensagemTodos(tchau);
                    
                    {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(GerenteClientes.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    this.removeClienteByID(ID);
                    
                    break;
                    
                case (byte)0x0F:
                    //Serviço negados
                    if(remetente.temApelido()){
                        throw new ClienteSemOlaException();
                    }
                    
                    MensagemServicoNegado ServicoNegado = (MensagemServicoNegado)MensagemTratar;
                    try {
                        this.enviaMensagem(ServicoNegado, remetente);
                    } catch (IOException ex) {
                        throw new IncapazDeEscreverException();
                    }
                default:
                    throw new ServicoInexistenteException();
            } 
       } catch (ServicoInexistenteException | ClienteJaTemApelido | MensagemMuitoGrandeException | MensagemMuitoCurtaException  ex){
           ClienteGerenciado.adicionaMensagemServicoNegado((byte)0xFF, ID);
       } catch (IncapazDeEscreverException ex){
            System.out.println("Ocorreu um problema na hora de enviar uma mensagem.");
       } catch (ClienteJaRegistradoException ex) {
           ClienteGerenciado.adicionaMensagemServicoNegado((byte)0xBB, ID);
       } catch (ClienteInexistenteException ex){
           ClienteGerenciado.adicionaMensagemServicoNegado((byte)0xCC, ID);
       } catch (ClienteSemOlaException ex) {
           ClienteGerenciado.adicionaMensagemServicoNegado((byte)0xEE, ID);
       }
    }
}
