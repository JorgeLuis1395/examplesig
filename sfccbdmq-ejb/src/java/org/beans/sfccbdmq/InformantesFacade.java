/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Informantes;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class InformantesFacade extends AbstractFacade<Informantes> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InformantesFacade() {
        super(Informantes.class);
    }

    @Override
    protected String modificarObjetos(Informantes nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<perspectiva>" + nuevo.getPerspectiva() + "</perspectiva>";
        retorno += "<informante>" + nuevo.getInformante() + "</informante>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";

        return retorno;
    }

}
