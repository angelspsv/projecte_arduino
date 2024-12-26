package cat.iticbcn.clientiot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import com.amazonaws.services.iot.client.AWSIotMessage;

public class AccesMethodsToDB {

    public void selectAlumnes (Connection con) {
        String sql = "SELECT * FROM alumne"; // Consulta SQL
        
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            int id = rs.getInt("IdAlumne");
            String nombre = rs.getString("NomAlumne");
            System.out.println("ID: " + id + ", Nom: " + nombre);
        }
        
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta: " + e.getMessage());
        }


    }

    public void insertRegistry(Connection con, AWSIotMessage message) {

        //agafar el message.getStringPayload()
        //fer l'insert a la taula de registres

    }




    //metode propi per inserir una entrada en la taula EntradesSortides
    //metode abreviat 
    public void insertUserId(Connection conn, AWSIotMessage message) {
        //premaramos formato de fecha y hora para el insert
        LocalDateTime myDateObj = LocalDateTime.now();
        // Convertir la fecha y hora al formato de MySQL
        //retorna String formato "fecha hora"
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj); 
        String[] arrayDataHora = formattedDate.split(" ");
        String fecha = arrayDataHora[0];
        String hora = arrayDataHora[1];
        
        int userId = 1; //default

        //extraemos el numero de la tarjeta del mensaje recibido
        String mensaje_recibido = message.getStringPayload();
        /*
        String[] partes = mensaje_recibido.split("/");
        if(partes.length == 3){
            //me quedo con la ultima parte del mensaje que equivale al numero de tarjeta
            if((partes[2]).equals("63BF6F11")){
                userId = 2;
            }
        }else{
            System.out.println("Revisar el mensaje enviado o incorporar otros numeros de tarjeta");
        }
        */

        // Parsear el JSON
        JSONObject jsonObject = new JSONObject(mensaje_recibido);

        // Obtener el valor de cardUID
        String cardUID = jsonObject.getString("cardUID");

        if(cardUID.equals("34394180")){
            //equivale al numero de tarjeta sacado del objeto json recibido desde AWS
            userId = 2;
        } else if (cardUID.equals("3FEA1D0")){
            userId = 3;
        } else if (cardUID.equals("A3D5EFE0")){
            userId = 4;
        } else if (cardUID.equals("63BF6F11")){
            userId = 5;
        }else{
            System.out.println("Revisar el mensaje enviado o incorporar otros numeros de tarjeta");
        }


        
        //otros datos para el insert
        String tipus = "entrada";
        int idAula = 1;

        String insertSQL = "INSERT INTO EntradesSortides (id_usuario, data, hora, tipus, id_aula) VALUES (?,?,?,?,?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)){
            //preparamos los valores para el insert en la tabla
            insertStmt.setInt(1, userId);
            insertStmt.setString(2, fecha);
            insertStmt.setString(3, hora);
            insertStmt.setString(4, tipus);
            insertStmt.setInt(5, idAula);
            insertStmt.executeUpdate();
            System.out.println("Movimiento de usuario con id " + userId + " registrado correctamente como: " + tipus);
        
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta: " + e.getMessage());
        }

    }
    
}
