/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Verificacionesreferencias;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class VerificacionesreferenciasFacade extends AbstractFacade<Verificacionesreferencias> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VerificacionesreferenciasFacade() {
        super(Verificacionesreferencias.class);
    }

    @Override
    protected String modificarObjetos(Verificacionesreferencias nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<experiencia>" + nuevo.getExperiencia() + "</experiencia>";
        retorno += "<recomendacion>" + nuevo.getRecomendacion() + "</recomendacion>";
        retorno += "<causadesvinculacion>" + nuevo.getCausadesvinculacion() + "</causadesvinculacion>";
        retorno += "<recomiendacargo>" + nuevo.getRecomiendacargo() + "</recomiendacargo>";
        retorno += "<puntosdesarrollar>" + nuevo.getPuntosdesarrollar() + "</puntosdesarrollar>";
        retorno += "<descripcioncomportamiento>" + nuevo.getDescripcioncomportamiento() + "</descripcioncomportamiento>";
        retorno += "<observacion>" + nuevo.getObservacion() + "</observacion>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";

        return retorno;
    }

}
