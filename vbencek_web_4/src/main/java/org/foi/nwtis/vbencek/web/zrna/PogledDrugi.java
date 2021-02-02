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
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.vbencek.ejb.eb.SpremisteKomandi;
import org.foi.nwtis.vbencek.ejb.eb.SpremistePoruka;

/**
 * Klasa koja služi kao pogled za JMS poruke. Omogućuje njihov prikaz i brisanje.
 * @author vbencek
 */
@Named(value = "pogledDrugi")
@ViewScoped
public class PogledDrugi implements Serializable {

    @Inject
    Prijava prijava;

    @Getter
    @Setter
    String korIme = "";
    @Getter
    @Setter
    String lozinka = "";

    @Inject
    Lokalizacija lokalizacija;

    @Inject
    SpremistePoruka spremistePoruka;
    @Inject
    SpremisteKomandi spremisteKomandi;

    @PostConstruct
    public void init() {
        dohvatiPodatkeKorisnika();

    }
    
    /**
     * Metoda dohvaća podatke u prijavljenom korisniku
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
     * Metoda koja dohvaća JMS poruke koje se odnose na dodavanje aerodroma preko Web Socketa
     * @return 
     */
    public List<String> dohvatiJMS1() {
        dohvatiPodatkeKorisnika();
        List<String> mojePoruke= new ArrayList<>();
        if (!"".equals(korIme)) {
            for(String p: spremistePoruka.dajPoruke()){
                if(p.split(",")[1].split(" ")[2].trim().split("\"")[1].equals(korIme)){
                    mojePoruke.add(p);
                }
            }
            
            return mojePoruke;
        }
        return null;
    }
    
    /**
     * Metoda koja iz poruke pribalja ID
     * @param in
     * @return 
     */
    public String vratiId(String in) {
        return in.split(",")[0].split(" ")[1].trim();
    }
    
    /**
     * Metoda koja iz poruke pribavlja Korisnika
     * @param in
     * @return 
     */
    public String vratiKor(String in) {
        return in.split(",")[1].split(" ")[2].trim().split("\"")[1];
    }

    /**
     * Metoda koja iz poruke pribalja Aerodrom ili Komandu
     * @param in
     * @return 
     */
    public String vratiAer(String in) {
        return in.split(",")[2].split(" ")[2].trim().split("\"")[1];
    }
    
    /**
     * Metoda koja iz poruke pribalja vrijeme
     * @param in
     * @return 
     */
    public String vratiVri(String in) {
        return in.split(",")[3].split(" ")[2].trim().split("\"")[1];
    }
    
    /**
     * Metoda koja briše poruke iz prve tablice JMS poruka prijavljenog korisnika
     */
    public void obrisiJMS1() {
        dohvatiPodatkeKorisnika();
        if (!"".equals(korIme)) {
            spremistePoruka.obrisiPoruke(korIme);
        }
    }
    
    /**
     * Metoda koja dohvaća JMS poruke koje se odnose na pozivanje komandi na mrežnoj utičnici
     * @return 
     */
    public List<String> dohvatiJMS2() {
        dohvatiPodatkeKorisnika();
        List<String> mojeKomande= new ArrayList<>();
        if (!"".equals(korIme)) {
            for(String p: spremisteKomandi.vratiKomande()){
                if(p.split(",")[1].split(" ")[2].trim().split("\"")[1].equals(korIme)){
                    mojeKomande.add(p);
                }
            }
            
            return mojeKomande;
        }
        return null;
    }
    
    /**
     * Metoda koja vraća vrijemeSlanja iz poruke
     * @param in
     * @return 
     */
    public String vratiVrijeme1(String in) {
        return in.split(",")[3].split("\"")[3];
    }
    
    /**
     * metoda koja vraća vrijemePrijeme iz poruke
     * @param in
     * @return 
     */
    public String vratiVrijeme2(String in) {
        return in.split(",")[4].split("\"")[3];
    }
    
    /**
     * Metoda koja briše poruke iz druge tablice JMS poruka odabranog korisnika
     */
    public void obrisiJMS2(){
        dohvatiPodatkeKorisnika();
        if (!"".equals(korIme)) {
            spremisteKomandi.obrisiPoruke(korIme);
        }
    }
}
