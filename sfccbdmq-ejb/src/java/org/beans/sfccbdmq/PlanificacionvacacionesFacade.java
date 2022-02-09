/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Planificacionvacaciones;

/**
 *
 * @author edwin
 */
@Stateless
public class PlanificacionvacacionesFacade extends AbstractFacade<Planificacionvacaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlanificacionvacacionesFacade() {
        super(Planificacionvacaciones.class);
    }

    @Override
    protected String modificarObjetos(Planificacionvacaciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<desde>" + nuevo.getDesde()+ "</desde>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        return retorno;
    }
    
}
