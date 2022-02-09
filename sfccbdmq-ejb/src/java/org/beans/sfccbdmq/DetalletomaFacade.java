/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Detalletoma;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class DetalletomaFacade extends AbstractFacade<Detalletoma> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetalletomaFacade() {
        super(Detalletoma.class);
    }

    @Override
    protected String modificarObjetos(Detalletoma nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<toma>" + nuevo.getToma() + "</toma>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";
        retorno += "<verificado>" + nuevo.getVerificado() + "</verificado>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<localidad>" + nuevo.getLocalidad() + "</localidad>";

        return retorno;
    }

}
