package org.foi.nwtis.vbencek.ejb.eb;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Klasa koja služi kao spremište naredbi iz aplikacije 1 koje su poslane na mrežnu utičnicu
 * @author vbencek
 */
@Startup
@Singleton
public class SpremisteKomandi {
    
    public List<String> komande = new ArrayList<>();
    private static FileWriter file;
    
    /**
     * Metoda koja dodaje naredbu u listu naredbi.
     * @param por 
     */
     public void dodajPoruku(String por) {
        komande.add(por);
        System.out.println("ejb modul 3: Dodana poruka: " + por + " Broj poruka: " + komande.size());
    }
     
     /**
      * Metoda koja vraća listu naredba
      * @return 
      */
     public List<String> vratiKomande(){
         return komande;
     }
     
     /**
      * Metoda koja briše naredbe korisnika
      * @param korIme 
      */
     public void obrisiPoruke(String korIme) {
        for(Iterator<String> iter = komande.listIterator(); iter.hasNext(); ){
            String s = iter.next();
            if(s.split(",")[1].split(" ")[2].trim().split("\"")[1].equals(korIme)){
                iter.remove();
            }
        }
    }
     
     /**
      * Metoda koja prilikom gasenja ejb-a, sprema naredbe u datoteku
      */
    public void spremiUdat() {
        try {
            file = new FileWriter("/home/NWTiS_2/vbencek/vbencek/vbencek_projekt/vbencek_modul_3/JMSkomande.txt");
            for (String a : komande) {
                file.write(a);
                file.write(System.getProperty("line.separator"));
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {

            try {
                file.flush();
                file.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    /**
     * Metoda koja prilikom paljenja ejb-a učitava naredbe iz datoteke
     */
    public void ucitajIzDat() {
        try {
            FileReader fr = new FileReader("/home/NWTiS_2/vbencek/vbencek/vbencek_projekt/vbencek_modul_3/JMSkomande.txt");
            StringBuffer sb = new StringBuffer();

            while (fr.ready()) {
                char c = (char) fr.read();
                if (c == '\n') {
                    komande.add(sb.toString());
                    System.out.println("ejb modul 3: ucitavam: "+sb.toString());
                    sb = new StringBuffer();
                } else {
                    sb.append(c);
                }
            }
            if (sb.length() > 0) {
                komande.add(sb.toString());
                System.out.println("ejb modul 3: ucitavam: "+sb.toString());
            }

        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Pokreće učitavanje iz datoteke prilikom inicijalizacije
     */
    @PostConstruct
    public void init(){
        ucitajIzDat();
        
    }
    
    /**
     * Prije gašenja ejb-a pokreće spremanje u datoteku.
     */
    @PreDestroy
    public void zatvori() {
        spremiUdat();
        

    }
}
