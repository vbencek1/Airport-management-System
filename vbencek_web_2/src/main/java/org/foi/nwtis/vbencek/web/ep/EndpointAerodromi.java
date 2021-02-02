/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.ep;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.foi.nwtis.vbencek.ejb.eb.Airports;
import org.foi.nwtis.vbencek.ejb.eb.DnevnikRada;
import org.foi.nwtis.vbencek.ejb.eb.Korisnici;
import org.foi.nwtis.vbencek.ejb.eb.Myairports;
import org.foi.nwtis.vbencek.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.DnevnikRadaFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.vbencek.konfiguracije.bp.BP_Konfiguracija;

/**
 * KLasa koja služi kao Krajnja točka odnosno Web socket
 * @author vbencek
 */
@ServerEndpoint("/aerodromiEP")
public class EndpointAerodromi {

    @EJB(beanName = "MyairportsFacade")
    MyairportsFacadeLocal myairportsFacade;
    @EJB(beanName = "AirportsFacade")
    AirportsFacadeLocal airportsFacade;
    @EJB(beanName = "KorisniciFacade")
    KorisniciFacadeLocal korisniciFacade;
    @EJB(beanName = "DnevnikRadaFacade")
    DnevnikRadaFacadeLocal dnevnikRadaFacade;

    @Inject
    ServletContext servletContext;
    @Inject
    SlanjePoruke slanjePoruke;

    static Queue<Session> queue = new ConcurrentLinkedQueue<>();

    int ciklus;
    static Boolean pokreni = true;
    static int brojac = 0;

    @PostConstruct
    public void init() {
        BP_Konfiguracija bpk = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");
        ciklus = Integer.parseInt(bpk.getKonfig().dajPostavku("websocket.ciklus"));
    }

    /**
     * Metoda koja prima poruku od korisnika. Ako je prazna poruka šalje broj
     * aerodroma za koje se preuzimaju podaci. Ako poruka nije prazna tada se
     * dodaje aerodrom korisniku i salje se JMS poruka ejb modulu.
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        if ("".equals(message) && pokreni) {
            pokreni = false;
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    send(dajBrojIDatum());
                }
            };
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(timerTask, 0, ciklus * 1000);
        }
        if (!"".equals(message)) {
            System.out.println("Web Socket primio: " + message);
            Myairports myairports = new Myairports();
            myairports.setIdent(dohvatiAerodrom(message.split(";")[1]));
            myairports.setUsername(dohvatiKorisnika(message.split(";")[0]));
            myairports.setStored(new Date(System.currentTimeMillis()));
            myairportsFacade.create(myairports);
            dodajUDnevnikRada("Dodaj aerodrom '" + message.split(";")[1] + "' u kolekciju", dohvatiKorisnika(message.split(";")[0]));
            slanjePoruke.saljiPoruku(oblikujPoruku(message.split(";")[0], message.split(";")[1]));
        }
    }

    @OnOpen
    public void openConnection(Session session) {
        queue.add(session);
        System.out.println("Otvorena veza.");
    }

    @OnClose
    public void closedConnection(Session session) {
        queue.remove(session);
        System.out.println("Zatvorena veza.");
    }

    @OnError
    public void error(Session session, Throwable t) {
        queue.remove(session);
        System.out.println("Zatvorena veza.");
    }

    /**
     * metoda kojom se šalje poruka.
     *
     * @param poruka
     */
    public static void send(String poruka) {
        try {
            for (Session session : queue) {
                session.getBasicRemote().sendText(poruka);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Metoda koja daje broj aerodroma za koje se preuzimaju podaci te trenutni
     * datum i vraća korisniku.
     *
     * @return
     */
    public String dajBrojIDatum() {
        String broj = String.valueOf(myairportsFacade.dohvatiBrojAerodroma());
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        return broj + " (" + date + ")";

    }

    /**
     * Metoda koja dohvaća korisnika prema korisničkom imenu.
     *
     * @param korime
     * @return
     */
    private Korisnici dohvatiKorisnika(String korime) {
        return korisniciFacade.find(korime);
    }

    /**
     * metoda koja vraća aerodrom ovisno o ident-u.
     *
     * @param ident
     * @return
     */
    private Airports dohvatiAerodrom(String ident) {
        return airportsFacade.find(ident);
    }

    /**
     * metoda koja zapisuje zahtjev korisnika u dnevnik rada.
     *
     * @param zahtjev
     * @param kor
     */
    public void dodajUDnevnikRada(String zahtjev, Korisnici kor) {
        DnevnikRada dr = new DnevnikRada();
        dr.setKorIme(kor);
        dr.setZahtjev("Web Socket Zahtjev: " + zahtjev);
        dr.setStored(new Date(System.currentTimeMillis()));
        dnevnikRadaFacade.create(dr);
    }

    /**
     * Metoda koja oblikuj poruku koja se dodaje u red JMS poruka.
     *
     * @param korisnik
     * @param icao
     * @return
     */
    public String oblikujPoruku(String korisnik, String icao) {
        brojac++;
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSSS").format(new Date());
        String poruka = "{\"id\": " + brojac + ", \"korisnik\": \"" + korisnik + "\", \"aerodrom\": \""
                + icao + "\", \"vrijeme\": \"" + date + "\"} ";
        return poruka;
    }
}
