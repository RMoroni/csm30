package dao;

import ConnectionFactory.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import pojo.Imagem;

public class ImagemDao {

    private final Connection connection;

    public ImagemDao() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public long addImagem(Imagem imagem) {
        /*
         a hora (de inicio e fim) e iterações só são obtidas
         no decorrer/fim do processo. Na criação não aparecem...
         */
        String sql = "insert into noturno.imagem "
                + "(altura, largura, status, cliente_id, nome) "
                + "values (?,?,?,?,?) returning id;";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setShort(1, imagem.getAltura());
            stmt.setShort(2, imagem.getLargura());
            stmt.setString(3, String.valueOf(imagem.getStatus()));
            stmt.setLong(4, imagem.getCliente_id());
            stmt.setString(5, imagem.getNome());

            ResultSet rs = stmt.executeQuery();

            //Preciso do id da imagem pra utilizar como nome do arquivo
            if (rs.next()) {
                return rs.getLong("id");
            } else {
                return -1;
            }
        } catch (SQLException ex) {
            System.err.println("Falha ao adicionar Imagem no Banco de Dados: " + ex);
            return -1;
        }
    }

    public Imagem getFirstOrNull(char status) {
        Imagem imagem = null;
        String sql = "select * from noturno.imagem where status = '"
                    + status + "' order by id limit 1;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                imagem = new Imagem();
                imagem.setAltura(rs.getShort("altura"));
                imagem.setLargura(rs.getShort("largura"));
                imagem.setStatus(rs.getString("status").charAt(0));
                imagem.setId(rs.getLong("id"));
            }

            rs.close();
            stmt.close();
            return imagem;
        } catch (SQLException ex) {
            System.err.println("Falha ao buscar imagem: " + ex);
            return null;
        }
    }

    public void updateImagem(Imagem imagem) {
        String sql = "update noturno.imagem set data_hora_inicio='" + imagem.getData_hora_inicio()
                + "', data_hora_fim='" + imagem.getData_hora_fim()
                + "', status=?, iteracoes=? where id=?;";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(imagem.getStatus()));
            stmt.setShort(2, imagem.getIteracoes());
            stmt.setLong(3, imagem.getId());
            stmt.execute();
        } catch (SQLException ex) {
            System.err.println("Falha ao atualizar imagem com id: " + imagem.getId());
            throw new RuntimeException(ex);
        }
    }
    
    public void updateImagemStatus(Imagem imagem) {
        String sql = "update noturno.imagem set status=? where id=?;";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(imagem.getStatus()));
            stmt.setLong(2, imagem.getId());
            stmt.execute();
        } catch (SQLException ex) {
            System.err.println("Falha ao atualizar status da imagem com id: " + imagem.getId());
            throw new RuntimeException(ex);
        }
    }
}
