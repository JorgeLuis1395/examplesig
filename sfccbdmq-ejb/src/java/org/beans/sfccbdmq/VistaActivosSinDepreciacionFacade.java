/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.VistaActivosSinDepreciacion;

/**
 *
 * @author edwin
 */
@Stateless
public class VistaActivosSinDepreciacionFacade extends AbstractFacade<VistaActivosSinDepreciacion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VistaActivosSinDepreciacionFacade() {
        super(VistaActivosSinDepreciacion.class);
    }

    @Override
    protected String modificarObjetos(VistaActivosSinDepreciacion nuevo) {
         String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fechaingreso>" + nuevo.getFechaingreso()+ "</fechaingreso>";
        retorno += "<fechabaja>" + nuevo.getFechabaja()+ "</fechabaja>";
        return retorno;
    }
    
}
