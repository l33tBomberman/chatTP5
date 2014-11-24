/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.Mensagens;
import chattrabalho.Mensagens.Exception.MensagemMuitoGrandeException;
import chattrabalho.Mensagens.Exception.MensagemMuitoCurtaException;
import chattrabalho.Mensagens.Exception.TamanhoErradoException;
import chattrabalho.Mensagens.Exception.CheckSumErradoException;
import chattrabalho.LeandroUteis.Uteis;

/**
 * @author pedro_000
 */
public class Mensagem {
    private final int tamanho;
    
    private final byte servico;
    final byte[] dados;
    int Checksum;
    
    private static final int tamanho_minimo = 0;
    private static final int tamanho_maximo = 65535;
    //private static final int tamanhoDosBytesQueNaoSaoCorpoDaMensagem = 5;
    
    /**
     * Seviços cobrados no trabalho por ordem numérica.
     */
    public static final byte[] ServicoByte = new byte[] {
        (byte)0x01, /** Ola                 [0] **/
        (byte)0x02, /** Mudar_Apelido       [1] **/
        (byte)0x03, /** Clientes_Conectados [2] **/
        (byte)0x04, /** Requisitar_Apelido  [3] **/
        (byte)0x05, /** Enviar_Mensagem     [4] **/
        (byte)0x0A, /** Tchau               [5] **/
        (byte)0x7f  /** Serviço Negado      [6] **/
    };
    
    public Mensagem (byte servico, int tamanho){
        this.servico = servico;
        this.tamanho = tamanho;
        this.dados   = new byte[tamanho];
    }
    
    /**
     * 
     * @param servico serviço ao qual a mensagem obedece
     * @param mensagem corpo da mensagem 
     * @throws MensagemMuitoGrandeException exceção lançada quando o corpo da 
     * mensagem excede o tamanho máximo que pode ser escrito na tag tamanho
     * @throws MensagemMuitoCurtaException exceção lançada quando o corpo da 
     * mensagem é menor do que inexistente.
     */
    public Mensagem(byte servico, String mensagem) 
            throws MensagemMuitoGrandeException,
                   MensagemMuitoCurtaException {
        if (mensagem != null){
            this.dados = Uteis.converteStringEmVetorBytes(mensagem);
            this.tamanho = mensagem.length() * 2;
        } else {
            this.tamanho = 0;
            this.dados = null;
        }
        
        
        if (this.tamanho > tamanho_maximo) {
            throw new MensagemMuitoGrandeException();
        } else if (this.tamanho < tamanho_minimo){
            throw new MensagemMuitoCurtaException();
        }
        
        this.servico = servico;
        this.Checksum = calculaCheckSum(this);
    }
    
    public static int calculaCheckSum(Mensagem mensagem){
        int checksum = (int)mensagem.servico & 0xFF;
        checksum += (int) (mensagem.tamanho >> 8) & 0xFF;
        checksum += (int) (mensagem.tamanho >> 0) & 0xFF;
        
        if (mensagem.dados != null){
            for (int i = 0; i < mensagem.dados.length; i++){
                checksum += ((int)mensagem.dados[i] & 0xFF);
            }
        }
        //checksum de 16 bits
        return checksum & 0xFFFF;
    }

    public static byte[] obtemMensagemComoVetorBytes(Mensagem mensagem){
        byte mensagemComoByte[] = new byte[mensagem.tamanho +5];
        //5 é payload
        mensagemComoByte[0] = mensagem.servico;
        mensagemComoByte[1] = (byte)(mensagem.tamanho >> 8);
        mensagemComoByte[2] = (byte)(mensagem.tamanho >> 0);
        
        if (mensagem.dados != null){
            System.arraycopy(mensagem.dados, 0 , mensagemComoByte, 3, mensagem.tamanho);
        }
        
        mensagemComoByte[mensagem.tamanho + 3] = (byte)((mensagem.Checksum >> 8) & 0x000000FF);
        mensagemComoByte[mensagem.tamanho + 4] = (byte)((mensagem.Checksum >> 0) & 0x000000FF);
        
        return mensagemComoByte;
    }
    
    public String obtemMensagemComoString(){
       String convertido =  Uteis.converteVetorBytesEmString(dados);
       return convertido;
    }
    
    public static void verificaChecksum(int checksum, Mensagem mensagem)
            throws CheckSumErradoException {
                
        if (checksum != calculaCheckSum(mensagem)){
            throw new CheckSumErradoException();
        } else {
            System.out.println("O checksum está certo"); 
        }
    }
    
    public void verificaTamanho() throws TamanhoErradoException{
        if (this.tamanho != this.dados.length){
            throw new TamanhoErradoException();
        } else {
            System.out.println("O tamanho procede"); 
        }
    }
    
    public int retornaTamanho(){
        return this.tamanho;
    }
    
    public void setaDado(int index, byte informacao){
        this.dados[index] = informacao;
    }
    
    public void setaDados (int index, byte[] informacao){
        for(int i = 0; i < informacao.length; i++){
            this.setaDado(index + i, informacao[i]);
        }
    }
    
    public byte retornaServico(){
        return this.servico;
    }
    
    public byte[] retornaDados(){
        return this.dados;
    }
    
    public int retornaChecksum() {
        return calculaCheckSum(this);
    }
}
