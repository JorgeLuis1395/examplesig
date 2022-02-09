/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Amortizcontables;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class AmortizcontablesFacade extends AbstractFacade<Amortizcontables> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AmortizcontablesFacade() {
        super(Amortizcontables.class);
    }

    @Override
    protected String modificarObjetos(Amortizcontables nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<ctacredito>" + nuevo.getCtacredito()+ "</ctacredito>";
        retorno += "<ctadebito>" + nuevo.getCtadebito()+ "</ctadebito>";
        retorno += "<motivo>" + nuevo.getMotivo()+ "</motivo>";
        retorno += "<dias>" + nuevo.getDias()+ "</dias>";
        retorno += "<fecha>" + nuevo.getFecha()+ "</fecha>";
        retorno += "<finalizado>" + nuevo.getFinalizado()+ "</finalizado>";
        
        return retorno;
    }
    
}
