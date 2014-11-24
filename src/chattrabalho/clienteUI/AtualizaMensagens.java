/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattrabalho.clienteUI;

import chattrabalho.cliente.BufferRecebidos;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro_000
 */
public class AtualizaMensagens implements Runnable {
    private final ClienteUI CUI;
    private static final int umSegundo = 1000;
    
    public AtualizaMensagens(ClienteUI CUI){
        this.CUI = CUI;
    }
    
    @Override
    public void run() {
        while(true){
            if(!this.CUI.cliente.temMensagemTratada()){
                this.CUI.atualizaMensagens();
            } else {
                //Não consegui resolver com synchronized
                try {
                    Thread.sleep(umSegundo);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AtualizaMensagens.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
