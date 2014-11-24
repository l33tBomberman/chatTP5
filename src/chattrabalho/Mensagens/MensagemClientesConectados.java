/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.Mensagens;

import chattrabalho.LeandroUteis.Uteis;
import chattrabalho.Mensagens.Exception.MensagemMuitoCurtaException;
import chattrabalho.Mensagens.Exception.MensagemMuitoGrandeException;
import java.util.ArrayList;

/**
 *
 * @author pedro_000
 */
public class MensagemClientesConectados extends Mensagem{  
    //Cliente Solicitando
    public MensagemClientesConectados() 
            throws MensagemMuitoGrandeException, 
                   MensagemMuitoCurtaException {
        super(ServicoByte[2], null);
    }
    
    //Cliente recebendo
    
    public MensagemClientesConectados(int tamanho, byte[] dadosClientes){
        super(ServicoByte[2], tamanho);
        System.arraycopy(dadosClientes, 0, this.dados, 0, tamanho);
        this.Checksum = this.retornaChecksum();
    }
    
    // Servidor
    public MensagemClientesConectados(ArrayList<Integer> ClientesConectados) 
            throws MensagemMuitoGrandeException,
                   MensagemMuitoCurtaException{
        //Cada inteiro tem 4 bytes.
        super(ServicoByte[2], ClientesConectados.size() * 4);
        for (int i = 0; i < ClientesConectados.size(); i++){
           this.setaDados(i*4, Uteis.converteIntEmVetorBytes(ClientesConectados.get(i)));
        }
        
        this.Checksum = this.retornaChecksum();
    }
    
}
