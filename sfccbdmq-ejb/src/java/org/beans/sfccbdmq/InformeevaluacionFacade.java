/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Informeevaluacion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class InformeevaluacionFacade extends AbstractFacade<Informeevaluacion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InformeevaluacionFacade() {
        super(Informeevaluacion.class);
    }

    @Override
    protected String modificarObjetos(Informeevaluacion nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<indicadores>" + nuevo.getIndicadores() + "</indicadores>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<informante>" + nuevo.getInformante() + "</informante>";
        retorno += "<evaluacion>" + nuevo.getEvaluacion() + "</evaluacion>";

        return retorno;
    }

}
