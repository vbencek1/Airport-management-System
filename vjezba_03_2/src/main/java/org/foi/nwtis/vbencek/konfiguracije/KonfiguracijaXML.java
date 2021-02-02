
package org.foi.nwtis.vbencek.konfiguracije;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class KonfiguracijaXML extends KonfiguracijaApstraktna{

    public KonfiguracijaXML(String nazivDatoteke) {
        super(nazivDatoteke);
    }

   @Override
    public void ucitajKonfiguraciju() throws NemaKonfiguracije, NeispravnaKonfiguracija {
        ucitajKonfiguraciju(this.nazivDatoteke);
    }

    @Override
    public void ucitajKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija {
        this.obrisiSvePostavke();
        
        if(datoteka==null || datoteka.length()==0)
            throw new NemaKonfiguracije("Neispravni naziv datoteke!"+getClass());
        
        File f=new File(datoteka);
        if(f.exists() && f.isFile()){
            try {
                this.postavke.loadFromXML(new FileInputStream(f));
            }catch (IOException ex) {
                throw new NeispravnaKonfiguracija("Problem kod učitavanja konfiguracije");
            }
        }else{
            throw new NemaKonfiguracije("Datoteka pod nazivom "+datoteka+" ne postoji ili nije datoteka!");
        }
    }

    @Override
    public void spremiKonfiguraciju() throws NemaKonfiguracije, NeispravnaKonfiguracija {
        spremiKonfiguraciju(this.nazivDatoteke);
    }

    @Override
    public void spremiKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija {
        
        if(datoteka==null || datoteka.length()==0)
            throw new NemaKonfiguracije("Neispravni naziv datoteke!"+getClass());
        
        File f=new File(datoteka);
        if((f.exists() && f.isFile()) || !f.exists()){
            try {
                this.postavke.storeToXML(new FileOutputStream(f), "vbencek");
            }catch (IOException ex) {
                throw new NeispravnaKonfiguracija("Problem kod učitavanja konfiguracije");
            }
        }else{
            throw new NemaKonfiguracije("Datoteka pod nazivom "+datoteka+" ne postoji ili nije datoteka!");
        }
    }
    
}
