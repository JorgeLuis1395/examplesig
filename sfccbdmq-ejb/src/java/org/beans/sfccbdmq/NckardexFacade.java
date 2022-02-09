/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Nckardex;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class NckardexFacade extends AbstractFacade<Nckardex> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NckardexFacade() {
        super(Nckardex.class);
    }

    @Override
    protected String modificarObjetos(Nckardex nuevo) {
        String retorno = "";
        retorno += "<kardexbanco>" + nuevo.getKardexbanco() + "</kardexbanco>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<notacredito>" + nuevo.getNotacredito() + "</notacredito>";

        return retorno;
    }

}
