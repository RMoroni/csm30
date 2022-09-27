package reconstrucao;

import dao.ImagemDao;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.imageio.ImageIO;
import javax.jws.WebService;
import org.jblas.DoubleMatrix;
import pojo.Imagem;

@WebService(endpointInterface = "reconstrucao.ReconstrucaoServer")
public class ReconstrucaoServerImpl implements ReconstrucaoServer {
           
    public void salvaSinal(long id_img, DoubleMatrix sinal){
        
        //Arquivo que será salvo (a extensão tanto faz)
        final File arquivo_sinal = new File("../Sinal/" + id_img + ".si");
        
        ObjectOutputStream out_stream;
        
        try{
            out_stream = new ObjectOutputStream(new FileOutputStream(arquivo_sinal));
            //escreve o objeto em disco
            out_stream.writeObject(sinal);
        } catch (IOException ex) {
            System.err.println("Falha ao salvar arquivo de sinal com id:"
                    + id_img + "\nIOException: " + ex);
        }
    }
    
    @Override
    public Image getImagem(long i){
        try {
            //Abre imagem a partir do id
            Image imagem = ImageIO.read(new File("../Imagem/" + i + ".png"));
            return imagem;
        } catch (IOException ex) {
            System.err.println("Falha ao abrir imagem reconstruída: " + ex);
            return null;
        } catch (Exception ex){
            System.err.println("Falha ao abrir imagem reconstruída: " + ex);
            return null;
        }
    }

    @Override
    public void request(DoubleMatrix sinal, long id_user, short altura, 
        short largura, String nome_imagem) {
        
        //Nova imagem que será salva na fila
        Imagem imagem = new Imagem();
        imagem.setAltura(altura);
        imagem.setLargura(largura);
        imagem.setCliente_id(id_user);
        imagem.setNome(nome_imagem);
        imagem.setStatus('a'); //a -> aguardando
        
        //adicionado imagem no banco de dados (entrou na 'fifo')
        //Salvo o id gerado para utilizar no nome dos arquivos
        long id_img = new ImagemDao().addImagem(imagem);
        
        //Finaliza o request caso não tenha conseguido add
        if (id_img == -1) return;
        
        //salva o sinal
        this.salvaSinal(id_img, sinal);
    }
}
