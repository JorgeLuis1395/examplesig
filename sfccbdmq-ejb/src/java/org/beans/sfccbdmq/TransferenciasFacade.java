/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Transferencias;

/**
 *
 * @author luis
 */
@Stateless
public class TransferenciasFacade extends AbstractFacade<Transferencias> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TransferenciasFacade() {
        super(Transferencias.class);
    }

    @Override
    protected String modificarObjetos(Transferencias nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<bien>" + nuevo.getBien()+ "</bien>";
        retorno += "<solicitud>" + nuevo.getSolicitud()+ "</solicitud>";
        
        return retorno;
    }
    
}
