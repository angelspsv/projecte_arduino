package cat.iticbcn.clientiot;

import com.amazonaws.services.iot.client.AWSIotQos;

public class Constants {

    public static final String TOPIC = "esp32/sub";   //cambiado
    public static final String CLIENT_ID = "MyJavaClient2"; //cambiado
    public static final AWSIotQos TOPIC_QOS = AWSIotQos.QOS0;

    static String DB_URL = "jdbc:mysql://192.168.12.2:3306/RegistreAsistencia"; 
    static String DB_USER = "root"; 
    static String DB_PSSWD = "pirineus"; 
}
