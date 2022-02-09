/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Partidaspoa;

/**
 *
 * @author luis
 */
@Stateless
public class PartidaspoaFacade extends AbstractFacade<Partidaspoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PartidaspoaFacade() {
        super(Partidaspoa.class);
    }

    @Override
    protected String modificarObjetos(Partidaspoa nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<ingreso>" + nuevo.getIngreso() + "</ingreso>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<imputable>" + nuevo.getImputable() + "</imputable>";

        return retorno;
    }

}
