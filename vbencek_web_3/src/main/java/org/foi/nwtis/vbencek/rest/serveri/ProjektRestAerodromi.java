/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.rest.serveri;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.podaci.Odgovor;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.vbencek.web.serveri.Aerodrom;
import org.foi.nwtis.vbencek.ws.klijenti.Projekt_WS;

/**
 * REST Web Service klasa
 *
 * @author NWTiS_2
 */
@Path("aerodromi")
@RequestScoped
public class ProjektRestAerodromi {

    @Context
    private UriInfo context;

    @Inject
    PodaciREST podaciREST;
    
    @Inject
    SlanjePoruke slanjePoruke;

    /**
     * Creates a new instance of ProjektRestAerodromi
     */
    public ProjektRestAerodromi() {
    }

    /**
     * Metoda koja vraća aerodrome koje prati korisnik
     *
     * @param korisnik
     * @param lozinka
     * @return
     */
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodomeKorisnika(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka) {
        podaciREST.dodajUDnevnikRada(korisnik, lozinka, "Dohvat vlastitih aviona");
        List<Aerodrom> aerodromi = null;
        Projekt_WS projekt_WS = new Projekt_WS();
        aerodromi = projekt_WS.dajAerodromeKorisnika(korisnik, lozinka);
        Odgovor odgovor = new Odgovor();
        if (aerodromi == null) {
            odgovor.setStatus("40");
            odgovor.setPoruka("Prazna lista");
        } else {
            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
            odgovor.setOdgovor(aerodromi.toArray());
        }
        return Response.ok(odgovor).status(200).build();
    }

    /**
     * Metoda koja vraća izabrani aerodrom korisnika
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return
     */
    @Path("{icao}")
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajIzabraniAerodrom(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        
        podaciREST.dodajUDnevnikRada(korisnik, lozinka,"Dohvat izabranog aviona: "+icao);
        List< org.foi.nwtis.podaci.Aerodrom> aerodrom = new ArrayList<>();
        org.foi.nwtis.vbencek.ejb.eb.Airports aer = podaciREST.dajIzabraniAerodromKorisnika(korisnik, lozinka, icao);
        if (aer != null) {
            org.foi.nwtis.podaci.Aerodrom a = new  org.foi.nwtis.podaci.Aerodrom();
            a.setDrzava(aer.getIsoCountry());
            a.setNaziv(aer.getName());
            a.setIcao(aer.getIdent());
            a.setLokacija(new Lokacija(aer.getCoordinates().split(",")[1].trim(), aer.getCoordinates().split(",")[0]));
            aerodrom.add(a);
        }
        
        Odgovor odgovor = new Odgovor();
        if (aerodrom == null) {
            odgovor.setStatus("40");
            odgovor.setPoruka("Nema aerodroma");
        } else {
            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
            odgovor.setOdgovor(aerodrom.toArray());
        }

        return Response.ok(odgovor).status(200).build();
    }
    
    /**
     * Metoda koja vraća aerodrome prema nazivu ili drzavi
     *
     * @param korisnik
     * @param lozinka
     * @param naziv
     * @param drzava
     * @return an instance of Response
     */
    @Path("/svi")
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodome(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("naziv") String naziv,
            @QueryParam("drzava") String drzava) {

        Projekt_WS projekt_WS = new Projekt_WS();
        List<Aerodrom> aerodromi = new ArrayList<>();
        if (naziv != null && !naziv.isEmpty()) {
            podaciREST.dodajUDnevnikRada(korisnik, lozinka, "Dohvaćanje svih aerodroma po nazivu: "+naziv);
            aerodromi = projekt_WS.dajAerodromeNaziv(korisnik, lozinka, naziv);
        } else if (drzava != null && !drzava.isEmpty()) {
            podaciREST.dodajUDnevnikRada(korisnik, lozinka, "Dohvaćanje svih aerodroma po drzavi: "+drzava);
            aerodromi = projekt_WS.dajAerodromeDrzava(korisnik, lozinka, drzava);

        } else {
            podaciREST.dodajUDnevnikRada(korisnik, lozinka, "Dohvaćanje svih aerodroma po %");
            aerodromi = projekt_WS.dajAerodromeNaziv(korisnik, lozinka, "");
        }
        Odgovor odgovor = new Odgovor();
        if (aerodromi == null) {
            odgovor.setStatus("40");
            odgovor.setPoruka("Lista je prazna");
        } else {
            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
            odgovor.setOdgovor(aerodromi.toArray());
        }

        return Response.ok(odgovor).status(200).build();
    }
    
    /**
     * Metoda koja briše aerodrom korisnika
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return 
     */
    @Path("{icao}")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response obrisiAerodrom(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao){
        
        Odgovor odgovor = new Odgovor();
        podaciREST.dodajUDnevnikRada(korisnik, lozinka, "Zahtjev za brisanjem aerodroma: "+icao);
        if (podaciREST.obrisiAerodromKorisnika(korisnik, lozinka, icao)) {
            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
        } else {
            odgovor.setStatus("40");
            odgovor.setPoruka("Aerodrom nije obrisan");
        }
        return Response.ok(odgovor).status(200).build();
    }
    
    /**
     * Metoda koja briše avione aerodroma korisnika
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return 
     */
    @Path("{icao}/avioni")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response obrisiAvioneAerodroma(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao){
        
        Odgovor odgovor = new Odgovor();
        podaciREST.dodajUDnevnikRada(korisnik, lozinka, "Zahtjev za brisanjem aviona aerodroma: "+icao);
        if (podaciREST.obrisiAvioneAerodroma(korisnik, lozinka, icao)) {
            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
        } else {
            odgovor.setStatus("40");
            odgovor.setPoruka("Avioni nisu obrisani");
        }
        return Response.ok(odgovor).status(200).build();
    }
    
    /**
     * Post metoda koja šalje JMS poruku sa naredbama iz aplikacije 1 u red poruka
     * @param korisnik
     * @param lozinka
     * @param komanda
     * @param vrijeme
     * @return 
     */
    @Path("/komande")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dodajKomandu(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("komanda") String komanda,
            @QueryParam("vrijeme") String vrijeme) {
        
        Odgovor odgovor = new Odgovor();
        boolean yes=true;
        try{
            String poruka = slanjePoruke.formirajForuku(korisnik, komanda, vrijeme);
            slanjePoruke.saljiPoruku(poruka);
        }catch(Exception e){
            System.out.println(e.getMessage());
            yes=false;
        }
        if (yes) {
            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
        } else {
            odgovor.setStatus("40");
            odgovor.setPoruka("Komanda nije poslana");
        }
        return Response.ok(odgovor).status(200).build();
    }
}
