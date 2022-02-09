/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Aspectos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class AspectosFacade extends AbstractFacade<Aspectos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AspectosFacade() {
        super(Aspectos.class);
    }

    @Override
    protected String modificarObjetos(Aspectos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<aspecto>" + nuevo.getAspecto() + "</aspecto>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<zonaevaluacion>" + nuevo.getZonaevaluacion() + "</zonaevaluacion>";

        return retorno;
    }

}
