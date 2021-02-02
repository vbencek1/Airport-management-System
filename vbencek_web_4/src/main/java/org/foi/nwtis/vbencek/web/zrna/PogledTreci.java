/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.vbencek.ejb.eb.Airports;
import org.foi.nwtis.vbencek.ejb.eb.Myairports;
import org.foi.nwtis.vbencek.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.vbencek.konfiguracije.bp.BP_Konfiguracija;
import org.primefaces.PrimeFaces;

/**
 * Klasa koja služi kao pogled za dohvaćanje aerodroma iz baze podataka. Komunicira sa Web socketom i
 * dodaje aerodrom korisniku. Također daje geo i meteo podatke za odabrani aerodrom
 * @author vbencek
 */
@Named(value = "pogledTreci")
@ViewScoped
public class PogledTreci implements Serializable {

    @EJB(beanName = "MyairportsFacade")
    MyairportsFacadeLocal myairportsFacade;
    @EJB(beanName = "MyairportslogFacade")
    MyairportslogFacadeLocal myairportslogFacade;
    @EJB(beanName = "AirplanesFacade")
    AirplanesFacadeLocal AirplanesFacade;
    @EJB(beanName = "AirportsFacade")
    AirportsFacadeLocal AirportsFacade;

    @Inject
    Prijava prijava;

    @Inject
    ServletContext context;
    String apiKey = "";
    String locationKey = "";

    @Getter
    @Setter
    String korIme = "";
    @Getter
    @Setter
    String lozinka = "";

    @Setter
    @Getter
    String odabraniAerodrom = null;
    @Setter
    @Getter
    String porukaAerodroma = "";

    @Getter
    String NAsirina = "";
    @Getter
    String NAduzina = "";
    @Getter
    String temp = "";
    @Getter
    String vlaga = "";

    @Getter
    List<Myairports> myairportsSvi = new ArrayList<>();
    @Getter
    List<Myairports> myairportsKorisnik = new ArrayList<>();
    @Getter
    List<Airports> airportsSvi = new ArrayList<>();

    @Getter
    @Setter
    String nazivAerodroma = "";
    @Getter
    @Setter
    String aerodromZaDodat = "";
    @Setter
    @Getter
    String porukaDodavanja = "";

    @Inject
    Lokalizacija lokalizacija;
    
    /**
     * Metoda koja dohvaća podatke iz konfiguracije te podatke o prijavljenom korisniku
     */
    @PostConstruct
    public void init() {
        BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        apiKey = bpk.getOWPKey();
        locationKey = bpk.getLocationIQToken();
        dohvatiPodatkeKorisnika();

    }

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
     * Metoda kojom se dohvaćaju aerodromi koje prati korisnik
     * @return 
     */
    public List<Myairports> prikaziAerodromeKorisnika() {
        dohvatiPodatkeKorisnika();
        myairportsSvi = myairportsFacade.findAll();
        List<Myairports> vrati = new ArrayList<>();
        for (Myairports ma : myairportsSvi) {
            if (ma.getUsername().getKorIme().equals(korIme)) {
                vrati.add(ma);
            }
        }
        return vrati;
    }
    
    /**
     * Metoda koja daje broj korisnika koji prati pojedini aerodrom
     * @param ident
     * @return 
     */
    public int brojKorisnikaAerodroma(String ident) {
        return myairportsFacade.dohvatiBrojPratitelja(ident);
    }
    
    /**
     * Metoda koja daje broj dana za koje su preuzeti podaci za pojedini aerodrom
     * @param ident
     * @return 
     */
    public int brojDatumaAerodroma(String ident) {
        return myairportslogFacade.dajBrojDana(ident);
    }
    
    /**
     * Metoda koja dohvaća broj preuzetih aviona za pojedini aerodrom
     * @param ident
     * @return 
     */
    public int brojAvionaAerodroma(String ident) {
        return AirplanesFacade.dohvatiBrojAerodroma(ident);
    }

    /**
     * Metoda koja služi za komunikaciju sa više web servisa a pribavlja podatke
     * o poziciji aerodroma kao i podatke o temperaturi i vlagi zraka
     *
     * @param ident
     */
    public void preuzmiPodatkeAerodroma(String ident) {
        odabraniAerodrom = ident;
        if (odabraniAerodrom == null) {
        } else {
            if (!korIme.trim().isEmpty() && korIme != null) {
                LIQKlijent lIQKlijent = new LIQKlijent(locationKey);
                NAsirina = lIQKlijent.getGeoLocation(odabraniAerodrom).getLatitude();
                NAduzina = lIQKlijent.getGeoLocation(odabraniAerodrom).getLongitude();
                OWMKlijent omKlijent = new OWMKlijent(apiKey);
                temp = omKlijent.getRealTimeWeather(NAsirina, NAduzina).getTemperatureValue() + " " + omKlijent.getRealTimeWeather(NAsirina, NAduzina).getTemperatureUnit();
                vlaga = omKlijent.getRealTimeWeather(NAsirina, NAduzina).getHumidityValue() + " " + omKlijent.getRealTimeWeather(NAsirina, NAduzina).getHumidityUnit();
            }
        }

    }
    
    /**
     * Metoda koja služi za filtriranje aerodroma po nazivu
     */
    public void filtrirajAerodrome() {
        if (!"".equals(korIme)) {
            airportsSvi.clear();
            for (Airports a : AirportsFacade.findAll()) {
                if (a.getName().contains(nazivAerodroma)) {
                    airportsSvi.add(a);
                }
            }
        }
    }
    
    /**
     * Metoda koja dodaje aerodrom u kolekciju aerodroma korisnika. Šalje se poruka na Web Socket u aolikaciji 2
     */
    public void dodajAerodrom(){
        dohvatiPodatkeKorisnika();
        porukaDodavanja="";
        if (!"".equals(korIme)) {
            ResourceBundle res = ResourceBundle.getBundle("org.foi.nwtis.vbencek.web.Prijevod", new Locale(lokalizacija.getJezik()));
            if(!provjeriPostojanje(aerodromZaDodat)){
                PrimeFaces.current().executeScript("saljiDodaj('" + korIme + "','" + aerodromZaDodat + "');");
                porukaDodavanja= res.getString("pogledBaza.porukaDodaj");
            }else{
                porukaDodavanja= res.getString("pogledBaza.porukaDodajFalse");
            }
        }
    }
    
    /**
     * Metoda koja provjerava da li već postoji aerodrom u kolekciji aerodroma korisnika
     * @param ident
     * @return 
     */
    public boolean provjeriPostojanje(String ident){
        for(Myairports my: prikaziAerodromeKorisnika()){
            if(my.getIdent().getIdent().equals(ident)){
                return true;
            }
        }return false;
    }

}
