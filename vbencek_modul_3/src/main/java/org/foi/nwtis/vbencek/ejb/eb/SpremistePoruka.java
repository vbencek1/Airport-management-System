/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.ejb.eb;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import lombok.Getter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import javax.annotation.PostConstruct;

/**
 * Klasa koja služi za spremanje JMS poruka koje šalje Web socket.
 * @author NWTiS_2
 */
@Startup
@Singleton
public class SpremistePoruka {

    @Getter
    public static List<String> poruka = new ArrayList<>();
    private static FileWriter file;
    
    /**
     * Metoda koja služi za spremanje poruka i listu
     * @param por 
     */
    public static void dodajPoruku(String por) {
        poruka.add(por);
        System.out.println("ejb modul 3: Dodana poruka: " + por + " Broj poruka: " + poruka.size());
    }
    
    /**
     * Metoda koja briše sve poruke jednog korisnika
     * @param korIme 
     */
    public void obrisiPoruke(String korIme) {
        for(Iterator<String> iter = poruka.listIterator(); iter.hasNext(); ){
            String s = iter.next();
            if(s.split(",")[1].split(" ")[2].trim().split("\"")[1].equals(korIme)){
                iter.remove();
            }
        }
    }
    
    /**
     * Metoda koja vraća listu poruka
     * @return 
     */
    public List<String>dajPoruke(){
        return poruka;
    }
    
    /**
      * Metoda koja prilikom gasenja ejb-a, sprema poruke u datoteku
      */
    public void spremiUdat() {
        try {
            file = new FileWriter("/home/NWTiS_2/vbencek/vbencek/vbencek_projekt/vbencek_modul_3/JMSporuke.txt");
            for (String a : poruka) {
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
      * Metoda koja prilikom pokretanja ejb-a, učitava poruke iz datoteke
      */
    public void ucitajIzDat() {
        try {
            FileReader fr = new FileReader("/home/NWTiS_2/vbencek/vbencek/vbencek_projekt/vbencek_modul_3/JMSporuke.txt");
            StringBuffer sb = new StringBuffer();

            while (fr.ready()) {
                char c = (char) fr.read();
                if (c == '\n') {
                    poruka.add(sb.toString());
                    System.out.println("ejb modul 3: ucitavam: "+sb.toString());
                    sb = new StringBuffer();
                } else {
                    sb.append(c);
                }
            }
            if (sb.length() > 0) {
                poruka.add(sb.toString());
                System.out.println("ejb modul 3: ucitavam: "+sb.toString());
            }

        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Metoda koja se pokreće prilikom pokretanja
     */
    @PostConstruct
    public void init(){
        System.out.println("ejb modul 3: ucitavam poruke");
        ucitajIzDat();
        
    }
    
    /**
     * Metoda koja se pokreće prilikom gašenja
     */
    @PreDestroy
    public void zatvori() {
        System.out.println("ejb modul 3: spremam poruke");
        spremiUdat();
        

    }

}
