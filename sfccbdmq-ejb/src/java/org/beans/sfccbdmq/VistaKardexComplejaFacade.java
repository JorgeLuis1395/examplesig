/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.VistaKardexCompleja;

/**
 *
 * @author edwin
 */
@Stateless
public class VistaKardexComplejaFacade extends AbstractFacade<VistaKardexCompleja> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VistaKardexComplejaFacade() {
        super(VistaKardexCompleja.class);
    }

    @Override
    protected String modificarObjetos(VistaKardexCompleja nuevo) {
        return null;
    }
    
}
