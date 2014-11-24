/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.LeandroUteis;

import javax.swing.JOptionPane;

/**
 *
 * @author Leandro
 */
public class Saidas {
    
    /**
     * Diálogo de saída com mensagem de alerta.
     * @param titulo Título que será exibido no diálogo.
     * @param mensagem Mensagem que será exibida no diálogo.
     */
    public static void dialogoAlerta(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, titulo, JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Diálogo de saída com mensagem de informação.
     * @param titulo Título que será exibido no diálogo.
     * @param mensagem Mensagem que será exibida no diálogo.
     */
    public static void dialogoInformacao(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Diálogo de saída com mensagem de erro.
     * @param titulo Título que será exibido no diálogo.
     * @param mensagem Mensagem que será exibida no diálogo.
     */
    public static void dialogoErro(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, titulo, JOptionPane.ERROR_MESSAGE);
    }
    
}
