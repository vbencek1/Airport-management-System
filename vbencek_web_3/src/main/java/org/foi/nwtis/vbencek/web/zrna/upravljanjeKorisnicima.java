package org.foi.nwtis.vbencek.web.zrna;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.net.Socket;
import javax.ejb.EJB;
import org.foi.nwtis.vbencek.ejb.sb.KorisniciFacadeLocal;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.foi.nwtis.vbencek.ejb.eb.Korisnici;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.vbencek.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa koja služi za prijavu, odjavu, registraciju te pregled korisnika
 * @author vbencek
 */
@Named(value = "upravljanjeKorisnicima")
@SessionScoped
public class upravljanjeKorisnicima implements Serializable {

    @Inject
    ServletContext context;

    @EJB(beanName = "KorisniciFacade")
    KorisniciFacadeLocal korisniciFacade;

    @Getter
    @Setter
    List<Korisnici> korisnici = new ArrayList<>();

    @Getter
    @Setter
    String korIme = "";
    @Getter
    @Setter
    String lozinka = "";
    @Getter
    @Setter
    private String porukaPrijave;

    @Getter
    @Setter
    String novoKorIme = "";
    @Getter
    @Setter
    String novoLozinka = "";
    @Getter
    @Setter
    String novoEmail = "";
    @Getter
    @Setter
    String novoIme = "";
    @Getter
    @Setter
    String novoPrezime = "";
    @Getter
    @Setter
    private String porukaRegistracije;

    @PostConstruct
    public void init() {
        //korisnici = korisniciFacade.findAll();
    }
    
    /**
     * Metoda koja služi za dohvat svih korisnika
     */
    public void dohvatiKorisnike() {
        if (!"".equals(korIme)) {
            korisnici = korisniciFacade.findAll();
        } else {
            korisnici.clear();
        }
    }
    
    /**
     * Metoda koja služi za prijavu korisnika
     */
    public void prijavaKorisnika() {
        BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
        if (komuniciraj(korIme, lozinka, port, "")) {
            porukaPrijave = "Uspješna prijava! Dobrodošli " + korIme;
        } else {
            porukaPrijave = "Neuspješna prijava";
            korIme = "";
            lozinka = "";
        }

    }
    
    /**
     * Metoda koja služi za odjavu korisnika
     */
    public void odjaviKorisnika() {
        setKorIme("");
        setLozinka("");
        setPorukaPrijave("");
    }
    
    /**
     * Metoda koja služi za registraciju korisnika. Korisnik s dodaje u obje baze podataka
     */
    public void registrirajKorisnika() {

        if (novoKorIme.trim().isEmpty() || novoLozinka.trim().isEmpty() || novoEmail.trim().isEmpty() || novoIme.trim().isEmpty() || novoPrezime.trim().isEmpty()) {
            porukaRegistracije = "Neuspješna registracija! Neka polja su prazna!";

        } else {
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
            if (komuniciraj(novoKorIme, novoLozinka, port, " DODAJ;")) {
                dodajUBazu2();
                porukaRegistracije = "Uspješna registracija!";
            } else {
                porukaRegistracije = "Neuspješna registracija! Korisničko ime već postoji!";
            }
        }
    }
    
    /**
     * Metoda kojom se korisnik dodaje u bazu podataka 2
     */
    private void dodajUBazu2() {
        Korisnici noviKor = new Korisnici();
        noviKor.setKorIme(novoKorIme);
        noviKor.setIme(novoIme);
        noviKor.setPrezime(novoPrezime);
        noviKor.setLozinka(novoLozinka);
        noviKor.setEmailAdresa(novoEmail);
        noviKor.setDatumKreiranja(new Date(System.currentTimeMillis()));
        noviKor.setDatumPromjene(new Date(System.currentTimeMillis()));
        korisniciFacade.create(noviKor);

    }

    /**
     * Metoda koja ispisuje aktivnog korisnika u obliku formirane poruke
     *
     * @return formirana poruka
     */
    public String ispisiKorisnika() {
        if (korIme.trim().isEmpty() || korIme == null) {
            return "Niste prijavljeni!";
        } else {
            return "Prijavljeni ste kao: " + korIme;
        }
    }
    
    /**
     * Metoda kojom se komunicira sa poslužiteljem na mrežnoj utičnici
     * @param korIme
     * @param lozinka
     * @param port
     * @param naredba
     * @return 
     */
    private boolean komuniciraj(String korIme, String lozinka, int port, String naredba) {
        String poruka = "KORISNIK " + korIme + "; LOZINKA " + lozinka + ";" + naredba;
        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("vbencek_web_3: saljem poruku: " + poruka);
            posaljiPoruku(socket, poruka);
            String stanje = primiPoruku(socket);
            if (stanje.equals("OK 10;")) {
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
     * Metoda kojom se šalje poruka poslužitelju na mrežnoj utičnici
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
            Logger.getLogger(upravljanjeKorisnicima.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.println("vbencek_web_3: Primio poruku: " + poruka);
        socket.shutdownInput();
        return poruka;
    }

}
