/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.serveri;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.util.List;
import org.foi.nwtis.vbencek.ejb.eb.Airports;
import javax.inject.Inject;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 *
 * @author NWTiS_2
 */
@WebService(serviceName = "ProjektWS")
public class ProjektWS {
    
    @Inject
    PodaciWS podaciWS;
    
    /**
     * Metoda koja provjerava postojanje korisnika (autentikacija)
     * @param korisnik
     * @param lozinka
     * @return 
     */
    @WebMethod(operationName = "provjeraKorisnika")
    public Boolean provjeraKorisnika(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka) {
        return podaciWS.autentikacija(korisnik, lozinka);
    }

    /**
     * Metoda koja vraća sve aerodrome koji imaju dani naziv
     *
     * @param korisnik
     * @param lozinka
     * @param naziv
     * @return vraća popis svih aerodroma sa danim nazivom, ako aerodroma nema
     * tada vraća praznu listu
     */
    @WebMethod(operationName = "dohvatiAerodromeNaziv")
    public List<Aerodrom> dohvatiAerodromeNaziv(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "naziv") String naziv) {
        return podaciWS.dajAerodromeNaziv(korisnik, lozinka, naziv);

    }
    
    /**
     * Metoda koja vraća sve aerodrome koji se nazale u danoj državi
     *
     * @param korisnik
     * @param lozinka
     * @param drzava
     * @return vraća popis svih aerodroma sa danim državom, ako aerodroma nema
     * tada vraća praznu listu
     */
    @WebMethod(operationName = "dohvatiAerodromeDrzava")
    public List<Aerodrom> dohvatiAerodromeDrzava(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "drzava") String drzava) {
        return podaciWS.dajAerodromeDrzava(korisnik, lozinka, drzava);

    }
    /**
     * Metoda koja vraća sve aerodrome korisnika
     *
     * @param korisnik
     * @param lozinka
     * @return vraća popis svih aerodroma, ako aerodroma nema tada vraća praznu
     * listu
     */
    @WebMethod(operationName = "dohvatiAerodromeKorisnika")
    public List<Aerodrom> dohvatiAerodromeKorisnika(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka) {
        return podaciWS.dajAerodromeKorisnika(korisnik, lozinka,true);
    }
    
     /**
     * Metoda koja vraća listu svih aviona koji su polijetali sa zadanog
     * aerodroma u zadanom razdoblju
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @param odVremena
     * @param dovremena
     * @return vraća listu aviona ako se pronađu inače vraća null
     */
    @WebMethod(operationName = "poletjeliAvioniAerodrom")
    public List<AvionLeti> poletjeliAvioniAerodrom(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "odVremena") long odVremena,
            @WebParam(name = "doVremena") long dovremena) {
        return podaciWS.dajAvioneAerodroma(korisnik, lozinka, icao, odVremena, dovremena);
    }
    
    /**
     * Metoda koja vraća listu svih aviona koji imaju danu oznaku i poletjeli su u danom rasponu vremena
     *
     * @param korisnik
     * @param lozinka
     * @param icao24
     * @param odVremena
     * @param dovremena
     * @return vraća listu aviona ako se pronađu inače vraća null
     */
    @WebMethod(operationName = "letoviAviona")
    public List<AvionLeti> letoviAviona(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao24") String icao24,
            @WebParam(name = "odVremena") long odVremena,
            @WebParam(name = "doVremena") long dovremena) {
        return podaciWS.dajLetAviona(korisnik, lozinka, icao24, odVremena, dovremena);
    }
    
    /**
     * Metoda koja služi za izračun udaljenosti između dva zadana aerodroma.
     *
     * @param korisnik
     * @param lozinka
     * @param icao1
     * @param icao2
     * @return udaljenost kao double
     */
    @WebMethod(operationName = "izracunajUdaljenostAerodroma")
    public double izracunajUdaljenostAerodroma(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao1") String icao1,
            @WebParam(name = "icao2") String icao2) {
        return podaciWS.udaljenostDvaAerodroma(korisnik, lozinka, icao1, icao2);
    }
    
    /**
     * Metoda koja vraća vlastite aerodrome korisnika koji su udaljeni od zadanog aerodroma u nekom rasponu.
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @param rasponOd
     * @param rasponDo
     * @return vraća listu aerodroma 
     */
    @WebMethod(operationName = "dajBliskeAerodrome")
    public List<Aerodrom> dajBliskeAerodrome(
            @WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "rasponOd") double rasponOd,
            @WebParam(name = "rasponDo") double rasponDo) {
        return podaciWS.dajBliskeAerodrome(korisnik, lozinka, icao, rasponOd, rasponDo);
    }
}
