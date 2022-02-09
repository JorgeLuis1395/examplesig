/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Comisionpostulacion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ComisionpostulacionFacade extends AbstractFacade<Comisionpostulacion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComisionpostulacionFacade() {
        super(Comisionpostulacion.class);
    }

    @Override
    protected String modificarObjetos(Comisionpostulacion nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<solicitud>" + nuevo.getSolicitud() + "</solicitud>";

        return retorno;
    }

}
