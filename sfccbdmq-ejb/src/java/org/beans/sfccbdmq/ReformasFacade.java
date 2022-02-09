/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Reformas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ReformasFacade extends AbstractFacade<Reformas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReformasFacade() {
        super(Reformas.class);
    }

    @Override
    protected String modificarObjetos(Reformas nuevo) {
        String retorno = "";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cabecera>" + nuevo.getCabecera() + "</cabecera>";
        retorno += "<asignacion>" + nuevo.getAsignacion() + "</asignacion>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";

        return retorno;
    }

}
