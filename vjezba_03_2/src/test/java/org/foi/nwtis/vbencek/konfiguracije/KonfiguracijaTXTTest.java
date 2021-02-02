
package org.foi.nwtis.vbencek.konfiguracije;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;


public class KonfiguracijaTXTTest {
    
    private Konfiguracija konfiguracija=null;
    private Properties postavke=null;
    private String datoteka="NWTiS_vbencek_test.txt";
    
    public KonfiguracijaTXTTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
       
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws NemaKonfiguracije, NeispravnaKonfiguracija {
        postavke = new Properties();
        postavke.setProperty("1", "prva");
        postavke.setProperty("2", "druga");
        postavke.setProperty("3", "treca");
        postavke.setProperty("4", "cetvrta");
        postavke.setProperty("5", "peta");
        postavke.setProperty("6", "šesta");
        
        konfiguracija= KonfiguracijaApstraktna.kreirajKonfiguraciju(datoteka);
    }
    
    @After
    public void tearDown() {
        konfiguracija.obrisiSvePostavke();
        postavke.clear();
        
        File f=new File(datoteka);
        if(f.exists())
            f.delete();
    }

    /**
     * Test of ucitajKonfiguraciju method, of class KonfiguracijaTXT.
     */
    @Test
    public void testUcitajKonfiguraciju() throws Exception {
        System.out.println("ucitajKonfiguraciju");
        File f=new File(datoteka);
        if(!f.exists())
            fail("Problem kod kreiranja datoteke!");
        konfiguracija.ucitajKonfiguraciju();
        assertEquals(new Properties(), this.konfiguracija.dajSvePostavke());
        
        this.konfiguracija.dodajKonfiguraciju(postavke);
        this.konfiguracija.spremiKonfiguraciju();
        this.konfiguracija.obrisiSvePostavke();
        
        assertTrue(this.konfiguracija instanceof KonfiguracijaTXT);
        assertEquals(new Properties(), this.konfiguracija.dajSvePostavke());
        
        konfiguracija.ucitajKonfiguraciju();
        assertEquals(this.postavke, this.konfiguracija.dajSvePostavke());
        assertArrayEquals(this.postavke.keySet().toArray(), this.konfiguracija.dajSvePostavke().keySet().toArray());
        assertArrayEquals(this.postavke.entrySet().toArray(), this.konfiguracija.dajSvePostavke().entrySet().toArray());
        
    }
    
    
    @Test(expected = NeispravnaKonfiguracija.class)
    
    public void testUcitajKonfiguracijuSaKrivomDatotekom() throws Exception{
        konfiguracija=KonfiguracijaApstraktna.kreirajKonfiguraciju("datoteka");
    }
    
     @Test
    public void testUcitajKonfiguracijuSaKrivomDatotekom2(){
        try {
            konfiguracija=KonfiguracijaApstraktna.kreirajKonfiguraciju("datoteka");
            fail("Nije se bacila iznimka");
        } catch (NemaKonfiguracije ex) {
            fail("Kriva iznimka");
        } catch (NeispravnaKonfiguracija ex) {
            System.out.println("Iznimka ispravno izbacena");
        }
    }
    /**
     * Test of ucitajKonfiguraciju method, of class KonfiguracijaTXT.
     */
    @Test
     @Ignore
    public void testUcitajKonfiguraciju_String() throws Exception {
        System.out.println("ucitajKonfiguraciju");
        String datoteka = "";
        KonfiguracijaTXT instance = null;
        instance.ucitajKonfiguraciju(datoteka);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of spremiKonfiguraciju method, of class KonfiguracijaTXT.
     */
    @Test
     @Ignore
    public void testSpremiKonfiguraciju() throws Exception {
        System.out.println("spremiKonfiguraciju");
        KonfiguracijaTXT instance = null;
        instance.spremiKonfiguraciju();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of spremiKonfiguraciju method, of class KonfiguracijaTXT.
     */
    @Test
     @Ignore
    public void testSpremiKonfiguraciju_String() throws Exception {
        System.out.println("spremiKonfiguraciju");
        String datoteka = "";
        KonfiguracijaTXT instance = null;
        instance.spremiKonfiguraciju(datoteka);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
