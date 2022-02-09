/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.VistaAsignaciones;

/**
 *
 * @author edwin
 */
@Stateless
public class VistaAsignacionesFacade extends AbstractFacade<VistaAsignaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VistaAsignacionesFacade() {
        super(VistaAsignaciones.class);
    }

    @Override
    protected String modificarObjetos(VistaAsignaciones nuevo) {
       return null;
    }
    
}
