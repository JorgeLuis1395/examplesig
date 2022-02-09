/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Trackingcontrato;

/**
 *
 * @author luis
 */
@Stateless
public class TrackingcontratoFacade extends AbstractFacade<Trackingcontrato> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TrackingcontratoFacade() {
        super(Trackingcontrato.class);
    }

    @Override
    protected String modificarObjetos(Trackingcontrato nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<contrato>" + nuevo.getContrato() + "</contrato>";
        retorno += "<administrador>" + nuevo.getAdministradorcontrato() + "</administrador>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";

        return retorno;
    }

}
