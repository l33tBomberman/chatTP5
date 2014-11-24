/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chattrabalho.LeandroUteis;

/**
 *
 * @author Leandro
 */
public class Uteis {
   public static char montaCaractere2Bytes(byte maisSig, byte menosSig) {
      int mais,menos;
      
      mais = (int)maisSig & 0xFF;
      menos = (int)menosSig & 0xFF;

      return (char)(((mais << 8) | menos) & 0xFFFF);
   }

   public static String converteVetorBytesEmString(byte[] vetorBytes) {
      if ((vetorBytes.length % 2) != 0) {
         // Não possui um número par de bytes
         throw  new RuntimeException("Não é possível montar uma string com um número impar de bytes");
      }

      String stringMontada = "";
      for(int i=0; i < vetorBytes.length; i+=2) {
         char caractere = montaCaractere2Bytes(vetorBytes[i], vetorBytes[i+1]);
         stringMontada+= String.valueOf(caractere);
      }
      return stringMontada;
   }

   public static byte[] converteStringEmVetorBytes(String texto) {
      if (texto != null){
        byte[] tmpByte = new byte [ texto.length() * 2 ];

        for( int i = 0; i < texto.length(); i++) {
           tmpByte[i*2 + 0] = (byte)(texto.charAt(i) >> 8);
           tmpByte[i*2 + 1] = (byte)(texto.charAt(i) >> 0);
        }

        return tmpByte;
      } else return null;
   }


   public static int converteVetorBytesEmInt(byte[] vetorBytes) {
      if (vetorBytes.length < 4) {
         // Não dá para converter para inteiro
         throw new RuntimeException("Não é possível converter um vetor com menos de 4 bytes para inteiro");
      }

      return (((int)vetorBytes[0] << 24) & 0xFF000000) |
             (((int)vetorBytes[1] << 16) & 0x00FF0000) |
             (((int)vetorBytes[2] <<  8) & 0x0000FF00) |
             (((int)vetorBytes[3] <<  0) & 0x000000FF);
   }

   public static byte[] converteIntEmVetorBytes(int inteiro) {
      byte[] tmpBytes = new byte[4];
      tmpBytes[0] = (byte)(inteiro >> 24);
      tmpBytes[1] = (byte)(inteiro >> 16);
      tmpBytes[2] = (byte)(inteiro >>  8);
      tmpBytes[3] = (byte)(inteiro >>  0);
      return tmpBytes;
   }
   
   public static int montaInt2Bytes(byte maisSig, byte menosSig) {
      int mais,menos;
      
      mais = (int)maisSig & 0xFF;
      menos = (int)menosSig & 0xFF;

      return (int)(((mais << 8) | menos) & 0xFFFF);
   }
   
   
   public static final byte[] intParaArrayByte(int value){
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)(value)
        };
   }
   
    /**
     * Fonte: 
     * http://stackoverflow.com/questions/11208479/how-do-i-initialize-a-byte-array-in-java
     * @param s string a ser convertida para Array de bytes;
     * @return Array de bytes;
     */
    public static byte[] hexStringParaByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
