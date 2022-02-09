/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Unidades;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class UnidadesFacade extends AbstractFacade<Unidades> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UnidadesFacade() {
        super(Unidades.class);
    }

    @Override
    protected String modificarObjetos(Unidades nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<equivalencia>" + nuevo.getEquivalencia() + "</equivalencia>";
        retorno += "<factor>" + nuevo.getFactor() + "</factor>";
        retorno += "<base>" + nuevo.getBase() + "</base>";

        return retorno;
    }

}
