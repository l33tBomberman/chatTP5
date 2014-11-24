/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattrabalho.cliente;

import chattrabalho.Mensagens.Mensagem;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author pedro_000
 */
public class BufferEnviar implements Runnable {
    private final ArrayList<Mensagem> listaDeMensagens;
    private final DataOutputStream saida;
    private final Object bastao;
    
    public BufferEnviar(DataOutputStream saida){
        this.bastao = new Object();
        this.listaDeMensagens = new ArrayList<>();
        this.saida            = saida;
    }
    
    private Mensagem retornaMensagem(){
        return listaDeMensagens.remove(0);
    }
    
    public void adicionaMensagem(Mensagem mensagem){
        listaDeMensagens.add(mensagem);
        synchronized (this.bastao){
            bastao.notifyAll();
        }
    }
    
     public void enviaMensagem(Mensagem mensagemASerEnviada){
        byte[] bytesCompletos = Mensagem.obtemMensagemComoVetorBytes(mensagemASerEnviada);
        try {
            this.saida.write(bytesCompletos);
        } catch (IOException ex) {
            System.out.println("Deu erro de IO na hora de enviar a mensagem");
        }
    }
    
    @Override
    public void run() {
        while (true){
            if (this.listaDeMensagens.isEmpty()){
                synchronized(this.bastao){
                    try {
                        bastao.wait();
                    } catch (InterruptedException ex) {
                        System.err.println("Deu erro de thread interrompida na thread que envia");
                    }
                }
            } else {
                Mensagem mensagem = this.retornaMensagem();
                this.enviaMensagem(mensagem);
            }
        }
    }
    
}
