/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Reclamos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ReclamosFacade extends AbstractFacade<Reclamos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReclamosFacade() {
        super(Reclamos.class);
    }

    @Override
    protected String modificarObjetos(Reclamos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<poliza>" + nuevo.getPoliza() + "</poliza>";
        retorno += "<numeroreclamo>" + nuevo.getNumeroreclamo() + "</numeroreclamo>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";

        return retorno;
    }

}
