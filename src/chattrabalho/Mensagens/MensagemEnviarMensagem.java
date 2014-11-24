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
public class MensagemEnviarMensagem extends Mensagem {    
    public MensagemEnviarMensagem(int remetente, int destinatario, String mensagem) 
            throws MensagemMuitoGrandeException, 
                   MensagemMuitoCurtaException{
        
        super(ServicoByte[4], 8 + mensagem.length()*2);
        
        this.setaDados(0, Uteis.intParaArrayByte(remetente));
        this.setaDados(4, Uteis.intParaArrayByte(destinatario));
        this.setaDados(8, Uteis.converteStringEmVetorBytes(mensagem));
        
        this.Checksum = this.retornaChecksum();
    }
}
