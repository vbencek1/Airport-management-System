/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.ws.klijenti;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.vbencek.web.serveri.Aerodrom;
import org.foi.nwtis.vbencek.web.serveri.AvionLeti;

/**
 * Klasa koja služi za pozivanje metoda Web servisa iz aplikacije 2
 * @author NWTiS_2
 */
public class Projekt_WS {
    
    /**
     * Metoda koja služi za prijavu korisnika. Poziva se metoda web servisa "provjeraKorisnika"
     * @param korisnik
     * @param lozinka
     * @return 
     */
    public boolean prijaviKorisnika(String korisnik, String lozinka) {
        try {
            org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service service = new org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service();
            org.foi.nwtis.vbencek.web.serveri.ProjektWS port = service.getProjektWSPort();
            java.lang.Boolean result = port.provjeraKorisnika(korisnik, lozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }
    
    /**
     * Metoda koja vraća aerodrome korisnika. Poziva se metoda Web servisa "dohvatiAerodromeKorisnika"
     * @param korisnik
     * @param lozinka
     * @return 
     */
    public List<Aerodrom> dajAerodromeKorisnika(String korisnik, String lozinka) {
        List<Aerodrom> vraceniAerodromi = new ArrayList<>();
        try {
            org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service service = new org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service();
            org.foi.nwtis.vbencek.web.serveri.ProjektWS port = service.getProjektWSPort();
            java.util.List<org.foi.nwtis.vbencek.web.serveri.Aerodrom> result = port.dohvatiAerodromeKorisnika(korisnik, lozinka);
            vraceniAerodromi = result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return vraceniAerodromi;
    }
    
    /**
     * Metoda koja vraća sve avione aerodrome koji su letjeli unutar danog vremenskog raspona.
     * Poziva se metoda web servisa "poletjeliAvioniAerodrom"
     * @param korisnik
     * @param lozinka
     * @param icao
     * @param odVremena
     * @param doVremena
     * @return 
     */
    public List<AvionLeti> dajAvioneAerodroma(String korisnik, String lozinka, String icao, long odVremena, long doVremena) {
        List<AvionLeti> vraceniAvioni = new ArrayList<>();
        try {
            org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service service = new org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service();
            org.foi.nwtis.vbencek.web.serveri.ProjektWS port = service.getProjektWSPort();
            java.util.List<org.foi.nwtis.vbencek.web.serveri.AvionLeti> result = port.poletjeliAvioniAerodrom(korisnik, lozinka, icao, odVremena, doVremena);
            vraceniAvioni = result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return vraceniAvioni;
    }

    /**
     * Metoda koja vraća letova aviona unutar vremenskog raspona. Poziva se metoda web servisa "letoviAviona"
     * @param korisnik
     * @param lozinka
     * @param icao24
     * @param odVremena
     * @param doVremena
     * @return 
     */
    public List<AvionLeti> dajLetoveAviona(String korisnik, String lozinka, String icao24, long odVremena, long doVremena) {
        List<AvionLeti> vraceniAvioni = new ArrayList<>();
        try {
            org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service service = new org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service();
            org.foi.nwtis.vbencek.web.serveri.ProjektWS port = service.getProjektWSPort();
            java.util.List<org.foi.nwtis.vbencek.web.serveri.AvionLeti> result = port.letoviAviona(korisnik, lozinka, icao24, odVremena, doVremena);
            vraceniAvioni = result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return vraceniAvioni;
    }
    
    /**
     * Metoda koja vraće sve aerodrome korisnika koji se nalaze u blizini zadanog aerodroma
     * i unutar zadanog raspona. Poziva se metoda web servisa "dajBliskeAerodrome"
     * @param korisnik
     * @param lozinka
     * @param icao
     * @param rasponOd
     * @param rasponDo
     * @return 
     */
    public List<Aerodrom> dajAerodromeRasponaa(String korisnik,String lozinka,String icao, double rasponOd,double rasponDo){
        List<Aerodrom> vraceniAerodromi = new ArrayList<>();
        try { 
            org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service service = new org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service();
            org.foi.nwtis.vbencek.web.serveri.ProjektWS port = service.getProjektWSPort();
            java.util.List<org.foi.nwtis.vbencek.web.serveri.Aerodrom> result = port.dajBliskeAerodrome(korisnik, lozinka, icao, rasponOd, rasponDo);
            vraceniAerodromi = result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return vraceniAerodromi;

    }
    
    /**
     * Metoda koja izračunava udaljenost između dva aerodroma. Poziva se metoda web servisa "izracunajUdaljenostAerodroma"
     * @param korisnik
     * @param lozinka
     * @param icao1
     * @param icao2
     * @return 
     */
    public double dajUdaljenostAerodroma(String korisnik, String lozinka, String icao1, String icao2){
        
        try { 
            org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service service = new org.foi.nwtis.vbencek.web.serveri.ProjektWS_Service();
            org.foi.nwtis.vbencek.web.serveri.ProjektWS port = service.getProjektWSPort();
            double result = port.izracunajUdaljenostAerodroma(korisnik, lozinka, icao1, icao2);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }return 0;

    }

}
