/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.cliente;

import chattrabalho.LeandroUteis.Uteis;
import chattrabalho.Mensagens.*;
import chattrabalho.Mensagens.Exception.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro_000
 */
public class ClienteNegocios implements Runnable {
    /** Constantes Globais **/
    private static final int portaPadrao        = 8885;
    private static final int menorPortaPossivel = 0;
    private static final int maiorPortaPossivel = 65565;    
    
    private final BufferEnviar mensagensEnviar;
    private final BufferRecebidos mensagensRecebidas;
    private final BufferConectados conectados;
    
    /** Elementos do ClienteNegocios **/
    private int ID;
    private boolean IDJaSetado = false;
    
    /** Parâmetro do servidor **/
    private final String IP;
    private int porta;
    
    /*Parâmetros da conexão **/
    private final Socket conexao;
    private final DataOutputStream saida;
    private final DataInputStream entrada;
    
    /* thread */
    private final Thread recebeThread;
    private final Thread enviaThread;
    private final Thread conectadosThread;
    private final Thread trataMensagensThread;

    public final Object bastaoTchau;
    public final Object bastaoRecebidos;
    
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public ClienteNegocios(String IP, int porta, String apelido) {
        this.bastaoTchau     = new Object();
        this.bastaoRecebidos = new Object();
        
        if (porta < menorPortaPossivel || this.porta > maiorPortaPossivel){
           this.porta = portaPadrao; 
        } else {
           this.porta = porta;
        }
        
        //Inicializa parâmetros
        this.IP = IP;
        try {
            this.conexao = new Socket(this.IP, this.porta);
            this.saida   = new DataOutputStream(this.conexao.getOutputStream());
            this.entrada = new DataInputStream(this.conexao.getInputStream());
            
            //Inicializa os Buffers
            this.mensagensRecebidas = new BufferRecebidos(this);        
            this.mensagensEnviar    = new BufferEnviar(this.saida);
            this.conectados         = new BufferConectados(this.mensagensEnviar);

            //Envia Ola
            MensagemOla ola;
            try {
                ola = new MensagemOla(apelido);
                this.mensagensEnviar.adicionaMensagem(ola);
            } catch (MensagemMuitoGrandeException | MensagemMuitoCurtaException ex) {
                System.err.println("Tretou no construtor na hora de construir a "
                        + "mensagem");
            }
            

            this.conectadosThread     = new Thread(this.conectados);
            this.enviaThread          = new Thread(this.mensagensEnviar);
            this.recebeThread         = new Thread(this);
            this.trataMensagensThread = new Thread(this.mensagensRecebidas);

            //Inicia as threads (iniciar no construtor é um mal necessário)
            this.enviaThread.start();
            this.recebeThread.start();
            this.conectadosThread.start();
            this.trataMensagensThread.start();
            
        } catch (IOException ex) {
            throw new RuntimeException("Erro de IO, porque?");
        }
    }
    
    public BufferRecebidos retornBufferRecebidos(){
        return this.mensagensRecebidas;
    }
    
    public int retornaID(){
        return this.ID;
    }
    
    public Boolean temMensagemTratada(){
        return this.mensagensRecebidas.temMensagemTratada();
    }
    
    public String retornaMensagemTratada (){
        return this.mensagensRecebidas.pegaMensagem();
    }
 
    public boolean verificaSeIDJaFoiSetado(){
        return this.IDJaSetado;
    }
    
    public void setaID(int IDRecebido){
        this.ID = IDRecebido;
        this.IDJaSetado = true;
    }
    
    public String getApelidoByID(int id){
        return this.conectados.getApelidoByID(id);
    }
    
    public String retornaIp(){
        return this.IP;
    }
    
    public int retornaPorta(){
        return this.porta;
    }
    
    public void setaConectados(ArrayList usuarios){
        this.conectados.recebeLista(usuarios);
    }
    
    public void adicionaMensagem(Mensagem mensagem){
        this.mensagensEnviar.adicionaMensagem(mensagem);
    }
    
    public void AdicionaApelidoALista(String apelido){
        this.conectados.AdicionaApelido(apelido);
    }
    
        public void leMensagem() {
        
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
                    int IDRecebido01 = Uteis.converteVetorBytesEmInt(dados);
                    MensagemOla M01  = new MensagemOla(IDRecebido01);
                    if ((Mensagem.calculaCheckSum(M01) == checksum)){
                        this.mensagensRecebidas.adicionaMensagem(M01);
                    } else {
                        throw new CheckSumErradoException();
                    }
                    
                    
                    break;
                    
                case (byte)0x02:
                    //Mudar apelido
                    String apelidoCaso2      = Uteis.converteVetorBytesEmString(dados);
                    MensagemMudarApelido M02 = new MensagemMudarApelido(apelidoCaso2);
                    if (Mensagem.calculaCheckSum(M02) == checksum) {
                        this.mensagensRecebidas.adicionaMensagem(M02);
                    } else {
                        throw new CheckSumErradoException();
                    }
                    
                    break;
                    
                case (byte)0x03:
                    //Clientes Conectados
                    MensagemClientesConectados M03 = new MensagemClientesConectados(tamanho, dados);
                    if (Mensagem.calculaCheckSum(M03) == checksum){
                        this.mensagensRecebidas.adicionaMensagem(M03);
                    } else {
                        throw new CheckSumErradoException();
                    }
                    
                    break;
                    
                case (byte)0x04:
                    //Requisita Apelid
                    String apelidoCaso4          = Uteis.converteVetorBytesEmString(dados);
                    MensagemRequisitaApelido M04 = new MensagemRequisitaApelido(apelidoCaso4);
                    if (Mensagem.calculaCheckSum(M04) == checksum){
                        this.mensagensRecebidas.adicionaMensagem(M04);
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
                        this.mensagensRecebidas.adicionaMensagem(M05);
                    } else {
                        throw new CheckSumErradoException();
                    }
                    
                    break;
                    
                case (byte)0x0A:
                    //Tchau
                    int IDRecebido0A  = Uteis.converteVetorBytesEmInt(dados);
                    MensagemTchau M0A = new MensagemTchau(IDRecebido0A);
                    if(Mensagem.calculaCheckSum(M0A) == checksum){
                        this.mensagensRecebidas.adicionaMensagem(M0A);
                    } else {
                        throw new CheckSumErradoException();
                    }
                    
                    break;
                    
                case (byte)0x0F:
                    //Serviço negado
                    MensagemServicoNegado M0F = new MensagemServicoNegado(dados[0]);
                    if (Mensagem.calculaCheckSum(M0F) == checksum){
                        this.mensagensRecebidas.adicionaMensagem(M0F);
                    } else {
                        throw new CheckSumErradoException();
                    }
                    
                    break;
                    
                default:
                    throw new ServicoInexistenteException();
            } 
        } catch (IOException ex) {
            System.out.println("Erro Inesperado: Tem problema na escrita");
        } catch (MensagemMuitoGrandeException ex) {
            System.out.println("Erro Inesperado: A mensagem excede o limite de tamanho");
        } catch (MensagemMuitoCurtaException ex) {
            System.out.println("Erro Inesperado: O tamanho da mensagem está errado");
        } catch (CheckSumErradoException ex) {
            System.out.println("Erro Inesperado: Mensagem Corrompida");
        } catch (ServicoInexistenteException ex) {
            System.out.println("Erro Inesperado: O serviço recebido não consta na lista");
        }
    }
    
   public BufferEnviar retornaBufferEnviar(){
       return this.mensagensEnviar;
   }
   
   public ArrayList retornaListaConectados(){
       return this.conectados.getListaPronta();
   }
    
    
    @SuppressWarnings({"FinalizeCalledExplicitly", "WaitWhileNotSynced"})
    public void tchauEBencao() throws Throwable{
        
        try {
            MensagemTchau tchau = new MensagemTchau();
            this.mensagensEnviar.adicionaMensagem(tchau);
            
            synchronized(this.bastaoTchau){
                try {
                    wait();
                } catch (InterruptedException ex) {
                    System.out.println("Falhou na hora de dar wait.");
                }
            }
            
            this.finalize();

        } catch (MensagemMuitoGrandeException | MensagemMuitoCurtaException ex) {
            System.out.println("A mensagem possui um tamanho não compatível com o protocolo");
        }
    }
    
    /**
     * @throws Throwable
     */
    @Override
    @SuppressWarnings("FinalizeDeclaration")
    public void finalize() throws Throwable{
       super.finalize();
       
       this.saida.close();
       this.entrada.close();
       this.conexao.close();
       
       this.conectadosThread.interrupt();
       this.enviaThread.interrupt();
       this.recebeThread.interrupt();
       this.trataMensagensThread.interrupt();
       
       System.exit(0);  
    }
    
    public void enviaMensagemTeste(String texto){
        try {
            MensagemEnviarMensagem mensagem = new MensagemEnviarMensagem(0, 0, texto);
            this.mensagensEnviar.adicionaMensagem(mensagem);
        } catch (MensagemMuitoGrandeException | MensagemMuitoCurtaException ex) {
            Logger.getLogger(ClienteNegocios.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args){
        int umSegundo = 1000;
        
        ClienteNegocios clienteteste = new ClienteNegocios("127.0.0.1", portaPadrao, "Teste");
        clienteteste.enviaMensagemTeste("Apenas um teste");
        try {
            sleep(umSegundo);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClienteNegocios.class.getName()).log(Level.SEVERE, null, ex);
        }
        clienteteste.enviaMensagemTeste("Como vai você?");
        try {
            sleep(umSegundo);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClienteNegocios.class.getName()).log(Level.SEVERE, null, ex);
        }
        clienteteste.enviaMensagemTeste("ASL");
        try {
            sleep(umSegundo);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClienteNegocios.class.getName()).log(Level.SEVERE, null, ex);
        }
        clienteteste.enviaMensagemTeste("18, Masculino, Belo Horizonte");
        try {
            sleep(umSegundo);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClienteNegocios.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            MensagemTchau tchauzinho = new MensagemTchau();
            clienteteste.mensagensEnviar.adicionaMensagem(tchauzinho);
        } catch (MensagemMuitoGrandeException | MensagemMuitoCurtaException ex) {
            Logger.getLogger(ClienteNegocios.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true){
            if (this.conexao.isConnected()){
                    this.leMensagem();
            } else {
                
                try {
                    this.tchauEBencao();
                } catch (Throwable ex) {
                    System.out.println("Deu treta");
                } 
            }
        } 
    }
}