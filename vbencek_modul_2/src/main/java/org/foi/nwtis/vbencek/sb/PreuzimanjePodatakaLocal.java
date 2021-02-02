/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.sb;

import javax.ejb.Local;

/**
 *
 * @author NWTiS_2
 */
@Local
public interface PreuzimanjePodatakaLocal {

    void test();

    void preuzimanjePodataka(String korIme, 
            String lozinka, 
            String pocetakDatumPreuzimanja,
            String krajDatumPreuzimanja, 
            int preuzimanjeCiklus, 
            int preuzimanjePauza, 
            int port);
    
}
