/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.dretve;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.vbencek.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.vbencek.web.naredbe.KomandeGrupe;
import org.foi.nwtis.vbencek.web.naredbe.KomandePosluzitelja;

/**
 * Klasa koja služi za obradu komandi koje dolaze na mrežnu utičnicu 
 * @author vbencek
 */
public class ObradaKomandi extends Thread {

    private Socket socket;
    BP_Konfiguracija konf;

    public ObradaKomandi(Socket socket, BP_Konfiguracija konf) {
        this.socket = socket;
        this.konf = konf;
    }

    @Override
    public void run() {
        try {
            String por = primiPoruku(socket);
            obradiPoruku(por);
        } catch (IOException ex) {
            Logger.getLogger(ObradaKomandi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metoda pomoću koje se šalju poruke serveru
     *
     * @param socket socket na koji se treba spojiti i poslati poruku
     * @param poruka poruka koju treba poslati serveru
     * @throws IOException
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
            Logger.getLogger(ObradaKomandi.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.println(poruka);
        socket.shutdownInput();
        return poruka;
    }
    
    /**
     * Metoda koja utvrđuje o kakvoj se poruci radi. Da li je una upućena poslužitelju 
     * ili grupi te iz poruke dohvaća korisnika, lozinku te naredbu
     * @param poruka
     */
    private void obradiPoruku(String poruka) {
        String polje[] = poruka.split(";");
        String korisnik = polje[0].split(" ")[1];
        String lozinka = polje[1].split(" ")[2];
        KomandePosluzitelja kP = new KomandePosluzitelja(konf);
        KomandeGrupe kG = new KomandeGrupe(konf);
        if (polje.length < 3) {
            posaljiPoruku(socket, kP.autentikacijaOrg(korisnik, lozinka));
        } else {
            if (polje[2].contains("GRUPA")) {
                String opcija = polje[2].split(";")[0].trim();
                System.out.println(opcija);
                switchGrupa(opcija, kG);
                posaljiPost(korisnik, lozinka, opcija);
            } else {
                String opcija = polje[2].split(";")[0].trim();
                System.out.println(opcija);
                switchPosluzitelj(opcija, korisnik, lozinka, kP);
                posaljiPost(korisnik, lozinka, opcija);
            }
        }
    }
    
    /**
     * Metoda koja obrađuje naredbe poslužitelja te poziva slanje poruke korisniku mrežne utičnice
     * @param opcija ulazni parametar
     * @param korisnik
     * @param lozinka
     * @param kP klasa KomandePosluzitelja
     */
    private void switchPosluzitelj(String opcija, String korisnik, String lozinka, KomandePosluzitelja kP) {
        switch (opcija) {
            case "DODAJ":
                posaljiPoruku(socket, kP.dodaj(korisnik, lozinka));
                break;
            case "PAUZA":
                posaljiPoruku(socket, kP.pauza(korisnik, lozinka));
                break;
            case "RADI":
                posaljiPoruku(socket, kP.radi(korisnik, lozinka));
                break;
            case "KRAJ":
                posaljiPoruku(socket, kP.kraj(korisnik, lozinka));
                PrimanjeKomandi.radi = false;
                break;
            case "STANJE":
                posaljiPoruku(socket, kP.stanje(korisnik, lozinka));
                break;
        }
    }
    
    /**
     * Metoda koja obrađuje naredbe grupe te poziva slanje poruke korisniku mrežne utičnice
     * @param opcija
     * @param kG klasa KomandeGrupe
     */
    private void switchGrupa(String opcija, KomandeGrupe kG) {
        switch (opcija) {
            case "GRUPA PRIJAVI":
                posaljiPoruku(socket, kG.prijavi());
                break;
            case "GRUPA ODJAVI":
                posaljiPoruku(socket, kG.odjavi());
                break;
            case "GRUPA AKTIVIRAJ":
                posaljiPoruku(socket, kG.aktiviraj());
                break;
            case "GRUPA BLOKIRAJ":
                posaljiPoruku(socket, kG.blokiraj());
                break;
            case "GRUPA STANJE":
                posaljiPoruku(socket, kG.dajStatus());
                break;
            default: break;
        }
        if(opcija.contains("GRUPA AERODROMI")){
            String aers=opcija.split(" ")[2];
            String[] aerodromi=aers.split(",");
            posaljiPoruku(socket, kG.dodajAerodrome(aerodromi));
        }
    }
    
    /**
     * Metoda koja šalje post zahtjev na treću aplikaciju gdje se dodaje komanda u red poruka.
     * @param korisnik
     * @param lozinka
     * @param komanda 
     */
    private void posaljiPost(String korisnik, String lozinka, String komanda) {
        String vrijemePrijema = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSSS").format(new Date());
        URL url;
        HttpURLConnection conn = null;
        try {
            String query = getQuery(komanda, vrijemePrijema);
            url = new URL("http://localhost:8084/vbencek_web_3/webresources/aerodromi/komande" + "?" + query);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("korisnik", korisnik);
            conn.setRequestProperty("lozinka", lozinka);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", "UTF-8");
            
            OutputStream os = conn.getOutputStream();
            os.write(getQuery(komanda, vrijemePrijema).getBytes("UTF-8"));
            os.flush();
            os.close();
            conn.getResponseCode();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }
    /**
     * Pomoćna metoda post metode koja oblikuje ulazne query parametra kako bi se mogli prenositi preo url-a
     * @param komanda
     * @param vrijemePrijema
     * @return
     * @throws UnsupportedEncodingException 
     */
    private String getQuery(String komanda, String vrijemePrijema) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        result.append(URLEncoder.encode("komanda", "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(komanda, "UTF-8"));
        result.append("&");
        result.append(URLEncoder.encode("vrijeme", "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(vrijemePrijema, "UTF-8"));
        return result.toString();
    }

}
