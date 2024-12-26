package cat.iticbcn.clientiot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

/* 
    private String StringConn;
    private String userdb;
    private String passwd;

    public ConnectDB(String stringConn, String userdb, String passwd) {
        this.StringConn = stringConn;
        this.userdb = userdb;
        this.passwd = passwd;
    }

    public String getStringConn() {
        return StringConn;
    }

    public String getUserdb() {
        return userdb;
    }

    public String getPasswd() {
        return passwd;
    } */

    public static Connection getConnection(String urlConn, String name, String pass) throws SQLException {

        Connection connection;

        try {
            // Establecer la conexión
            connection = DriverManager.getConnection(urlConn, name, pass);
            return connection;
        } catch (SQLException e) {
            // Si ocurre un error, se lanza la excepción
            throw new SQLException("Error al establecer la conexión a la base de datos", e);
        }
    }
   
}
