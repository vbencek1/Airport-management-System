/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.vbencek.ejb.eb.Korisnici;

/**
 *
 * @author NWTiS_2
 */
@Local
public interface KorisniciFacadeLocal {

    void create(Korisnici korisnici);

    void edit(Korisnici korisnici);

    void remove(Korisnici korisnici);

    Korisnici find(Object id);

    List<Korisnici> findAll();

    List<Korisnici> findRange(int[] range);

    int count();
    
}
