/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.iticbcn.clientiot;

import java.sql.Connection;
import java.sql.SQLException;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

public class DispositiuIot{

    //private static final String FICH_CLAU_PUBLICA = "./client1certs/";
    private static final String FICH_CLAU_PRIVADA = "./client1certs/e7671e5cf12d30a3a09da9c17429c21c1ebce27ce52c586fce8d3bcab0eca70a-private.pem.key"; //cambiado
    private static final String FICH_CERT_DISP_IOT = "./client1certs/e7671e5cf12d30a3a09da9c17429c21c1ebce27ce52c586fce8d3bcab0eca70a-certificate.pem.crt"; //cambiado
    private static final String ENDPOINT = "a2vhqvhv1hog7d-ats.iot.us-east-1.amazonaws.com"; //cambiado
    //public static final String TOPIC = "iticbcn/espnode01/pub";
    //topic de subscribcion; el topic de respuesta es esp32/pub
   

    private static AWSIotMqttClient awsIotClient;
    private AWSIotMessage latestMessage;    //codi nou afegit

    public static void setClient(AWSIotMqttClient cli) {
        awsIotClient = cli;
    }

    public static void initClient() {
        String cliEP = ENDPOINT;
        String cliId = Constants.CLIENT_ID;

        String certFile = FICH_CERT_DISP_IOT;
        String pKFile = FICH_CLAU_PRIVADA;
    

        if (awsIotClient == null && certFile != null && pKFile != null) {
            String algorithm = null;
            
            KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certFile, pKFile, algorithm);

            awsIotClient = new AWSIotMqttClient(cliEP, cliId, pair.keyStore, pair.keyPassword);
        }

        if (awsIotClient == null) {
            throw new IllegalArgumentException("Error als construir client amb el certificat o les credencials.");
        }
    }

    public void conecta() throws AWSIotException{
        initClient();
        awsIotClient.connect();
    }
/*
    public void subscriu() throws AWSIotException{
        TopicIoT topic= new TopicIoT(TOPIC, TOPIC_QOS);
        awsIotClient.subscribe(topic, true);
    }
*/
//codi nou afegit
    public void subscriu() throws AWSIotException {
        TopicIoT topic = new TopicIoT(Constants.TOPIC, Constants.TOPIC_QOS) {
            @Override
            public void onMessage(AWSIotMessage message) {
                latestMessage = message; // Store the latest message
                System.out.println("Message received: " + message.getStringPayload());
                try (Connection con = ConnectDB.getConnection(Constants.DB_URL, Constants.DB_USER, Constants.DB_PSSWD)) {
                    AccesMethodsToDB access = new AccesMethodsToDB();
                    access.insertUserId(con, message);
                } catch (SQLException e) {
                    System.out.println("Error de conexión: " + e.getMessage());
                    //MANDO MENSAJE ERROR
                    return;
                }
                //MANDO MENSAJE OK
            }
        };
        awsIotClient.subscribe(topic, true);
    }

    public AWSIotMessage getLatestMessage() {
        return latestMessage;
    }
}
