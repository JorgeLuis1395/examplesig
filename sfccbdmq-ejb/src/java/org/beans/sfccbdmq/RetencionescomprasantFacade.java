/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Retencionescomprasant;

/**
 *
 * @author edwin
 */
@Stateless
public class RetencionescomprasantFacade extends AbstractFacade<Retencionescomprasant> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RetencionescomprasantFacade() {
        super(Retencionescomprasant.class);
    }

    @Override
    protected String modificarObjetos(Retencionescomprasant nuevo) {
        return "";
    }
    public void procesoIgualar(){
        Query q=em.createQuery("Select OBJECT(o) FROM Retencionescomprasant as o WHERE o.retencionimpuesto=false");
        List<Retencionescomprasant> listaFuente=q.getResultList();
        for (Retencionescomprasant rant:listaFuente){
            // traer obligacion
            Obligaciones o=null;
//            Query qObligaciones=em.createQuery(deleteQuery)
        }
    }
}
