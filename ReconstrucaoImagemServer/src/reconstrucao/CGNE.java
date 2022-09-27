package reconstrucao;

import dao.ImagemDao;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import org.jblas.DoubleMatrix;
import pojo.Imagem;

public class CGNE implements Runnable {

    public DoubleMatrix abreSinal(long id_img) {

        //Recupera arquivo com dados do sinal, salvo com id da img
        final File arquivo_sinal = new File("../Sinal/" + id_img + ".si");
        DoubleMatrix sinal;
        ObjectInputStream input_stream;

        try {
            input_stream = new ObjectInputStream(new FileInputStream(arquivo_sinal));
            sinal = (DoubleMatrix) input_stream.readObject();
            input_stream.close();
        } catch (IOException ex) {
            System.err.println("Falha ao abrir arquivo de sinal com id:"
                    + id_img + "\nException: " + ex);
            return null;
        } catch (ClassNotFoundException ex) {
            System.err.println("Falha ao converter classe na abertura de sinal com id:"
                    + id_img + "\nException: " + ex);
            return null;
        }
        return sinal;
    }

    public DoubleMatrix getModelo(short altura, short largura) {
        System.out.print("Abrindo modelo...");
        String caminho;

        //Verifica qual modelo será utilizado
        if (altura == 60 && largura == 60) {
            caminho = "../Arquivos/Imagem-A.part01/H-1.txt";
        } else {
            return null;
        }

        //Abre o modelo no formato de matriz
        DoubleMatrix modelo = new DoubleMatrix(50816, 3600);
        try (FileReader arquivo_modelo = new FileReader(caminho)) {
            BufferedReader buffer_arquivo_modelo = new BufferedReader(arquivo_modelo);
            String linha = buffer_arquivo_modelo.readLine();
            String[] coluna = linha.split(",");
            for (int i = 0; i < 50816; i++) {
                for (int j = 0; j < 3600; j++) {
                    modelo.put(i, j, Double.valueOf(coluna[j]));
                }
                linha = buffer_arquivo_modelo.readLine();
                coluna = linha.split(",");
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Falha na abertura do arquivo de modelo: " + ex);
            return null;
        } catch (IOException ex) {
            System.err.println("Falha na leitura do arquivo de modelo: " + ex);
            return null;
        } catch (NullPointerException ex) {
            //System.err.println("Falha no split (tá susse): " + ex);            
        }
        System.out.println("pronto!");
        return modelo;
    }

    public void salvaImagem(DoubleMatrix f, long id_img) {
        System.out.print("salvando imagem no disco...");
        float menor = Float.MAX_VALUE;
        float maior = Float.MIN_VALUE;

        //Normalização -> ajustar os valores para 0 a 255
        for (int i = 0; i < 3600; i++) {
            if (f.get(i) >= maior) {
                maior = (float) f.get(i);
            }
            if (f.get(i) <= menor) {
                menor = (float) f.get(i);
            }
        }

        //como eu já sei que o menor será negativo...
        maior -= menor;

        //ajusta escala pra ficar entre 0 e 255         
        float aux;
        for (int i = 0; i < 3600; i++) {
            aux = (float) (((f.get(i) - menor) / maior) * 255);
            f.put(i, aux);
        }

        //Cria imagem no buffer e adiciona os valores normalizados
        short linha = 0;
        BufferedImage imagem = new BufferedImage(60, 60, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < 60; i++) {
            for (int j = 0; j < 60; j++) {
                short cor = (short) f.get(linha);
                imagem.setRGB(i, j, new Color(cor, cor, cor).getRGB());
                linha++;
            }
        }

        //Salva imagem no disco, formato png (nome do arquivo é o id da img)
        try {
            ImageIO.write(imagem, "PNG", new File("../Imagem/" + id_img + ".png"));
            //System.out.println("imagem salva com sucesso: " + id_img);
        } catch (IOException ex) {
            System.err.println("Erro ao salvar imagem em disco: " + ex);
        }
    }

    public short cgne(DoubleMatrix modelo, DoubleMatrix sinal, long id_img) {

        //ñ conseguiu obter sinal/modelo
        if (modelo == null || sinal == null) {
            return -1;
        }
        System.out.print("Reconstrução Iniciada...");

        DoubleMatrix f = DoubleMatrix.zeros(3600, 1);
        double norma;
        double aux_norma = Double.MAX_VALUE;
        double alfa;
        double beta;
        double aux;
        short i = 0;

        DoubleMatrix residuo = sinal.sub(modelo.mmul(f)); //g - H*f
        DoubleMatrix p = modelo.transpose().mmul(residuo); //Ht * r0
        norma = residuo.norm2();
        while (Math.abs(aux_norma - norma) > 1e-4) {
            aux = (residuo.transpose().mmul(residuo)).get(0);
            alfa = aux / (p.transpose().mmul(p)).get(0); //(Rt*R)/(Pt*P)            
            f.addi(p.mmul(alfa)); //f = f + a*p -> addi (in-place)
            residuo.subi(modelo.mmul(alfa).mmul(p)); //r i+1 = r - a*H*p
            beta = (residuo.transpose().mmul(residuo)).get(0) / aux; //(Rt*R)/(Rt*R)
            p = modelo.transpose().mmul(residuo).add(p.mmul(beta)); //Ht*r + b*p

            aux_norma = norma; //salva norma anterior
            norma = residuo.norm2(); //atualiza a norma

            i++;
            if (i >= 20) return -1; //limite de iterações
        }

        //Salva a imagem no disco e retorna o número de iterações
        this.salvaImagem(f, id_img);
        System.out.println("finalizado!");
        return i;
    }

    @Override
    public void run() {
        ImagemDao imgdao = new ImagemDao();
        Imagem imagem;

        //Formato para gravar no PostgreSQL (timestamp)
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        while (true) {
            //Busca a próxima da fila
            imagem = imgdao.getFirstOrNull('a');

            //se não tem ninguem na fila, vai para prox iteração
            if (imagem == null) continue;

            //Atualiza status da imagem para 'r'
            imagem.setStatus('r');
            imgdao.updateImagemStatus(imagem);

            //Inicia reconstrução
            String data_hora_inicio = dateFormat.format(new Date());
            short resultado = this.cgne(this.getModelo(imagem.getAltura(),
                    imagem.getLargura()),
                    abreSinal(imagem.getId()),
                    imagem.getId());
            String data_hora_fim = dateFormat.format(new Date());

            //Salva o resultado no banco de dados
            imagem.setData_hora_inicio(data_hora_inicio);
            imagem.setData_hora_fim(data_hora_fim);
            if (resultado == -1) {
                imagem.setStatus('i'); //i -> impossível
                System.out.println("Não foi possível reconstruir a imagem!");
            } else {
                imagem.setStatus('s'); //s -> sucesso
                imagem.setIteracoes(resultado);
            }
            //atualiza no bd
            imgdao.updateImagem(imagem);
        }
    }
}
