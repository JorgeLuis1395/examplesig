/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Detallecertificacionespoa;

/**
 *
 * @author luis
 */
@Stateless
public class DetallecertificacionespoaFacade extends AbstractFacade<Detallecertificacionespoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetallecertificacionespoaFacade() {
        super(Detallecertificacionespoa.class);
    }

    @Override
    protected String modificarObjetos(Detallecertificacionespoa nuevo) {

        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<asignacion>" + nuevo.getAsignacion() + "</asignacion>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<certificacion>" + nuevo.getCertificacion() + "</certificacion>";
        retorno += "<adjudicado>" + nuevo.getAdjudicado() + "</adjudicado>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";

        return retorno;
    }

}
