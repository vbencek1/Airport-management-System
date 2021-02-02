/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.vbencek.ejb.eb.DnevnikRada;

/**
 *
 * @author NWTiS_2
 */
@Local
public interface DnevnikRadaFacadeLocal {

    void create(DnevnikRada dnevnikRada);

    void edit(DnevnikRada dnevnikRada);

    void remove(DnevnikRada dnevnikRada);

    DnevnikRada find(Object id);

    List<DnevnikRada> findAll();

    List<DnevnikRada> findRange(int[] range);

    int count();
    
}
