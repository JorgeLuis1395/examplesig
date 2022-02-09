/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Reembolsos;

/**
 *
 * @author edwin
 */
@Stateless
public class ReembolsosFacade extends AbstractFacade<Reembolsos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReembolsosFacade() {
        super(Reembolsos.class);
    }

    @Override
    protected String modificarObjetos(Reembolsos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<establecimiento>" + nuevo.getEstablecimiento()+ "</establecimiento>";
        retorno += "<numero>" + nuevo.getNumero()+ "</numero>";
        retorno += "<punto>" + nuevo.getPunto()+ "</punto>";
        retorno += "<ruc>" + nuevo.getRuc()+ "</ruc>";
        retorno += "<baseimponible>" + nuevo.getBaseimponible()+ "</baseimponible>";
        retorno += "<obligacion>" + nuevo.getObligacion()+ "</obligacion>";

        return retorno;
    }
    
}
