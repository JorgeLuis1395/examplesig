/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Maestros;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class MaestrosFacade extends AbstractFacade<Maestros> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MaestrosFacade() {
        super(Maestros.class);
    }

    @Override
    protected String modificarObjetos(Maestros nuevo) {
        String retorno = "";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }

}
