/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Bodegas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class BodegasFacade extends AbstractFacade<Bodegas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BodegasFacade() {
        super(Bodegas.class);
    }

    @Override
    protected String modificarObjetos(Bodegas nuevo) {
        String retorno = "";
        retorno += "<venta>" + nuevo.getVenta() + "</venta>";
        retorno += "<sucursal>" + nuevo.getSucursal() + "</sucursal>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        return retorno;
    }

}
