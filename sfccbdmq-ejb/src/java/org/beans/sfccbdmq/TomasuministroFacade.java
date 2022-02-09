/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tomasuministro;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TomasuministroFacade extends AbstractFacade<Tomasuministro> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TomasuministroFacade() {
        super(Tomasuministro.class);
    }

    @Override
    protected String modificarObjetos(Tomasuministro nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<saldo>" + nuevo.getSaldo() + "</saldo>";
        retorno += "<suministro>" + nuevo.getSuministro() + "</suministro>";
        retorno += "<constatado>" + nuevo.getConstatado() + "</constatado>";
        retorno += "<tomafisica>" + nuevo.getTomafisica() + "</tomafisica>";
        return retorno;
    }

}
