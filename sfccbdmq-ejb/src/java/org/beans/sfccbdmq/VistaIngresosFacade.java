/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.VistaIngresos;

/**
 *
 * @author edwin
 */
@Stateless
public class VistaIngresosFacade extends AbstractFacade<VistaIngresos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VistaIngresosFacade() {
        super(VistaIngresos.class);
    }

    @Override
    protected String modificarObjetos(VistaIngresos nuevo) {
        return null;
    }
    
}
