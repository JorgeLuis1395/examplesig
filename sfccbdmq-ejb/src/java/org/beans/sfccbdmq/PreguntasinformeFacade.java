/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Preguntasinforme;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class PreguntasinformeFacade extends AbstractFacade<Preguntasinforme> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PreguntasinformeFacade() {
        super(Preguntasinforme.class);
    }

    @Override
    protected String modificarObjetos(Preguntasinforme nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<pregunta>" + nuevo.getPregunta() + "</pregunta>";
        retorno += "<informe>" + nuevo.getInforme() + "</informe>";
        retorno += "<respuesta>" + nuevo.getRespuesta() + "</respuesta>";
        retorno += "<configuracion>" + nuevo.getConfiguracion() + "</configuracion>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";

        return retorno;
    }

}
