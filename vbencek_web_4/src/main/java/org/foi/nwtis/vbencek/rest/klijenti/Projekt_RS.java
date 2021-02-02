/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.rest.klijenti;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:ProjektRestAerodromi
 * [aerodromi]<br>
 * USAGE:
 * <pre>
 *        Projekt_RS client = new Projekt_RS();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author vbencek
 */
public class Projekt_RS {

    private final WebTarget webTarget;
    private final Client client;
    private static final String BASE_URI = "http://localhost:8084/vbencek_web_3/webresources";
    private String korisnik = "";
    private  String lozinka = "";

    public Projekt_RS(String korisnik, String lozinka) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("aerodromi");
        this.korisnik = korisnik;
        this.lozinka = lozinka;
    }
    
    /**
     * Poziva se delete metoda REst servisa iz aplikacije 3 pod putanjom icao/avioni
     * Brišu se svi avioni zadanog aerodroma
     * @param icao zadani aerodrom
     * @return
     * @throws ClientErrorException 
     */
    public Response obrisiAvioneAerodroma(String icao) throws ClientErrorException {
        
        return webTarget
                .path(java.text.MessageFormat.format("{0}/avioni", new Object[]{icao}))
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .delete();
    }
    
    /**
     * Metoda koja poziva Get metodu REST servisa na putanji /svi
     * Dohvaćaju se aerodromi po državi ili nazivu ovisno o danom parametru
     * @param <T>
     * @param responseType
     * @param drzava
     * @param naziv
     * @return
     * @throws ClientErrorException 
     */
    public <T> T dajAerodome(Class<T> responseType, String drzava, String naziv) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (drzava != null) {
            resource = resource.queryParam("drzava", drzava);
        }
        if (naziv != null) {
            resource = resource.queryParam("naziv", naziv);
        }
        resource = resource.path("svi");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);
    }
    
    /**
     * Metoda koja poziva REST metodu iz zadaca 3 na putanji /icao
     * Daje izabrani aerodrom
     * @param <T>
     * @param responseType
     * @param icao izrabrani aerodrom
     * @return
     * @throws ClientErrorException 
     */
    public <T> T dajIzabraniAerodrom(Class<T> responseType, String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{icao}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);
    }
    
    /**
     * Metoda koja poziva REST metodu na osnovnoj putanji.
     * Daje sve aerodrome koju se nalaze u kolekciji korisnika
     * @param <T>
     * @param responseType
     * @return
     * @throws ClientErrorException 
     */
    public <T> T dajAerodomeKorisnika(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);
    }
    
    /**
     * Metoda koja poziva DELETE metodu rest servisa iz aplikacije 3.
     * Brište zadani aerodrom ukoliko nema pridruženih aviona
     * @param icao
     * @return
     * @throws ClientErrorException 
     */
    public Response obrisiAerodrom(String icao) throws ClientErrorException {
        return webTarget
                .path(java.text.MessageFormat.format("{0}", new Object[]{icao}))
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .delete();
    }

    public void close() {
        client.close();
    }
    
}
