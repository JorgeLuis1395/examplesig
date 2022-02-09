/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tiposanciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TiposancionesFacade extends AbstractFacade<Tiposanciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TiposancionesFacade() {
        super(Tiposanciones.class);
    }

    @Override
    protected String modificarObjetos(Tiposanciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<sancion>" + nuevo.getSancion() + "</sancion>";

        return retorno;
    }

}
