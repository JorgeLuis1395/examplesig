/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tipoactivo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TipoactivoFacade extends AbstractFacade<Tipoactivo> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoactivoFacade() {
        super(Tipoactivo.class);
    }

    @Override
    protected String modificarObjetos(Tipoactivo nuevo) {
        String retorno = "";
        retorno += "<padre>" + nuevo.getPadre() + "</padre>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<imputable>" + nuevo.getImputable() + "</imputable>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }

}
