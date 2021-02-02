/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.rest.serveri;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.foi.nwtis.vbencek.ejb.eb.Airplanes;
import org.foi.nwtis.vbencek.ejb.eb.Airports;
import org.foi.nwtis.vbencek.ejb.eb.DnevnikRada;
import org.foi.nwtis.vbencek.ejb.eb.Korisnici;
import org.foi.nwtis.vbencek.ejb.eb.Myairports;
import org.foi.nwtis.vbencek.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.DnevnikRadaFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.MyairportsFacadeLocal;

/**
 * Klasa koja se koristi kao pomoćna klasa REST servisnoj klasi.
 * @author vbencek
 */
@Named(value = "podaciREST")
@SessionScoped
public class PodaciREST implements Serializable {

    @EJB(beanName = "KorisniciFacade")
    KorisniciFacadeLocal korisniciFacade;
    @EJB(beanName = "MyairportsFacade")
    MyairportsFacadeLocal myairportsFacade;
    @EJB(beanName = "DnevnikRadaFacade")
    DnevnikRadaFacadeLocal dnevnikRadaFacade;
    @EJB(beanName = "AirplanesFacade")
    AirplanesFacadeLocal airplanesFacade;

    List<Korisnici> korisnici = new ArrayList<>();
    List<Myairports> myaerodromi = new ArrayList<>();
    Korisnici aktivniKorisnik = new Korisnici();

    @PostConstruct
    public void ucitajBean() {
        System.out.println("Ucitavam");
        korisnici = korisniciFacade.findAll();
        myaerodromi = myairportsFacade.findAll();
    }
    
    /**
     * Metoda koja dodaje zahtjev prema REST servisu u dnevnik rada.
     * @param korime
     * @param lozinka
     * @param zahtjev 
     */
    public void dodajUDnevnikRada(String korime, String lozinka, String zahtjev) {
        if (autentikacija(korime, lozinka)) {
            DnevnikRada dr = new DnevnikRada();
            dr.setKorIme(aktivniKorisnik);
            dr.setZahtjev("JAX-RS Zahtjev: " + zahtjev);
            dr.setStored(new Date(System.currentTimeMillis()));
            dnevnikRadaFacade.create(dr);
        }
    }
    
    /**
     * Metoda koja provjerava postojanje korisnika u bazi podataka
     * @param korime
     * @param lozinka
     * @return 
     */
    public boolean autentikacija(String korime, String lozinka) {
        for (Korisnici k : korisnici) {
            if (k.getKorIme().equals(korime) && k.getLozinka().equals(lozinka)) {
                aktivniKorisnik = k;
                return true;
            }
        }
        return false;
    }
    
    /**
     * Metoda koja vraća izabrani aerodrom Korinika iz vlastite kolekcije
     * @param korime
     * @param lozinka
     * @param icao
     * @return 
     */
    public Airports dajIzabraniAerodromKorisnika(String korime, String lozinka, String icao) {
        if (autentikacija(korime, lozinka)) {
            for (Myairports ma : myaerodromi) {
                if (ma.getIdent().getIdent().equals(icao) && ma.getUsername().getKorIme().equals(korime)) {
                    return ma.getIdent();
                }
            }
        }
        return null;
    }
    
    /**
     * Metoda koja briše izabrani aerodrom korisnika
     * @param korime
     * @param lozinka
     * @param icao
     * @return 
     */
    public boolean obrisiAerodromKorisnika(String korime, String lozinka, String icao) {
        if (autentikacija(korime, lozinka)) {
            for (Myairports ma : myaerodromi) {
                if (ma.getIdent().getIdent().equals(icao) && ma.getUsername().getKorIme().equals(korime)) {
                    if (airplanesFacade.dohvatiBrojAerodroma(icao) == 0) {
                        myairportsFacade.remove(ma);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Metoda koja briše avione aerodrome korisnika koji je dan u parametru
     * @param korime
     * @param lozinka
     * @param icao
     * @return 
     */
    public boolean obrisiAvioneAerodroma(String korime, String lozinka, String icao) {
        List<Airplanes> listaAvio = airplanesFacade.dohvatiAvioneOdDo(icao, 0, 0);
        System.out.println(listaAvio.size());
        if (autentikacija(korime, lozinka)) {
            for (Myairports ma : myaerodromi) {
                if (ma.getIdent().getIdent().equals(icao) && ma.getUsername().getKorIme().equals(korime)) {
                    for (Airplanes avio : listaAvio) {
                        airplanesFacade.remove(avio);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
