/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Pondnivelgestion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class PondnivelgestionFacade extends AbstractFacade<Pondnivelgestion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PondnivelgestionFacade() {
        super(Pondnivelgestion.class);
    }

    @Override
    protected String modificarObjetos(Pondnivelgestion nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nivelgestion>" + nuevo.getNivelgestion() + "</nivelgestion>";
        retorno += "<aspecto>" + nuevo.getAspecto() + "</aspecto>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<ponderacion>" + nuevo.getPonderacion() + "</ponderacion>";

        return retorno;
    }

}
