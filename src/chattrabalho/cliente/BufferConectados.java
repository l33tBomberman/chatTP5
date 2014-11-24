/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.cliente;

import chattrabalho.Mensagens.Exception.MensagemMuitoCurtaException;
import chattrabalho.Mensagens.Exception.MensagemMuitoGrandeException;
import chattrabalho.Mensagens.MensagemClientesConectados;
import chattrabalho.Mensagens.MensagemRequisitaApelido;
import java.util.ArrayList;

/**
 *
 * @author pedro_000
 */

class BufferConectados implements Runnable {
    private ArrayList<String> usuariosApelidos = new ArrayList<>(); 
    private ArrayList<Integer> usuariosID      = new ArrayList<>();
    private ArrayList<ClienteID> listaPronta   = new ArrayList<>();
    
    private final BufferEnviar enviar;
    public static final Object bastao = new Object();
    public static final int dezSegundos = 10000;
    
    public BufferConectados(BufferEnviar enviar){
        this.enviar = enviar;
    }
    
    public ArrayList getListaPronta(){
        return (ArrayList)this.listaPronta.clone();
    }
    
    public void recebeLista(ArrayList usuarios){
        this.usuariosID = usuarios;
        synchronized(bastao){
            bastao.notifyAll();
        }
    }
    
    public boolean listasDoMesmoTamanho(){
        return this.usuariosApelidos.size() == this.usuariosID.size();
    }
    
    @SuppressWarnings("SynchronizeOnNonFinalField")
    public void AtivaBastao(){
        if (this.listasDoMesmoTamanho()){
            synchronized(bastao){
                bastao.notifyAll();
            }
        }
    }
    
    public void AdicionaApelido(String apelido){
        this.usuariosApelidos.add(apelido);
        this.AtivaBastao();
    }
    
    public String getApelidoByID(int id){
        ArrayList lista = this.getListaPronta();
        while(!lista.isEmpty()){
            ClienteID cid = (ClienteID)lista.remove(0);
            if(cid.getID() == id){
                return cid.getApelido();
            }
        }
        return null;
    }
    
    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public final void run() {
        while(true){
            try {
                
                this.usuariosApelidos = new ArrayList<>();
                this.usuariosID       = new ArrayList<>();
                               
                //Mensagem solicitada recebe um notify
                MensagemClientesConectados clientesConectados = new MensagemClientesConectados();
                this.enviar.adicionaMensagem(clientesConectados);
                
                //Quando ela chega e é lida recebe um notify;
                synchronized (bastao){
                    bastao.wait();
                }
                                

                //Após receber o notify ela envia um por um os IDs dos clientes
                for (Integer usuariosID1 : usuariosID) {
                    MensagemRequisitaApelido SolicitaApelido = new MensagemRequisitaApelido(usuariosID1);
                    this.enviar.adicionaMensagem(SolicitaApelido);
                }
                
                //Quando o cliente ler  e interpretar as mensagens ele salvará-as
                //no array usuariosApelidos, quando as duas listas tiverem o mesmo 
                //tamanho recebe um notify
                synchronized (bastao){
                    bastao.wait();
                }
                
                //Faz uma lista provisória, para evitar que a lista seja pega 
                //enquanto se escreve nela;
                ArrayList<ClienteID> listaProvisoria = new ArrayList<>();
                
                for (int i = 0; i < usuariosID.size(); i++) {
                    ClienteID clienteid = new ClienteID(this.usuariosID.get(i), this.usuariosApelidos.get(i));
                    listaProvisoria.add(clienteid);
                }
                
                this.listaPronta = listaProvisoria;
                Thread.sleep(dezSegundos);
               
            } catch (MensagemMuitoGrandeException | MensagemMuitoCurtaException ex) {
                System.out.println("Deu erro na thread de conectados devido ao tamanho da mensagem");
            } catch (InterruptedException ex) {
                System.out.println("A thread conectados foi interrompida quando não devia");
            }
        }
    }
}
