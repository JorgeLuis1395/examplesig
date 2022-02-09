/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Operaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class OperacionesFacade extends AbstractFacade<Operaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OperacionesFacade() {
        super(Operaciones.class);
    }

    @Override
    protected String modificarObjetos(Operaciones nuevo) {
        String retorno = "";
        retorno += "<constante>" + nuevo.getConstante() + "</constante>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<campo1>" + nuevo.getCampo1() + "</campo1>";
        retorno += "<campo2>" + nuevo.getCampo2() + "</campo2>";
        retorno += "<operacion>" + nuevo.getOperacion() + "</operacion>";

        return retorno;
    }

}
