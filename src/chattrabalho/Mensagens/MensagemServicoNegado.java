/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.Mensagens;

/**
 *
 * @author pedro_000
 */
public class MensagemServicoNegado extends Mensagem{
    private static final byte[] NackBytes  = new byte[]{
        (byte)0x02, /** Apelido já Registrado    **/
        (byte)0xBB, /** Cliente Já Registrado    **/
        (byte)0xCC, /** Cliente Nao Identificado **/
        (byte)0xDD, /** Mensagem Mal Formada     **/
        (byte)0xEE, /** Sem Ola                  **/
        (byte)0xFF, /** Checksum Invalido        **/
    };
            
    public MensagemServicoNegado(byte Nack){
        super(ServicoByte[6], 1);
        this.setaDado(0, Nack);
        this.Checksum = this.retornaChecksum();
    }
    
    public byte getNack(){
        return this.dados[0];
    }
    
    public boolean nackExiste(){
        for(int i = 0; i < NackBytes.length; i++){
            if (this.getNack() == NackBytes[i]){
                return true;
            }
        }
        
        return false;
    }
}
