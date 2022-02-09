/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Acumulados;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class AcumuladosFacade extends AbstractFacade<Acumulados> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AcumuladosFacade() {
        super(Acumulados.class);
    }

    @Override
    protected String modificarObjetos(Acumulados nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<concepto>" + nuevo.getConcepto() + "</concepto>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<mes>" + nuevo.getMes() + "</mes>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<cantidad>" + nuevo.getCantidad() + "</cantidad>";
        retorno += "<pagado>" + nuevo.getPagado() + "</pagado>";

        return retorno;
    }

}
