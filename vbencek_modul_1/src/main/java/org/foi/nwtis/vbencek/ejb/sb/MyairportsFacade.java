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
import org.foi.nwtis.vbencek.ejb.eb.Airports;
import org.foi.nwtis.vbencek.ejb.eb.Myairports;

/**
 *
 * @author NWTiS_2
 */
@Stateless
public class MyairportsFacade extends AbstractFacade<Myairports> implements MyairportsFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_Projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MyairportsFacade() {
        super(Myairports.class);
    }
    
    @Override
    public int dohvatiBrojPratitelja(String ident) {
        javax.persistence.criteria.CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        javax.persistence.criteria.Root<Myairports> rt = cq.from(Myairports.class);
        javax.persistence.criteria.ParameterExpression<String> parametar = cb.parameter(String.class);
        cq.select(cb.count(rt)).where(cb.equal(rt.get("ident").get("ident"), parametar));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setParameter(parametar, ident);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    @Override
    public int dohvatiBrojAerodroma() {
        javax.persistence.criteria.CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        javax.persistence.criteria.Root<Myairports> rt = cq.from(Myairports.class);
        cq.select(cb.countDistinct(rt.get("ident")));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        
        return ((Long) q.getSingleResult()).intValue();
    }
    
    @Override
    public List<Airports> dajAerodromePreuzimanja(){
        javax.persistence.criteria.CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<Myairports> cq = cb.createQuery(Myairports.class); 
        javax.persistence.criteria.Root<Myairports> rt = cq.from(Myairports.class);
         cq.select(rt.get("ident")).distinct(true);
         javax.persistence.Query q = getEntityManager().createQuery(cq);
         List<Airports> aerodromi = q.getResultList();
         return aerodromi;
    }
    
}
