package ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://200.134.10.32/0CSM30?currentSchema=noturno", "noturno", "@noturno#");
        } catch (SQLException ex) {
            System.err.println("Falha na conexão com Banco de Dados: " + ex);            
            System.exit(0);
            return null;
        }
    }
}
