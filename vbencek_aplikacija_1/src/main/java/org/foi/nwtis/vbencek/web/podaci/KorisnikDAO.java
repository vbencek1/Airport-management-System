/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.vbencek.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa koja dodaje ili dohvaća korisnike iz baze podataka.
 * @author vbencek
 */
public class KorisnikDAO {
    /**
     * Metoda koja vraća pojedninog korisnika iz baze podataka
     * @param korisnik
     * @param lozinka
     * @param prijava
     * @param bpk
     * @return vraća korisnika ako postoji inaće vraća null
     */
    public boolean dohvatiKorisnika(String korisnik, String lozinka, Boolean prijava, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "SELECT KOR_IME, LOZINKA FROM korisnici WHERE "
                + "KOR_IME = '" + korisnik + "'";

        if (prijava) {
            upit += " and LOZINKA = '" + lozinka + "'";
        }

        try {
            Class.forName(bpk.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String korisnik1 = rs.getString("kor_ime");
                    if(korisnik1!=null)
                    return true;
                }

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
     /**
     * Metoda koja dodaje korisnika u bazu podataka
     * @param korIme
     * @param lozinka
     * @param bpk
     * @return vraća true ako je sve uredu
     */
    public boolean dodajKorisnika(String korIme,String lozinka, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "INSERT INTO korisnici (KOR_IME, LOZINKA) VALUES ("
                + "'" + korIme + "', '" + lozinka + "')";

        try {
            Class.forName(bpk.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {

                int brojAzuriranja = s.executeUpdate(upit);

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
