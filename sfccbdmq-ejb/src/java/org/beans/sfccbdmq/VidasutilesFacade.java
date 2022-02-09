/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Vidasutiles;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class VidasutilesFacade extends AbstractFacade<Vidasutiles> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VidasutilesFacade() {
        super(Vidasutiles.class);
    }
    
    @Override
    protected String modificarObjetos(Vidasutiles nuevo) {
        String retorno = "";
        retorno += "<mes>" + nuevo.getMes() + "</mes>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<unidades>" + nuevo.getUnidades() + "</unidades>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<valorresidual>" + nuevo.getValorresidual() + "</valorresidual>";
        retorno += "<vidautil>" + nuevo.getVidautil() + "</vidautil>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        return retorno;

    }
}
