/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Recomendaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class RecomendacionesFacade extends AbstractFacade<Recomendaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RecomendacionesFacade() {
        super(Recomendaciones.class);
    }

    @Override
    protected String modificarObjetos(Recomendaciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<telefono>" + nuevo.getTelefono() + "</telefono>";
        retorno += "<cargo>" + nuevo.getCargo() + "</cargo>";
        retorno += "<empresa>" + nuevo.getEmpresa() + "</empresa>";
        retorno += "<validado>" + nuevo.getValidado() + "</validado>";
        retorno += "<responvalid>" + nuevo.getResponvalid() + "</responvalid>";
        retorno += "<obsvalidado>" + nuevo.getObsvalidado() + "</obsvalidado>";

        return retorno;
    }

}
