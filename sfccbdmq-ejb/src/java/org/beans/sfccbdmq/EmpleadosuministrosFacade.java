/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Empleadosuministros;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class EmpleadosuministrosFacade extends AbstractFacade<Empleadosuministros> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmpleadosuministrosFacade() {
        super(Empleadosuministros.class);
    }

    @Override
    protected String modificarObjetos(Empleadosuministros nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<suministro>" + nuevo.getSuministro() + "</suministro>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<cantidad>" + nuevo.getCantidad() + "</cantidad>";
        retorno += "<explicacion>" + nuevo.getExplicacion() + "</explicacion>";
        retorno += "<aprobado>" + nuevo.getAprobado() + "</aprobado>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";

        return retorno;
    }

}
