/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Detallecertificaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class DetallecertificacionesFacade extends AbstractFacade<Detallecertificaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetallecertificacionesFacade() {
        super(Detallecertificaciones.class);
    }

    @Override
    protected String modificarObjetos(Detallecertificaciones nuevo) {
        String retorno = "";
        retorno += "<asignacion>" + nuevo.getAsignacion() + "</asignacion>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<certificacion>" + nuevo.getCertificacion() + "</certificacion>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";

        return retorno;
    }

}
