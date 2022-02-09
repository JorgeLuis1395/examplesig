/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Vencimientos;

/**
 *
 * @author edison
 */
@Stateless
public class VencimientosFacade extends AbstractFacade<Vencimientos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VencimientosFacade() {
        super(Vencimientos.class);
    }
    
     @Override
    protected String modificarObjetos(Vencimientos nuevo) {
        String retorno = "";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<contrato>" + nuevo.getContrato() + "</contrato>";
        retorno += "<texto>" + nuevo.getTexto() + "</texto>";
        retorno += "<numerodias>" + nuevo.getNumerodias() + "</numerodias>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }
    
}
