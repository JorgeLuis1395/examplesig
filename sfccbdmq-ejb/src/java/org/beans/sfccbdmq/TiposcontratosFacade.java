/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tiposcontratos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TiposcontratosFacade extends AbstractFacade<Tiposcontratos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TiposcontratosFacade() {
        super(Tiposcontratos.class);
    }

    @Override
    protected String modificarObjetos(Tiposcontratos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<nombramiento>" + nuevo.getNombramiento() + "</nombramiento>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<duracion>" + nuevo.getDuracion() + "</duracion>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<codigoalterno>" + nuevo.getCodigoalterno()+ "</codigoalterno>";
        retorno += "<ordinal>" + nuevo.getOrdinal() + "</ordinal>";

        return retorno;
    }

}
