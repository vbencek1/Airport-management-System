/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.foi.nwtis.vbencek.ejb.eb.DnevnikRada;

/**
 *
 * @author NWTiS_2
 */
@Stateless
public class DnevnikRadaFacade extends AbstractFacade<DnevnikRada> implements DnevnikRadaFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_Projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DnevnikRadaFacade() {
        super(DnevnikRada.class);
    }
}
