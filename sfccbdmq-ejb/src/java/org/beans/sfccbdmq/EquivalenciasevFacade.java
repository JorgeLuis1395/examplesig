/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Equivalenciasev;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class EquivalenciasevFacade extends AbstractFacade<Equivalenciasev> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EquivalenciasevFacade() {
        super(Equivalenciasev.class);
    }

    @Override
    protected String modificarObjetos(Equivalenciasev nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<notaminima>" + nuevo.getNotaminima() + "</notaminima>";
        retorno += "<notamaxima>" + nuevo.getNotamaxima() + "</notamaxima>";
        retorno += "<puntajefijo>" + nuevo.getPuntajefijo() + "</puntajefijo>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";

        return retorno;
    }

}
