package org.foi.nwtis.vbencek.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.vbencek.ejb.eb.DnevnikRada;
import org.foi.nwtis.vbencek.ejb.sb.DnevnikRadaFacadeLocal;
import org.foi.nwtis.vbencek.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa koja implementira zrno te sadrži metode za rad sa dnevnikom rada
 * @author NWTiS_2
 */
@Named(value = "pregledDnevnika")
@SessionScoped
public class PregledDnevnika implements Serializable {

    @EJB(beanName = "DnevnikRadaFacade")
    DnevnikRadaFacadeLocal dnevnikRadaFacade;

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
    @Setter
    int brojRedaka=10;

    @Getter
    List<DnevnikRada> dnevnik = new ArrayList<>();
    
    /**
     * Metoda koja se pokreće prilikom konstrukcije zrna te ocitava konfiguraciju
     */
    @PostConstruct
    public void init(){
        BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        brojRedaka = Integer.parseInt(bpk.getKonfig().dajPostavku("dnevnik.zapisi"));
    }
    
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
     * Metoda kojom se dohvaćaju zapisi dnevnika
     */
    public void dohvatiZapiseDnevnika() {
        preuzmiPodatkeKorisnika();
        if (!"".equals(korIme)) {
            dnevnik=pretraziDnevnik(dnevnikRadaFacade.findAll());
        } else {
            dnevnik.clear();
        }
    }
    
    /**
     * Metoda kojom se pretražuje dnevnik prema korisničkom imenu
     * @param dnevnik
     * @return 
     */
    public List<DnevnikRada> pretraziDnevnik(List<DnevnikRada> dnevnik){
        List<DnevnikRada> dnevnikVrati= new ArrayList<>();
        for(DnevnikRada dr: dnevnik){
            if(dr.getKorIme().getKorIme().equals(korIme)){
                dnevnikVrati.add(dr);
            }
        }return dnevnikVrati;
    }
}
