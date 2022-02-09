/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Auxiliares;

/**
 *
 * @author edwin
 */
@Stateless
public class AuxiliaresFacade extends AbstractFacade<Auxiliares> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AuxiliaresFacade() {
        super(Auxiliares.class);
    }

    @Override
    protected String modificarObjetos(Auxiliares nuevo) {
       String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<codigo>" + nuevo.getCodigo()+ "</codigo>";
        retorno += "<nombre>" + nuevo.getNombre()+ "</nombre>";
        return retorno;
    }
    
}
