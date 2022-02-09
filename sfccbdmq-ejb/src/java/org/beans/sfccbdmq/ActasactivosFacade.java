/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Actasactivos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ActasactivosFacade extends AbstractFacade<Actasactivos> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActasactivosFacade() {
        super(Actasactivos.class);
    }

    @Override
    protected String modificarObjetos(Actasactivos nuevo) {
       String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<acta>" + nuevo.getActa()+ "</acta>";
        retorno += "<activo>" + nuevo.getActivo()+ "</activo>";
        return retorno;
    }
    
}
