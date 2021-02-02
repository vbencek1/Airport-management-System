/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.serveri;

import java.io.Serializable;
import javax.ejb.EJB;
import org.foi.nwtis.vbencek.ejb.eb.Airports;
import org.foi.nwtis.vbencek.ejb.sb.AirportsFacadeLocal;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import org.foi.nwtis.vbencek.ejb.eb.Korisnici;
import org.foi.nwtis.vbencek.ejb.eb.Myairports;
import org.foi.nwtis.vbencek.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.MyairportsFacadeLocal;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.vbencek.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.vbencek.ejb.eb.Airplanes;
import org.foi.nwtis.vbencek.ejb.eb.DnevnikRada;
import org.foi.nwtis.vbencek.ejb.sb.DnevnikRadaFacadeLocal;

/**
 * Klasa koja pruža potpuru klasi web servisa. Poziva metode fasada koje dodaju i čitaju zapise iz baze podataka
 * @author vbencek
 */
@Named(value = "podaciWS")
@SessionScoped
public class PodaciWS implements Serializable {

    @EJB(beanName = "AirportsFacade")
    AirportsFacadeLocal airportsFacade;
    @EJB(beanName = "KorisniciFacade")
    KorisniciFacadeLocal korisniciFacade;
    @EJB(beanName = "MyairportsFacade")
    MyairportsFacadeLocal myairportsFacade;
    @EJB(beanName = "AirplanesFacade")
    AirplanesFacadeLocal airplanesFacade;
    @EJB(beanName = "DnevnikRadaFacade")
    DnevnikRadaFacadeLocal dnevnikRadaFacade;

    List<Korisnici> korisnici = new ArrayList<>();
    List<Airports> aerodromi = new ArrayList<>();
    List<Myairports> myaerodromi = new ArrayList<>();
    List<Airplanes> airplanes = new ArrayList<>();
    Korisnici aktivniKorisnik=new Korisnici();

    @PostConstruct
    public void ucitajBean() {
        korisnici = korisniciFacade.findAll();
        aerodromi = airportsFacade.findAll();
        myaerodromi = myairportsFacade.findAll();

    }
    
    /**
     * Testna metoda pokretanja ejb-a
     */
    public void test() {
        System.out.println("test: " + korisnici.size());
    }
    
    /**
     * Metoda koja vrši upis zahtjeva u bazu podataka
     * @param zahtjev 
     */
    public void dodajUDnevnikRada(String zahtjev){
        DnevnikRada dr= new DnevnikRada();
            dr.setKorIme(aktivniKorisnik);
            dr.setZahtjev("JAX-WS Zahtjev: "+zahtjev);
            dr.setStored(new Date(System.currentTimeMillis()));
            dnevnikRadaFacade.create(dr);
    }
    
    /**
     * Metoda koja provjerava da li postoji korisnik u bazi podataka
     * @param korime
     * @param lozinka
     * @return 
     */
    public boolean autentikacija(String korime, String lozinka) {
        for (Korisnici k : korisnici) {
            if (k.getKorIme().equals(korime) && k.getLozinka().equals(lozinka)) {
                aktivniKorisnik=k;
                return true;
            }
        }
        return false;
    }
    
    /**
     * Metoda koja dohvaća aerodrome po nazivu iz baze podataka
     * @param korime
     * @param lozinka
     * @param naziv
     * @return 
     */
    public List<Aerodrom> dajAerodromeNaziv(String korime, String lozinka, String naziv) {

        if (autentikacija(korime, lozinka)) {
            
            dodajUDnevnikRada("Dohvaćanje aerodroma po nazivu");
            List<Aerodrom> aerodromiReturn = new ArrayList<>();
            for (Airports aer : aerodromi) {
                if (aer.getName().contains(naziv)) {
                    Aerodrom a = new Aerodrom();
                a.setDrzava(aer.getIsoCountry());
                a.setNaziv(aer.getName());
                a.setIcao(aer.getIdent());
                a.setLokacija(new Lokacija(aer.getCoordinates().split(",")[1].trim(), aer.getCoordinates().split(",")[0]));
                aerodromiReturn.add(a);
                }
            }
            return aerodromiReturn;
        }

        return null;
    }
    
    /**
     * Metoda koja dohvaća aerodrome po država iz baze podataka
     * @param korime
     * @param lozinka
     * @param drzava
     * @return 
     */
    public List<Aerodrom> dajAerodromeDrzava(String korime, String lozinka, String drzava) {

        if (autentikacija(korime, lozinka)) {
            List<Aerodrom> aerodromiReturn = new ArrayList<>();
            dodajUDnevnikRada("Dohvaćanje aerodroma po državi");
            for (Airports aer : aerodromi) {
                if (aer.getIsoCountry() != null) {
                    if (aer.getIsoCountry().equals(drzava)) {
                        Aerodrom a = new Aerodrom();
                a.setDrzava(aer.getIsoCountry());
                a.setNaziv(aer.getName());
                a.setIcao(aer.getIdent());
                a.setLokacija(new Lokacija(aer.getCoordinates().split(",")[1].trim(), aer.getCoordinates().split(",")[0]));
                aerodromiReturn.add(a);
                    }
                }
            }
            return aerodromiReturn;
        }

        return null;
    }
    
    /**
     * Metoda koja dohvaća aerodrome korisnika iz baze podataka
     * @param korime
     * @param lozinka
     * @param osnovno
     * @return 
     */
    public List<Aerodrom> dajAerodromeKorisnika(String korime, String lozinka, boolean osnovno) {

        if (autentikacija(korime, lozinka)) {
            if(osnovno) dodajUDnevnikRada("Dohvaćanje vlastiti aerodroma");
            List<Aerodrom> aerodromiReturn = new ArrayList<>();
            for (Myairports myAer : myaerodromi) {
                if (myAer.getUsername().getKorIme().equals(korime)) {
                    Aerodrom a = new Aerodrom();
                a.setDrzava(myAer.getIdent().getIsoCountry());
                a.setNaziv(myAer.getIdent().getName());
                a.setIcao(myAer.getIdent().getIdent());
                a.setLokacija(new Lokacija(myAer.getIdent().getCoordinates().split(",")[1].trim(), myAer.getIdent().getCoordinates().split(",")[0]));
                aerodromiReturn.add(a);
                }
            }
            return aerodromiReturn;
        }

        return null;
    }
    
    /**
     * Metoda koja dohvaća avione iz baze podataka te ih vraća u obliku objekta AvionLeti.
     * @param korime
     * @param lozinka
     * @param icao
     * @param odVr
     * @param doVr
     * @return 
     */
    public List<AvionLeti> dajAvioneAerodroma(String korime, String lozinka, String icao, long odVr, long doVr) {

        if (autentikacija(korime, lozinka)) {
            dodajUDnevnikRada("Dohvaćanje aviona koji pripadaju aerodromu: "+icao+" Timestamp: "+odVr+" - "+doVr);
            airplanes = airplanesFacade.dohvatiAvioneOdDo(icao, odVr, doVr);
            List<AvionLeti> avionLeti = new ArrayList<>();
            for (Airplanes avio : airplanes) {
                if (avio.getEstdepartureairport().equals(icao.toUpperCase()) && avio.getFirstseen() >= odVr && avio.getLastseen() <= doVr) {
                    AvionLeti a = new AvionLeti();
                    a.setIcao24(avio.getIcao24());
                    a.setFirstSeen(avio.getFirstseen());
                    a.setEstDepartureAirport(avio.getEstdepartureairport());
                    a.setLastSeen(avio.getLastseen());
                    if (avio.getEstarrivalairport() != null) {
                        a.setEstArrivalAirport(avio.getEstarrivalairport());
                    } else {
                        a.setEstArrivalAirport("NEPOZNATO");
                    }
                    a.setCallsign(avio.getCallsign());
                    a.setEstDepartureAirportHorizDistance(avio.getEstdepartureairporthorizdistance());
                    a.setEstDepartureAirportVertDistance(avio.getEstdepartureairportvertdistance());
                    a.setEstArrivalAirportHorizDistance(avio.getEstarrivalairporthorizdistance());
                    a.setEstArrivalAirportVertDistance(avio.getEstarrivalairportvertdistance());
                    a.setDepartureAirportCandidatesCount(avio.getDepartureairportcandidatescount());
                    a.setArrivalAirportCandidatesCount(avio.getArrivalairportcandidatescount());
                    avionLeti.add(a);
                }

            }
            return avionLeti;
        }
        return null;
    }
    
    /**
     * Metoda koja dohvaća letove pojedinog aviona unutar vremenskog razdoblja
     * @param korime
     * @param lozinka
     * @param icao24
     * @param odVr
     * @param doVr
     * @return 
     */
    public List<AvionLeti> dajLetAviona(String korime, String lozinka, String icao24, long odVr, long doVr) {

        if (autentikacija(korime, lozinka)) {
            dodajUDnevnikRada("Dohvaćanje letova aviona: "+icao24+" Timestamp: "+odVr+" - "+doVr);
            airplanes = airplanesFacade.dohvatiLetAviona(icao24, odVr, doVr);
            List<AvionLeti> avionLeti = new ArrayList<>();
            for (Airplanes avio : airplanes) {
                if (avio.getFirstseen() >= odVr && avio.getLastseen() <= doVr) {
                    AvionLeti a = new AvionLeti();
                    a.setIcao24(avio.getIcao24());
                    a.setFirstSeen(avio.getFirstseen());
                    a.setEstDepartureAirport(avio.getEstdepartureairport());
                    a.setLastSeen(avio.getLastseen());
                    if (avio.getEstarrivalairport() != null) {
                        a.setEstArrivalAirport(avio.getEstarrivalairport());
                    } else {
                        a.setEstArrivalAirport("NEPOZNATO");
                    }
                    a.setCallsign(avio.getCallsign());
                    a.setEstDepartureAirportHorizDistance(avio.getEstdepartureairporthorizdistance());
                    a.setEstDepartureAirportVertDistance(avio.getEstdepartureairportvertdistance());
                    a.setEstArrivalAirportHorizDistance(avio.getEstarrivalairporthorizdistance());
                    a.setEstArrivalAirportVertDistance(avio.getEstarrivalairportvertdistance());
                    a.setDepartureAirportCandidatesCount(avio.getDepartureairportcandidatescount());
                    a.setArrivalAirportCandidatesCount(avio.getArrivalairportcandidatescount());
                    avionLeti.add(a);
                }

            }
            return avionLeti;
        }
        return null;
    }
    
    /**
     * Metoda koja vraća udaljenost između dva aerodroma.
     * @param korime
     * @param lozinka
     * @param icao1
     * @param icao2
     * @return 
     */
    public double udaljenostDvaAerodroma(String korime, String lozinka, String icao1, String icao2) {
        if (autentikacija(korime, lozinka)) {
            dodajUDnevnikRada("Dohvaćanje udaljenosti između aerodroma: "+icao1+" i "+icao2);
            Airports prvi = new Airports();
            Airports drugi = new Airports();
            for (Airports aer : aerodromi) {
                if (aer.getIdent().equals(icao1.toUpperCase())) {
                    prvi = aer;
                    break;
                }
            }
            for (Airports aer : aerodromi) {
                if (aer.getIdent().equals(icao2.toUpperCase())) {
                    drugi = aer;
                    break;
                }
            }
            KoordinataWS prvaUd = new KoordinataWS(Double.parseDouble(prvi.getCoordinates().split(",")[1].trim()),
                    Double.parseDouble(prvi.getCoordinates().split(",")[0]));
            KoordinataWS drugaUd = new KoordinataWS(Double.parseDouble(drugi.getCoordinates().split(",")[1].trim()),
                    Double.parseDouble(drugi.getCoordinates().split(",")[0]));

            return distance(prvaUd.geoSirina, drugaUd.geoSirina, prvaUd.geoDuzina, drugaUd.geoDuzina, 0, 0);
        }
        return 0;
    }
    
    /**
     * Metoda koja vraća sve aerodrome korisnika koji se nalaze u blizini odabranog aerodroma unutar određenog razdoblja
     * @param korime
     * @param lozinka
     * @param icao
     * @param rasponOd
     * @param rasponDo
     * @return 
     */
    public List<Aerodrom> dajBliskeAerodrome(String korime, String lozinka, String icao, double rasponOd, double rasponDo) {
        if (autentikacija(korime, lozinka)) {
            dodajUDnevnikRada("Dohvaćanje vlastitih aerodroma u blizini aerodroma: "+icao+" u rasponu: "+rasponOd+" - "+rasponDo);
            Airports izabrani = new Airports();
            for (Airports aer : aerodromi) {
                if (aer.getIdent().equals(icao.toUpperCase())) {
                    izabrani = aer;
                    break;
                }
            }
            KoordinataWS prvaUd = new KoordinataWS(Double.parseDouble(izabrani.getCoordinates().split(",")[1].trim()),
                    Double.parseDouble(izabrani.getCoordinates().split(",")[0]));
            List<Aerodrom> aerodromiReturn = new ArrayList<>();
            List<Aerodrom> aerodromiKorisnika= dajAerodromeKorisnika(korime,lozinka,false);
            for (Aerodrom aer : aerodromiKorisnika) {
                KoordinataWS drugaUd = new KoordinataWS(Double.parseDouble(aer.getLokacija().getLatitude()),
                        Double.parseDouble(aer.getLokacija().getLongitude()));
                double absUdaljenost = Math.abs(distance(prvaUd.geoSirina, drugaUd.geoSirina, prvaUd.geoDuzina, drugaUd.geoDuzina, 0, 0));
                
                if (absUdaljenost >= rasponOd && absUdaljenost <= rasponDo) {
                    aerodromiReturn.add(aer);
                }
            }
            return aerodromiReturn;
        }

        return null;

    }
    
    /**
     * Metoda koja vraća udaljenost između dvije koordinate. Metoda je modificirana prema temi na stackoverflow-u.
     * @param lat1
     * @param lat2
     * @param lon1
     * @param lon2
     * @param el1
     * @param el2
     * @return 
     */
    public double distance(double lat1, double lat2, double lon1,
            double lon2, double el1, double el2) {

        final int R = 6371; // Radius zemlje

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

}
