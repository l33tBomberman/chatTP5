/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattrabalho.clienteUI;

import chattrabalho.cliente.ClienteID;
import java.util.ArrayList;

/**
 *
 * @author pedro_000
 */
public class AtualizaConectados implements Runnable{
    private static final int umMinuto  = 60000;
    private final ClienteUI CUI;
    
    public AtualizaConectados(ClienteUI CUI){
        this.CUI = CUI;
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        while(true){
            try {
                ArrayList Lista = this.CUI.retornaListaConectados();
                this.CUI.setaClientesConectados("");
                
                while(!Lista.isEmpty()){
                    ClienteID cid = (ClienteID)Lista.remove(0);
                    String texto = cid.getID()+ ": " + cid.getApelido();
                    
                    this.CUI.setaClientesConectados(this.CUI.retornaClientesConectados() + "\n" + texto);
                }
                
                System.out.println("A tela atualizou");
                Thread.sleep(umMinuto);
                
            } catch (InterruptedException ex) {
                System.out.println("Não dormiu");
            }
        }
    }
}
