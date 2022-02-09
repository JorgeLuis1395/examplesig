/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Detalleamortiz;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class DetalleamortizFacade extends AbstractFacade<Detalleamortiz> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetalleamortizFacade() {
        super(Detalleamortiz.class);
    }

    @Override
    protected String modificarObjetos(Detalleamortiz nuevo) {
       String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<amortizcontab>" + nuevo.getAmortizcontab()+ "</amortizcontab>";
        retorno += "<contabilizado>" + nuevo.getContabilizado()+ "</contabilizado>";
        retorno += "<fecha>" + nuevo.getFecha()+ "</fecha>";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";
        return retorno;
    }
    
}
