/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Detallesoposicion;
import org.entidades.sfccbdmq.Detallesvales;

/**
 *
 * @author edison
 */
@Stateless
public class DetallesvalesFacade extends AbstractFacade<Detallesvales> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetallesvalesFacade() {
        super(Detallesvales.class);
    }

    @Override
    protected String modificarObjetos(Detallesvales nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<vale>" + nuevo.getVale() + "</vale>";
        retorno += "<detallecompromiso>" + nuevo.getDetallecompromiso() + "</detallecompromiso>";
        retorno += "<detallecertificacion>" + nuevo.getDetallecertificacion() + "</detallecertificacion>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        return retorno;
    }

}