/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.Mensagens;

import chattrabalho.LeandroUteis.Uteis;
import chattrabalho.Mensagens.Exception.CheckSumErradoException;
import chattrabalho.Mensagens.Exception.MensagemMuitoCurtaException;
import chattrabalho.Mensagens.Exception.MensagemMuitoGrandeException;

/**
 *
 * @author pedro_000
 */
public class MensagemMudarApelido extends Mensagem{
    //Cliente
    public MensagemMudarApelido(String apelido) 
            throws MensagemMuitoGrandeException, 
                   MensagemMuitoCurtaException {
        super(ServicoByte[1], apelido);
    }
    
    //Servidor
    public MensagemMudarApelido(int ID, String apelido) 
            throws MensagemMuitoGrandeException, 
                   MensagemMuitoCurtaException {
        super(ServicoByte[1], 4 + apelido.length());
        this.setaDados(0, Uteis.intParaArrayByte(ID));
        this.setaDados(4, Uteis.converteStringEmVetorBytes(apelido));
        
        this.Checksum = this.retornaChecksum();
    }
}
