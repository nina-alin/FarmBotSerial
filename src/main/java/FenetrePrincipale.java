import jssc.SerialPortException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FenetrePrincipale extends JDialog {

    private JPanel panel;
    private JButton btnAllumage;
    private JButton btnExtinction;
    private JButton DeplXdownButton;
    private JButton DeplYupButton;
    private JButton DeplYdownButton;
    private JButton DeplXupButton;
    private JButton DeplZupButton;
    private JButton DeplZdownButton;
    private JButton button7;
    private JButton button8;
    private JButton CaseB1;
    private JButton CaseC2;
    private JButton CaseC3;
    private JButton CaseC4;
    private JButton CaseC5;
    private JButton CaseC6;
    private JButton CaseA1;
    private JButton CaseA2;
    private JButton CaseA3;
    private JButton CaseA4;
    private JButton CaseA5;
    private JButton CaseA6;
    private JButton CaseB2;
    private JButton CaseB3;
    private JButton CaseB4;
    private JButton CaseB5;
    private JButton CaseB6;
    private JButton CaseC1;
    FarmbotSerial2 farmbot1;
    private int posX = 0, posY = 0, posZ = 0;

    public FenetrePrincipale() {
        setContentPane(panel);
        try {
            // Ajouter une variable de lancement
            String shell = System.getenv("SHELL");
            if (shell == null) // /bin/bash sous linux
                farmbot1 = FarmbotSerial2.getInstance("COM3");
            else
                farmbot1 = FarmbotSerial2.getInstance("/dev/ttyACM0");

            btnAllumage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        System.out.println("Bouton appuyé");

                        farmbot1.envoyerOrdre("F41 P7 V1 M0 Q0");
                    } catch (Exception er) {
                        System.out.println("erreur " + er.getMessage());
                    }
                }
            });
            btnExtinction.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {

                        farmbot1.envoyerOrdre("F41 P7 V0 M0 Q0");
                    } catch (Exception er) {
                        System.out.println("erreur " + er.getMessage());
                    }
                }
            });
            DeplXupButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        System.out.println("*************Bouton appuyé x"+ farmbot1.GetX());

                        posX += 5;
                        farmbot1.gotoXYZ(posX, posY, posZ);
                    } catch (Exception er) {
                        System.out.println("erreur " + er.getMessage());
                    }
                }
            });
        } catch (Exception er) {
            er.printStackTrace();

        }








    }


    public static void main(String[] args) {
        FenetrePrincipale dialog = new FenetrePrincipale();
        dialog.pack();
        dialog.setVisible(true);
    }
}
