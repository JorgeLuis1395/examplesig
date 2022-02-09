/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Experiencias;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ExperienciasFacade extends AbstractFacade<Experiencias> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExperienciasFacade() {
        super(Experiencias.class);
    }

    @Override
    protected String modificarObjetos(Experiencias nuevo) {
        String retorno = "";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        retorno += "<datosvalidacion>" + nuevo.getDatosvalidacion() + "</datosvalidacion>";
        retorno += "<validado>" + nuevo.getValidado() + "</validado>";
        retorno += "<cargo>" + nuevo.getCargo() + "</cargo>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<empresa>" + nuevo.getEmpresa() + "</empresa>";

        return retorno;
    }

}
