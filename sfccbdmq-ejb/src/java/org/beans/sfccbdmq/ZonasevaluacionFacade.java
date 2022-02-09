/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Zonasevaluacion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ZonasevaluacionFacade extends AbstractFacade<Zonasevaluacion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ZonasevaluacionFacade() {
        super(Zonasevaluacion.class);
    }

    @Override
    protected String modificarObjetos(Zonasevaluacion nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<zonaevaluacion>" + nuevo.getZonaevaluacion() + "</zonaevaluacion>";
        retorno += "<ponderacion>" + nuevo.getPonderacion() + "</ponderacion>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<total>" + nuevo.getTotal() + "</total>";

        return retorno;
    }

}
