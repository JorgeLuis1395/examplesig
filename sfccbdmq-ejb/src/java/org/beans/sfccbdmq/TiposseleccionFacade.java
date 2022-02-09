/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tiposseleccion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TiposseleccionFacade extends AbstractFacade<Tiposseleccion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TiposseleccionFacade() {
        super(Tiposseleccion.class);
    }

    @Override
    protected String modificarObjetos(Tiposseleccion nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<estatus>" + nuevo.getEstatus() + "</estatus>";
        retorno += "<ponderacion>" + nuevo.getPonderacion() + "</ponderacion>";
        retorno += "<notamaxima>" + nuevo.getNotamaxima() + "</notamaxima>";
        retorno += "<orden>" + nuevo.getOrden() + "</orden>";
        retorno += "<notaminima>" + nuevo.getNotaminima() + "</notaminima>";

        return retorno;
    }

}
