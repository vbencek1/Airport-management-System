/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.OdgovorAerodrom;
import org.foi.nwtis.vbencek.rest.klijenti.Projekt_RS;
import org.foi.nwtis.vbencek.ws.klijenti.Projekt_WS;

/**
 * Klasa koja korisi REST i Web servis za realizaciju svojih metoda. Služi za brisanje aerodroma i avione
 * te dohvaćanje aerodroma unutar raspona kao i dohvaćanje udaljenosti između aerodroma
 * @author NWTiS_2
 */
@Named(value = "pogledPeti")
@ViewScoped
public class PogledPeti implements Serializable {

    @Inject
    Prijava prijava;

    @Inject
    Lokalizacija lokalizacija;

    @Getter
    @Setter
    String korIme = "";
    @Getter
    @Setter
    String lozinka = "";
    @Getter
    @Setter
    String odabraniAerodrom = "";
    @Getter
    @Setter
    String min = "0";
    @Getter
    @Setter
    String max = "2000";
    @Getter
    @Setter
    String icao1 = "";
    @Getter
    @Setter
    String icao2 = "";
    @Getter
    @Setter
    String udaljenost = "0";

    @Getter
    String porukaBrisanja = "";

    @Getter
    List<org.foi.nwtis.vbencek.web.serveri.Aerodrom> aerodromiRaspon = new ArrayList<>();
    
    /**
     * Prije konstrukcije zrna dohvaćaju se podaci o prijavljenom korisniku
     */
    @PostConstruct
    public void init() {
        dohvatiPodatkeKorisnika();

    }
    
    /**
     * Metoda kojom se dohvaćaju podaci o aktivnom korisniku
     */
    public void dohvatiPodatkeKorisnika() {
        korIme = prijava.getKorIme();
        lozinka = prijava.getLozinka();
    }

    /**
     * Metoda koja ispisuje aktivnog korisnika u obliku formirane poruke
     *
     * @return formirana poruka
     */
    public String ispisiKorisnika() {
        ResourceBundle res = ResourceBundle.getBundle("org.foi.nwtis.vbencek.web.Prijevod", new Locale(lokalizacija.getJezik()));
        if ("".equals(korIme)) {
            return res.getString("pogled.nePrijavljeni");
        } else {
            return res.getString("pogled.Prijavljeni") + korIme;
        }
    }

    /**
     * Metoda koja pribavlja aerodrome korisnika iz baze podataka preko REST servisa.
     *
     * @return
     */
    public List<Aerodrom> dajVlastiteAerodrome() {
        dohvatiPodatkeKorisnika();
        Projekt_RS projekt_RS = new Projekt_RS(korIme, lozinka);
        OdgovorAerodrom odgovor = projekt_RS.dajAerodomeKorisnika(OdgovorAerodrom.class);
        return Arrays.asList(odgovor.getOdgovor());
    }
    
    /**
     * Metoda koja briše odabrani aerodrom ukoliko on nema letove.
     * Poziva se metoda REST servisa
     */
    public void obrisiAerodrom() {
        dohvatiPodatkeKorisnika();
        porukaBrisanja = "";
        if (!"".equals(odabraniAerodrom)) {
            ResourceBundle res = ResourceBundle.getBundle("org.foi.nwtis.vbencek.web.Prijevod", new Locale(lokalizacija.getJezik()));
            Projekt_RS projekt_RS = new Projekt_RS(korIme, lozinka);
            Response odgovor = projekt_RS.obrisiAerodrom(odabraniAerodrom);
            String status = odgovor.readEntity(String.class).split("\"")[7];
            if ("10".equals(status)) {
                porukaBrisanja = res.getString("pogledRS.brisanjeAer10");
            } else {
                porukaBrisanja = res.getString("pogledRS.brisanjeAer40");
            }

        }
    }
    
    /**
     * Metoda koja briše letove aerodroma. Poziva se metoda REST servisa
     */
    public void obrisiLetoveAerodroma() {
        dohvatiPodatkeKorisnika();
        porukaBrisanja = "";
        if (!"".equals(odabraniAerodrom)) {
            ResourceBundle res = ResourceBundle.getBundle("org.foi.nwtis.vbencek.web.Prijevod", new Locale(lokalizacija.getJezik()));
            Projekt_RS projekt_RS = new Projekt_RS(korIme, lozinka);
            Response odgovor = projekt_RS.obrisiAvioneAerodroma(odabraniAerodrom);
            String status = odgovor.readEntity(String.class).split("\"")[7];
            if ("10".equals(status)) {
                porukaBrisanja = res.getString("pogledRS.brisanjeAvi10");
            } else {
                porukaBrisanja = res.getString("pogledRS.brisanjeAvi40");
            }
        }
    }
    
    /**
     * Metoda koja vraće sve aerodrome korisnika koji se nalaze u blizi izabranog aerodrome u danom rasponu.
     * Poziva se metoda web servisa
     */
    public void pretraziAerodromeRaspon() {
        dohvatiPodatkeKorisnika();
        if (!"".equals(odabraniAerodrom)) {
            double minD = Double.parseDouble(min);
            double maxD = Double.parseDouble(max);
            Projekt_WS projekt_WS = new Projekt_WS();
            aerodromiRaspon = projekt_WS.dajAerodromeRasponaa(korIme, lozinka, odabraniAerodrom, minD, maxD);
        }
    }
    
    /**
     * Metoda koja izračunava udaljenost između dva aerodroma.
     * Poziva se metoda Web servisa
     */
    public void izracunajUdaljenost() {
        dohvatiPodatkeKorisnika();
        Projekt_WS projekt_WS = new Projekt_WS();
        double dist = projekt_WS.dajUdaljenostAerodroma(korIme, lozinka, icao1.toUpperCase(), icao2.toUpperCase());
        udaljenost = String.valueOf(dist);
    }

}
