/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Novedadesxempleado;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class NovedadesxempleadoFacade extends AbstractFacade<Novedadesxempleado> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NovedadesxempleadoFacade() {
        super(Novedadesxempleado.class);
    }

    @Override
    protected String modificarObjetos(Novedadesxempleado nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<concepto>" + nuevo.getConcepto() + "</concepto>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<mes>" + nuevo.getMes() + "</mes>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";

        return retorno;
    }

}
