/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Calificacionesoposicion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class CalificacionesoposicionFacade extends AbstractFacade<Calificacionesoposicion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CalificacionesoposicionFacade() {
        super(Calificacionesoposicion.class);
    }

    @Override
    protected String modificarObjetos(Calificacionesoposicion nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<postulacion>" + nuevo.getPostulacion() + "</postulacion>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<definitivo>" + nuevo.getDefinitivo() + "</definitivo>";
        retorno += "<entrevista>" + nuevo.getEntrevista() + "</entrevista>";
        retorno += "<detalle>" + nuevo.getDetalle() + "</detalle>";

        return retorno;
    }

}
