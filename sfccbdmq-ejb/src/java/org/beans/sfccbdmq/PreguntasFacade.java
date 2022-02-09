/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Preguntas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class PreguntasFacade extends AbstractFacade<Preguntas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PreguntasFacade() {
        super(Preguntas.class);
    }

    @Override
    protected String modificarObjetos(Preguntas nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<pregunta>" + nuevo.getPregunta() + "</pregunta>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<orden>" + nuevo.getOrden() + "</orden>";
        retorno += "<formulario>" + nuevo.getFormulario() + "</formulario>";

        return retorno;
    }

}
