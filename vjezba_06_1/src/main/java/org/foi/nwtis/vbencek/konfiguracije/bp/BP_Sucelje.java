package org.foi.nwtis.vbencek.konfiguracije.bp;


import java.util.Properties;
import org.foi.nwtis.vbencek.konfiguracije.Konfiguracija;

/**
 *
 * @author dkermek
 */
public interface BP_Sucelje {
    String getAdminDatabase();
    String getAdminPassword();
    String getAdminUsername();
    String getDriverDatabase();
    String getDriverDatabase(String bp_url);
    Properties getDriversDatabase();
    String getServerDatabase();
    String getUserDatabase();
    String getUserPassword();
    String getUserUsername();  
    Konfiguracija getKonfig();
    
    String getLocationIQToken();
    String getOWPKey();
    String getOSNUsername();
    String getOSNPassword();
    String getDownloadStatus();
    String getDownloadStart();
    String getDownloadEnd();
    String getDownloadCycle();
    String getDownloadBreak();
}
