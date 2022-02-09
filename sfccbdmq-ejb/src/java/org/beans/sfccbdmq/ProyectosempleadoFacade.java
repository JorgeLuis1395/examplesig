/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Proyectosempleado;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ProyectosempleadoFacade extends AbstractFacade<Proyectosempleado> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProyectosempleadoFacade() {
        super(Proyectosempleado.class);
    }

    @Override
    protected String modificarObjetos(Proyectosempleado nuevo) {
        String retorno = "";
        retorno += "<actividades>" + nuevo.getActividades() + "</actividades>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";

        return retorno;
    }

}
