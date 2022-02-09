/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Ejecutado2018;

/**
 *
 * @author edwin
 */
@Stateless
public class Ejecutado2018Facade extends AbstractFacade<Ejecutado2018> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Ejecutado2018Facade() {
        super(Ejecutado2018.class);
    }

    @Override
    protected String modificarObjetos(Ejecutado2018 nuevo) {
       String retorno = "";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";
        retorno += "<partida>" + nuevo.getPartida()+ "</partida>";
        retorno += "<proyecto>" + nuevo.getProyecto() + "</proyecto>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        return retorno;
    }
    
}
