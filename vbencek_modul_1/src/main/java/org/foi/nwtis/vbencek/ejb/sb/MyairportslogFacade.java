/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.ejb.sb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.foi.nwtis.vbencek.ejb.eb.Myairportslog;

/**
 *
 * @author NWTiS_2
 */
@Stateless
public class MyairportslogFacade extends AbstractFacade<Myairportslog> implements MyairportslogFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_Projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MyairportslogFacade() {
        super(Myairportslog.class);
    }
    @Override
    public int dajBrojDana(String ident) {
        javax.persistence.criteria.CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        javax.persistence.criteria.Root<Myairportslog> rt = cq.from(Myairportslog.class);
        javax.persistence.criteria.ParameterExpression<String> parametar = cb.parameter(String.class);
        cq.select(cb.count(rt)).where(cb.equal(rt.get("airports").get("ident"), parametar));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setParameter(parametar, ident);
        return ((Long) q.getSingleResult()).intValue();
    }
}
