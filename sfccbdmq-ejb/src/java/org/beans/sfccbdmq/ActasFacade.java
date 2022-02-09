/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Actas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ActasFacade extends AbstractFacade<Actas> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActasFacade() {
        super(Actas.class);
    }

    @Override
    protected String modificarObjetos(Actas nuevo) {
       String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<antecedentes>" + nuevo.getAntecedentes()+ "</antecedentes>";
        retorno += "<entregan>" + nuevo.getEntregan()+ "</entregan>";
        retorno += "<numerotexto>" + nuevo.getNumerotexto()+ "</numerotexto>";
        retorno += "<reciben>" + nuevo.getReciben()+ "</reciben>";
        retorno += "<numero>" + nuevo.getNumero()+ "</numero>";

        return retorno;
    }

    
    
}
