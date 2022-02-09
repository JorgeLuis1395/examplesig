/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Ejecutado;

/**
 *
 * @author edwin
 */
@Stateless
public class EjecutadoFacade extends AbstractFacade<Ejecutado> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EjecutadoFacade() {
        super(Ejecutado.class);
    }

    @Override
    protected String modificarObjetos(Ejecutado nuevo) {
       String retorno = "";
        retorno += "<detallecomrpomiso>" + nuevo.getDetallecomrpomiso()+ "</detallecomrpomiso>";
        retorno += "<ejecutado>" + nuevo.getEjecutado()+ "</ejecutado>";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        return retorno;
    }
    
}
