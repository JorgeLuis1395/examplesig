/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Clientes;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ClientesFacade extends AbstractFacade<Clientes> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClientesFacade() {
        super(Clientes.class);
    }

    @Override
    protected String modificarObjetos(Clientes nuevo) {
        String retorno="";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empresa>" + nuevo.getEmpresa()+ "</empresa>";
        return retorno;
    }
    
}
