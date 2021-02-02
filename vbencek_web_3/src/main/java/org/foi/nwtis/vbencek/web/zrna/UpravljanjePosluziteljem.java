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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.vbencek.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa koja služi kao zrno za jsf stranicu stanjePosluzitelja.xhtml. Komunicira sa posluziteljem na mrežnoj utičnici te pribavlja stanje
 * preuzimanja podataka i status grupe.
 * @author NWTiS_2
 */
@Named(value = "upravljanjePosluziteljem")
@SessionScoped
public class UpravljanjePosluziteljem implements Serializable {

    @Inject
    upravljanjeKorisnicima uK;

    @Inject
    ServletContext context;

    @Getter
    @Setter
    String korIme = "";
    @Getter
    @Setter
    String lozinka = "";
    @Getter
    String porukaPauza = "";
    @Getter
    String porukaRadi = "";
    @Getter
    String porukaKraj = "";
    @Getter
    String porukaStatus = "";
    @Getter
    String porukaInfo = "";
    @Getter
    String porukaPrijavi = "";
    @Getter
    String porukaOdjavi = "";
    @Getter
    String porukaAktiviraj = "";
    @Getter
    String porukaBlokiraj = "";
    @Getter
    String porukaStanje = "";
    @Getter
    String porukaPostavi = "";
    @Getter
    String porukaInfo2 = "";
    @Setter
    @Getter
    String aerodromi="";

    /**
     * Metoda koja preuzima podatke o prijavljenom korisniku
     */
    public void preuzmiPodatkeKorisnika() {
        korIme = uK.getKorIme();
        lozinka = uK.getLozinka();
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
     * Metoda kojom se odjavljuje aktivni korisnik
     */
    public void odjaviKorisnika() {
        uK.setKorIme("");
        uK.setLozinka("");
        uK.setPorukaPrijave("");
    }
    
    /**
     * Metoda koja šalje naredbu PAUZA na mrežnu utičnicu te ispisuje dobiveni rezultat od poslužitelja
     */
    public void posluziteljPauza() {
        ocistiPoruke();
        if (!"".equals(korIme)) {
            porukaInfo = "";
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
            String kom = komuniciraj(korIme, lozinka, port, " PAUZA;", "OK 10;", "ERR 13;");
            if ("OK 10;".equals(kom)) {
                porukaPauza = "Preuzimanje pauzirano!";
            } else if ("ERR 13;".equals(kom)) {
                porukaPauza = "Preuzimanje je već pauzirano!";
            } else if ("server_closed".equals(kom)) {
                porukaPauza = "";
                porukaInfo = "Spajanje na poslužitelj neuspješno";
            }
        } else {
            porukaPauza = "";
            porukaInfo = "Niste prijavljeni, ne možete slati naredbe";
        }
    }
    
    /**
     * Metoda koja šalje naredbu RADI na mrežnu utičnicu te ispisuje dobiveni rezultat od poslužitelja
     */
    public void posluziteljRadi() {
        ocistiPoruke();
        if (!"".equals(korIme)) {
            porukaInfo = "";
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
            String kom = komuniciraj(korIme, lozinka, port, " RADI;", "OK 10;", "ERR 14;");
            if ("OK 10;".equals(kom)) {
                porukaRadi = "Preuzimanje pokrenuto!";
            } else if ("ERR 14;".equals(kom)) {
                porukaRadi = "Preuzimanje je već pokrenuto!";
            } else if ("server_closed".equals(kom)) {
                porukaRadi = "";
                porukaInfo = "Spajanje na poslužitelj neuspješno";
            }
        } else {
            porukaRadi = "";
            porukaInfo = "Niste prijavljeni, ne možete slati naredbe";
        }

    }
    
    /**
     * Metoda koja šalje naredbu KRAJ na mrežnu utičnicu te ispisuje dobiveni rezultat od poslužitelja
     */
    public void posluziteljKraj() {
        ocistiPoruke();
        if (!"".equals(korIme)) {
            porukaInfo = "";
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
            if ("OK 10;".equals(komuniciraj(korIme, lozinka, port, " KRAJ;", "OK 10;", ""))) {
                porukaKraj = "Preuzimanje prekinuto!";
            } else {
                porukaKraj = "";
                porukaInfo = "Spajanje na poslužitelj neuspješno";
            }
        } else {
            porukaKraj = "";
            porukaInfo = "Niste prijavljeni, ne možete slati naredbe";
        }
    }
    
    /**
     * Metoda koja šalje naredbu STATUS na mrežnu utičnicu te ispisuje dobiveni rezultat od poslužitelja
     */
    public void posluziteljStatus() {
        ocistiPoruke();
        if (!"".equals(korIme)) {
            porukaInfo = "";
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
            String kom = komuniciraj(korIme, lozinka, port, " STANJE;", "OK 11;", "OK 12;");
            if ("OK 11;".equals(kom)) {
                porukaStatus = "Podaci se preuzimaju!";
            } else if ("OK 12;".equals(kom)) {
                porukaStatus = "Podaci se ne preuzimaju!";
            } else if ("server_closed".equals(kom)) {
                porukaStatus = "";
                porukaInfo = "Spajanje na poslužitelj neuspješno";
            }
        } else {
            porukaStatus = "";
            porukaInfo = "Niste prijavljeni, ne možete slati naredbe";
        }
    }
    
    /**
     * Metoda koja resetira poruke korisniku
     */
    public void ocistiPoruke() {
        porukaPauza = "";
        porukaRadi = "";
        porukaKraj = "";
        porukaStatus = "";
        porukaInfo = "";
    }
    
    /**
     * Metoda koja šalje naredbu GRUPA PRIJAVI na mrežnu utičnicu te ispisuje dobiveni rezultat od poslužitelja
     */
    public void prijaviGrupu() {
        ocistiPoruke2();
        if (!"".equals(korIme)) {
            porukaInfo2 = "";
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
            String kom = komuniciraj(korIme, lozinka, port, " GRUPA PRIJAVI;", "OK 20;", "ERR 20;", "");
            if ("OK 20;".equals(kom)) {
                porukaPrijavi = "Grupa registrirana";
            } else if ("ERR 20;".equals(kom)) {
                porukaPrijavi = "Grupa je već registrirana!";
            } else if ("server_closed".equals(kom)) {
                porukaPrijavi = "";
                porukaInfo2 = "Spajanje na poslužitelj neuspješno";
            }
        } else {
            porukaPrijavi = "";
            porukaInfo2 = "Niste prijavljeni, ne možete slati naredbe";
        }
    }
    
    /**
     * Metoda koja šalje naredbu GRUPA ODJAVI na mrežnu utičnicu te ispisuje dobiveni rezultat od poslužitelja
     */
    public void odjaviGrupu() {
        ocistiPoruke2();
        if (!"".equals(korIme)) {
            porukaInfo2 = "";
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
            String kom = komuniciraj(korIme, lozinka, port, " GRUPA ODJAVI;", "OK 20;", "ERR 21;", "");;
            if ("OK 20;".equals(kom)) {
                porukaOdjavi = "Grupa odjavljena";
            } else if ("ERR 21;".equals(kom)) {
                porukaOdjavi = "Grupa je već odjavljena!";
            } else if ("server_closed".equals(kom)) {
                porukaOdjavi = "";
                porukaInfo2 = "Spajanje na poslužitelj neuspješno";
            }
        } else {
            porukaOdjavi = "";
            porukaInfo2 = "Niste prijavljeni, ne možete slati naredbe";
        }
    }
    
    /**
     * Metoda koja šalje naredbu GRUPA AKTIVIRAJ na mrežnu utičnicu te ispisuje dobiveni rezultat od poslužitelja
     */
    public void aktivirajGrupu() {
        ocistiPoruke2();
        if (!"".equals(korIme)) {
            porukaInfo2 = "";
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
            String kom = komuniciraj(korIme, lozinka, port, " GRUPA AKTIVIRAJ;", "OK 20;", "ERR 22;", "ERR 21;");
            if ("OK 20;".equals(kom)) {
                porukaAktiviraj = "Grupa aktivirana!";
            } else if ("ERR 22;".equals(kom)) {
                porukaAktiviraj = "Grupa je već aktivirana!";
            } else if ("ERR 21;".equals(kom)) {
                porukaAktiviraj = "Grupa ne postoji!";
            } else if ("server_closed".equals(kom)) {
                porukaAktiviraj = "";
                porukaInfo2 = "Spajanje na poslužitelj neuspješno";
            }
        } else {
            porukaAktiviraj = "";
            porukaInfo2 = "Niste prijavljeni, ne možete slati naredbe";
        }

    }
    
    /**
     * Metoda koja šalje naredbu GRUPA BLOKIRAJ na mrežnu utičnicu te ispisuje dobiveni rezultat od poslužitelja
     */
    public void blokirajGrupu() {
        ocistiPoruke2();
        if (!"".equals(korIme)) {
            porukaInfo2 = "";
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
            String kom = komuniciraj(korIme, lozinka, port, " GRUPA BLOKIRAJ;", "OK 20;", "ERR 23;", "ERR 21;");
            if ("OK 20;".equals(kom)) {
                porukaBlokiraj = "Grupa blokirana!";
            } else if ("ERR 23;".equals(kom)) {
                porukaBlokiraj = "Grupa je već blokirana!";
            } else if ("ERR 21;".equals(kom)) {
                porukaBlokiraj = "Grupa ne postoji!";
            } else if ("server_closed".equals(kom)) {
                porukaBlokiraj = "";
                porukaInfo2 = "Spajanje na poslužitelj neuspješno";
            }
        } else {
            porukaBlokiraj = "";
            porukaInfo2 = "Niste prijavljeni, ne možete slati naredbe";
        }
    }
    
    /**
     * Metoda koja šalje naredbu GRUPA STANJE na mrežnu utičnicu te ispisuje dobiveni rezultat od poslužitelja
     */
    public void stanjeGrupe() {
        ocistiPoruke2();
        if (!"".equals(korIme)) {
            porukaInfo2 = "";
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
            String kom = komuniciraj(korIme, lozinka, port, " GRUPA STANJE;", "OK 21;", "OK 22;", "ERR 21;");
            if ("OK 21;".equals(kom)) {
                porukaStanje = "Grupa je aktivna!";
            } else if ("OK 22;".equals(kom)) {
                porukaStanje = "Grupa je blokirana!";
            } else if ("ERR 21;".equals(kom)) {
                porukaStanje = "Grupa ne postoji!";
            } else if ("server_closed".equals(kom)) {
                porukaStanje = "";
                porukaInfo2 = "Spajanje na poslužitelj neuspješno";
            }
        } else {
            porukaStanje = "";
            porukaInfo2 = "Niste prijavljeni, ne možete slati naredbe";
        }
    }
    
    /**
     * Metoda koja šalje naredbu GRUPA AERODROMI na mrežnu utičnicu te ispisuje dobiveni rezultat od poslužitelja.
     * Također šalje ident-e aerodroma odvojene zarezom
     */
    public void postaviAerodrome() {
        ocistiPoruke2();
        if (!"".equals(korIme)) {
            porukaInfo2 = "";
            if(!aerodromi.trim().isEmpty()){
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(bpk.getKonfig().dajPostavku("posluzitelj.port"));
            String kom = komuniciraj(korIme, lozinka, port, " GRUPA AERODROMI "
                    +aerodromi.toUpperCase()+";", "OK 20;", "ERR 31;", "ERR 32;");
            if ("OK 20;".equals(kom)) {
                porukaPostavi = "Aerodromi dodani!!";
            } else if ("ERR 31;".equals(kom)) {
                porukaPostavi = "Grupa nije blokirana!";
            } else if ("ERR 32;".equals(kom)) {
                porukaPostavi = "Greska: aerodromi nisu dodani!";
            } else if ("server_closed".equals(kom)) {
                porukaPostavi = "";
                porukaInfo2 = "Spajanje na poslužitelj neuspješno";
            }}else{
                porukaInfo2="Unesite aerodrome";
            }
        } else {
            porukaPostavi = "";
            porukaInfo2 = "Niste prijavljeni, ne možete slati naredbe";
        }
        
    }

    /**
     * Metoda koja resetira poruke koje se prikazuju korisniku
     */
    private void ocistiPoruke2() {
        porukaPrijavi = "";
        porukaOdjavi = "";
        porukaAktiviraj = "";
        porukaBlokiraj = "";
        porukaStanje = "";
        porukaPostavi = "";
        porukaInfo2 = "";
    }
    
    /**
     * Metoda koja služi za komuniciranje sa poslužiteljem na mržnoj utičnici.
     * @param korIme
     * @param lozinka 
     * @param port port na kojem je poslužitelj
     * @param naredba naredba koja se šalje poslužitelju
     * @param kod1 jedan od tri koda koje vraća poslužitelj
     * @param kod2 jedan od tri koda koje vraća poslužitelj
     * @param kod3 jedan od tri koda koje vraća poslužitelj
     * @return 
     */
    private String komuniciraj(String korIme, String lozinka, int port, String naredba, String kod1, String kod2, String kod3) {
        String poruka = "KORISNIK " + korIme + "; LOZINKA " + lozinka + ";" + naredba;
        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("vbencek_web_3: saljem poruku: " + poruka);
            posaljiPoruku(socket, poruka);
            String stanje = primiPoruku(socket);
            if (stanje.equals(kod1)) {
                return kod1;
            } else if (stanje.equals(kod2)) {
                return kod2;
            } else if (stanje.equals(kod3)) {
                return kod3;
            }
        } catch (IOException ex) {
            System.out.println("Spajanje na server neuspješno!");
        }
        return "server_closed";
    }
    
    /**
     * Metoda koja služi za komuniciranje sa poslužiteljem na mržnoj utičnici.
     * @param korIme
     * @param lozinka
     * @param port port na kojem je poslužitelj
     * @param naredba naredba koja se šalje poslužitelju
     * @param kodOk jedan od dva koda koje vraća poslužitelj
     * @param kodErr jedan od dva koda koje vraća poslužitelj
     * @return 
     */
    private String komuniciraj(String korIme, String lozinka, int port, String naredba, String kodOk, String kodErr) {
        String poruka = "KORISNIK " + korIme + "; LOZINKA " + lozinka + ";" + naredba;
        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("vbencek_web_3: saljem poruku: " + poruka);
            posaljiPoruku(socket, poruka);
            String stanje = primiPoruku(socket);
            if (stanje.equals(kodOk)) {
                return kodOk;
            } else if (stanje.equals(kodErr)) {
                return kodErr;
            }
        } catch (IOException ex) {
            System.out.println("Spajanje na server neuspješno!");
        }
        return "server_closed";
    }
    
    /**
     * Metoda koja služi za slanje poruke poslužitelju na mrežnoj utičnici
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
