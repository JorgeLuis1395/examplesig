/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Trackingproyectospoa;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author luis
 */
@Stateless
public class TrackingproyectospoaFacade extends AbstractFacade<Trackingproyectospoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TrackingproyectospoaFacade() {
        super(Trackingproyectospoa.class);
    }

    @Override
    protected String modificarObjetos(Trackingproyectospoa nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fechainicio>" + nuevo.getFechainicio() + "</fechainicio>";
        retorno += "<fechapublicacion>" + nuevo.getFechapublicacion() + "</fechapublicacion>";
        retorno += "<fechadesierto>" + nuevo.getFechadesierto() + "</fechadesierto>";
        retorno += "<proyecto>" + nuevo.getProyecto() + "</proyecto>";
        retorno += "<usuario>" + nuevo.getProyecto() + "</usuario>";
        retorno += "<observaciones>" + nuevo.getProyecto() + "</observaciones>";
        retorno += "<analista>" + nuevo.getProyecto() + "</analista>";

        return retorno;
    }

}
