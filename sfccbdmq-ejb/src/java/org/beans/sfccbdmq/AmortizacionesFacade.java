/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Amortizaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class AmortizacionesFacade extends AbstractFacade<Amortizaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AmortizacionesFacade() {
        super(Amortizaciones.class);
    }

    @Override
    protected String modificarObjetos(Amortizaciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<prestamo>" + nuevo.getPrestamo() + "</prestamo>";
        retorno += "<mes>" + nuevo.getMes() + "</mes>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<cuota>" + nuevo.getCuota() + "</cuota>";
        retorno += "<valorpagado>" + nuevo.getValorpagado() + "</valorpagado>";
        return retorno;
    }

}
