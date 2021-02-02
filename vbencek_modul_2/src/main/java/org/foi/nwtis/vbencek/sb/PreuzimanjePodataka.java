/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.sb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import javax.ejb.Asynchronous;
import javax.inject.Inject;
import javax.ejb.EJB;
import org.foi.nwtis.vbencek.ejb.eb.Airplanes;
import org.foi.nwtis.vbencek.ejb.eb.Airports;
import org.foi.nwtis.vbencek.ejb.eb.Myairports;
import org.foi.nwtis.vbencek.ejb.eb.Myairportslog;
import org.foi.nwtis.vbencek.ejb.eb.MyairportslogPK;
import org.foi.nwtis.vbencek.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.vbencek.ejb.sb.MyairportslogFacadeLocal;

/**
 * Klasa koja služi za preuzimanje aviona pozivanjem metode Open Sky Network web servisa.
 * @author vbencek
 */
@Singleton
public class PreuzimanjePodataka implements PreuzimanjePodatakaLocal {

    @EJB(beanName = "MyairportsFacade")
    MyairportsFacadeLocal myairportsFacade;
    @EJB(beanName = "MyairportslogFacade")
    MyairportslogFacadeLocal myairportslogFacade;
    @EJB(beanName = "AirplanesFacade")
    AirplanesFacadeLocal airplanesFacade;

    long dan = 86400; //dan u sekundama

    @Override
    public void test() {
        System.out.println("Bean se pokrenuo sada");
    }
    
    /**
     * Glavna metoda ejb-a služi za preuzimanje aviona unutar postavljenih granica datuma.
     * Pokreće ju web modul prilikom pokretanja
     * @param korIme
     * @param lozinka
     * @param pocetakDatumPreuzimanja
     * @param krajDatumPreuzimanja
     * @param preuzimanjeCiklus
     * @param preuzimanjePauza
     * @param port 
     */
    @Override
    @Asynchronous
    public void preuzimanjePodataka(String korIme, String lozinka, String pocetakDatumPreuzimanja,
            String krajDatumPreuzimanja, int preuzimanjeCiklus, int preuzimanjePauza, int port) {
        OSKlijent oSKlijent = new OSKlijent(korIme, lozinka);
        long odVremena = vratiLongDatumPocetka(pocetakDatumPreuzimanja);
        long doVremena = vratiLongDatumSlijedeci(odVremena);
        long krajDatumPreuz = vratiLongDatumKraja(krajDatumPreuzimanja);
        int brojacPauzi = 0;
        List<Airports> aerodromiPreuz = myairportsFacade.dajAerodromePreuzimanja();
        List<Myairportslog> aerodromiLog = myairportslogFacade.findAll();
        while (true) {
            try {
                if (stanjePreuzimanja(port)) {
                    long danasnjiDatum = System.currentTimeMillis() / 1000 - 7200;
                    if (doVremena >= danasnjiDatum) {
                        System.out.println("vbencek_modul_2: Dostignut današnji datum; spavam 1 dan");
                        Thread.sleep(dan * 1000);
                    }
                    System.out.println("vbencek_modul_2: započinjem preuzimanje aviona...."); 
                    
                    for (Airports aerodrom : aerodromiPreuz) {
                        java.sql.Date sqlDatum = new java.sql.Date(odVremena * 1000);
                        System.out.println("POSTOJANJE: " + provjeriAkoPostojiUMyairportsLog(aerodrom, sqlDatum, aerodromiLog));
                        if (!provjeriAkoPostojiUMyairportsLog(aerodrom, sqlDatum, aerodromiLog)) {
                            
                            List<AvionLeti> avioni = oSKlijent.getDepartures(aerodrom.getIdent(), odVremena, doVremena);
                            new Thread() {
                                public void run() {
                                    dodajDohvaceneAvione(avioni, aerodrom, sqlDatum);
                                }
                            }.start();
                           
                            System.out.println("vbencek_modul_2: Obrađen: " + aerodrom.getIdent());
                           
                            Thread.sleep(preuzimanjePauza);
                            
                            brojacPauzi++;
                        } else {
                            System.out.println("vbencek_modul_2: Svi podaci za aerodrom: " + aerodrom.getIdent() + " na datum: " + sqlDatum + " su već preuzeti!");
                        }
                    }
                    System.out.println("vbencek_modul_2: Završen ciklus preuzimanja");
                    odVremena = odVremena + dan;
                    doVremena = doVremena + dan;
                    if (preuzimanjeCiklus - brojacPauzi * 1000 > 0) {
                        Thread.sleep(preuzimanjeCiklus - brojacPauzi * 1000);
                    }
                    brojacPauzi = 0;
                    if (odVremena >= krajDatumPreuz) {
                        System.out.println("Kraj preuzimanja!");
                        break;
                    }
                } else {
                    System.out.println("vbencek_modul_2: preuzimanje blokirano....");
                    Thread.sleep(preuzimanjeCiklus);

                }
            } catch (InterruptedException ex) {
                Logger.getLogger(PreuzimanjePodataka.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    /**
     * Metoda koja služi za slanje poruke na mrežnu utičnicu kako bi dobila status preuzimanja
     * @param port
     * @return 
     */
    private boolean stanjePreuzimanja(int port) {
        String poruka = "KORISNIK pero; LOZINKA 123456; STANJE;";
        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("vbencek_modul_2: Saljem poruku STANJE");
            posaljiPoruku(socket, poruka);
            String stanje = primiPoruku(socket);
            if (stanje.equals("OK 11;")) {
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
            System.out.println("Spajanje na server neuspješno!");
        }
        return false;
    }
    
    /**
     * Metoda koja služi za slanje poruke na mrežnu utičnicu
     * @param socket
     * @param poruka 
     */
    private void posaljiPoruku(Socket socket, String poruka) {
        OutputStream os;
        try {
            os = socket.getOutputStream();

            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(poruka);
            bw.flush();
            socket.shutdownOutput();
        } catch (IOException ex) {
            Logger.getLogger(PreuzimanjePodataka.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Metoda koja služi za primanje poruke od servera
     *
     * @param socket socket preko koje se prima poruka
     * @throws IOException
     */
    private String primiPoruku(Socket socket) throws IOException {
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String poruka = br.readLine();
        System.out.println("vbencek_modul_2: Primio poruku: " + poruka);
        socket.shutdownInput();
        return poruka;
    }

    /**
     * Metoda koja pretvara datum u long
     *
     * @return
     */
    private long vratiLongDatumPocetka(String pocetakDatumPreuzimanja) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date parsed;
        try {
            parsed = dateFormat.parse(pocetakDatumPreuzimanja);
            java.sql.Date date = new java.sql.Date(parsed.getTime());
            return date.getTime() / 1000;
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return 0;
    }

    /**
     * Metoda koja uvećava datum za 1 dan. Metoda se više ne koristi
     *
     * @param datum
     * @return
     */
    private java.sql.Date vratiSQLDatumSlijedeci(java.sql.Date datum) {
        return java.sql.Date.valueOf(datum.toLocalDate().plusDays(1));
    }

    /**
     * Metoda koja uvečava datum za 1 dan
     *
     * @param datum
     * @return
     */
    private long vratiLongDatumSlijedeci(long datum) {
        return datum + dan;
    }

    /**
     * Metoda koja pretvara datum kraja preuzimanja u long
     *
     * @return
     */
    private long vratiLongDatumKraja(String krajDatumPreuzimanja) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date parsed;
        try {
            parsed = dateFormat.parse(krajDatumPreuzimanja);
            java.sql.Date date = new java.sql.Date(parsed.getTime());
            return date.getTime() / 1000;
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return 0;
    }
    
    /**
     * Metoda koja služi za spremanje liste aerodroma za koje je potrebno preuzeti avione
     * @param myair
     * @return 
     */
    private List<Airports> spremiMyairports(List<Myairports> myair) {
        List<Airports> air = new ArrayList<>();
        for (Myairports my : myair) {
            System.out.println(my.getIdent());
            air.add(my.getIdent());
        }
        return air;
    }
    
    /**
     * Metoda koja provjerava da li aerodrom sa datumom preuzimanja već postoji u tablici myAirports
     * @param aerodrom
     * @param sqlDatum
     * @param aerodromiLog
     * @return 
     */
    private boolean provjeriAkoPostojiUMyairportsLog(Airports aerodrom, java.sql.Date sqlDatum, List<Myairportslog> aerodromiLog) {
        for (Myairportslog maLog : aerodromiLog) {
            if (maLog.getMyairportslogPK().getIdent().equals(aerodrom.getIdent())
                    && maLog.getMyairportslogPK().getFlightdate().getTime() == sqlDatum.getTime() - 3600 * 1000) {
                
                return true;
            }
        }
        return false;
    }

    /**
     * Metoda koja dodaje dohvaćene avione u bazu podataka
     * Za ull vrijednost aviona dodaje vrijednost NEPOZNATO
     * @param avioni
     */
    private void dodajDohvaceneAvione(List<AvionLeti> avioni, Airports aerodrom, java.sql.Date sqlDatum) {
        for (AvionLeti avio : avioni) {
            Airplanes a = new Airplanes();
            a.setIcao24(avio.getIcao24());
            a.setFirstseen(avio.getFirstSeen());
            a.setEstdepartureairport(avio.getEstDepartureAirport());
            a.setLastseen(avio.getLastSeen());
            if (avio.getEstArrivalAirport() != null) {
                a.setEstarrivalairport(avio.getEstArrivalAirport());
            } else {
                a.setEstarrivalairport("NEPOZNATO");
            }
            a.setCallsign(avio.getCallsign());
            a.setEstdepartureairporthorizdistance(avio.getEstDepartureAirportHorizDistance());
            a.setEstdepartureairportvertdistance(avio.getEstDepartureAirportVertDistance());
            a.setEstarrivalairporthorizdistance(avio.getEstArrivalAirportHorizDistance());
            a.setEstarrivalairportvertdistance(avio.getEstArrivalAirportVertDistance());
            a.setDepartureairportcandidatescount(avio.getDepartureAirportCandidatesCount());
            a.setArrivalairportcandidatescount(avio.getArrivalAirportCandidatesCount());
            a.setStored(new Date(System.currentTimeMillis()));
            airplanesFacade.create(a);

        }
        Myairportslog myairportslog = new Myairportslog(new MyairportslogPK(aerodrom.getIdent(), sqlDatum), new Date(System.currentTimeMillis()));
        try{
        myairportslogFacade.create(myairportslog);
        }catch(Exception e){
            System.out.println("TESTTTTTTTTT");
        }

    }
}
