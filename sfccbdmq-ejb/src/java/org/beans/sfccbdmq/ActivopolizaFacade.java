/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Activopoliza;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ActivopolizaFacade extends AbstractFacade<Activopoliza> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActivopolizaFacade() {
        super(Activopoliza.class);
    }

    @Override
    protected String modificarObjetos(Activopoliza nuevo) {
        String retorno = "";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<poliza>" + nuevo.getPoliza() + "</poliza>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";
        retorno += "<valorasegurado>" + nuevo.getValorasegurado() + "</valorasegurado>";

        return retorno;
    }

}
