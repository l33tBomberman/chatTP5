/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.servidor;

import chattrabalho.servidor.Exception.PortaInexistenteException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Leandro
 */
public class Main {
    private static int PortaUtilizada;
    private static final int PortaDefault = 8885;
    private static final String parametroDePorta = "-p";
    
    private static final int MenorPortaExistente = 1;
    private static final int PortaBemConhecida   = 1024;
    private static final int MaiorPortaExistente = 65535;
    
    /**
     * @param args the command line arguments
     * @throws chattrabalho.servidor.Exception.PortaInexistenteException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws PortaInexistenteException, IOException {
       // TODO code application logic here
        
        if (args.length ==  0){
            PortaUtilizada = PortaDefault;
        } else if (args.length == 2) {
            if(parametroDePorta.equals(args[0])){
                PortaUtilizada = Integer.parseInt(args[1]);
                if (PortaUtilizada > MaiorPortaExistente ||
                    PortaUtilizada < MenorPortaExistente){
                    throw new PortaInexistenteException();
                } else if (PortaUtilizada <= PortaBemConhecida){
                    chattrabalho.LeandroUteis.Saidas.dialogoAlerta("Porta bem conhecida", 
                            "A porta utilizada pertence à lista de portas bem "
                            + "conhecidas.\n Isso pode tanto não significar nada, "
                            + "quanto induzir comportamenentos estranhos no sistema "
                            + "devido a conflito com outras aplicações.\n O "
                            + "programa prosseguira normalmente."
                    );
                }
            }
        } else {
            throw new IOException("Ocorreu um input indevido de dados no programa "
                    + "ele foi criado para funcionar com os argumentos "
                    + "\"-p numeroDaPorta\" ou sem argumento algum.");
        }

       try {
         ServerSocket server = new ServerSocket(PortaUtilizada);
         System.out.println("SERVIDOR > " + server.getInetAddress().getHostAddress()
                 + ":" + server.getLocalPort() + " (" 
                 + server.getInetAddress().getCanonicalHostName() + ")");

         GerenteClientes gerente = new GerenteClientes();
         Thread threadGerente = new Thread(gerente);
         threadGerente.start();

         while(true) {
            // Iniciando o cliente
            ClienteGerenciado cliente = new ClienteGerenciado((server.accept()));
            Socket socket = cliente.getSocket();
            System.out.println("CLIENTE. >" + socket.getInetAddress().getHostAddress() 
                    + ":" + socket.getLocalPort() + " (" 
                    + socket.getInetAddress().getCanonicalHostName() + ")");

            // Disparando uma thread para aceitar uma nova conexão
            Thread t = new Thread(cliente);
            t.start();
         }

       } catch (IOException e) {
          System.out.println(e);
       } 
    }
}


