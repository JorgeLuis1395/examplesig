/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Cabdocelect;

/**
 *
 * @author edwin
 */
@Stateless
public class CabdocelectFacade extends AbstractFacade<Cabdocelect> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CabdocelectFacade() {
        super(Cabdocelect.class);
    }

    @Override
    protected String modificarObjetos(Cabdocelect nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<compromiso>" + nuevo.getCompromiso()+ "</compromiso>";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";
        return retorno;
    }
    
}
