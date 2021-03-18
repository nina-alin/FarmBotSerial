import jssc.SerialPort;
import jssc.SerialPortException;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class FarmbotSerial {

    private SerialPort serialPort;

    private List<String> currentBuffer = new ArrayList<>();

    // Singleton instance
    private static FarmbotSerial _instance = null;

    public String readStringFarmBot() throws SerialPortException {
        String reponse = "";
        String s = "";
        while (s.compareTo("\n") != 0) {
            s = serialPort.readString(1);
            if (s != null) {
                reponse = reponse + s;
            }

        }
        return reponse;
    }

    public void envoyerOrdre(String ordre) throws SerialPortException {
        String rep = "";

        serialPort.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR | SerialPort.PURGE_RXABORT | SerialPort.PURGE_TXABORT);  // on vide les buffers envoie et réception


        //serialPort.writeBytes((ordre + "\r\n").getBytes(StandardCharsets.ISO_8859_1));
        serialPort.writeString(ordre + "\r\n");

        LocalTime startTime = LocalTime.now();


        while (!rep.startsWith("R02 Q0")) // attendre fin de commande avec succès
        {


            rep = readStringFarmBot();
            System.out.println((rep));


        }

    }

    private FarmbotSerial(String port) throws SerialPortException, InterruptedException {
        serialPort = new SerialPort(port);
        serialPort.openPort();//Open serial port
        serialPort.setParams(115200, 8, 1, 0);//Set params.*/
        serialPort.writeString("\r\n");

        if (!serialPort.isOpened()) {
            System.out.println("Impossible d'ouvrir le port " + port);
            return;
        }
        // Il faut attendre le démarrage du farmbot (soit on attends 5s soit on attends la reception de la trame R99
        String firstMessage;
        do {
            firstMessage = readStringFarmBot();
            System.out.println((firstMessage));
        } while (!firstMessage.startsWith("R99"));

        System.out.println("Startup completed");

        envoyerOrdre("F22 P2 V1 Q0");
        envoyerOrdre("F22 P101 V1");
        envoyerOrdre("F22 P102 V1");
        envoyerOrdre("F22 P103 V1");
        envoyerOrdre("F22 P125 V1");
        envoyerOrdre("F22 P126 V1");
        envoyerOrdre("F22 P127 V1");
        envoyerOrdre("F22 P71 V300");
        envoyerOrdre("F22 P72 V300");
        envoyerOrdre("F22 P73 V300");
        envoyerOrdre("F22 P65 V200");
        envoyerOrdre("F22 P66 V200");
        envoyerOrdre("F22 P67 V200");


    }

    public static FarmbotSerial getInstance(String port) throws SerialPortException, InterruptedException {
        if (_instance == null) {

            _instance = new FarmbotSerial(port);

        }
        return _instance;
    }

    public void gotoXYZ(int x, int y, int z) {
        String sx = String.valueOf(x);
        String sy = String.valueOf(y);
        String sz = String.valueOf(z);
        try {
            envoyerOrdre("G0 X" + sx + " Y" + sy + " Z" + sz);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

}
