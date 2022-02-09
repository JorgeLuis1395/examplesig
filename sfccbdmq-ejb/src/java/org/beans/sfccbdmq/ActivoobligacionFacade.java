/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Activoobligacion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ActivoobligacionFacade extends AbstractFacade<Activoobligacion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActivoobligacionFacade() {
        super(Activoobligacion.class);
    }

    @Override
    protected String modificarObjetos(Activoobligacion nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<obligacion>" + nuevo.getObligacion() + "</obligacion>";

        return retorno;
    }

}
