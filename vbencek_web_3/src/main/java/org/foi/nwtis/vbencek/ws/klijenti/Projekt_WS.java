/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.ws.klijenti;

import java.util.List;
import org.foi.nwtis.vbencek.web.serveri.Aerodrom;

/**
 * Klasa koja služi za komunikaciju sa webservisom. Poziva metode web servisa iz aplikacije 2
 * @author vbencek
 */
public class Projekt_WS {
    
    /**
     * metoda koja dohvaća aerodrome korisnika. Poziva se metoda web servisa "dohvatiAerodromeKorisnika".
     * @param korisnik
     * @param lozinka
     * @return 
     */
    public List<Aerodrom> dajAerodromeKorisnika(String korisnik, String lozinka) {
        try {
            org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service service = new org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service();
            org.foi.nwtis.vbencek.web.serveri.ProjektWS port = service.getProjektWSPort();
            java.util.List<org.foi.nwtis.vbencek.web.serveri.Aerodrom> result = port.dohvatiAerodromeKorisnika(korisnik, lozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    
    /**
     * Metoda koja daje aerodrom ovisno o upisanom nazivu. Poziva se metoda web servisa "dohvatiAerodromeNaziv"
     * @param korisnik
     * @param lozinka
     * @param naziv
     * @return 
     */
    public List<Aerodrom> dajAerodromeNaziv(String korisnik, String lozinka, String naziv) {;

        try {
            org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service service = new org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service();
            org.foi.nwtis.vbencek.web.serveri.ProjektWS port = service.getProjektWSPort();
            java.util.List<org.foi.nwtis.vbencek.web.serveri.Aerodrom> result = port.dohvatiAerodromeNaziv(korisnik, lozinka, naziv);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;

    }
    
    /**
     * Metoda koja daje aerodrome ovisno o drzavi. Poziva se metoda web servisa "dohvatiAerodromeDrzava"
     * @param korisnik
     * @param lozinka
     * @param drzava
     * @return 
     */
    public List<Aerodrom> dajAerodromeDrzava(String korisnik, String lozinka, String drzava) {

        try {
            org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service service = new org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service();
            org.foi.nwtis.vbencek.web.serveri.ProjektWS port = service.getProjektWSPort();
            java.util.List<org.foi.nwtis.vbencek.web.serveri.Aerodrom> result = port.dohvatiAerodromeDrzava(korisnik, lozinka, drzava);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;

    }
}
