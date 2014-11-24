/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.servidor;

import chattrabalho.Mensagens.Mensagem;

/**
 *
 * @author pedro_000
 */
public class MensagemTupla {
    private final int IdentificacaoDoRemetente;
    private final Mensagem mensagemBruta;
    
    public MensagemTupla(int ID, Mensagem mensagem){
        this.IdentificacaoDoRemetente = ID;
        this.mensagemBruta = mensagem;
    }
    
    public int getID(){
        return this.IdentificacaoDoRemetente;
    }
    
    public Mensagem getMensagem(){
        return this.mensagemBruta;
    }
}
