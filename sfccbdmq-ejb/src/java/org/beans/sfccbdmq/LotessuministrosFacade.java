/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Lotessuministros;

/**
 *
 * @author edwin
 */
@Stateless
public class LotessuministrosFacade extends AbstractFacade<Lotessuministros> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LotessuministrosFacade() {
        super(Lotessuministros.class);
    }

    @Override
    protected String modificarObjetos(Lotessuministros nuevo) {
         String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<lote>" + nuevo.getLote()+ "</lote>";
        retorno += "<cantidad>" + nuevo.getCantidad()+ "</cantidad>";
        retorno += "<fechaCaduca>" + nuevo.getFechaCaduca()+ "</fechaCaduca>";
        retorno += "<suministro>" + nuevo.getSuministro()+ "</suministro>";
        return retorno;
    }
    
}