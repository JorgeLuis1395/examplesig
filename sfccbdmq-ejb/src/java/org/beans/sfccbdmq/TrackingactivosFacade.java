/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Trackingactivos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TrackingactivosFacade extends AbstractFacade<Trackingactivos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TrackingactivosFacade() {
        super(Trackingactivos.class);
    }

    @Override
    protected String modificarObjetos(Trackingactivos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<cuenta2>" + nuevo.getCuenta2() + "</cuenta2>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<cuenta1>" + nuevo.getCuenta1() + "</cuenta1>";
        retorno += "<valornuevo>" + nuevo.getValornuevo() + "</valornuevo>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";

        return retorno;
    }
}
