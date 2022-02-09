/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Actividadescargos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ActividadescargosFacade extends AbstractFacade<Actividadescargos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActividadescargosFacade() {
        super(Actividadescargos.class);
    }

    @Override
    protected String modificarObjetos(Actividadescargos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<cargo>" + nuevo.getCargo() + "</cargo>";
        retorno += "<pesoevaluacion>" + nuevo.getPesoevaluacion() + "</pesoevaluacion>";
        retorno += "<actividad>" + nuevo.getActividad() + "</actividad>";

        return retorno;
    }

}
