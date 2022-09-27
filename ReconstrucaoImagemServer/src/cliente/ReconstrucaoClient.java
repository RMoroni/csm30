package cliente;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import org.jblas.DoubleMatrix;
import reconstrucao.ReconstrucaoServer;

/**
 *
 * @author rodrigo
 */
public class ReconstrucaoClient {

    public static void main(String args[]) throws Exception {
        URL url = new URL("http://127.0.0.1:9876/reconstrucao?wsdl");
      
        //QName qname = new QName("http://reconstrucao/", "ReconstrucaoServerImplService");
        //Service ws = Service.create(url, qname);
        
        //é preciso ter a interface (ReconstrucaoServer)
        //ReconstrucaoServer recon = ws.getPort(ReconstrucaoServer.class);                
        
        /*
            Como fazer uma requisição
        */
        DoubleMatrix sinal = ReconstrucaoClient.abre_sinal("../Arquivos/g-3.txt");
        short tamanho = 60;
        long id_user = 1;
        //recon.request(sinal, id_user, tamanho, tamanho, "teste 4");
        
        /*
            Como recuperar uma imagem:
        */
        //Receber a imagem com id específicado
        //Image imagem = recon.getImagem(0); 
        //BufferedImage img = (BufferedImage) imagem;
        //Coloca imagem no jframe
        //JFrame frame = new JFrame("Abre imagem recontruída");
        //frame.add(new JLabel(new ImageIcon(imagem)));
        //frame.pack();
        //frame.setVisible(true);        
    }

    //não precisa ser static, coloquei apenas pra não precisar instanciar na main
    public static DoubleMatrix abre_sinal(String caminho) {
        DoubleMatrix sinal = new DoubleMatrix(50816, 1); //cria matriz coluna
        try (FileReader arquivo_sinal = new FileReader(caminho)) {
            BufferedReader buffer_arquivo_sinal = new BufferedReader(arquivo_sinal);
            String linha = buffer_arquivo_sinal.readLine();
            
            short ganho = 1;
            short col = 64;
            for (int i = 0; i < 50816; i++) {
                //Controle de ganho comentado...
                sinal.put(i, Double.valueOf(linha) /* + ganho*/);
                col--;
                if (col == 0){
                    ganho++;
                    col = 64;
                }
                linha = buffer_arquivo_sinal.readLine();
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Falha na abertura do arquivo de sinal: " + ex);
        } catch (IOException ex) {
            System.err.println("Falha na leitura do arquivo de sinal: " + ex);
        }
        return sinal;
    }
}
