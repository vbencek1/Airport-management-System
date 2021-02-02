/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.zrna;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.vbencek.web.serveri.Aerodrom;
import org.foi.nwtis.vbencek.web.serveri.AvionLeti;
import org.foi.nwtis.vbencek.ws.klijenti.Projekt_WS;

/**
 * Klasa koja za svoje prikaze korisi metode web servisa. prikazuje aerodrome korisnika,
 * avione aerodroma u određenom vremenskom intervalu te letove odabranog aviona.
 * @author NWTiS_2
 */
@Named(value = "pogledCetvrti")
@ViewScoped
public class PogledCetvrti implements Serializable {

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
    String odDatuma = "";
    @Getter
    @Setter
    String doDatuma = "";
    @Getter
    @Setter
    String odabraniAerodrom = "";
    @Getter
    @Setter
    String odabraniAvion= "";

    
    /**
     * Pri konstruiranju se dohvaćaju podaci o prijavljenom korisniku
     */
    @PostConstruct
    public void init() {
        dohvatiPodatkeKorisnika();

    }
    
    /**
     * Metoda kojom se dohvaćaju podaci o prijavljenom korisniku
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
     * Metoda kojom se dohvaćaju aerodromi prijavljenog korisnika
     * @return 
     */
    public List<Aerodrom> prikaziAerodromeKorisnika() {
        dohvatiPodatkeKorisnika();
        Projekt_WS projekt_WS = new Projekt_WS();
        return projekt_WS.dajAerodromeKorisnika(korIme, lozinka);

    }
    
    /**
     * Metoda koja postavlja izabrani aerodrom
     * @param ident 
     */
    public void prikaziAvione(String ident) {
        odabraniAerodrom = ident;
    }
    
    /**
     * Metoda koja dohvaća avione aerodroma u određenom vremenskom intervalu
     * @return 
     */
    public List<AvionLeti> prikaziAvioneAerodroma() {
        if (!"".equals(odabraniAerodrom)) {
            dohvatiPodatkeKorisnika();
            Projekt_WS projekt_WS = new Projekt_WS();
            return projekt_WS.dajAvioneAerodroma(korIme, lozinka, odabraniAerodrom, konvertirajVrijeme(odDatuma), konvertirajVrijeme(doDatuma));
        }
        return new ArrayList<>();
    }
    
    /**
     * Metoda koja služi za konverziju vremena kako bi se korisniku prikazalu u hrvatskom formatu
     * @param vrijeme
     * @return 
     */
    public long konvertirajVrijeme(String vrijeme){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date parsed;
        try {
            parsed = dateFormat.parse(vrijeme);
            java.sql.Date date = new java.sql.Date(parsed.getTime());
            long vr = date.getTime() / 1000;
            return vr;
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        } return 0;
    }
    
    /**
     * Metoda koja služi za pretvorbu unix timestamp-a u ljudsko vrijeme.
     * @param unixVr
     * @return 
    */
    public String pretvoriUDatum(long unixVr) {
        Date date = new java.util.Date(unixVr * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        String strDat = sdf.format(date);
        return strDat;
    }
    
    /**
     * Metoda koja pridružuje izrabrani aerodrom varijabli
     * @param icao24 
     */
    public void prikaziLetove(String icao24) {
        odabraniAvion = icao24;
    }
    
    /**
     * Metoda koja služi za dohvaćanje letova izabranog aviona unutar danog vremenskog razmaka
     * @return 
     */
    public List<AvionLeti> prikaziLetoveAviona() {
    if (!"".equals(odabraniAvion)) {
            dohvatiPodatkeKorisnika();
            Projekt_WS projekt_WS = new Projekt_WS();
            return projekt_WS.dajLetoveAviona(korIme, lozinka, odabraniAvion, konvertirajVrijeme(odDatuma), konvertirajVrijeme(doDatuma));
        }
        return new ArrayList<>();
    }
    
    

}
