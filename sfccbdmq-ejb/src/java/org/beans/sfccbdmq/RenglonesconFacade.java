/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Renglonescon;

/**
 *
 * @author edwin
 */
@Stateless
public class RenglonesconFacade extends AbstractFacade<Renglonescon> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RenglonesconFacade() {
        super(Renglonescon.class);
    }

    @Override
    protected String modificarObjetos(Renglonescon nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<referencia>" + nuevo.getReferencia()+ "</referencia>";
        retorno += "<conciliacion>" + nuevo.getConciliacion()+ "</conciliacion>";
        retorno += "<detalleconciliacion>" + nuevo.getDetalleconciliacion()+ "</detalleconciliacion>";
        retorno += "<fecha>" + nuevo.getFecha()+ "</fecha>";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";
        return retorno;
    }
    
}
