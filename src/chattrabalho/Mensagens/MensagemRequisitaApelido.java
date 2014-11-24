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
public class MensagemRequisitaApelido extends Mensagem{
    public MensagemRequisitaApelido(int ID) 
            throws MensagemMuitoGrandeException, 
                   MensagemMuitoCurtaException {
        super(ServicoByte[3], 4);
        this.setaDados(0, Uteis.converteIntEmVetorBytes(ID));
        this.Checksum = this.retornaChecksum();
    }
    
    public MensagemRequisitaApelido(String apelido) 
            throws MensagemMuitoGrandeException, 
                   MensagemMuitoCurtaException {
        super(ServicoByte[3], apelido);
    }
}
