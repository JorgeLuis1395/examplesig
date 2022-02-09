/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Requerimientoscargo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class RequerimientoscargoFacade extends AbstractFacade<Requerimientoscargo> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RequerimientoscargoFacade() {
        super(Requerimientoscargo.class);
    }

    @Override
    protected String modificarObjetos(Requerimientoscargo nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<competencia>" + nuevo.getCompetencia() + "</competencia>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<pesoevaluacion>" + nuevo.getPesoevaluacion() + "</pesoevaluacion>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<entrevista>" + nuevo.getEntrevista() + "</entrevista>";
        retorno += "<orden>" + nuevo.getOrden() + "</orden>";

        return retorno;
    }

}
