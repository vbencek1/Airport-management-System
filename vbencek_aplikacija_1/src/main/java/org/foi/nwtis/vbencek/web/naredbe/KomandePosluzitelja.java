/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.naredbe;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.vbencek.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.vbencek.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.vbencek.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.vbencek.web.podaci.KorisnikDAO;

/**
 * Klasa koja služi za generiranje poruke koja se vraća korisnik ovisno o trenutnom stanju poslužitelja.
 * @author vbencek
 */
public class KomandePosluzitelja {
    
    BP_Konfiguracija bpk;
    Boolean preuzimanje;

    public KomandePosluzitelja(BP_Konfiguracija bpk) {
        this.bpk = bpk;
        preuzimanje=Boolean.parseBoolean(bpk.getKonfig().dajPostavku("preuzimanje.status"));
    }
    
    /**
     * Metoda koja služi za autentikaciju korinika. Provjerava postojanje korisnika u svojoj bazi podataka
     * @param korisnik
     * @param lozinka
     * @return 
     */
     private boolean autentikacija(String korisnik, String lozinka) {
        KorisnikDAO korisnikDAO = new KorisnikDAO();
        if (korisnikDAO.dohvatiKorisnika(korisnik, lozinka, true, bpk)) {
            return true;
        } else {
            return false;
        }
    }
    
     /**
      * Metoda koja služi za autentikaciju korinika. Provjerava postojanje korisnika u svojoj bazi podataka.
      * Također generira prikladnu poruku koja se vraća korisniku.
      * @param korisnik
      * @param lozinka
      * @return 
      */
    public String autentikacijaOrg(String korisnik, String lozinka) {
        KorisnikDAO korisnikDAO = new KorisnikDAO();
        if (korisnikDAO.dohvatiKorisnika(korisnik, lozinka, true, bpk)) {
                return "OK 10;";

        } else {
            return "ERR 11;";

        }
    }
    
    /**
     * Metoda koja se poziva kad korisnik pošalje naredbu "DODAJ". Dodaje korisnika u bazu podataka
     * @param korime
     * @param lozinka
     * @return 
     */
    public String dodaj(String korime, String lozinka){
        KorisnikDAO korisnikDAO = new KorisnikDAO();
        if(korisnikDAO.dodajKorisnika(korime,lozinka, bpk)){
            return "OK 10;";
        }else{
            return "ERR 12;";
        }
    }
    
    /**
     * Metoda koja se poziva kad korisnik pošalje naredbu "Pauza".
     * Pauzira se preuzimanje aviona
     * @param korime
     * @param lozinka
     * @return 
     */
    public String pauza(String korime, String lozinka){
        if(autentikacija(korime, lozinka)){
            if(!preuzimanje){
                return "ERR 13;";
            }else{
                bpk.getKonfig().azurirajPostavku("preuzimanje.status", "false");
                try {
                    bpk.getKonfig().spremiKonfiguraciju();
                } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
                    Logger.getLogger(KomandePosluzitelja.class.getName()).log(Level.SEVERE, null, ex);
                }
                return "OK 10;";
            }
        }else{
            return "ERR 11;";
        }
    }
    
    /**
     * Metoda koja se poziva kad korisnik pošalje naredbu "RADI".
     * Pokreće se preuzimanje aviona
     * @param korime
     * @param lozinka
     * @return 
     */
    public String radi(String korime, String lozinka){
        if(autentikacija(korime, lozinka)){
            if(preuzimanje){
                return "ERR 14;";
            }else{
                bpk.getKonfig().azurirajPostavku("preuzimanje.status", "true");
                try {
                    bpk.getKonfig().spremiKonfiguraciju();
                } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
                    Logger.getLogger(KomandePosluzitelja.class.getName()).log(Level.SEVERE, null, ex);
                }
                return "OK 10;";
            }
        }else{
            return "ERR 11;";
        }
    }
    
    /**
     * Metoda koja se poziva kad korisnik pošalje naredbu "KRAJ".
     * Zaustavlja se primanje naredbi
     * @param korime
     * @param lozinka
     * @return 
     */
    public String kraj(String korime, String lozinka){
        if(autentikacija(korime, lozinka)){
            if(preuzimanje){
                bpk.getKonfig().azurirajPostavku("preuzimanje.status", "false");
                try {
                    bpk.getKonfig().spremiKonfiguraciju();
                } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
                    Logger.getLogger(KomandePosluzitelja.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return "OK 10;";
        }else{
            return "ERR 11;";
        }
    }
    
    /**
     * Metoda koja se poziva kad korisnik pošalje naredbu "STANJE".
     * Vraća se trenutno stanje preuzimanje (da li se preuzima ili ne)
     * @param korime
     * @param lozinka
     * @return 
     */
    public String stanje(String korime, String lozinka){
        if(autentikacija(korime, lozinka)){
            if(preuzimanje){
                return "OK 11;";
            }else{
                return "OK 12;";
            }
        }else{
            return "ERR 11;";
        }
    }
}
