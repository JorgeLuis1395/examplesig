/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Detallesoposicion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class DetallesoposicionFacade extends AbstractFacade<Detallesoposicion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetallesoposicionFacade() {
        super(Detallesoposicion.class);
    }

    @Override
    protected String modificarObjetos(Detallesoposicion nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<area>" + nuevo.getArea() + "</area>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<notamaxima>" + nuevo.getNotamaxima() + "</notamaxima>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<orden>" + nuevo.getOrden() + "</orden>";

        return retorno;
    }

}
