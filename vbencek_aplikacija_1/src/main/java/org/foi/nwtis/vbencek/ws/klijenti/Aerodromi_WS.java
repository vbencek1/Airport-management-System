/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.ws.klijenti;

import java.util.List;
import org.foi.nwtis.dkermek.ws.serveri.StatusKorisnika;

/**
 * Klasa koja direktno poziva metode web servisa AerodromiWS
 * @author vbencek
 */
public class Aerodromi_WS {
    
    /**
     * Metoda koja poziva metodu web servisa "registrirajGrupu"
     * @param korisnickoIme
     * @param korisnickaLozinka
     * @return 
     */
    public boolean registrirajGrupu(String korisnickoIme, String korisnickaLozinka) {
        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            java.lang.Boolean result = port.registrirajGrupu(korisnickoIme, korisnickaLozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;

    }
    
    /**
     * Metoda koja poziva metodu web servisa "deregistrirajGrupu"
     * @param korisnickoIme
     * @param korisnickaLozinka
     * @return 
     */
    public boolean odjaviGrupu(String korisnickoIme, String korisnickaLozinka) {

        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            java.lang.Boolean result = port.deregistrirajGrupu(korisnickoIme, korisnickaLozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }
    
    /**
     * Metoda koja poziva metodu web servisa "aktivirajGrupu"
     * @param korisnickoIme
     * @param korisnickaLozinka
     * @return 
     */
    public boolean aktivirajGrupu(String korisnickoIme, String korisnickaLozinka) {

        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            java.lang.Boolean result = port.aktivirajGrupu(korisnickoIme, korisnickaLozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }
    
    /**
     * Metoda koja poziva metodu web servisa "blokirajGrupu"
     * @param korisnickoIme
     * @param korisnickaLozinka
     * @return 
     */
    public boolean blokirajGrupu(String korisnickoIme, String korisnickaLozinka) {

        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            java.lang.Boolean result = port.blokirajGrupu(korisnickoIme, korisnickaLozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }
    
    /**
     * Metoda koja poziva metodu web servisa "dajStatusGrupe"
     * @param korisnickoIme
     * @param korisnickaLozinka
     * @return 
     */
    public StatusKorisnika stanjeGrupe(String korisnickoIme, String korisnickaLozinka) {

        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            org.foi.nwtis.dkermek.ws.serveri.StatusKorisnika result = port.dajStatusGrupe(korisnickoIme, korisnickaLozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;

    }
    
    /**
     * Metoda koja poziva metodu web servisa "dodajAerodromIcaoGrupi"
     * @param korisnickoIme
     * @param korisnickaLozinka
     * @param icao
     * @return 
     */
    public boolean grupaAerodromi(String korisnickoIme, String korisnickaLozinka, String icao) {

        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            java.lang.Boolean result = port.dodajAerodromIcaoGrupi(korisnickoIme, korisnickaLozinka, icao);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    /**
     * Metoda koja poziva metodu web servisa "obrisiSveAerodromeGrupe"
     * @param korisnickoIme
     * @param korisnickaLozinka
     * @return 
     */
    public boolean obrisiAerodrome(String korisnickoIme, String korisnickaLozinka) {
        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            java.lang.Boolean result = port.obrisiSveAerodromeGrupe(korisnickoIme, korisnickaLozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }
    
    /**
     * Metoda koja poziva metodu web servisa "dajSveAerodromeGrupe"
     * @param korisnickoIme
     * @param korisnickaLozinka
     * @return 
     */
    public List<org.foi.nwtis.dkermek.ws.serveri.Aerodrom> dajAerodrome(String korisnickoIme, String korisnickaLozinka){
        
        try { 
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            java.util.List<org.foi.nwtis.dkermek.ws.serveri.Aerodrom> result = port.dajSveAerodromeGrupe(korisnickoIme, korisnickaLozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
            return null;
    }
}
