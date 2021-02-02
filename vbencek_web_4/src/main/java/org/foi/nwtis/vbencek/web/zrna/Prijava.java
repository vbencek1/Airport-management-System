/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.vbencek.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.vbencek.ws.klijenti.Projekt_WS;

/**
 * Klasa koja služi za prijavu korisnika te čuvanje podataka o aktivnom korisniku
 * @author vbencek
 */
@Named(value = "prijava")
@SessionScoped
public class Prijava implements Serializable {
    
    @EJB(beanName = "KorisniciFacade")
    KorisniciFacadeLocal korisniciFacade;
    
    @Getter
    @Setter
    String korIme = "";
    @Getter
    @Setter
    String lozinka = "";
    @Getter
    @Setter
    private String porukaPrijave;
    
    @Inject
    Lokalizacija lokalizacija;
    
    /**
     * Metoda koja vrši prijavu korisnika pozivanjem metode web servise
     */
    public void prijavaKorisnika() {
        Projekt_WS ws= new Projekt_WS();
        ResourceBundle res = ResourceBundle.getBundle("org.foi.nwtis.vbencek.web.Prijevod", new Locale(lokalizacija.getJezik()));
        if(ws.prijaviKorisnika(korIme, lozinka)){
            porukaPrijave=res.getString("prijava.porukaUspjeh")+korIme;
        }else{
            porukaPrijave=res.getString("prijava.poruka");
            korIme="";
            lozinka="";
        }

    }
    
    /**
     * Metoda kojom se odjavljuje aktivni korisnik
     */
    public void odjaviKorisnika() {
        setKorIme("");
        setLozinka("");
        setPorukaPrijave("");
    }
}
