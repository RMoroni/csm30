package reconstrucao;

import java.awt.Image;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import org.jblas.DoubleMatrix;

@WebService(name = "ReconstrucaoServer")
@SOAPBinding(style = Style.RPC)
public interface ReconstrucaoServer {
    
    //Recebe requisição do cliente, cria registro no db com status: 'a'
    @WebMethod void request(DoubleMatrix sinal, long id_user, short altura, 
            short largura, String nome_imagem);
    
    //Permite ao cliente acessar uma imagem salva por ele, @param: id da imagem no bd
    @WebMethod Image getImagem(long i);
}
