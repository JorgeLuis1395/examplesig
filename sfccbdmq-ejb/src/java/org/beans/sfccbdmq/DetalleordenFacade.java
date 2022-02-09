/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Detalleorden;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class DetalleordenFacade extends AbstractFacade<Detalleorden> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetalleordenFacade() {
        super(Detalleorden.class);
    }

    @Override
    protected String modificarObjetos(Detalleorden nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<suministro>" + nuevo.getSuministro() + "</suministro>";
        retorno += "<ordencompra>" + nuevo.getOrdencompra() + "</ordencompra>";
        retorno += "<pvp>" + nuevo.getPvp() + "</pvp>";
        retorno += "<cantidad>" + nuevo.getCantidad() + "</cantidad>";
        return retorno;
    }

}
