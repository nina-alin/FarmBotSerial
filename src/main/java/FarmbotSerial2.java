import jssc.SerialPort;
import jssc.SerialPortException;

import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class FarmbotSerial2 {

    private SerialPort serialPort;

    private List<String> currentBuffer = new ArrayList<>();

    // Singleton instance
    private static FarmbotSerial2 _instance = null;
    private boolean isStarted = false;
    private String statusCommand = "R00";
    private double X=0;
    private double Y=0;
    private double Z=0;
 public double GetX(){
     return X;
 }
    public class FarmotReaderThread implements Runnable {

        @Override
        public void run() {
            while (serialPort != null && serialPort.isOpened()) {
                try {
                    String line = readStringFarmBot();
                    if (line == null) {
                        continue;
                    }
                    if (line.contains("R99")) {
                        isStarted = true;
                    }
                    if (line.contains("R00")){
                        statusCommand = "R00";
                    }
                    if (line.contains("R01")){
                        statusCommand = "R01";
                    }
                    if (line.contains("R02")){
                        statusCommand = "R02";
                    }
                    if (line.contains("R03")){
                        statusCommand = "R03";
                    }
                    if (line.contains("R04")){
                        statusCommand = "R04";
                    }
                    if (line.contains("R82")){
                       String[] tab= line.split(" "); //Split "R82 X-4.2 Y32.3 Z40.0
                       String sX = tab[1].substring(1); //"X-4.2" -> "-4.2"
                       // sX=sX.replace(".",",");
                        X = Double.valueOf(sX);
                        String sY = tab[2].substring(1); //"X-4.2" -> "-4.2"
                       // sY=sY.replace(".",",");
                        Y = Double.valueOf(sY);
                        String sZ = tab[3].substring(1); //"X-4.2" -> "-4.2"
                       // sZ=sZ.replace(".",",");
                        Z = Double.valueOf(sX);
                    }
                    System.out.println(line);
                  //  currentBuffer.add(line);
                } catch (SerialPortException e) {
                    System.out.println("Impossible de lire les données du Farmbot");
                }
            }
            System.out.println("Fin du thread de lecture");
        }

    }

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
        envoyerOrdre(ordre,3000);
    }
    public void envoyerOrdre(String ordre,int timeOut) throws SerialPortException {
        String rep = "";

      statusCommand = "R00";

        serialPort.writeString(ordre + "\r\n");

        long start= System.currentTimeMillis();
        long time = 0;

        while ((statusCommand!= "R02")&&(statusCommand!= "R03")&&(time<timeOut))// attendre fin de commande avec succès
        {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time = System.currentTimeMillis()-start;

        }

    }

    private FarmbotSerial2(String port) throws SerialPortException, InterruptedException {
        serialPort = new SerialPort(port);
        serialPort.openPort();//Open serial port
        serialPort.setParams(115200, 8, 1, 0);//Set params.*/
        serialPort.writeString("\r\n");

        if (!serialPort.isOpened()) {
            System.out.println("Impossible d'ouvrir le port " + port);
            return;
        }

        // Je lance le thread de lecture
        Thread t = new Thread(new FarmotReaderThread());
        t.start();
        // Il faut attendre le démarrage du farmbot (soit on attends 5s soit on attends la reception de la trame R99
        String firstMessage;
        do {
            System.out.println("********************isstarted*****************");
            Thread.sleep(500);
        } while (!isStarted);

        System.out.println("**********************Startup completed********************");

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
        System.out.println("**********************Startup completed********************");


    }

    public static FarmbotSerial2 getInstance(String port) throws SerialPortException, InterruptedException {
        if (_instance == null) {

            _instance = new FarmbotSerial2(port);

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
