/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.vbencek.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.vbencek.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.vbencek.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.vbencek.sb.PreuzimanjePodatakaLocal;

/**
 * Web application lifecycle listener.
 *
 * @author NWTiS_1
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    @Inject
    PreuzimanjePodatakaLocal ppl;
    String korImeOSN;
    String lozinkaOSN;
    String pocetakDatumPreuzimanja;
    String krajDatumPreuzimanja;
    int preuzimanjeCiklus;
    int preuzimanjePauza;
    int port;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String datoteke = sc.getInitParameter("konfiguracija");
        String putanja = sc.getRealPath("WEB-INF") + File.separator + datoteke;
        try {
            BP_Konfiguracija konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            ppl.test();
            try {
                Thread.sleep(4000);
                dodijeliVrijednosti(konf);
                ppl.preuzimanjePodataka(korImeOSN, lozinkaOSN, pocetakDatumPreuzimanja, krajDatumPreuzimanja, preuzimanjeCiklus, preuzimanjePauza,port);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Aplikacija je pokrenuta!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Aplikacija je zaustavljana!");
    }

    private void dodijeliVrijednosti(BP_Konfiguracija konf) {
        korImeOSN = konf.getOSNUsername();
        lozinkaOSN = konf.getOSNPassword();
        pocetakDatumPreuzimanja = konf.getDownloadStart();
        krajDatumPreuzimanja = konf.getDownloadEnd();
        preuzimanjeCiklus = Integer.parseInt(konf.getDownloadCycle()) * 1000;
        preuzimanjePauza = Integer.parseInt(konf.getDownloadBreak());
        port = Integer.parseInt(konf.getKonfig().dajPostavku("posluzitelj.port"));
    }

}
