/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Detallesolicitud;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class DetallesolicitudFacade extends AbstractFacade<Detallesolicitud> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetallesolicitudFacade() {
        super(Detallesolicitud.class);
    }

    @Override
    protected String modificarObjetos(Detallesolicitud nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<solicitudsuministro>" + nuevo.getSolicitudsuministro() + "</solicitudsuministro>";
        retorno += "<cantidad>" + nuevo.getCantidad() + "</cantidad>";
        retorno += "<aprobado>" + nuevo.getAprobado()+ "</aprobado>";
        retorno += "<aprobadoinversion>" + nuevo.getAprobadoinversion()+ "</aprobadoinversion>";
        retorno += "<cantidadinvercion>" + nuevo.getCantidadinvercion()+ "</cantidadinvercion>";

        return retorno;
    }

}
