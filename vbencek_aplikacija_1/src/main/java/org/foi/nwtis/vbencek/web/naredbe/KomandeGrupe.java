/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.naredbe;

import org.foi.nwtis.dkermek.ws.serveri.Aerodrom;
import org.foi.nwtis.vbencek.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.vbencek.web.podaci.KorisnikDAO;
import org.foi.nwtis.vbencek.ws.klijenti.Aerodromi_WS;

/**
 * Klasa koja služi za generiranje poruke koja se vraća korisnik ovisno o trenutnom stanju grupe.
 * Služi za pozivanje metoda web servise te njihovo tumačenje.
 * @author vbencek
 */
public class KomandeGrupe {

    BP_Konfiguracija bpk;
    String korisnickoIme = "";
    String korisnickaLozinka = "";

    public KomandeGrupe(BP_Konfiguracija bpk) {
        this.bpk = bpk;
        korisnickoIme = bpk.getKonfig().dajPostavku("svn.korisnik");
        korisnickaLozinka = bpk.getKonfig().dajPostavku("svn.lozinka");
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
     * Metoda koja služi za registriranje grupe. Vraća true jedino ako grupa ne postoji
     * @return 
     */
    public String prijavi() {
        Aerodromi_WS ws = new Aerodromi_WS();
        String stanje = ws.stanjeGrupe(korisnickoIme, korisnickaLozinka).value();
        if ("DEREGISTRIRAN".equals(stanje)) {
            ws.registrirajGrupu(korisnickoIme, korisnickaLozinka);
            return "OK 20;";
        } else {
            return "ERR 20;";
        }
    }
    
    /**
     * Metoda koja odjavljuje trenutno aktivnu grupu
     * @return 
     */
    public String odjavi() {
        Aerodromi_WS ws = new Aerodromi_WS();
        String stanje = ws.stanjeGrupe(korisnickoIme, korisnickaLozinka).value();
        if (!"DEREGISTRIRAN".equals(stanje)) {
            ws.odjaviGrupu(korisnickoIme, korisnickaLozinka);
            return "OK 20;";
        } else {
            return "ERR 21;";
        }
    }
    
    /**
     * Metoda koja služi za aktiviranje grupe. Grupa se može jedino aktivirati ako nije već aktivirana ili ako postoji.
     * @return 
     */
    public String aktiviraj() {
        Aerodromi_WS ws = new Aerodromi_WS();
        String stanje = ws.stanjeGrupe(korisnickoIme, korisnickaLozinka).value();
        if ("BLOKIRAN".equals(stanje) || "NEAKTIVAN".equals(stanje) || "REGISTRIRAN".equals(stanje)) {
            ws.aktivirajGrupu(korisnickoIme, korisnickaLozinka);
            return "OK 20;";
        }
        if ("AKTIVAN".equals(stanje)) {
            return "ERR 22;";
        } else {
            return "ERR 21;";
        }

    }
    
    /**
     * Metoda koja služi za blokiranje aktivne grupe. Može se blokirati jedino ako postoji ili ako već nije blokirana.
     * @return 
     */
    public String blokiraj() {
        Aerodromi_WS ws = new Aerodromi_WS();
        String stanje = ws.stanjeGrupe(korisnickoIme, korisnickaLozinka).value();
        if ("AKTIVAN".equals(stanje) || "REGISTRIRAN".equals(stanje)) {
            ws.blokirajGrupu(korisnickoIme, korisnickaLozinka);
            return "OK 20;";
        }
        if ("BLOKIRAN".equals(stanje) || "NEAKTIVAN".equals(stanje)) {
            return "ERR 23;";
        } else {
            return "ERR 21;";
        }

    }
    
    /**
     * Metoda koja vraća trenutni status grupe. Daje informaciju da li je grupa aktivna ili blokirana, te da li postoji.
     * @return 
     */
    public String dajStatus() {
        Aerodromi_WS ws = new Aerodromi_WS();
        String stanje = ws.stanjeGrupe(korisnickoIme, korisnickaLozinka).value();
        if ("AKTIVAN".equals(stanje)) {
            return "OK 21;";
        }
        if ("BLOKIRAN".equals(stanje) || "NEAKTIVAN".equals(stanje)) {
            return "OK 22;";
        } else {
            return "ERR 21;";
        }
    }
    
    /**
     * Metoda koja dodaje aerodrome u aktivnu grupu korisnika. Grupa mora postojati i mora biti blokirana kako bi se izvršilo dodavanje.
     * Briše prijašnje dodane aerodrome iz grupe.
     * @param aerodromi
     * @return 
     */
    public String dodajAerodrome(String[] aerodromi) {
        Aerodromi_WS ws = new Aerodromi_WS();
        String stanje = ws.stanjeGrupe(korisnickoIme, korisnickaLozinka).value();
        String poruka="ERR 31;";
        if ("BLOKIRAN".equals(stanje)) {
            if (ws.obrisiAerodrome(korisnickoIme, korisnickaLozinka)) {
                for (String a : aerodromi) {
                    ws.grupaAerodromi(korisnickoIme, korisnickaLozinka, a);
                }
                for(Aerodrom a: ws.dajAerodrome(korisnickoIme, korisnickaLozinka)){
                    System.out.println("DODAN: "+a.getIcao()+" "+a.getNaziv());
                }
                poruka="OK 20;";
            }else{
                poruka="ERR 32;";
            }

        }
        return poruka;
    }
}
