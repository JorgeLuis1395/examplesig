/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Conciliaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author luis
 */
@Stateless
public class ConciliacionesFacade extends AbstractFacade<Conciliaciones> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConciliacionesFacade() {
        super(Conciliaciones.class);
    }

    @Override
    protected String modificarObjetos(Conciliaciones nuevo) {
        String retorno = "";
        retorno += "<observaciones>" + nuevo.getObservaciones()+ "</observaciones>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<usuario>" + nuevo.getUsuario()+ "</usuario>";
        retorno += "<banco>" + nuevo.getBanco()+ "</banco>";
        retorno += "<fecha>" + nuevo.getFecha()+ "</fecha>";
        retorno += "<terminado>" + nuevo.getTerminado()+ "</terminado>";

        return retorno;
    }
    
}
