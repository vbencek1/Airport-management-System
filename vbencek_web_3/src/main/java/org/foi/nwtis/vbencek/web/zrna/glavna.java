package org.foi.nwtis.vbencek.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.inject.Inject;

/**
 * Klasa koja služi za odjavu korisnika
 * @author vbencek
 */
@Named(value = "glavna")
@SessionScoped
public class glavna implements Serializable {

    @Inject
    upravljanjeKorisnicima uK;
       
    /**
     * Metoda kojom se odjaljuje aktivni korisnik
     */
    public void odjaviKorisnika(){
        uK.setKorIme("");
        uK.setLozinka("");
        uK.setPorukaPrijave("");
    }
    
}
