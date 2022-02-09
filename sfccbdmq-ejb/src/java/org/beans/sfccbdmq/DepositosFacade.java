/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Depositos;

/**
 *
 * @author luis
 */
@Stateless
public class DepositosFacade extends AbstractFacade<Depositos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DepositosFacade() {
        super(Depositos.class);
    }

    @Override
    protected String modificarObjetos(Depositos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<kardexliquidacion>" + nuevo.getKardexliquidacion() + "</kardexliquidacion>";
        retorno += "<viaticoempleado>" + nuevo.getViaticoempleado() + "</viaticoempleado>";
        return retorno;
    }

}
