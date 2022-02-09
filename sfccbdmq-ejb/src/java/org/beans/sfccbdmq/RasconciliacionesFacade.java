/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Rasconciliaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author luis
 */
@Stateless
public class RasconciliacionesFacade extends AbstractFacade<Rasconciliaciones> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RasconciliacionesFacade() {
        super(Rasconciliaciones.class);
    }

    @Override
    protected String modificarObjetos(Rasconciliaciones nuevo) {
        String retorno = "";
        retorno += "<manual>" + nuevo.getManual()+ "</manual>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<renglon>" + nuevo.getRenglon()+ "</renglon>";
        retorno += "<detalle>" + nuevo.getDetalle()+ "</detalle>";

        return retorno;
    }
    
}
