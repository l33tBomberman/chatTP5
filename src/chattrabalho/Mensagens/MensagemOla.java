/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.Mensagens;

import chattrabalho.LeandroUteis.Uteis;
import chattrabalho.Mensagens.Exception.MensagemMuitoCurtaException;
import chattrabalho.Mensagens.Exception.MensagemMuitoGrandeException;
import static chattrabalho.Mensagens.Mensagem.ServicoByte;

/**
 *
 * @author pedro_000
 */
public class MensagemOla extends Mensagem{
    //Cliente
    public MensagemOla(String mensagem) 
            throws MensagemMuitoGrandeException, 
                   MensagemMuitoCurtaException {
        super(ServicoByte[0], mensagem);
    }
    
    //Servidor
    public MensagemOla(int ID) 
            throws MensagemMuitoGrandeException, 
                   MensagemMuitoCurtaException{
        super(ServicoByte[0], 4);
        this.setaDados(0, Uteis.intParaArrayByte(ID));
        
        this.Checksum = this.retornaChecksum();
    }
}
