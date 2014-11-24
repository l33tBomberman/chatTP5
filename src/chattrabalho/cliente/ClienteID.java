/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattrabalho.cliente;

/**
 *
 * @author pedro_000
 */
public class ClienteID {
    private final int ID;
    private final String Apelido;
    
    public ClienteID(int ID, String Apelido){
        this.Apelido = Apelido;
        this.ID = ID;
    }
    
    public String getApelido(){
        return this.Apelido;
    }
    
    public int getID(){
        return this.ID;
    }
}
