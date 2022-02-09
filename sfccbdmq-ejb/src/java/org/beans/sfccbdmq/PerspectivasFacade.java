/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Perspectivas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class PerspectivasFacade extends AbstractFacade<Perspectivas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PerspectivasFacade() {
        super(Perspectivas.class);
    }

    @Override
    protected String modificarObjetos(Perspectivas nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cargoevaluado>" + nuevo.getCargoevaluado() + "</cargoevaluado>";
        retorno += "<perspectiva>" + nuevo.getPerspectiva() + "</perspectiva>";
        retorno += "<ponderacion>" + nuevo.getPonderacion() + "</ponderacion>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";

        return retorno;
    }

}
