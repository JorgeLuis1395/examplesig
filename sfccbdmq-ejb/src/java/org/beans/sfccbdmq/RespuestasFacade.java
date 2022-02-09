/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Respuestas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class RespuestasFacade extends AbstractFacade<Respuestas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RespuestasFacade() {
        super(Respuestas.class);
    }

    @Override
    protected String modificarObjetos(Respuestas nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<orden>" + nuevo.getOrden() + "</orden>";
        retorno += "<respuestas>" + nuevo.getRespuestas() + "</respuestas>";
        retorno += "<pregunta>" + nuevo.getPregunta() + "</pregunta>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";

        return retorno;
    }

}
