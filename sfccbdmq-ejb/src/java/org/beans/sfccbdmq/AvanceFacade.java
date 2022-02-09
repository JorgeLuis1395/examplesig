/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Avance;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class AvanceFacade extends AbstractFacade<Avance> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AvanceFacade() {
        super(Avance.class);
    }

    @Override
    protected String modificarObjetos(Avance nuevo) {
        String retorno = "";
        retorno += "<usuario>" + nuevo.getUsuario()+ "</usuario>";
        retorno += "<contrato>" + nuevo.getContrato()+ "</contrato>";
        retorno += "<fecha>" + nuevo.getFecha()+ "</fecha>";
        retorno += "<porcentaje>" + nuevo.getPorcentaje()+ "</porcentaje>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        return retorno;
    }
    
}
