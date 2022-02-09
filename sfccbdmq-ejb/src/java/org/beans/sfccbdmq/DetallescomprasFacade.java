/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Detallescompras;

/**
 *
 * @author edwin
 */
@Stateless
public class DetallescomprasFacade extends AbstractFacade<Detallescompras> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetallescomprasFacade() {
        super(Detallescompras.class);
    }

    @Override
    protected String modificarObjetos(Detallescompras nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<detallecompromiso>" + nuevo.getDetallecompromiso() + "</detallecompromiso>";
        retorno += "<cabeceradoc>" + nuevo.getCabeceradoc()+ "</cabeceradoc>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        return retorno;
    }

}
