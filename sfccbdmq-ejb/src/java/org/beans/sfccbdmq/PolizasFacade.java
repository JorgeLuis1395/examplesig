/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Polizas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class PolizasFacade extends AbstractFacade<Polizas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PolizasFacade() {
        super(Polizas.class);
    }

    @Override
    protected String modificarObjetos(Polizas nuevo) {
        String retorno = "";
        retorno += "<prima>" + nuevo.getPrima() + "</prima>";
        retorno += "<aseguradora>" + nuevo.getAseguradora() + "</aseguradora>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";

        return retorno;
    }

}
