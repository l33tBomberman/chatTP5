/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.Mensagens;

import chattrabalho.LeandroUteis.Uteis;
import chattrabalho.Mensagens.Exception.MensagemMuitoCurtaException;
import chattrabalho.Mensagens.Exception.MensagemMuitoGrandeException;

/**
 *
 * @author pedro_000
 */
public class MensagemTchau extends Mensagem {
    //Cliente
    public MensagemTchau() 
            throws MensagemMuitoGrandeException, 
                   MensagemMuitoCurtaException {
        super(ServicoByte[5], null);
    }
    
    //Servidor
    public MensagemTchau(int ID) 
            throws MensagemMuitoGrandeException, 
                   MensagemMuitoCurtaException {
        super(ServicoByte[5], 4);
        this.setaDados(0, Uteis.intParaArrayByte(ID));
        this.Checksum = this.retornaChecksum();
    }
}
