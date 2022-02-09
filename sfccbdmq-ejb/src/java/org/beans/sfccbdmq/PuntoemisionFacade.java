/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Puntoemision;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class PuntoemisionFacade extends AbstractFacade<Puntoemision> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PuntoemisionFacade() {
        super(Puntoemision.class);
    }

    @Override
    protected String modificarObjetos(Puntoemision nuevo) {
        String retorno = "";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<automatica>" + nuevo.getAutomatica() + "</automatica>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<codigoalterno>" + nuevo.getCodigoalterno() + "</codigoalterno>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<sucursal>" + nuevo.getSucursal() + "</sucursal>";

        return retorno;
    }

}
