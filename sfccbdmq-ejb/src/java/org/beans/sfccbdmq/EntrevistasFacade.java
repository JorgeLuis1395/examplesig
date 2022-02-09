/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Entrevistas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class EntrevistasFacade extends AbstractFacade<Entrevistas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntrevistasFacade() {
        super(Entrevistas.class);
    }

    @Override
    protected String modificarObjetos(Entrevistas nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<postulacion>" + nuevo.getPostulacion() + "</postulacion>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<observacion>" + nuevo.getObservacion() + "</observacion>";
        retorno += "<responsable>" + nuevo.getResponsable() + "</responsable>";

        return retorno;
    }

}
