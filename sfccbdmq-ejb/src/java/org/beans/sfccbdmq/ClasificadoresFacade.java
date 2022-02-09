/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Clasificadores;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ClasificadoresFacade extends AbstractFacade<Clasificadores> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClasificadoresFacade() {
        super(Clasificadores.class);
    }

    @Override
    protected String modificarObjetos(Clasificadores nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<ingreso>" + nuevo.getIngreso() + "</ingreso>";
        retorno += "<imputable>" + nuevo.getImputable() + "</imputable>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";

        return retorno;
    }

}
