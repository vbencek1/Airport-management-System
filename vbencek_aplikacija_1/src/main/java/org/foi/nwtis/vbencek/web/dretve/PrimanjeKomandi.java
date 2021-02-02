/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.dretve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import org.foi.nwtis.vbencek.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa koja čeka korisnika da se spoji na mrežnu utičnicu te mu dodijeljuje dretvu unutar svoje run metode koja zatim obrađuje zahtjev korisnika
 * @author vbencek
 */
public class PrimanjeKomandi extends Thread {

    private BP_Konfiguracija konf;
    Thread dretva = null;
    public ServerSocket server = null;
    private int port;
    private int makscekaca;
    public static boolean radi=true;

    public PrimanjeKomandi(BP_Konfiguracija konf) {
        this.konf = konf;
    }


    @Override
    public void run() {
        try {
            port = Integer.parseInt(konf.getKonfig().dajPostavku("posluzitelj.port"));
            makscekaca = Integer.parseInt(konf.getKonfig().dajPostavku("posluzitelj.cekaci"));
            server = new ServerSocket(port, makscekaca);
            while (radi) {
                try {
                    System.out.println("Čekam komande...");
                    Socket socket = server.accept();
                    dretva = new ObradaKomandi(socket, konf);
                    dretva.start();
                } catch (SocketException e) {
                    break;
                }
            }
            server.close();
        } catch (IOException e) {
            System.out.println("Socket je zatvoren!");
        }
    }

    @Override
    public void interrupt() {
        if (dretva != null) {
            dretva.interrupt();
        }
        if (server != null) {
            try {
                server.close();
            } catch (IOException ex) {
                System.out.println("Nije bilo moguće zatvoriti socket");
            }
        }

    }

}
