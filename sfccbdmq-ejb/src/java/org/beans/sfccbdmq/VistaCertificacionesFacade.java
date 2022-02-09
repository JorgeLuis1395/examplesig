/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.VistaCertificaciones;

/**
 *
 * @author edwin
 */
@Stateless
public class VistaCertificacionesFacade extends AbstractFacade<VistaCertificaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VistaCertificacionesFacade() {
        super(VistaCertificaciones.class);
    }

    @Override
    protected String modificarObjetos(VistaCertificaciones nuevo) {
       return null;
    }
    
}
